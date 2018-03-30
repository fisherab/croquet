package uk.org.harwellcroquet.server.bean;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;

import uk.org.harwellcroquet.server.entity.Entrant;
import uk.org.harwellcroquet.server.entity.Event;
import uk.org.harwellcroquet.server.entity.Fixture;
import uk.org.harwellcroquet.server.entity.FixtureName;
import uk.org.harwellcroquet.server.entity.Result;
import uk.org.harwellcroquet.server.entity.User;
import uk.org.harwellcroquet.shared.AuthException;
import uk.org.harwellcroquet.shared.BadInputException;
import uk.org.harwellcroquet.shared.EntrantTO;
import uk.org.harwellcroquet.shared.EventTO;
import uk.org.harwellcroquet.shared.FixtureNameTO;
import uk.org.harwellcroquet.shared.FixtureTO;
import uk.org.harwellcroquet.shared.Win;

@Stateless
public class EventBean {

	@PersistenceContext(unitName = "croquet")
	private EntityManager entityManager;

	final static Logger logger = Logger.getLogger(EventBean.class);

	// TODO need synchronization here ...
	final private static DateFormat dateYearFormat = new SimpleDateFormat("yyyy");

	public void add(String sessionid, EventTO eto) throws AuthException, BadInputException {
		logger.debug("Add an event for year " + eto.getYear());
		User ui = LoginBean.getUser(entityManager, sessionid);
		if (ui.getPriv() != User.Priv.SUPER && !ui.isScorer()) {
			throw new AuthException("You are not allowed to do this");
		}
		Event event = new Event(eto);
		List<Entrant> entrants = event.getEntrants();

		if (entrants.size() < 2) {
			throw new BadInputException("At least two entrants are required!");
		}
		if (event.getFormat() == Event.Format.DRAWANDPROCESS) {
			Set<Integer> draw = new HashSet<Integer>();
			Set<Integer> process = new HashSet<Integer>();
			for (Entrant entrant : entrants) {
				if (!draw.add(entrant.getDrawPos()) || !process.add(entrant.getProcessPos())) {
					throw new BadInputException("Two entrants cannot have same position.");
				}
			}
		}
		entityManager.persist(event);
	}

	public List<String> eventsForYear(int year) {
		List<Event> evs = entityManager.createNamedQuery("EventsByYear", Event.class).setParameter("year", year)
				.getResultList();
		List<String> names = new ArrayList<String>();
		for (Event ev : evs) {
			names.add(ev.getName());
			logger.debug(ev.getName() + " " + ev.getYear());
		}
		logger.debug("Found " + names.size() + " events for " + year);
		return names;
	}

	public List<EventTO> listDeletableEvents() {
		List<EventTO> etos = new ArrayList<EventTO>();
		List<Event> evs = entityManager.createNamedQuery("AllEvents", Event.class).getResultList();
		for (Event ev : evs) {
			if (ev.getResults().isEmpty()) {
				etos.add(ev.getTransferObject());
			}
		}
		logger.debug("Found " + etos.size() + " events");
		return etos;
	}

	public void delete(String sessionid, List<Long> todelete) throws AuthException {
		User ui = LoginBean.getUser(entityManager, sessionid);
		if (ui.getPriv() != User.Priv.SUPER && !ui.isScorer()) {
			throw new AuthException("You are not allowed to do this");
		}
		for (Long key : todelete) {
			entityManager.remove(entityManager.find(Event.class, key));
		}
	}

	public EventTO getEventForYear(int year, String name) {
		return entityManager.createNamedQuery("NamedEvent", Event.class).setParameter("year", year)
				.setParameter("name", name).getSingleResult().getTransferObject();
	}

	public List<EventTO> getOpenEvents() {
		List<EventTO> etos = new ArrayList<EventTO>();
		List<Event> evs = entityManager.createNamedQuery("IncompleteEvents", Event.class).getResultList();
		for (Event ev : evs) {
			etos.add(ev.getTransferObject());
		}
		logger.debug("Found " + etos.size() + " events");
		return etos;
	}

	public void update(EventTO eto) {
		Event newData = new Event(eto);
		Event event = entityManager.find(Event.class, newData.getId());
		event.setResults(newData.getResults());
		event.setWinner(newData.getWinner());
	}

	public List<Integer> getYears() {
		List<Date> result1 = entityManager.createNamedQuery("Fixture.Date", Date.class).getResultList();
		List<Integer> result2 = entityManager.createNamedQuery("Event.Year", Integer.class).getResultList();
		logger.debug("Found " + result1.size() + " fixture years and " + result2.size() + " event years");
		Set<Integer> result12 = new HashSet<Integer>();

		for (Date d : result1) {
			result12.add(Integer.parseInt(dateYearFormat.format(d)));
		}
		result12.addAll(result2);
		List<Integer> results = new ArrayList<Integer>(result12);
		Collections.sort(results);
		return results;
	}

	public List<FixtureNameTO> getFixtureNames() {
		List<FixtureNameTO> results = new ArrayList<FixtureNameTO>();
		List<FixtureName> fns = entityManager.createNamedQuery("FixtureName.All", FixtureName.class).getResultList();
		for (FixtureName fn : fns) {
			results.add(fn.getTransferObject());
		}
		return results;
	}

	public void updateFixtureNames(List<FixtureNameTO> fntos) {
		for (FixtureNameTO fnto : fntos) {
			logger.debug(fnto.getKey() + " " + fnto.getName());
			if (fnto.getKey() == null) {
				if (!fnto.getName().isEmpty()) {
					FixtureName fn = new FixtureName(fnto);
					entityManager.persist(fn);
				}
			} else {
				FixtureName fn = entityManager.find(FixtureName.class, fnto.getKey());
				if (!fnto.getName().isEmpty()) {
					fn.setName(fnto.getName());
				} else {
					entityManager.remove(fn);
				}
			}
		}
	}

	public List<FixtureTO> getFixturesForYear(int year) throws BadInputException {
		Date date1;
		Date date2;
		try {
			date1 = dateYearFormat.parse(Integer.toString(year));
			date2 = dateYearFormat.parse(Integer.toString(year + 1));
		} catch (ParseException e) {
			throw new BadInputException(e.getMessage());
		}
		List<Fixture> fixtures = entityManager.createNamedQuery("Fixture.ByYear", Fixture.class)
				.setParameter("date1", date1).setParameter("date2", date2).getResultList();
		List<FixtureTO> ftos = new ArrayList<FixtureTO>();
		for (Fixture fixture : fixtures) {
			ftos.add(fixture.getTransferObject());
		}
		return ftos;
	}

	public void update(Set<FixtureTO> modified) {
		for (FixtureTO fto : modified) {
			logger.debug("Fixture " + fto.getKey() + " for " + fto.getName() + " " + fto.getDate()
					+ " is being updated/created/deleted");

			if (fto.getKey() == null) {
				if (!fto.getName().isEmpty() && !fto.isDelete()) {
					Fixture f = new Fixture(fto);
					entityManager.persist(f);
				}
			} else {
				if (fto.isDelete()) {
					Fixture f = entityManager.find(Fixture.class, fto.getKey());
					entityManager.remove(f);
				} else {
					Fixture f = new Fixture(fto);
					entityManager.merge(f);
				}
			}
		}
	}

	public List<String> getFixtureNamesForYear(int year) throws BadInputException {
		Set<String> names = new HashSet<String>();

		Date date1;
		Date date2;
		try {
			date1 = dateYearFormat.parse(Integer.toString(year));
			date2 = dateYearFormat.parse(Integer.toString(year + 1));
		} catch (ParseException e) {
			throw new BadInputException(e.getMessage());
		}
		names.addAll(entityManager.createNamedQuery("Fixture.NameByYear", String.class).setParameter("date1", date1)
				.setParameter("date2", date2).getResultList());

		logger.debug("Found " + names.size() + " fixtures for " + year);
		List<String> results = new ArrayList<String>(names);
		Collections.sort(results);
		return results;
	}

	public List<FixtureTO> getFixtures(String sessionid, int year, String name)
			throws BadInputException, AuthException {
		User ui = LoginBean.getUserTolerant(entityManager, sessionid);
		List<FixtureTO> ftos = new ArrayList<FixtureTO>();
		Date date1;
		Date date2;
		try {
			date1 = dateYearFormat.parse(Integer.toString(year));
			date2 = dateYearFormat.parse(Integer.toString(year + 1));
		} catch (ParseException e) {
			throw new BadInputException(e.getMessage());
		}
		List<Fixture> fixtures = entityManager.createNamedQuery("Fixture.NamedEvent", Fixture.class)
				.setParameter("date1", date1).setParameter("date2", date2).setParameter("name", name).getResultList();
		for (Fixture fixture : fixtures) {
			FixtureTO fto = fixture.getTransferObject();
			if (ui == null) {
				fto.setPlayers(null);
			}
			ftos.add(fto);
		}
		return ftos;
	}

	public void deleteEntrant(List<EntrantTO> entrants) {
		for (EntrantTO entrantTO : entrants) {
			Long entrantId = entrantTO.getId();
			Entrant entrant = entityManager.find(Entrant.class, entrantId);
			Event ev = entrant.getEvent();
			Long uid = entrant.getUser().getId();

			Iterator<Result> it = ev.getResults().iterator();
			while (it.hasNext()) {
				Result r = it.next();
				logger.debug("Consider result between " + r.getUser1().getName() + " and " + r.getUser2().getName());
				if (r.getUser1().getId() == uid || r.getUser2().getId() == uid) {
					it.remove();
					logger.debug("Deleted result between " + r.getUser1().getName() + " and " + r.getUser2().getName());
				}
			}
			ev.getEntrants().remove(entrant);
			logger.debug("Deleted entrant " + entrant.getUser().getName() + " from " + ev.getName() + " for "
					+ ev.getYear());

			if (ev.getEntrants().size() == 0) {
				entityManager.remove(ev);
				logger.debug("Deleted event " + ev.getName() + " for " + ev.getYear() + " as it has no entrants left");
			}

		}
	}

	public List<Win> getRollOfHonour() {
		List<Win> results = new ArrayList<>();
		for (Event event : entityManager.createNamedQuery("RollOfHonour", Event.class).getResultList()) {
			logger.debug("Found event " + event.getYear() + " " + event.getName() + " "
					+ (event.getWinner() == null ? "NULL!!!" : event.getWinner().getName()));
			results.add(new Win(event.getYear(), event.getName(), event.getWinner().getName()));
		}
		results.add(new Win(2006, "Founder's Cup", "Gerald Mitchell"));
		results.add(new Win(2005, "Founder's Cup", "Steve Fisher"));

		results.add(new Win(1999, "Founder's Cup", "Mike Duck and John Munro"));
		results.add(new Win(1998, "Founder's Cup", "Doug Ironside and Gerald Mitchell"));
		return results;
	}
}
