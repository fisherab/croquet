package uk.org.harwellcroquet.client;

import java.util.logging.Logger;

import uk.org.harwellcroquet.client.service.ItemServiceAsync;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;

public class ContactPanel extends HTML {

	private final ItemServiceAsync itemService = ItemServiceAsync.Util.getInstance();
	final private static Logger logger = Logger.getLogger(ContactPanel.class.getName());

	public ContactPanel() {
		itemService.getContactHtml(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				logger.severe(caught.toString());
				Window.alert("Failure " + caught);
			}

			@Override
			public void onSuccess(String result) {
				setHTML(result);
			}
		});
		
		
	
		
	}

}
