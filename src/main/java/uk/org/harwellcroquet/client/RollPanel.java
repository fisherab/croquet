package uk.org.harwellcroquet.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import uk.org.harwellcroquet.client.service.EventServiceAsync;
import uk.org.harwellcroquet.shared.Win;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;

public class RollPanel extends Composite {

	interface RollPanelUiBinder extends UiBinder<Widget, RollPanel> {
	}

	private static RollPanelUiBinder uiBinder = GWT
			.create(RollPanelUiBinder.class);

	private final EventServiceAsync eventService = EventServiceAsync.Util
			.getInstance();

	final private static Logger logger = Logger.getLogger(RollPanel.class
			.getName());

	@UiField
	Grid table;

	public RollPanel() {
		initWidget(uiBinder.createAndBindUi(this));

		eventService.getRollOfHonour(new AsyncCallback<List<Win>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.toString());

			}

			@Override
			public void onSuccess(List<Win> wins) {
				logger.fine("Number of wins " + wins.size());
				Set<Integer> years = new HashSet<>();
				Map<String, Integer> eventNames = new HashMap<>();
				for (Win win : wins) {
					years.add(win.getYear());
					String eventName = win.getEventName();
					if (!eventNames.containsKey(eventName)) {
						eventNames.put(eventName, eventNames.size());
					}
				}
				logger.fine("Number of years with wins " + years.size());
				logger.fine("Number of event names " + eventNames.size());
				table.resize(years.size() + 1, eventNames.size() + 2);

				table.setText(0, 0, "Year");
				table.getCellFormatter().addStyleName(0,0,CroquetBundle.INSTANCE.css().rollHeader());
				
				for (Entry<String, Integer> entry : eventNames.entrySet()) {
					
					table.setText(0, 1 + entry.getValue(), entry.getKey());
					table.getCellFormatter().addStyleName(0, 1 + entry.getValue(),CroquetBundle.INSTANCE.css().rollHeader());
				}

				int year = 0;
				int n = 0;
				for (Win win : wins) {
					if (year != win.getYear()) {
						n++;
						year = win.getYear();
					}
					String eventName = win.getEventName();
					table.setText(n, 0, Integer.toString(year));
					table.getCellFormatter().addStyleName(n,0,CroquetBundle.INSTANCE.css().rollEntry());
					table.setText(n, 1 + eventNames.get(eventName),
							win.getPersonName());
					table.getCellFormatter().addStyleName(n,1 + eventNames.get(eventName),CroquetBundle.INSTANCE.css().rollEntry());
				}
			}
		});

	}

}
