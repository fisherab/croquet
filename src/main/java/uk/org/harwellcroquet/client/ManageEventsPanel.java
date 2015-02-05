package uk.org.harwellcroquet.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

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
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ManageEventsPanel extends Composite {

	private final static LoginServiceAsync loginService = LoginServiceAsync.Util.getInstance();
	private final static EventServiceAsync eventService = EventServiceAsync.Util.getInstance();
	final private static Logger logger = Logger.getLogger(ManageEventsPanel.class.getName());
	private VerticalPanel main;
	private FlexTable g;
	private final static DateTimeFormat yearFormat = DateTimeFormat.getFormat("yyyy");
	private FlexTable entrants;
	private TextBox yearBox;
	private ListBox eventBox;
	private ListBox typeBox;
	private ListBox formatBox;
	protected ListBox usBox;
	private Button apply;
	private Map<String, UserTO> userFromName = new HashMap<String, UserTO>();
	private ChangeHandler ch;
	private FlexTable events;
	private HorizontalPanel bs;
	private Button dapply;
	private FlexTable entrantsToGo;
	private Button applyEntrants;

	public ManageEventsPanel() {
		ch = new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				if (!yearBox.getValue().trim().isEmpty()
						&& !eventBox.getValue(eventBox.getSelectedIndex()).trim().isEmpty()
						&& !typeBox.getValue(typeBox.getSelectedIndex()).isEmpty()
						&& !formatBox.getValue(formatBox.getSelectedIndex()).isEmpty()) {
					g.setWidget(4, 0, new Label("Add entrant"));
					g.setWidget(4, 1, usBox);
				}
			}
		};
		this.main = new VerticalPanel();
		refresh();
		this.initWidget(this.main);
	}

	private void refresh() {
		this.main.clear();

		g = new FlexTable();
		entrants = new FlexTable();

		loginService.getUsers(Cookies.getCookie(Consts.COOKIE), "AllCurrentUsers", new AsyncCallback<List<UserTO>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.toString());
				History.newItem("home");
			}

			@Override
			public void onSuccess(List<UserTO> result) {
				usBox = new ListBox();
				usBox.addItem("");
				for (UserTO u : result) {
					usBox.addItem(u.getName());
					userFromName.put(u.getName(), u);
				}
				usBox.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						int n = entrants.getRowCount();
						if (n == 0) {
							try {
								int year = Integer.parseInt(yearBox.getValue());
								if (year < 2000 || year > 3000) {
									throw new NumberFormatException();
								}
							} catch (NumberFormatException e) {
								g.setWidget(0, 2, new HTML(
										"<p style=\"color:red\">Year must be between 2000 and 2999.</p>"));
								usBox.setItemSelected(0, true);
								return;
							}
							if (g.getRowCount() > 0 && g.getCellCount(0) > 2) {
								g.clearCell(0, 2);
							}
							g.setWidget(5, 0, new Label("Entrants"));
							g.setWidget(5, 1, entrants);
							if (formatBox.getSelectedIndex() == 1) {
								entrants.setWidget(0, 1, new Label("Draw #"));
								entrants.setWidget(0, 2, new Label("Process #"));
							} else if (formatBox.getSelectedIndex() == 2) {
								entrants.setText(0, 0, "");
							}
							g.setText(0, 1, yearBox.getValue());
							g.setText(1, 1, eventBox.getValue(eventBox.getSelectedIndex()));
							g.setText(2, 1, typeBox.getValue(typeBox.getSelectedIndex()));
							g.setText(3, 1, formatBox.getValue(formatBox.getSelectedIndex()));
							addButtons();
							n = 1;

						}
						int index = usBox.getSelectedIndex();
						if (index != 0) {
							String text = usBox.getValue(index);
							entrants.setText(n, 0, text);
							if (formatBox.getSelectedIndex() == 1) {
								TextBox tb = new TextBox();
								tb.setWidth("4em");
								entrants.setWidget(n, 1, tb);
								tb = new TextBox();
								tb.setWidth("4em");
								entrants.setWidget(n, 2, tb);
							}

							else if (formatBox.getSelectedIndex() == 3) {
								ListBox lb = new ListBox();
								lb.addItem("Block 1");
								lb.addItem("Block 2");
								entrants.setWidget(n, 1, lb);
							}
							usBox.removeItem(index);
						}
					}
				});

			}
		});

		this.main.add(new HTML("<h2>Create an event</h2>"));
		this.main
				.add(new HTML(
						"<p>An event once created cannot be changed. It can only be deleted "
								+ "if no results are associated with it. Individual entrants can be removed until such time as an event is complete.</p>"));

		this.main.add(g);
		g.setCellSpacing(5);
		g.setWidget(0, 0, new Label("Year"));
		yearBox = new TextBox();
		yearBox.setValue(yearFormat.format(new Date()));
		yearBox.setWidth("4em");
		g.setWidget(0, 1, yearBox);
		yearBox.addChangeHandler(ch);

		g.setWidget(1, 0, new Label("Name of event"));
		eventBox = new ListBox();
		eventBox.setWidth("20em");
		eventBox.addItem("");
		eventBox.addItem("Founder's Bowl");
		eventBox.addItem("Harwell GC Competition");
		g.setWidget(1, 1, eventBox);
		eventBox.addChangeHandler(ch);

		g.setWidget(2, 0, new Label("Type of game"));
		typeBox = new ListBox();
		typeBox.setWidth("10em");
		typeBox.addItem("");
		typeBox.addItem("Golf", "GOLF");
		typeBox.addItem("Association", "ASSOCIATION");
		g.setWidget(2, 1, typeBox);
		typeBox.addChangeHandler(ch);

		g.setWidget(3, 0, new Label("Event format"));
		formatBox = new ListBox();
		formatBox.setWidth("10em");
		formatBox.addItem("");
		formatBox.addItem("Draw and process");
		formatBox.addItem("All play all");
		formatBox.addItem("Two blocks");
		g.setWidget(3, 1, formatBox);
		formatBox.addChangeHandler(ch);

		bs = new HorizontalPanel();
		main.add(bs);

		this.main.add(new HTML("<h2>Events that may be deleted</h2>"));
		events = new FlexTable();
		this.main.add(events);
		final Map<Integer, EventTO> map = new HashMap<Integer, EventTO>();
		eventService.listDeletableEvents(new AsyncCallback<List<EventTO>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.toString());
			}

			@Override
			public void onSuccess(List<EventTO> etos) {
				int n = 0;
				events.setHTML(n, 0, "<b>Year</b>");
				events.setHTML(n, 1, "<b>Name</b>");
				events.setHTML(n, 2, "<b>Delete</b>");
				n = 1;
				for (EventTO eto : etos) {
					map.put(n, eto);
					events.setText(n, 0, Integer.toString(eto.getYear()));
					events.setText(n, 1, eto.getName());
					events.setWidget(n, 2, new CheckBox());
					n++;
				}

			}

		});

		HorizontalPanel dbs = new HorizontalPanel();
		main.add(dbs);
		dbs.setSpacing(10);
		dapply = new Button("Apply");
		Button discard = new Button("Discard");
		dbs.add(this.dapply);
		dbs.add(discard);
		final List<Long> todelete = new ArrayList<Long>();
		this.dapply.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

				int n = events.getRowCount();
				for (int i = 1; i < n; i++) {
					CheckBox cb = (CheckBox) events.getWidget(i, 2);
					if (cb.getValue()) {
						todelete.add(map.get(i).getKey());
					}
				}

				eventService.delete(Cookies.getCookie(Consts.COOKIE), todelete, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(Void result) {
						refresh();
					}
				});
				logger.fine("Requested deletion of " + todelete.size() + " events");
			}

		});
		discard.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int n = events.getRowCount();
				for (int i = 1; i < n; i++) {
					CheckBox cb = (CheckBox) events.getWidget(i, 2);
					cb.setValue(false);
				}
			}
		});

		// Now for the remove entrant section

		this.main.add(new HTML("<h2>Entrants that may be deleted</h2>"));
		this.main
				.add(new HTML(
						"<p>Individual entrants can be removed until such time as an event is complete. This will remove all record of any games the player may have taken part in.</p>"));

		entrantsToGo = new FlexTable();
		this.main.add(entrantsToGo);
		final Map<Integer, EntrantTO> entrantMap = new HashMap<Integer, EntrantTO>();
		eventService.getOpenEvents(new AsyncCallback<List<EventTO>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.toString());
			}

			@Override
			public void onSuccess(List<EventTO> etos) {
				int n = 0;
				entrantsToGo.setHTML(n, 0, "<b>Year</b>");
				entrantsToGo.setHTML(n, 1, "<b>Name</b>");
				entrantsToGo.setHTML(n, 2, "<b>Entrant</b>");
				entrantsToGo.setHTML(n, 3, "<b>Played</b>");
				entrantsToGo.setHTML(n, 4, "<b>Delete</b>");
				n = 1;
				for (EventTO eto : etos) {

					Map<UserTO, Integer> resultCount = new HashMap<UserTO, Integer>();
					Map<UserTO, EntrantTO> entrantFromUser = new HashMap<UserTO, EntrantTO>();
					for (EntrantTO entrant : eto.getEntrants()) {
						resultCount.put(entrant.getUserTO(), 0);
						entrantFromUser.put(entrant.getUserTO(), entrant);
					}
					for (ResultTO result : eto.getResults()) {
						resultCount.put(result.getUser1TO(), resultCount.get(result.getUser1TO()) + 1);
						resultCount.put(result.getUser2TO(), resultCount.get(result.getUser2TO()) + 1);
					}
					boolean header = false;
					for (Entry<UserTO, Integer> pair : resultCount.entrySet()) {
						if (!header) {
							entrantsToGo.setText(n, 0, Integer.toString(eto.getYear()));
							entrantsToGo.setText(n, 1, eto.getName());
							header = true;
						}
						entrantMap.put(n, entrantFromUser.get(pair.getKey()));
						entrantsToGo.setText(n, 2, pair.getKey().getName());
						entrantsToGo.setText(n, 3, "" + pair.getValue());
						entrantsToGo.setWidget(n, 4, new CheckBox());
						n++;
					}
				}
			}

		});

		HorizontalPanel debs = new HorizontalPanel();
		main.add(debs);
		debs.setSpacing(10);
		applyEntrants = new Button("Apply");
		Button discardEntrants = new Button("Discard");
		debs.add(applyEntrants);
		debs.add(discardEntrants);

		this.applyEntrants.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final List<EntrantTO> entrantsToDelete = new ArrayList<EntrantTO>();
				int n = entrantsToGo.getRowCount();
				for (int i = 1; i < n; i++) {
					CheckBox cb = (CheckBox) entrantsToGo.getWidget(i, 4);
					if (cb.getValue()) {
						EntrantTO entrant = entrantMap.get(i);
						entrantsToDelete.add(entrant);
					}
				}
				if (Window.confirm("Are you sure you want to delete " + entrantsToDelete.size() + " entrants")) {

					eventService.deleteEntrant(entrantsToDelete, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							Window.alert(caught.getMessage());
						}

						@Override
						public void onSuccess(Void result) {
							refresh();
						}
					});
					logger.fine("Requested deletion of " + entrantsToDelete.size() + " entrants");
				}
			}

		});
		discardEntrants.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int n = entrantsToGo.getRowCount();
				for (int i = 1; i < n; i++) {
					CheckBox cb = (CheckBox) entrantsToGo.getWidget(i, 4);
					cb.setValue(false);
				}
			}
		});

	}

	void addButtons() {
		bs.setSpacing(10);
		apply = new Button("Apply");
		Button discard = new Button("Discard");
		bs.add(this.apply);
		bs.add(discard);
		this.apply.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int n = entrants.getRowCount();
				EventTO eto = new EventTO(null, Integer.parseInt(yearBox.getValue().trim()), eventBox.getValue(eventBox
						.getSelectedIndex()), typeBox.getValue(typeBox.getSelectedIndex()).replace(" ", "")
						.toUpperCase(),
						formatBox.getValue(formatBox.getSelectedIndex()).replace(" ", "").toUpperCase(), null);
				boolean good = true;
				for (int i = 1; i < n; i++) {
					if (good) {
						EntrantTO entrantTO = new EntrantTO();
						entrantTO.setUserTO(userFromName.get(entrants.getText(i, 0)));
						if (formatBox.getSelectedIndex() == 1) {
							try {
								int draw = Integer.parseInt(((TextBox) entrants.getWidget(i, 1)).getValue());
								int process = Integer.parseInt(((TextBox) entrants.getWidget(i, 2)).getValue());
								if (draw <= 0 || process <= 0) {
									good = false;
									Window.alert("Draw and process position must be integers greater than zero");
								}
								entrantTO.setDrawPos(draw);
								entrantTO.setProcessPos(process);
							} catch (NumberFormatException e) {
								good = false;
								Window.alert("Draw and process position must be integers greater than zero");
							}
						}
						if (formatBox.getSelectedIndex() == 3) {
							int block = ((ListBox) entrants.getWidget(i, 1)).getSelectedIndex() + 1;
							entrantTO.setBlock(block);
						}
						eto.getEntrants().add(entrantTO);
					}
				}

				if (good) {
					eventService.add(Cookies.getCookie(Consts.COOKIE), eto, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							Window.alert(caught.getMessage());
						}

						@Override
						public void onSuccess(Void result) {
							refresh();
						}
					});
					logger.fine("Requested new " + eventBox.getValue(eventBox.getSelectedIndex()) + " with " + (n - 1)
							+ " entrants");
					for (EntrantTO e : eto.getEntrants()) {
						logger.fine("Add " + e.getUserTO().getName());
					}
				}
			}
		});
		discard.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				refresh();
			}
		});

	}

}
