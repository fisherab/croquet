package uk.org.harwellcroquet.server;

import java.util.List;
import java.util.Set;

import javax.ejb.EJB;

import uk.org.harwellcroquet.client.service.ItemService;
import uk.org.harwellcroquet.server.bean.ItemBean;
import uk.org.harwellcroquet.shared.AuthException;
import uk.org.harwellcroquet.shared.BadInputException;
import uk.org.harwellcroquet.shared.InternalException;
import uk.org.harwellcroquet.shared.ItemTO;

@SuppressWarnings("serial")
public class ItemServiceImpl extends ContextRemoteServiceServlet implements ItemService {

	@EJB
	private ItemBean itemBean;

	@Override
	public String getNavigationHtml() {
		return itemBean.getNavigationHtml();
	}

	@Override
	public void add(String sessionid, ItemTO itos) throws BadInputException, AuthException {
		itemBean.add(sessionid, itos);
	}

	@Override
	public String getHomeHtml() throws InternalException {
		return itemBean.getHomeHtml();
	}

	@Override
	public List<ItemTO> getItems(String sessionid, String type) throws AuthException,
			InternalException {
		return itemBean.getItems(sessionid, type);
	}

	@Override
	public void update(String sessionid, Set<ItemTO> modified) throws AuthException,
			BadInputException {
		itemBean.update(sessionid, modified);
	}

	@Override
	public String getContactHtml() throws BadInputException {
		return itemBean.getContactHtml();
	}

}
