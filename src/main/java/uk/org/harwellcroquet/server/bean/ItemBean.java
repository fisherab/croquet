package uk.org.harwellcroquet.server.bean;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import uk.org.harwellcroquet.server.entity.Item;
import uk.org.harwellcroquet.server.entity.User;
import uk.org.harwellcroquet.shared.AuthException;
import uk.org.harwellcroquet.shared.BadInputException;
import uk.org.harwellcroquet.shared.InternalException;
import uk.org.harwellcroquet.shared.ItemTO;

@Stateless
public class ItemBean {

	@PostConstruct
	public void init() {
		logger.info("Starting init of ItemBean");
	}

	@PersistenceContext(unitName = "croquet")
	private EntityManager entityManager;

	final static Logger logger = Logger.getLogger(ItemBean.class);

	DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);

	public String getNavigationHtml() {
		logger.debug("getNavigationHtml");
		List<Item> results = entityManager.createNamedQuery("ItemByType", Item.class)
				.setParameter("type", "NAV").getResultList();
		logger.debug("Retrieved " + results.size() + " rows.");
		StringBuilder sb = new StringBuilder();
		sb.append("<table>");
		for (Item item : results) {
			sb.append("<tr><td>").append(item.getContent()).append("</td></tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}

	public void add(String sessionid, ItemTO ito) throws BadInputException, AuthException {
		logger.debug("Add an item");
		User ui = LoginBean.getUser(entityManager, sessionid);
		Integer seq = entityManager.createNamedQuery("MaxSeq", Integer.class)
				.setParameter("type", ito.getType()).setParameter("user", ui.getId())
				.getSingleResult();
		if (seq == null) {
			seq = 0;
		} else {
			seq++;
		}
		Item item = new Item(ito);
		item.setUser(ui);
		item.setSeq(seq);
		entityManager.persist(item);
	}

	public String getHomeHtml() throws InternalException {
		logger.debug("Construct HTML for home page");

		StringBuilder sb = new StringBuilder("<div>");
		TypedQuery<Item> query = entityManager.createNamedQuery("ItemByType", Item.class)
				.setParameter("type", "BODY");
		for (Item item : query.getResultList()) {
			sb.append(item.getContent());
		}
		sb.append("</div><div><h2>News</h2><ul>");

		List<Item> items = entityManager.createNamedQuery("ItemByTypeDateOrdered", Item.class)
				.setParameter("type", "news").getResultList();
		for (Item item : items) {
			sb.append("<li>").append(dateFormat.format(item.getDate())).append("&nbsp;")
					.append(item.getContent()).append("&nbsp;&nbsp;<i>")
					.append(item.getUser().getName()).append("</i></li>");
		}
		sb.append("</ul></div>");
		return sb.toString();

	}

	public List<ItemTO> getItems(String sessionid, String type) throws AuthException,
			InternalException {
		logger.debug("getItems");
		User ui = LoginBean.getUser(entityManager, sessionid);
		TypedQuery<Item> query = null;
		if (ui.getPriv() == User.Priv.SUPER) {
			query = entityManager.createNamedQuery("ItemByType", Item.class);
		} else {
			query = entityManager.createNamedQuery("ItemByTypeAndUser", Item.class);
			query.setParameter("user", ui.getId());
		}

		List<Item> items = query.setParameter("type", type).getResultList();
		List<ItemTO> ittos = new ArrayList<ItemTO>();
		for (Item item : items) {
			ItemTO itto = item.getTransferObject();
			ittos.add(itto);
		}
		return ittos;
	}

	public void update(String sessionid, Set<ItemTO> modified) throws AuthException,
			BadInputException {
		logger.debug("Update " + modified.size() + " items");
		User ui = LoginBean.getUser(entityManager, sessionid);

		for (ItemTO to : modified) {
			Item itemo = entityManager.find(Item.class, to.getId());
			if (ui.getId() != itemo.getUser().getId() && ui.getPriv() != User.Priv.SUPER) {
				throw new AuthException(
						"You must be admin or the owner of the item to make this call");
			}
			if (to.isDelete()) {
				entityManager.remove(itemo);
			} else {
				itemo.setSeq(to.getSeq());
				itemo.setContent(to.getContent());
			}
		}
	}

	public String getContactHtml() throws BadInputException {
		logger.debug("getContactHtml");
		StringBuilder sb = new StringBuilder("<h2>Contacts</h2><table>");
		List<Item> contacts = entityManager.createNamedQuery("ItemByType", Item.class)
				.setParameter("type", "CONTACT").getResultList();

		for (Item item : contacts) {
			String[] bits = item.getContent().split(":");
			if (bits.length == 2) {
				User u = entityManager.find(User.class, Long.parseLong(bits[1]));
				sb.append("<tr><td>").append(bits[0]).append("</td><td>").append(u.getName())
						.append("</td><td><a href=\"mailto:").append(u.getEmail()).append("\">")
						.append(u.getEmail()).append("</a></td><td>").append(u.getPhone1())
						.append("</td><td>").append(u.getPhone2()).append("</td></tr>");
			}
		}
		sb.append("</table>");
		return sb.toString();

	}
}
