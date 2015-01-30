package uk.org.harwellcroquet.client.service;

import java.util.List;
import java.util.Set;

import uk.org.harwellcroquet.shared.AuthException;
import uk.org.harwellcroquet.shared.BadInputException;
import uk.org.harwellcroquet.shared.EntrantTO;
import uk.org.harwellcroquet.shared.EventTO;
import uk.org.harwellcroquet.shared.FixtureNameTO;
import uk.org.harwellcroquet.shared.FixtureTO;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("event")
public interface EventService extends RemoteService {

	void add(String sessionid, EventTO eto) throws AuthException, BadInputException;

	List<String> eventsForYear(int year);

	List<EventTO> listDeletableEvents();

	void delete(String sessionid, List<Long> todelete) throws AuthException;

	EventTO getEventForYear(int year, String name);

	List<EventTO> getOpenEvents();

	void update(EventTO eto);

	List<Integer> getYears();

	List<FixtureNameTO> getFixtureNames();

	void updateFixtureNames(List<FixtureNameTO> fntos);

	List<FixtureTO> getFixturesForYear(int year) throws BadInputException;

	void update(Set<FixtureTO> modified);

	List<String> getFixtureNamesForYear(int year) throws BadInputException;

	List<FixtureTO> getFixtures(String sessionid, int year, String name) throws BadInputException, AuthException;

	void deleteEntrant(List<EntrantTO> entrants);

}
