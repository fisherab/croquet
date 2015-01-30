package uk.org.harwellcroquet.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.org.harwellcroquet.client.KOTable.Round;
import uk.org.harwellcroquet.client.service.EventServiceAsync;
import uk.org.harwellcroquet.client.service.LoginServiceAsync;
import uk.org.harwellcroquet.shared.Consts;
import uk.org.harwellcroquet.shared.EntrantTO;
import uk.org.harwellcroquet.shared.EventTO;
import uk.org.harwellcroquet.shared.ResultTO;
import uk.org.harwellcroquet.shared.UserTO;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DisplayEventPanel extends Composite {

	public class EntrantStatus {
		public int win;
		public int hoopBal;
	}

	private VerticalPanel main;
	private int year;
	private String name;
	private final static EventServiceAsync eventService = EventServiceAsync.Util.getInstance();
	private final static LoginServiceAsync loginService = LoginServiceAsync.Util.getInstance();
	private final static HTML wait = new HTML("<h2>Waiting for current information ...</h2>");

	public DisplayEventPanel(int year, String name) {
		this.main = new VerticalPanel();
		this.year = year;
		this.name = name;
		this.main.add(wait);

		loginService.getUsers(Cookies.getCookie(Consts.COOKIE), "AllUsers",
				new AsyncCallback<List<UserTO>>() {

					@Override
					public void onFailure(Throwable caught) {
						main.remove(wait);
						Window.alert(caught.toString());
						History.newItem("home");
					}

					@Override
					public void onSuccess(List<UserTO> result) {
						main.remove(wait);
						eventService.getEventForYear(DisplayEventPanel.this.year,
								DisplayEventPanel.this.name, new AsyncCallback<EventTO>() {
									@Override
									public void onFailure(Throwable caught) {
										Window.alert("Failure " + caught);
									}

									@Override
									public void onSuccess(EventTO eto) {
										displayData(eto);

									}
								});
					}
				});

		this.initWidget(this.main);

	}

	private void displayData(EventTO eto) {
		main.add(new HTML("<h2>Results for " + year + " " + name + " "
				+ eto.getType().toLowerCase() + " event</h2>"));

		if (eto.getFormat().equals("DRAWANDPROCESS")) {
			displayDP("Draw", eto);
			displayDP("Process", eto);
			for (ResultTO r : eto.getResults()) {
				if ("FINAL".equals(r.getType())) {
					UserTO winner = KOTable.getWinner(r);
					UserTO loser = null;
					if (r.getUser1TO().equals(winner)) {
						loser = r.getUser2TO();
					} else {
						loser = r.getUser1TO();
					}
					main.add(new HTML("<h3>" + winner.getName() + " beat " + loser.getName()
							+ " in the final.</h3>"));
				}
			}
		} else if (eto.getFormat().equals("ALLPLAYALL")) {
			displayAPA(eto, null);
		} else {
			displayAPA(eto, 1);
			displayAPA(eto, 2);
		}
	}

	private void displayAPA(EventTO eto, Integer block) {
		if (block != null) {
			main.add(new HTML("<h3>Block " + block + "</h3>"));
		}

		FlexTable ft = new FlexTable();
		ft.setBorderWidth(1);
		ft.setCellPadding(5);
		main.add(ft);

		Map<UserTO, EntrantStatus> entrantStatuses = new HashMap<UserTO, EntrantStatus>();
		Map<UserTO, Integer> user2offset = new HashMap<UserTO, Integer>();
		int n = 1;
		for (EntrantTO ento : eto.getEntrants()) {
			if (block == null || block == ento.getBlock()) {
				UserTO user = ento.getUserTO();
				user2offset.put(user, n);
				entrantStatuses.put(user, new EntrantStatus());
				String uname = user.getName();
				ft.setText(n, 0, uname);
				String[] names = uname.split("\\s");
				StringBuilder sb = new StringBuilder();
				for (String name : names) {
					sb.append(name.charAt(0));
				}
				ft.setText(0, n, sb.toString());
				n++;
			}
		}
		for (int i = 1; i < n; i++) {
			for (int j = 1; j < n; j++) {
				ft.setWidget(i, j, new HTML("&nbsp;"));
			}
		}
		ft.setText(0, n, "Hoop Balance");
		ft.setText(0, n + 1, "Wins");
		for (ResultTO rto : eto.getResults()) {
			UserTO user1 = rto.getUser1TO();
			if (user2offset.get(user1) != null) {

				UserTO user2 = rto.getUser2TO();
				int one = 0;
				int two = 0;
				int hoopBal = 0;

				if (rto.getUser1Score1() > rto.getUser2Score1()) {
					one++;
				} else {
					two++;
				}
				hoopBal += (rto.getUser1Score1() - rto.getUser2Score1());

				if (rto.getUser1Score2() != null) {
					if (rto.getUser1Score2() > rto.getUser2Score2()) {
						one++;
					} else {
						two++;
					}
					hoopBal += (rto.getUser1Score2() - rto.getUser2Score2());
				}

				if (rto.getUser1Score3() != null) {
					if (rto.getUser1Score3() > rto.getUser2Score3()) {
						one++;
					} else {
						two++;
					}
					hoopBal += (rto.getUser1Score3() - rto.getUser2Score3());
				}

				EntrantStatus s1 = entrantStatuses.get(user1);
				EntrantStatus s2 = entrantStatuses.get(user2);
				if (one > two) {
					ft.setText(user2offset.get(user1), user2offset.get(user2), "W " + hoopBal);
					ft.setText(user2offset.get(user2), user2offset.get(user1), "L " + -hoopBal);
					s1.win++;
				} else {
					ft.setText(user2offset.get(user1), user2offset.get(user2), "L " + hoopBal);
					ft.setText(user2offset.get(user2), user2offset.get(user1), "W " + -hoopBal);
					s2.win++;
				}
				s1.hoopBal += hoopBal;
				s2.hoopBal -= hoopBal;
			}
		}

		for (EntrantTO ento : eto.getEntrants()) {
			if (block == null || block == ento.getBlock()) {
				UserTO user = ento.getUserTO();
				Integer offset = user2offset.get(user);
				EntrantStatus s = entrantStatuses.get(user);
				ft.setText(offset, n, Integer.toString(s.hoopBal));
				ft.setText(offset, n + 1, Integer.toString(s.win));
			}
		}
	}

	private void displayDP(String title, EventTO eto) {
		main.add(new HTML("<h3>" + title + "</h3>"));
		FlexTable ft = new FlexTable();
		ft.setBorderWidth(1);
		ft.setCellPadding(5);
		main.add(ft);
		KOTable kot = null;
		if (title.equals("Draw")) {
			kot = new KOTable(KOTable.Type.DRAW, eto.getEntrants(), eto.getResults());
		} else {
			kot = new KOTable(KOTable.Type.PROCESS, eto.getEntrants(), eto.getResults());
		}
		ArrayList<Round> rounds = kot.getRounds();
		int col = 0;
		int n = 1;
		for (Round round : rounds) {
			for (int row = 0; row < round.getUsers().length; row++) {
				UserTO s = round.getUsers()[row];
				if (s != null && s != KOTable.toPlay) {
					ft.setText(row * n, col, round.getUsers()[row].getName());
				} else {
					ft.setWidget(row * n, col, new HTML("&nbsp;"));
				}
				if (n != 1) {
					ft.getFlexCellFormatter().setRowSpan(row * n, col, n);
				}
			}
			n = n * 2;
			col++;
		}
	}
}
