package uk.org.harwellcroquet.client;

import java.util.List;

import uk.org.harwellcroquet.client.service.EventServiceAsync;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DisplayEventsPanel extends Composite {

	private final static EventServiceAsync eventService = EventServiceAsync.Util.getInstance();
	private final static HTML wait = new HTML("<h2>Waiting for current information ...</h2>");
	private VerticalPanel main;
	private int year;

	public DisplayEventsPanel(final int year) {
		this.main = new VerticalPanel();
		this.year = year;

		this.main.add(DisplayEventsPanel.wait);
		DisplayEventsPanel.eventService.eventsForYear(year, new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				DisplayEventsPanel.this.main.remove(DisplayEventsPanel.wait);
				Window.alert("Failure " + caught);
			}

			@Override
			public void onSuccess(final List<String> enames) {

				DisplayEventsPanel.eventService.getYears(new AsyncCallback<List<Integer>>() {

					@Override
					public void onFailure(Throwable caught) {
						DisplayEventsPanel.this.main.remove(DisplayEventsPanel.wait);
						Window.alert("Failure " + caught);
					}

					@Override
					public void onSuccess(final List<Integer> years) {
						DisplayEventsPanel.eventService.getFixtureNamesForYear(year, new AsyncCallback<List<String>>() {

							@Override
							public void onFailure(Throwable caught) {
								DisplayEventsPanel.this.main.remove(DisplayEventsPanel.wait);
								Window.alert("Failure " + caught);
							}

							@Override
							public void onSuccess(List<String> fnames) {
								DisplayEventsPanel.this.main.remove(DisplayEventsPanel.wait);
								DisplayEventsPanel.this.displayData(enames, fnames, years);
							}
						});

					}
				});
			}

		});

		this.initWidget(this.main);
	}

	private void displayData(List<String> enames, List<String> fnames, List<Integer> years) {
		main.add(new HTML("<h2>Events for " + year + "</h2>"));
		main.add(new HTML("<p>Click on the event you want to see</p>"));
		for (String name : enames) {
			this.main.add(new InlineHyperlink(name, "event/" + this.year + "/" + name));
		}
		main.add(new HTML("<h2>Fixtures for " + year + "</h2>"));
		main.add(new HTML("<p>Click on the fixture type you want to see</p>"));
		for (String name : fnames) {
			this.main.add(new InlineHyperlink(name, "fixture/" + this.year + "/" + name));
		}
		main.add(new HTML("<h2>Change year</h2>"));
		main.add(new HTML("<p>Click on the year you want to see</p>"));
		for (int year : years) {
			String yearS = Integer.toString(year);
			this.main.add(new InlineHyperlink(yearS, "events/" + yearS));
		}

	}

}
