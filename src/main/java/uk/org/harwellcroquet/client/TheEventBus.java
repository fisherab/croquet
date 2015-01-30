package uk.org.harwellcroquet.client;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;

public class TheEventBus {

	private static EventBus eventBus;

	static synchronized EventBus getInstance() {
		if (TheEventBus.eventBus == null) {
			TheEventBus.eventBus = GWT.create(SimpleEventBus.class);
		}
		return TheEventBus.eventBus;

	}

}
