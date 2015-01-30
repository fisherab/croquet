package uk.org.harwellcroquet.client;

import java.util.List;

import uk.org.harwellcroquet.client.service.EventServiceAsync;
import uk.org.harwellcroquet.shared.Consts;
import uk.org.harwellcroquet.shared.FixtureTO;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DisplayFixturePanel extends Composite {

	public class EntrantStatus {
		public int win;
		public int hoopBal;
	}

	private VerticalPanel main;
	private int year;
	private String name;
	private final static EventServiceAsync eventService = EventServiceAsync.Util.getInstance();
	private final static HTML wait = new HTML("<h2>Waiting for current information ...</h2>");
	private final static DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd MMM yyyy");
	private final static DateTimeFormat timeFormat = DateTimeFormat.getFormat("HH:mm");

	public DisplayFixturePanel(int year, String name) {
		this.main = new VerticalPanel();
		this.year = year;
		this.name = name;
		this.main.add(wait);

		eventService.getFixtures(Cookies.getCookie(Consts.COOKIE), DisplayFixturePanel.this.year,
				DisplayFixturePanel.this.name, new AsyncCallback<List<FixtureTO>>() {
					@Override
					public void onFailure(Throwable caught) {
						main.remove(wait);
						Window.alert("Failure " + caught);
					}

					@Override
					public void onSuccess(List<FixtureTO> ftos) {
						main.remove(wait);
						displayData(ftos);
					}
				});

		this.initWidget(this.main);

	}

	private void displayData(List<FixtureTO> ftos) {
		main.add(new HTML("<h2>" + name + " fixtures for " + year + "</h2>"));

		FlexTable ft = new FlexTable();
		ft.setBorderWidth(1);
		ft.setCellPadding(5);
		main.add(ft);

		ft.setWidget(0, 0, new HTML("<b>Date</b>"));
		ft.setWidget(0, 1, new HTML("<b>Time</b>"));
		ft.setWidget(0, 2, new HTML("<b>Opponents</b>"));
		ft.setWidget(0, 3, new HTML("<b>Location</b>"));
		ft.setWidget(0, 4, new HTML("<b>Players</b>"));
		ft.setWidget(0, 5, new HTML("<b>Results</b>"));

		int n = 1;
		for (FixtureTO fto : ftos) {
			ft.setText(n, 0, dateFormat.format(fto.getDate()));
			ft.setText(n, 1, timeFormat.format(fto.getDate()));
			ft.setText(n, 2, fto.getOpponents());
			ft.setText(n, 3, fto.getLocation());
			ft.setText(n, 4, fto.getPlayers());
			ft.setText(n, 5, fto.getResult());

			n++;

		}

	}

}
