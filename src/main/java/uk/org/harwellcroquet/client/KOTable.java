package uk.org.harwellcroquet.client;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import uk.org.harwellcroquet.shared.EntrantTO;
import uk.org.harwellcroquet.shared.ResultTO;
import uk.org.harwellcroquet.shared.UserTO;

public class KOTable {

	private final static Logger logger = Logger.getLogger(KOTable.class.getSimpleName());

	public class Game {

		private UserTO user1;
		private UserTO user2;

		public Game(UserTO user1, UserTO user2) {
			this.user1 = user1;
			this.user2 = user2;
		}

		public UserTO getUser2() {
			return this.user2;
		}

		public UserTO getUser1() {
			return user1;
		}

		public String toString() {
			return "Game between " + user1 + " and " + user2;
		}
	}

	public static UserTO toPlay = new UserTO();

	enum Type {
		DRAW, PROCESS
	};

	public class Round {

		private UserTO[] users;

		public Round(int max) {
			users = new UserTO[max];
		}

		public UserTO[] getUsers() {
			return users;
		}

		public String toString() {
			return "Round " + users;
		}
	}

	private ArrayList<Game> games;
	private UserTO winner;
	private ArrayList<Round> rounds;

	public KOTable(Type type, List<EntrantTO> entrants, List<ResultTO> results) {
		int n = 0;
		for (EntrantTO entrant : entrants) {
			if (type == Type.DRAW) {
				n = Math.max(n, entrant.getDrawPos());
			} else {
				n = Math.max(n, entrant.getProcessPos());
			}
		}
		results = new ArrayList<ResultTO>(results);
		int nround = 0;
		while (n != 1) {
			n = (n + 1) / 2;
			nround++;
		}
		int max = 1 << nround;
		rounds = new ArrayList<Round>();
		Round round = new Round(max);
		rounds.add(round);
		for (EntrantTO ento : entrants) {
			if (type == Type.DRAW) {
				round.users[ento.getDrawPos() - 1] = ento.getUserTO();
			} else {
				round.users[ento.getProcessPos() - 1] = ento.getUserTO();
			}
		}
		games = new ArrayList<Game>();
		while (max >= 2) {
			max = max / 2;
			Round oldRound = round;
			round = new Round(max);
			rounds.add(round);
			for (int i = 0; i < max; i++) {
				if (oldRound.users[2 * i] == toPlay || oldRound.users[2 * i + 1] == toPlay) {
					round.users[i] = toPlay;
					logger.fine("In round " + rounds.size() + " position " + i + " to play");
				} else if (oldRound.users[2 * i] == null) {
					round.users[i] = oldRound.users[2 * i + 1];
					logger.fine("In round " + rounds.size() + " position " + i + " bye");
				} else if (oldRound.users[2 * i + 1] == null) {
					round.users[i] = oldRound.users[2 * i];
					logger.fine("In round " + rounds.size() + " position " + i + " bye");
				} else {
					logger.fine("In round " + rounds.size() + " position " + i + " look for result");
					for (ResultTO result : results) {
						logger.fine("Consider result of type: " + result.getType() + " for " + type.name());
						if (result.getType().equals(type.name())) {
							if ((result.getUser1TO().equals(oldRound.users[2 * i]) && result.getUser2TO().equals(
									oldRound.users[2 * i + 1]))
									|| (result.getUser1TO().equals(oldRound.users[2 * i + 1]) && result.getUser2TO()
											.equals(oldRound.users[2 * i]))) {
								round.users[i] = getWinner(result);
								break;
							}
						}
					}
					if (round.users[i] == null) {
						round.users[i] = toPlay;
						games.add(new Game(oldRound.users[2 * i], oldRound.users[2 * i + 1]));
					}
				}
			}
		}
		if (round.users[0] != null && !round.users[0].equals(toPlay)) {
			winner = round.users[0];
		} else {
			winner = null;
		}
	}

	public UserTO getKOWinner() {
		return winner;
	}

	public static UserTO getWinner(ResultTO result) {
		int one = 0;
		int two = 0;
		if (result.getUser1Score1() > result.getUser2Score1()) {
			one++;
		} else {
			two++;
		}
		if (result.getUser1Score2() != null) {
			if (result.getUser1Score2() > result.getUser2Score2()) {
				one++;
			} else {
				two++;
			}
		}
		if (result.getUser1Score3() != null) {
			if (result.getUser1Score3() > result.getUser2Score3()) {
				one++;
			} else {
				two++;
			}
		}
		if (one > two) {
			logger.fine(result.getUser1TO() + " beat " + result.getUser2TO());
			return result.getUser1TO();
		} else {
			logger.fine(result.getUser2TO() + " beat " + result.getUser1TO());
			return result.getUser2TO();
		}
	}

	public List<Game> getGames() {
		return games;
	}

	public ArrayList<Round> getRounds() {
		return rounds;
	}

	@Override
	public String toString() {
		return rounds.toString();
	}

}
