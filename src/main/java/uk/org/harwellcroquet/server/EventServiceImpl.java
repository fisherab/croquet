package uk.org.harwellcroquet.server;

import java.util.List;
import java.util.Set;

import javax.ejb.EJB;

import uk.org.harwellcroquet.client.service.EventService;
import uk.org.harwellcroquet.server.bean.EventBean;
import uk.org.harwellcroquet.shared.AuthException;
import uk.org.harwellcroquet.shared.BadInputException;
import uk.org.harwellcroquet.shared.EntrantTO;
import uk.org.harwellcroquet.shared.EventTO;
import uk.org.harwellcroquet.shared.FixtureNameTO;
import uk.org.harwellcroquet.shared.FixtureTO;

@SuppressWarnings("serial")
public class EventServiceImpl extends ContextRemoteServiceServlet implements EventService {

	@EJB
	private EventBean eventBean;

	@Override
	public void add(String sessionid, EventTO eto) throws AuthException, BadInputException {
		eventBean.add(sessionid, eto);
	}

	@Override
	public List<String> eventsForYear(int year) {
		return eventBean.eventsForYear(year);
	}

	@Override
	public List<EventTO> listDeletableEvents() {
		return eventBean.listDeletableEvents();
	}

	@Override
	public void delete(String sessionid, List<Long> todelete) throws AuthException {
		eventBean.delete(sessionid, todelete);
	}

	@Override
	public EventTO getEventForYear(int year, String name) {
		return eventBean.getEventForYear(year, name);
	}

	@Override
	public List<EventTO> getOpenEvents() {
		return eventBean.getOpenEvents();
	}

	@Override
	public void update(EventTO eto) {
		eventBean.update(eto);
	}

	@Override
	public List<Integer> getYears() {
		return eventBean.getYears();
	}

	@Override
	public List<FixtureNameTO> getFixtureNames() {
		return eventBean.getFixtureNames();
	}

	@Override
	public void updateFixtureNames(List<FixtureNameTO> fntos) {
		eventBean.updateFixtureNames(fntos);

	}

	@Override
	public List<FixtureTO> getFixturesForYear(int year) throws BadInputException {
		return eventBean.getFixturesForYear(year);
	}

	@Override
	public void update(Set<FixtureTO> modified) {
		eventBean.update(modified);

	}

	@Override
	public List<String> getFixtureNamesForYear(int year) throws BadInputException {
		return eventBean.getFixtureNamesForYear(year);
	}

	@Override
	public List<FixtureTO> getFixtures(String sessionid, int year, String name) throws BadInputException, AuthException {
		return eventBean.getFixtures(sessionid, year, name);
	}

	@Override
	public void deleteEntrant(List<EntrantTO> entrants) {
		eventBean.deleteEntrant(entrants);
	}

}
