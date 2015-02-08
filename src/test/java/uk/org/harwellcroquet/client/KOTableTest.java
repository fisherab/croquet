package uk.org.harwellcroquet.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import uk.org.harwellcroquet.shared.EntrantTO;
import uk.org.harwellcroquet.shared.EventTO;
import uk.org.harwellcroquet.shared.ResultTO;
import uk.org.harwellcroquet.shared.UserTO;

public class KOTableTest {

	@Test
	public void test() {
		UserTO u1 = new UserTO(1L, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		UserTO u2 = new UserTO(2L, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		UserTO u3 = new UserTO(3L, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		UserTO u4 = new UserTO(4L, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		UserTO u5 = new UserTO(5L, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

		List<EntrantTO> entrants = new ArrayList<EntrantTO>();
		EventTO event = new EventTO();

		entrants.add(new EntrantTO(0L, u1, 1, 7, null));
		entrants.add(new EntrantTO(1L, u2, 2, 6, null));
		entrants.add(new EntrantTO(2L, u3, 3, 4, null));
		entrants.add(new EntrantTO(3L, u4, 4, 3, null));
		entrants.add(new EntrantTO(4L, u5, 5, 1, null));

		event.setEntrants(entrants);

		List<ResultTO> results = new ArrayList<ResultTO>();
		KOTable kot = new KOTable(KOTable.Type.DRAW, entrants, results);
		assertEquals(2, kot.getGames().size());
		assertEquals(u1, kot.getGames().get(0).getUser1());
		assertEquals(u2, kot.getGames().get(0).getUser2());
		assertEquals(u3, kot.getGames().get(1).getUser1());
		assertEquals(u4, kot.getGames().get(1).getUser2());
		assertNull(kot.getKOWinner());

		results.add(new ResultTO(0L, "DRAW", u1, 7, null, null, u2, 5, null, null, u1, new Date()));
		kot = new KOTable(KOTable.Type.DRAW, entrants, results);
		assertEquals(1, kot.getGames().size());
		assertEquals(u3, kot.getGames().get(0).getUser1());
		assertEquals(u4, kot.getGames().get(0).getUser2());
		assertNull(kot.getKOWinner());

		results.add(new ResultTO(1L, "DRAW", u3, 7, null, null, u4, 5, null, null, u1, new Date()));
		kot = new KOTable(KOTable.Type.DRAW, entrants, results);
		assertEquals(1, kot.getGames().size());
		assertEquals(u1, kot.getGames().get(0).getUser1());
		assertEquals(u3, kot.getGames().get(0).getUser2());
		assertNull(kot.getKOWinner());

		results.add(new ResultTO(2L, "DRAW", u3, 7, null, null, u1, 5, null, null, u1, new Date()));
		kot = new KOTable(KOTable.Type.DRAW, entrants, results);
		assertEquals(1, kot.getGames().size());
		assertEquals(u3, kot.getGames().get(0).getUser1());
		assertEquals(u5, kot.getGames().get(0).getUser2());
		assertNull(kot.getKOWinner());

		results.add(new ResultTO(3L, "DRAW", u3, 7, null, null, u5, 5, null, null, u1, new Date()));
		kot = new KOTable(KOTable.Type.DRAW, entrants, results);
		assertEquals(0, kot.getGames().size());
		assertEquals(u3, kot.getKOWinner());
	}

}
