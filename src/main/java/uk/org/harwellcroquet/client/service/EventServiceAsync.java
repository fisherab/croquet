package uk.org.harwellcroquet.client.service;

import java.util.List;
import java.util.Set;

import uk.org.harwellcroquet.shared.EntrantTO;
import uk.org.harwellcroquet.shared.EventTO;
import uk.org.harwellcroquet.shared.FixtureNameTO;
import uk.org.harwellcroquet.shared.FixtureTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface EventServiceAsync {

	public static final class Util {
		private static EventServiceAsync instance;

		public static final EventServiceAsync getInstance() {
			if (instance == null) {
				instance = (EventServiceAsync) GWT.create(EventService.class);
			}
			return instance;
		}

		private Util() {
		}
	}

	void add(String sessionid, EventTO eto, AsyncCallback<Void> callback);

	void delete(String sessionid, List<Long> todelete, AsyncCallback<Void> callback);

	void deleteEntrant(List<EntrantTO> entrants, AsyncCallback<Void> callback);

	void eventsForYear(int year, AsyncCallback<List<String>> callback);

	void getEventForYear(int year, String name, AsyncCallback<EventTO> callback);

	void getFixtureNames(AsyncCallback<List<FixtureNameTO>> callback);

	void getFixtureNamesForYear(int year, AsyncCallback<List<String>> callback);

	void getFixtures(String sessionid, int year, String name,
			AsyncCallback<List<FixtureTO>> callback);

	void getFixturesForYear(int year, AsyncCallback<List<FixtureTO>> callback);

	void getOpenEvents(AsyncCallback<List<EventTO>> callback);

	void getYears(AsyncCallback<List<Integer>> callback);

	void listDeletableEvents(AsyncCallback<List<EventTO>> callback);

	void update(Set<FixtureTO> modified, AsyncCallback<Void> callback);

	void update(EventTO eto, AsyncCallback<Void> callback);

	void updateFixtureNames(List<FixtureNameTO> fntos, AsyncCallback<Void> callback);

}
