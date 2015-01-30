package uk.org.harwellcroquet.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import uk.org.harwellcroquet.client.KOTable.Game;
import uk.org.harwellcroquet.client.service.EventServiceAsync;
import uk.org.harwellcroquet.client.service.LoginServiceAsync;
import uk.org.harwellcroquet.shared.Consts;
import uk.org.harwellcroquet.shared.EntrantTO;
import uk.org.harwellcroquet.shared.EventTO;
import uk.org.harwellcroquet.shared.ResultTO;
import uk.org.harwellcroquet.shared.UserTO;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RecordResultPanel extends Composite {

	final private static Logger logger = Logger.getLogger(RecordResultPanel.class.getName());

	private VerticalPanel main;
	private final static EventServiceAsync eventService = EventServiceAsync.Util.getInstance();
	private final static LoginServiceAsync loginService = LoginServiceAsync.Util.getInstance();

	private Map<String, UserTO> userFromUsername = new HashMap<String, UserTO>();
	private UserTO as;
	private UserTO me;
	private VerticalPanel aResult;
	private boolean first;
	private boolean scorer;
	private VerticalPanel wrapper;
	private final static HTML wait = new HTML("<h2>Waiting for current information ...</h2>");

	public RecordResultPanel(UserTO uto) {
		this.wrapper = new VerticalPanel();

		if (uto == null) {
			History.newItem("home");
		} else {
			this.as = this.me = uto;
			this.scorer = uto.getPriv().equals("SUPER") || uto.isScorer();
			final ListBox lb = new ListBox();
			if (scorer) {
				wrapper.add(new HTML("<h2>As a scorer you may act as someone else</h2>"));
				wrapper.add(lb);
				lb.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						String uName = lb.getItemText(lb.getSelectedIndex());
						as = userFromUsername.get(uName);
						setup();
					}
				});
			}
			wrapper.add(wait);
			loginService.getUsers(Cookies.getCookie(Consts.COOKIE), "AllUsers",
					new AsyncCallback<List<UserTO>>() {
						@Override
						public void onFailure(Throwable caught) {
							wrapper.remove(wait);
							Window.alert(caught.toString());
							History.newItem("home");
						}

						@Override
						public void onSuccess(List<UserTO> result) {
							wrapper.remove(wait);
							for (UserTO u : result) {
								userFromUsername.put(u.getName(), u);
								if (scorer) {
									lb.addItem(u.getName());
									if (u.equals(me)) {
										lb.setSelectedIndex(lb.getItemCount() - 1);
									}
								}
							}
						}
					});

			this.main = new VerticalPanel();
			this.wrapper.add(this.main);
			setup();

		}

		this.initWidget(this.wrapper);

	}

	private void setup() {
		main.clear();
		main.add(wait);
		eventService.getOpenEvents(new AsyncCallback<List<EventTO>>() {
			@Override
			public void onFailure(Throwable caught) {
				main.remove(wait);
				Window.alert("Failure " + caught);
			}

			@Override
			public void onSuccess(List<EventTO> etos) {
				main.remove(wait);
				analyse(etos);

			}
		});
	}

	private void analyse(List<EventTO> etos) {

		try {
			main.add(new HTML("<h2>Record Results</h2>"));
			first = true;
			for (EventTO eto : etos) {
				List<EntrantTO> entrants = eto.getEntrants();
				if (eto.getFormat().equals("DRAWANDPROCESS")) {
					logger.fine("analyse DRAWANDPROCESS " + eto.getName() + " for " + as);
					KOTable dkot = new KOTable(KOTable.Type.DRAW, entrants, eto.getResults());
					for (Game g : dkot.getGames()) {
						logger.fine(g.getUser1() + " vs. " + g.getUser2() + "In DRAW");
						if (g.getUser1().equals(as)) {
							display(eto, g.getUser2(), "Draw");
						} else if (g.getUser2().equals(as)) {
							display(eto, g.getUser1(), "Draw");
						}
					}
					KOTable pkot = new KOTable(KOTable.Type.PROCESS, entrants, eto.getResults());
					for (Game g : pkot.getGames()) {
						logger.fine(g.getUser1() + " vs. " + g.getUser2() + "In PROCESS");
						if (g.getUser1().equals(as)) {
							display(eto, g.getUser2(), "Process");
						} else if (g.getUser2().equals(as)) {
							display(eto, g.getUser1(), "Process");
						}
					}
					if (as.equals(dkot.getKOWinner()) && !as.equals(pkot.getKOWinner())
							&& pkot.getKOWinner() != null) {
						display(eto, pkot.getKOWinner(), "Final");
					} else if (as.equals(pkot.getKOWinner()) && !as.equals(dkot.getKOWinner())
							&& dkot.getKOWinner() != null) {
						display(eto, dkot.getKOWinner(), "Final");
					}
				} else if (eto.getFormat().equals("ALLPLAYALL")) {
					logger.fine("analyse ALLPLAYALL " + eto.getName());
					List<UserTO> potential = new ArrayList<UserTO>();
					for (EntrantTO ento : entrants) {
						potential.add(ento.getUserTO());
						logger.fine(ento.getUserTO().toString());
					}
					logger.fine(as.toString());
					if (potential.remove(as)) {
						logger.fine("ooooo " + eto.getName());
						for (ResultTO rto : eto.getResults()) {
							if (rto.getUser1TO().equals(as)) {
								potential.remove(rto.getUser2TO());
							} else if (rto.getUser2TO().equals(as)) {
								potential.remove(rto.getUser1TO());
							}
						}
						for (UserTO p : potential) {
							display(eto, p, "NA");
						}
					}
				} else if (eto.getFormat().equals("TWOBLOCKS")) {
					logger.fine("Analyse TWOBLOCKS " + eto.getName());

					int block = 0;
					for (EntrantTO ento : entrants) {
						if (ento.getUserTO().getId().longValue() == as.getId().longValue()) {
							block = ento.getBlock();
							break;
						}
					}
					if (block != 0) {
						logger.fine("You (" + as + ") are in block " + block);
						List<UserTO> potential = new ArrayList<UserTO>();
						for (EntrantTO ento : entrants) {
							if (ento.getBlock() == block) {
								potential.add(ento.getUserTO());
								logger.fine("Player: " + ento.getUserTO().toString() + " in block "
										+ block);
							}
						}
						if (potential.remove(as)) {
							logger.fine("Removed " + as + " as you can't play yourself");
							logger.fine("There are " + eto.getResults().size() + " results");
							for (ResultTO rto : eto.getResults()) {
								if (rto.getUser1TO().equals(as)) {
									potential.remove(rto.getUser2TO());
									logger.fine("Already played " + rto.getUser2TO());
								} else if (rto.getUser2TO().equals(as)) {
									potential.remove(rto.getUser1TO());
									logger.fine("Already played " + rto.getUser1TO());
								}
							}
						}
						for (UserTO p : potential) {
							display(eto, p, "NA");
						}
					}
				} else {
					logger.severe("Unexpected value for event formt");
				}
			}
			if (first) {
				main.add(new HTML("<p>You have no games to play at the moment.</p>"));
			}
		} catch (Throwable t) {
			t.printStackTrace();
			logger.fine("A " + t + " was thrown");
		}
	}

	private void display(final EventTO eto, final UserTO userTO, final String dp) {
		if (first) {
			main.add(new HTML(
					"<p>Please click on the event below to allow the score to be recorded.</p>"));
			first = false;
		}

		String dpp = null;
		if (dp.equals("NA")) {
			dpp = "";
		} else {
			dpp = "in the " + dp;
		}
		HTML game = new HTML(eto.getYear() + " " + eto.getName() + " " + dpp + " against "
				+ userTO.getName());
		game.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				edit(eto, userTO, dp);
			}
		});
		main.add(game);
	}

	private void edit(final EventTO eto, final UserTO userTO, final String dp) {
		if (aResult != null) {
			main.remove(aResult);
		}
		aResult = new VerticalPanel();
		main.add(aResult);
		aResult.add(new HTML("<h2>Record one match</h2>"));
		final boolean golf = eto.getType().equals("GOLF");
		if (golf) {
			aResult.add(new HTML(
					"<p>If there were onlg two games played then leave the last one blank"));
		}
		String dpp = null;
		if (dp.equals("NA")) {
			dpp = "";
		} else {
			dpp = "in the " + dp;
		}
		aResult.add(new HTML(eto.getYear() + " " + eto.getName() + " " + dpp));
		final FlexTable g = new FlexTable();
		aResult.add(g);
		g.setText(0, 0, as.getName());
		g.setText(1, 0, userTO.getName());
		g.setWidget(0, 1, new TextBox());
		g.getWidget(0, 1).setWidth("3em");
		g.setWidget(1, 1, new TextBox());
		g.getWidget(1, 1).setWidth("3em");
		if (golf) {
			g.setWidget(0, 2, new TextBox());
			g.getWidget(0, 2).setWidth("3em");
			g.setWidget(1, 2, new TextBox());
			g.getWidget(1, 2).setWidth("3em");
			g.setWidget(0, 3, new TextBox());
			g.getWidget(0, 3).setWidth("3em");
			g.setWidget(1, 3, new TextBox());
			g.getWidget(1, 3).setWidth("3em");
		}
		Button apply = new Button("Apply");
		aResult.add(apply);
		apply.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				int one = 0;
				int two = 0;
				try {
					if (getScore(0, 1) > getScore(1, 1)) {
						one++;
					} else if (getScore(0, 1) < getScore(1, 1)) {
						two++;
					} else {
						Window.alert("Games cannot be drawn");
						return;
					}
					if (golf) {
						if (getScore(0, 2) > getScore(1, 2)) {
							one++;
						} else if (getScore(0, 2) < getScore(1, 2)) {
							two++;
						} else {
							Window.alert("Games cannot be drawn");
							return;
						}
						if (!(((TextBox) g.getWidget(0, 3)).getValue()).trim().isEmpty()) {
							if (getScore(0, 3) > getScore(1, 3)) {
								one++;
							} else if (getScore(0, 3) < getScore(1, 3)) {
								two++;
							} else {
								Window.alert("Games cannot be drawn");
								return;
							}
						}
					}
					if (one == 3 || two == 3) {
						Window.alert("As someone has won two games there should not be a third recorded");
						return;
					}
					boolean record = false;
					if (one > two) {
						record = Window.confirm("Please confirm that you won");
					} else if (one < two) {
						record = Window.confirm("Please confirm that you lost");
					} else {
						Window.alert("You cannot record one game each");
					}
					if (record) {
						Integer user1Score2 = null;
						Integer user2Score2 = null;
						Integer user1Score3 = null;
						Integer user2Score3 = null;
						if (golf) {
							user1Score2 = getScore(0, 2);
							user2Score2 = getScore(1, 2);
							if (!(((TextBox) g.getWidget(0, 3)).getValue()).trim().isEmpty()) {
								user1Score3 = getScore(0, 3);
								user2Score3 = getScore(1, 3);
							}
						}

						String dpU = dp.toUpperCase();
						eto.getResults().add(
								new ResultTO(null, dpU, as, getScore(0, 1), user1Score2,
										user1Score3, userTO, getScore(1, 1), user2Score2,
										user2Score3, me, new Date()));

						if (dpU.equals("FINAL")) {
							eto.setComplete(true);
						} else if (dpU.equals("NA")) {
							int nentrants = eto.getEntrants().size();
							int gamesNeeded = (nentrants * (nentrants - 1)) / 2;
							if (eto.getResults().size() == gamesNeeded) {
								eto.setComplete(true);
							}
						}

						eventService.update(eto, new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Failure " + caught);
							}

							@Override
							public void onSuccess(Void result) {
								setup();

							}
						});
					}
				} catch (NumberFormatException e) {
					Window.alert("Boxes must contain integers only - a blank is only permissible if a third game was not required");
				}
			}

			private int getScore(int i, int j) {
				return Integer.parseInt(((TextBox) g.getWidget(i, j)).getValue());
			}
		});
	}
}
