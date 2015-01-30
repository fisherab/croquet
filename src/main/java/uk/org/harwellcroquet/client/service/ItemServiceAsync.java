package uk.org.harwellcroquet.client.service;

import java.util.List;
import java.util.Set;

import uk.org.harwellcroquet.shared.ItemTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ItemServiceAsync {

	public static final class Util {
		private static ItemServiceAsync instance;

		public static final ItemServiceAsync getInstance() {
			if (instance == null) {
				instance = (ItemServiceAsync) GWT.create(ItemService.class);
			}
			return instance;
		}

		private Util() {
		}
	}

	void add(String sessionid, ItemTO itos, AsyncCallback<Void> callback);

	void getContactHtml(AsyncCallback<String> callback);

	void getHomeHtml(AsyncCallback<String> callback);

	void getItems(String sessionid, String type, AsyncCallback<List<ItemTO>> callback);

	void getNavigationHtml(AsyncCallback<String> callback);

	void update(String sessionid, Set<ItemTO> modified, AsyncCallback<Void> callback);

}
