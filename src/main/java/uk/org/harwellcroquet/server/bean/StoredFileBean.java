package uk.org.harwellcroquet.server.bean;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import uk.org.harwellcroquet.server.entity.StoredFile;
import uk.org.harwellcroquet.server.entity.User;
import uk.org.harwellcroquet.shared.AuthException;
import uk.org.harwellcroquet.shared.BadInputException;
import uk.org.harwellcroquet.shared.StoredFileTO;

@Stateless
public class StoredFileBean {

	final static Logger logger = Logger.getLogger(StoredFileBean.class);

	@PersistenceContext(unitName = "croquet")
	private EntityManager entityManager;

	public Long storeFileName(long uid, String name) throws AuthException {
		StoredFile fn = new StoredFile();
		fn.setName(name);
		fn.setUser(entityManager.find(User.class, uid));
		entityManager.persist(fn);
		return fn.getId();
	}

	public List<StoredFileTO> getFileNamesStarting(String start) {

		List<StoredFileTO> results = new ArrayList<StoredFileTO>();

		TypedQuery<StoredFile> query = null;
		if (start != null) {
			query = entityManager.createNamedQuery(StoredFile.LIKE, StoredFile.class).setParameter("name", start + "%");
		} else {
			query = entityManager.createNamedQuery(StoredFile.ALL, StoredFile.class);
		}
		for (StoredFile fn : query.getResultList()) {
			results.add(fn.getTransferObject());
		}
		return results;
	}

	public void update(String sessionid, Set<StoredFileTO> modified) throws BadInputException, AuthException {
		logger.debug("Update " + modified.size() + " items");
		User ui = LoginBean.getUser(entityManager, sessionid);
		for (StoredFileTO to : modified) {

			StoredFile fn = entityManager.find(StoredFile.class, to.getId());
			if (ui.getPriv() == User.Priv.SUPER || fn.getUser().getId().longValue() == ui.getId().longValue()) {
				if (to.isDelete()) {
					entityManager.remove(fn);
				} else {
					fn.setName(to.getName());
					User u = entityManager.find(User.class, to.getUser().getId());
					if (u == null) {
						throw new BadInputException("No user has id " + to.getUser().getId());
					}
					fn.setUser(u);
				}
			}
		}
	}

	public File getFile(String name) {
		File dir = Paths.get("..", "data", "croquet", "files").toFile();
		StoredFile fileName = entityManager.createNamedQuery(StoredFile.LIKE, StoredFile.class)
				.setParameter("name", name).getSingleResult();
		return new File(dir, Long.toString(fileName.getId()));
	}
}
