package uk.org.harwellcroquet.client.service;

import java.util.List;
import java.util.Set;

import uk.org.harwellcroquet.shared.AuthException;
import uk.org.harwellcroquet.shared.BadInputException;
import uk.org.harwellcroquet.shared.InternalException;
import uk.org.harwellcroquet.shared.ItemTO;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("item")
public interface ItemService extends RemoteService {

	String getNavigationHtml();

	String getHomeHtml() throws InternalException;

	String getContactHtml() throws BadInputException;

	void add(String sessionid, ItemTO itos) throws BadInputException, AuthException;

	List<ItemTO> getItems(String sessionid, String type) throws AuthException, InternalException;

	void update(String sessionid, Set<ItemTO> modified) throws AuthException, BadInputException;
}
