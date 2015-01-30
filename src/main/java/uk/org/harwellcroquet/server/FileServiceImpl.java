package uk.org.harwellcroquet.server;

import java.util.List;
import java.util.Set;

import javax.ejb.EJB;

import uk.org.harwellcroquet.client.service.FileService;
import uk.org.harwellcroquet.server.bean.StoredFileBean;
import uk.org.harwellcroquet.shared.AuthException;
import uk.org.harwellcroquet.shared.BadInputException;
import uk.org.harwellcroquet.shared.InternalException;
import uk.org.harwellcroquet.shared.StoredFileTO;

@SuppressWarnings("serial")
public class FileServiceImpl extends ContextRemoteServiceServlet implements FileService {

	@EJB
	private StoredFileBean fileBean;

	@Override
	public List<StoredFileTO> getFileNamesStarting(String string) throws InternalException {
		return fileBean.getFileNamesStarting(string);
	}

	@Override
	public void update(String sessionid, Set<StoredFileTO> modified) throws InternalException,
			AuthException, BadInputException {
		fileBean.update(sessionid, modified);
	}

}
