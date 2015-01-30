package uk.org.harwellcroquet.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import uk.org.harwellcroquet.client.event.HomeChangedEvent;
import uk.org.harwellcroquet.client.service.EventServiceAsync;
import uk.org.harwellcroquet.shared.FixtureNameTO;
import uk.org.harwellcroquet.shared.FixtureTO;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.DatePickerCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;

public class RecordFixturesPanel extends Composite {

	private static DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd MM yyyy");

	private static DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("dd MM yyyy HH:mm");
	private final static EventServiceAsync eventService = EventServiceAsync.Util.getInstance();
	final private static Logger logger = Logger.getLogger(RecordFixturesPanel.class.getName());

	private static DateTimeFormat timeFormat = DateTimeFormat.getFormat("HH:mm");

	private final static HTML wait = new HTML("<h2>Waiting for current information ...</h2>");

	private final static DateTimeFormat yearFormat = DateTimeFormat.getFormat("yyyy");
	private Button apply;

	protected HorizontalPanel bs;

	private ListDataProvider<FixtureTO> dataProvider;
	private List<String> fixtureNames = new ArrayList<String>();

	protected HandlerRegistration lastReg;

	private final String mediumStyle = "width: 120px;";

	private Set<FixtureTO> modified = new HashSet<FixtureTO>();

	private VerticalPanel panel;

	private CellTable<FixtureTO> table;

	private final String timeStyle = "width: 50px;";

	private final String wideStyle = "width: 300px;";

	private int year;
	private HTML yearHeader;

	private List<Integer> years;

	RecordFixturesPanel() {

		this.panel = new VerticalPanel();
		this.year = Integer.parseInt(RecordFixturesPanel.yearFormat.format(new Date()));

		RecordFixturesPanel.eventService.getFixtureNames(new AsyncCallback<List<FixtureNameTO>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.toString());
				RecordFixturesPanel.this.panel.remove(RecordFixturesPanel.wait);

			}

			@Override
			public void onSuccess(List<FixtureNameTO> fntos) {
				RecordFixturesPanel.this.fixtureNames.add("");
				for (FixtureNameTO fnto : fntos) {
					RecordFixturesPanel.this.fixtureNames.add(fnto.getName());
				}
				RecordFixturesPanel.eventService.getYears(new AsyncCallback<List<Integer>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.toString());
						RecordFixturesPanel.this.panel.remove(RecordFixturesPanel.wait);
					}

					@Override
					public void onSuccess(List<Integer> years) {
						RecordFixturesPanel.this.years = years;
						RecordFixturesPanel.this.setup();
					}
				});
			}
		});

		this.initWidget(this.panel);
	}

	public void refresh() {
		this.apply.setEnabled(false);
		RecordFixturesPanel.eventService.getFixturesForYear(this.year,
				new AsyncCallback<List<FixtureTO>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.toString());
						RecordFixturesPanel.this.panel.remove(RecordFixturesPanel.wait);
					}

					@Override
					public void onSuccess(List<FixtureTO> result) {
						RecordFixturesPanel.this.yearHeader.setHTML("<h2>Modify "
								+ RecordFixturesPanel.this.year + " fixtures</h2>");
						List<FixtureTO> list = RecordFixturesPanel.this.dataProvider.getList();
						list.clear();
						for (FixtureTO ptto : result) {
							list.add(ptto);
						}
						RecordFixturesPanel.this.table.redraw();
						RecordFixturesPanel.this.panel.remove(RecordFixturesPanel.wait);
					}
				});
	}

	private void setup() {
		CellTableResources resources = GWT.create(CellTableResources.class);
		this.panel.add(new HTML("<h2>Create new fixture</h2>"));
		this.panel.add(RecordFixturesPanel.wait);
		Button create = new Button("Create");
		create.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				List<FixtureTO> list = RecordFixturesPanel.this.dataProvider.getList();
				FixtureTO fto = new FixtureTO(null, "", new Date(), null, null, null, null);
				list.add(fto);
				RecordFixturesPanel.this.modified.add(fto);
				RecordFixturesPanel.this.apply.setEnabled(true);
				RecordFixturesPanel.this.table.redraw();
			}
		});
		this.panel.add(create);

		this.yearHeader = new HTML("<h2>Modify " + this.year + " fixtures</h2>");
		this.panel.add(this.yearHeader);
		this.table = new CellTable<FixtureTO>(10, resources);

		this.dataProvider = new ListDataProvider<FixtureTO>();
		this.dataProvider.addDataDisplay(this.table);

		this.panel.add(this.table);
		HorizontalPanel bs = new HorizontalPanel();
		bs.setSpacing(10);
		this.panel.add(bs);
		this.apply = new Button("Apply");
		this.apply.setEnabled(false);
		Button discard = new Button("Discard");
		bs.add(this.apply);
		bs.add(discard);
		this.apply.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for (FixtureTO f : RecordFixturesPanel.this.modified) {
					String fSt = "Fixture " + f.getName() + " on " + f.getDate() + " against "
							+ f.getOpponents();
					if (f.getKey() == null) {
						if (!f.getName().isEmpty() && !f.isDelete()) {
							RecordFixturesPanel.logger.fine(fSt + " will be created");
						}
					} else {
						if (f.isDelete()) {
							RecordFixturesPanel.logger.fine(fSt + " will be deleted");
						} else {
							RecordFixturesPanel.logger.fine(fSt + " will be updated");
						}
					}
				}
				RecordFixturesPanel.eventService.update(RecordFixturesPanel.this.modified,
						new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								Window.alert(caught.toString());
							}

							@Override
							public void onSuccess(Void result) {
								TheEventBus.getInstance().fireEvent(new HomeChangedEvent());
								History.newItem("home");
							}
						});

			}
		});
		discard.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				RecordFixturesPanel.this.refresh();

			}
		});

		// Name column
		Column<FixtureTO, String> nameColumn = new Column<FixtureTO, String>(new SelectionCell(
				this.fixtureNames)) {

			@Override
			public String getValue(FixtureTO uto) {
				return uto.getName();
			}
		};
		nameColumn.setFieldUpdater(new FieldUpdater<FixtureTO, String>() {
			@Override
			public void update(int index, FixtureTO fto, String value) {
				fto.setName(value);
				RecordFixturesPanel.this.modified.add(fto);
				RecordFixturesPanel.this.apply.setEnabled(true);
			}
		});

		// Date column
		Column<FixtureTO, Date> dateColumn = new Column<FixtureTO, Date>(new DatePickerCell(
				DateTimeFormat.getFormat("yyyy-MM-dd"))) {
			@Override
			public Date getValue(FixtureTO fto) {
				return fto.getDate();
			}
		};
		dateColumn.setFieldUpdater(new FieldUpdater<FixtureTO, Date>() {
			@Override
			public void update(int index, FixtureTO fto, Date value) {
				String time = RecordFixturesPanel.timeFormat.format(fto.getDate());
				String date = RecordFixturesPanel.dateFormat.format(value);
				fto.setDate(RecordFixturesPanel.dateTimeFormat.parseStrict(date + " " + time));
				RecordFixturesPanel.this.modified.add(fto);
				RecordFixturesPanel.this.apply.setEnabled(true);
			}
		});

		// Time column
		Column<FixtureTO, String> timeColumn = new Column<FixtureTO, String>(
				new StyledTextInputCell(this.timeStyle)) {

			@Override
			public String getValue(FixtureTO fto) {
				return RecordFixturesPanel.timeFormat.format(fto.getDate());
			}
		};
		timeColumn.setFieldUpdater(new FieldUpdater<FixtureTO, String>() {
			@Override
			public void update(int index, FixtureTO fto, String value) {
				String date = RecordFixturesPanel.dateFormat.format(fto.getDate());
				try {
					fto.setDate(RecordFixturesPanel.dateTimeFormat.parseStrict(date + " " + value));
					RecordFixturesPanel.this.modified.add(fto);
					RecordFixturesPanel.this.apply.setEnabled(true);
				} catch (IllegalArgumentException e) {
					Window.alert("Time must be 24 hour clock hh:mm");
				}
			}
		});

		// Opponents column
		Column<FixtureTO, String> opponentsColumn = new Column<FixtureTO, String>(
				new StyledTextInputCell(this.mediumStyle)) {

			@Override
			public String getValue(FixtureTO fto) {
				return fto.getOpponents();
			}
		};
		opponentsColumn.setFieldUpdater(new FieldUpdater<FixtureTO, String>() {
			@Override
			public void update(int index, FixtureTO fto, String value) {
				fto.setOpponents(value);
				RecordFixturesPanel.this.modified.add(fto);
				RecordFixturesPanel.this.apply.setEnabled(true);
			}
		});

		// Location column
		Column<FixtureTO, String> locationColumn = new Column<FixtureTO, String>(
				new StyledTextInputCell(this.mediumStyle)) {

			@Override
			public String getValue(FixtureTO fto) {
				return fto.getLocation();
			}
		};
		locationColumn.setFieldUpdater(new FieldUpdater<FixtureTO, String>() {
			@Override
			public void update(int index, FixtureTO fto, String value) {
				fto.setLocation(value);
				RecordFixturesPanel.this.modified.add(fto);
				RecordFixturesPanel.this.apply.setEnabled(true);
			}
		});

		// Players column
		Column<FixtureTO, String> playersColumn = new Column<FixtureTO, String>(
				new StyledTextInputCell(this.wideStyle)) {

			@Override
			public String getValue(FixtureTO fto) {
				return fto.getPlayers();
			}
		};
		playersColumn.setFieldUpdater(new FieldUpdater<FixtureTO, String>() {
			@Override
			public void update(int index, FixtureTO fto, String value) {
				fto.setPlayers(value);
				RecordFixturesPanel.this.modified.add(fto);
				RecordFixturesPanel.this.apply.setEnabled(true);
			}
		});

		// Results column
		Column<FixtureTO, String> resultColumn = new Column<FixtureTO, String>(
				new StyledTextInputCell(this.wideStyle)) {

			@Override
			public String getValue(FixtureTO fto) {
				return fto.getResult();
			}
		};
		resultColumn.setFieldUpdater(new FieldUpdater<FixtureTO, String>() {
			@Override
			public void update(int index, FixtureTO fto, String value) {
				fto.setResult(value);
				RecordFixturesPanel.this.modified.add(fto);
				RecordFixturesPanel.this.apply.setEnabled(true);
			}
		});

		// Delete column
		Column<FixtureTO, Boolean> deleteColumn = new Column<FixtureTO, Boolean>(new CheckboxCell()) {

			@Override
			public Boolean getValue(FixtureTO object) {
				return object.isDelete();
			}
		};
		deleteColumn.setFieldUpdater(new FieldUpdater<FixtureTO, Boolean>() {
			@Override
			public void update(int index, FixtureTO object, Boolean value) {
				object.setDelete(value);
				RecordFixturesPanel.this.modified.add(object);
				RecordFixturesPanel.this.apply.setEnabled(true);
			}
		});

		// Add the columns.
		this.table.setWidth("auto", true);

		this.table.addColumn(nameColumn, "Name");
		this.table.addColumn(dateColumn, "Date");
		this.table.addColumn(timeColumn, "Time");
		this.table.addColumn(opponentsColumn, "Opponents");
		this.table.addColumn(locationColumn, "Location");
		this.table.addColumn(playersColumn, "Players");
		this.table.addColumn(resultColumn, "Result");
		this.table.addColumn(deleteColumn, "Delete");

		SimplePager pager = new SimplePager();
		pager.setDisplay(this.table);
		this.panel.add(pager);

		this.panel.add(new HTML("<h2>Change year</h2>"));
		this.panel.add(new HTML("<p>Select year you want to see</p>"));
		final ListBox lb = new ListBox();
		this.panel.add(lb);
		for (int year : this.years) {
			String yearS = Integer.toString(year);
			lb.addItem(yearS);
		}
		lb.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				String yearS = lb.getValue(lb.getSelectedIndex());
				RecordFixturesPanel.this.year = Integer.parseInt(yearS);
				RecordFixturesPanel.this.refresh();
			}
		});
		this.refresh();

	}

}
