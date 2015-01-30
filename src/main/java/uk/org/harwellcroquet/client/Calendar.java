package uk.org.harwellcroquet.client;

import java.util.Date;
import java.util.List;

import uk.org.harwellcroquet.client.service.EventServiceAsync;
import uk.org.harwellcroquet.shared.FixtureTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class Calendar extends Composite {

	private static CalendarUiBinder uiBinder = GWT.create(CalendarUiBinder.class);
	private final static DateTimeFormat yearFormat = DateTimeFormat.getFormat("yyyy");
	private final static DateTimeFormat monthFormat = DateTimeFormat.getFormat("MM");
	private final static DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd MMM yyyy");
	private final static DateTimeFormat timeFormat = DateTimeFormat.getFormat("HH:mm");
	private String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep",
			"Oct", "Nov", "Dec" };
	private final static EventServiceAsync eventService = EventServiceAsync.Util.getInstance();

	interface CalendarUiBinder extends UiBinder<Widget, Calendar> {
	}

	private int year;
	private int month;
	private int yearLoaded;
	private List<FixtureTO> fixtures;

	public Calendar() {
		initWidget(uiBinder.createAndBindUi(this));
		table.setHTML(0, 0, "<b>Date</b>");
		table.setHTML(0, 1, "<b>Time</b>");
		table.setHTML(0, 2, "<b>Name</b>");
		table.setHTML(0, 3, "<b>Opponents</b>");
		table.setHTML(0, 4, "<b>Location</b>");
		table.setHTML(0, 5, "<b>Result</b>");
		year = Integer.parseInt(yearFormat.format(new Date()));
		month = Integer.parseInt(monthFormat.format(new Date()));
		getAndDisplayFixtures(year, month);
	}

	private void displayFixtures(List<FixtureTO> fixtures, int year, int month) {
		label.setText(months[month - 1] + " to  " + months[month] + " " + year);
		table.clear();
		int i = 1;
		for (FixtureTO fixture : fixtures) {
			Date d = fixture.getDate();
			int fmon = Integer.parseInt(monthFormat.format(d));
			if (fmon == month || fmon == month + 1) {
				table.setWidget(i, 0, new HTML(dateFormat.format(d)));
				table.setWidget(i, 1, new HTML(timeFormat.format(d)));
				table.setWidget(i, 2, new HTML(fixture.getName()));
				table.setWidget(i, 3, new HTML(fixture.getOpponents()));
				table.setWidget(i, 4, new HTML(fixture.getLocation()));
				table.setWidget(i, 5, new HTML(fixture.getResult()));
				i++;
			}
		}
	}

	private void getAndDisplayFixtures(final int year, final int month) {
		if (yearLoaded != year) {
			eventService.getFixturesForYear(year, new AsyncCallback<List<FixtureTO>>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Failure " + caught);
				}

				@Override
				public void onSuccess(List<FixtureTO> fixtures) {
					Calendar.this.fixtures = fixtures;
					displayFixtures(Calendar.this.fixtures, year, month);
				}
			});
		} else {
			displayFixtures(Calendar.this.fixtures, year, month);
		}

	}

	@UiField
	Label label;

	@UiField
	FlexTable table;

	@UiField
	Button fwdButton;

	@UiField
	Button backButton;

	@UiHandler("fwdButton")
	void fwdButton(ClickEvent e) {
		month++;
		if (month == 13) {
			month = 1;
			year++;
		}
		getAndDisplayFixtures(year, month);
	}

	@UiHandler("backButton")
	void backButton(ClickEvent e) {
		month--;
		if (month == 0) {
			month = 12;
			year--;
		}
		getAndDisplayFixtures(year, month);
	}

}
