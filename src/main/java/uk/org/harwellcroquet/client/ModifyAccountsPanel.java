package uk.org.harwellcroquet.client;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import uk.org.harwellcroquet.client.event.HomeChangedEvent;
import uk.org.harwellcroquet.client.service.LoginServiceAsync;
import uk.org.harwellcroquet.shared.Consts;
import uk.org.harwellcroquet.shared.UserTO;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.DatePickerCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;

public class ModifyAccountsPanel extends Composite {

	final private static Logger logger = Logger.getLogger(ModifyAccountsPanel.class.getName());

	private Button apply;

	private ListDataProvider<UserTO> dataProvider;
	DateTimeFormat dateFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM);

	private final LoginServiceAsync loginService = LoginServiceAsync.Util.getInstance();

	private Set<UserTO> modified = new HashSet<UserTO>();

	private final String phoneStyle = "width: 80px;";

	private final String smallnumStyle = "width: 40px;";

	private CellTable<UserTO> table;

	ModifyAccountsPanel() {

		CellTableResources resources = GWT.create(CellTableResources.class);

		VerticalPanel panel = new VerticalPanel();
		panel.add(new HTML("<h2>Create new empty account</h2>"));
		Button create = new Button("Create");
		create.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ModifyAccountsPanel.this.loginService.registerSkeletonUser(Cookies.getCookie(Consts.COOKIE),
						new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Failed: " + caught);
							}

							@Override
							public void onSuccess(Void result) {
								refresh();

							}
						});

			}
		});
		panel.add(create);

		panel.add(new HTML("<h2>Modify accounts</h2>"));
		this.table = new CellTable<UserTO>(10, resources);

		this.dataProvider = new ListDataProvider<UserTO>();
		this.dataProvider.addDataDisplay(this.table);

		panel.add(this.table);
		HorizontalPanel bs = new HorizontalPanel();
		bs.setSpacing(10);
		panel.add(bs);
		this.apply = new Button("Apply");
		this.apply.setEnabled(false);
		Button discard = new Button("Discard");
		bs.add(this.apply);
		bs.add(discard);
		this.apply.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for (UserTO f : ModifyAccountsPanel.this.modified) {
					if (f.isDelete()) {
						ModifyAccountsPanel.logger.fine("Account " + f.getEmail() + " will be deleted");
					} else {
						ModifyAccountsPanel.logger.fine("Account " + f.getEmail() + " will be updated");
					}
				}
				ModifyAccountsPanel.this.loginService.update(Cookies.getCookie(Consts.COOKIE),
						ModifyAccountsPanel.this.modified, new AsyncCallback<Void>() {

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
				refresh();

			}
		});

		Column<UserTO, String> idColumn = new Column<UserTO, String>(new TextCell()) {
			@Override
			public String getValue(UserTO uto) {
				return Long.toString(uto.getId());
			}
		};

		Column<UserTO, String> emailColumn = new Column<UserTO, String>(new TextInputCell()) {

			@Override
			public String getValue(UserTO uto) {
				return uto.getEmail();
			}
		};
		emailColumn.setFieldUpdater(new FieldUpdater<UserTO, String>() {
			@Override
			public void update(int index, UserTO uto, String value) {
				uto.setEmail(value);
				ModifyAccountsPanel.this.modified.add(uto);
				ModifyAccountsPanel.this.apply.setEnabled(true);
			}
		});

		Column<UserTO, String> nameColumn = new Column<UserTO, String>(new TextInputCell()) {

			@Override
			public String getValue(UserTO uto) {
				return uto.getName();
			}
		};
		nameColumn.setFieldUpdater(new FieldUpdater<UserTO, String>() {
			@Override
			public void update(int index, UserTO uto, String value) {
				uto.setName(value);
				ModifyAccountsPanel.this.modified.add(uto);
				ModifyAccountsPanel.this.apply.setEnabled(true);
			}
		});

		Column<UserTO, String> phone1Column = new Column<UserTO, String>(new StyledTextInputCell(this.phoneStyle)) {

			@Override
			public String getValue(UserTO uto) {
				return uto.getPhone1();
			}
		};
		phone1Column.setFieldUpdater(new FieldUpdater<UserTO, String>() {
			@Override
			public void update(int index, UserTO uto, String value) {
				uto.setPhone1(value);
				ModifyAccountsPanel.this.modified.add(uto);
				ModifyAccountsPanel.this.apply.setEnabled(true);
			}
		});

		Column<UserTO, String> phone2Column = new Column<UserTO, String>(new StyledTextInputCell(this.phoneStyle)) {

			@Override
			public String getValue(UserTO uto) {
				return uto.getPhone2();
			}
		};
		phone2Column.setFieldUpdater(new FieldUpdater<UserTO, String>() {
			@Override
			public void update(int index, UserTO uto, String value) {
				uto.setPhone2(value);
				ModifyAccountsPanel.this.modified.add(uto);
				ModifyAccountsPanel.this.apply.setEnabled(true);
			}
		});

		Column<UserTO, String> assocHCapColumn = new Column<UserTO, String>(new StyledTextInputCell(this.smallnumStyle)) {

			@Override
			public String getValue(UserTO uto) {
				return uto.getAssocHCap();
			}
		};
		assocHCapColumn.setFieldUpdater(new FieldUpdater<UserTO, String>() {
			@Override
			public void update(int index, UserTO uto, String value) {
				uto.setAssocHCap(value);
				ModifyAccountsPanel.this.modified.add(uto);
				ModifyAccountsPanel.this.apply.setEnabled(true);
			}
		});

		Column<UserTO, String> golfHCapColumn = new Column<UserTO, String>(new StyledTextInputCell(this.smallnumStyle)) {

			@Override
			public String getValue(UserTO uto) {
				return uto.getGolfHCap();
			}
		};
		golfHCapColumn.setFieldUpdater(new FieldUpdater<UserTO, String>() {
			@Override
			public void update(int index, UserTO uto, String value) {
				uto.setGolfHCap(value);
				ModifyAccountsPanel.this.modified.add(uto);
				ModifyAccountsPanel.this.apply.setEnabled(true);
			}
		});

		Column<UserTO, Boolean> mainColumn = new Column<UserTO, Boolean>(new CheckboxCell()) {
			@Override
			public Boolean getValue(UserTO uto) {
				return uto.isMain();
			}
		};
		mainColumn.setFieldUpdater(new FieldUpdater<UserTO, Boolean>() {
			@Override
			public void update(int index, UserTO uto, Boolean value) {
				uto.setMain(value);
				ModifyAccountsPanel.this.modified.add(uto);
				ModifyAccountsPanel.this.apply.setEnabled(true);
			}
		});

		String[] privs = { "EMAIL_OK", "NEW", "NONE", "SUPER", "EX" };
		Column<UserTO, String> privColumn = new Column<UserTO, String>(new SelectionCell(Arrays.asList(privs))) {
			@Override
			public String getValue(UserTO uto) {
				return uto.getPriv();
			}
		};
		privColumn.setFieldUpdater(new FieldUpdater<UserTO, String>() {
			@Override
			public void update(int index, UserTO uto, String value) {
				uto.setPriv(value);
				ModifyAccountsPanel.this.modified.add(uto);
				ModifyAccountsPanel.this.apply.setEnabled(true);
			}
		});

		Column<UserTO, Boolean> scorerColumn = new Column<UserTO, Boolean>(new CheckboxCell()) {
			@Override
			public Boolean getValue(UserTO uto) {
				return uto.isScorer();
			}
		};
		scorerColumn.setFieldUpdater(new FieldUpdater<UserTO, Boolean>() {
			@Override
			public void update(int index, UserTO uto, Boolean value) {
				uto.setScorer(value);
				ModifyAccountsPanel.this.modified.add(uto);
				ModifyAccountsPanel.this.apply.setEnabled(true);
			}
		});

		Column<UserTO, Boolean> treasurerColumn = new Column<UserTO, Boolean>(new CheckboxCell()) {
			@Override
			public Boolean getValue(UserTO uto) {
				return uto.isTreasurer();
			}
		};
		treasurerColumn.setFieldUpdater(new FieldUpdater<UserTO, Boolean>() {
			@Override
			public void update(int index, UserTO uto, Boolean value) {
				uto.setTreasurer(value);
				ModifyAccountsPanel.this.modified.add(uto);
				ModifyAccountsPanel.this.apply.setEnabled(true);
			}
		});

		Column<UserTO, Date> paidDateColumn = new Column<UserTO, Date>(new DatePickerCell(
				DateTimeFormat.getFormat("yyyy-MM-dd"))) {
			@Override
			public Date getValue(UserTO uto) {
				return uto.getPaidDate();
			}
		};
		paidDateColumn.setFieldUpdater(new FieldUpdater<UserTO, Date>() {
			@Override
			public void update(int index, UserTO uto, Date value) {
				uto.setPaidDate(value);
				ModifyAccountsPanel.this.modified.add(uto);
				ModifyAccountsPanel.this.apply.setEnabled(true);
			}
		});

		Column<UserTO, String> paidPenceColumn = new Column<UserTO, String>(new StyledTextInputCell(this.smallnumStyle)) {
			@Override
			public String getValue(UserTO uto) {
				return uto.getPaidPence() == null ? null : uto.getPaidPence().toString();
			}
		};
		paidPenceColumn.setFieldUpdater(new FieldUpdater<UserTO, String>() {
			@Override
			public void update(int index, UserTO uto, String value) {
				uto.setPaidPence(Integer.parseInt(value));
				ModifyAccountsPanel.this.modified.add(uto);
				ModifyAccountsPanel.this.apply.setEnabled(true);
			}
		});

		Column<UserTO, Boolean> deleteColumn = new Column<UserTO, Boolean>(new CheckboxCell()) {

			@Override
			public Boolean getValue(UserTO object) {
				return object.isDelete();
			}
		};
		deleteColumn.setFieldUpdater(new FieldUpdater<UserTO, Boolean>() {
			@Override
			public void update(int index, UserTO object, Boolean value) {
				object.setDelete(value);
				ModifyAccountsPanel.this.modified.add(object);
				ModifyAccountsPanel.this.apply.setEnabled(true);
			}
		});

		// Add the columns.
		this.table.setWidth("auto", true);
		this.table.addColumn(idColumn, "Key");
		this.table.addColumn(emailColumn, "Email");
		this.table.addColumn(nameColumn, "Name");
		this.table.addColumn(phone1Column, "Phone1");
		this.table.addColumn(phone2Column, "Phone2");
		this.table.addColumn(assocHCapColumn, "Assoc");
		this.table.addColumn(golfHCapColumn, "Golf");
		this.table.addColumn(mainColumn, "P");
		this.table.addColumn(scorerColumn, "S");
		this.table.addColumn(treasurerColumn, "T");
		this.table.addColumn(privColumn, "Priv");
		this.table.addColumn(paidDateColumn, "Paid date");
		this.table.addColumn(paidPenceColumn, "Paid pence");
		this.table.addColumn(deleteColumn, "Delete");

		SimplePager pager = new SimplePager();
		pager.setDisplay(table);
		panel.add(pager);

		this.refresh();

		this.initWidget(panel);
	}

	public void refresh() {
		this.apply.setEnabled(false);
		this.loginService.getUsers(Cookies.getCookie(Consts.COOKIE), "AllUsers", new AsyncCallback<List<UserTO>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.toString());
			}

			@Override
			public void onSuccess(List<UserTO> result) {
				List<UserTO> list = ModifyAccountsPanel.this.dataProvider.getList();
				list.clear();
				for (UserTO ptto : result) {
					list.add(ptto);
				}
				ModifyAccountsPanel.this.table.redraw();
			}
		});
	}

}
