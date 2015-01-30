package uk.org.harwellcroquet.client;

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
import com.google.gwt.cell.client.TextCell;
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

public class ManageSubsPanel extends Composite {

	final private static Logger logger = Logger.getLogger(ManageSubsPanel.class.getName());

	private Button apply;

	private ListDataProvider<UserTO> dataProvider;
	DateTimeFormat dateFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM);

	private HTML header;
	private final LoginServiceAsync loginService = LoginServiceAsync.Util.getInstance();

	private Set<UserTO> modified = new HashSet<UserTO>();

	private final String smallnumStyle = "width: 40px;";

	private CellTable<UserTO> table;

	ManageSubsPanel() {

		VerticalPanel panel = new VerticalPanel();
		this.header = new HTML("<h2>Manage subscriptions</h2>");
		panel.add(this.header);
		this.table = new CellTable<UserTO>(10);

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
				for (UserTO f : ManageSubsPanel.this.modified) {
					ManageSubsPanel.logger.fine("Account " + f.getEmail() + " will be updated");
				}
				ManageSubsPanel.this.loginService.update(Cookies.getCookie(Consts.COOKIE),
						ManageSubsPanel.this.modified, new AsyncCallback<Void>() {

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

		Column<UserTO, String> emailColumn = new Column<UserTO, String>(new TextCell()) {

			@Override
			public String getValue(UserTO uto) {
				return uto.getEmail();
			}
		};

		Column<UserTO, String> nameColumn = new Column<UserTO, String>(new TextCell()) {

			@Override
			public String getValue(UserTO uto) {
				return uto.getName();
			}
		};

		Column<UserTO, String> phone1Column = new Column<UserTO, String>(new TextCell()) {

			@Override
			public String getValue(UserTO uto) {
				return uto.getPhone1();
			}
		};

		Column<UserTO, String> phone2Column = new Column<UserTO, String>(new TextCell()) {

			@Override
			public String getValue(UserTO uto) {
				return uto.getPhone2();
			}
		};

		Column<UserTO, Boolean> mainColumn = new Column<UserTO, Boolean>(new CheckboxCell()) {
			@Override
			public Boolean getValue(UserTO uto) {
				return uto.isMain();
			}
		};

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
				ManageSubsPanel.this.modified.add(uto);
				ManageSubsPanel.this.apply.setEnabled(true);
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
				ManageSubsPanel.this.modified.add(uto);
				ManageSubsPanel.this.apply.setEnabled(true);
			}
		});

		// Add the columns.
		this.table.setWidth("auto", true);
		this.table.addColumn(emailColumn, "Email");
		this.table.addColumn(nameColumn, "Name");
		this.table.addColumn(phone1Column, "Phone1");
		this.table.addColumn(phone2Column, "Phone2");
		this.table.addColumn(mainColumn, "P");
		this.table.addColumn(paidDateColumn, "Paid date");
		this.table.addColumn(paidPenceColumn, "Paid pence");

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
				List<UserTO> list = ManageSubsPanel.this.dataProvider.getList();
				list.clear();
				for (UserTO ptto : result) {
					list.add(ptto);
				}
				ManageSubsPanel.this.table.redraw();
			}
		});
	}

}
