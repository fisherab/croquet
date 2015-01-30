package uk.org.harwellcroquet.client;

import java.util.List;

import uk.org.harwellcroquet.client.service.LoginServiceAsync;
import uk.org.harwellcroquet.shared.Consts;
import uk.org.harwellcroquet.shared.UserTO;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;

public class ListUsersPanel extends Composite {

	private ListDataProvider<UserTO> dataProvider;
	DateTimeFormat dateFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM);

	private HTML header;
	private final LoginServiceAsync loginService = LoginServiceAsync.Util.getInstance();
	private final static HTML wait = new HTML("<h2>Waiting for current information ...</h2>");
	private CellTable<UserTO> table;
	private VerticalPanel panel;

	ListUsersPanel() {

		panel = new VerticalPanel();
		this.header = new HTML("<h2>List users</h2>");
		panel.add(this.header);
		panel.add(wait);
		this.table = new CellTable<UserTO>(10);

		this.dataProvider = new ListDataProvider<UserTO>();
		this.dataProvider.addDataDisplay(this.table);

		panel.add(this.table);

		Column<UserTO, String> nameColumn = new Column<UserTO, String>(new TextCell()) {

			@Override
			public String getValue(UserTO uto) {
				return uto.getName();
			}
		};

		Column<UserTO, String> emailColumn = new Column<UserTO, String>(new TextCell()) {

			@Override
			public String getValue(UserTO uto) {
				return uto.getEmail();
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

		Column<UserTO, String> assocHCapColumn = new Column<UserTO, String>(new TextCell()) {
			@Override
			public String getValue(UserTO uto) {
				return uto.getAssocHCap();
			}
		};

		Column<UserTO, String> golfHCapColumn = new Column<UserTO, String>(new TextCell()) {

			@Override
			public String getValue(UserTO uto) {
				return uto.getGolfHCap();
			}
		};

		// Add the columns.
		this.table.setWidth("auto", true);
		this.table.addColumn(nameColumn, "Name");
		this.table.addColumn(emailColumn, "Email");
		this.table.addColumn(phone1Column, "Phone1");
		this.table.addColumn(phone2Column, "Phone2");
		this.table.addColumn(assocHCapColumn, "Assoc");
		this.table.addColumn(golfHCapColumn, "Golf");

		SimplePager pager = new SimplePager();
		pager.setDisplay(table);
		panel.add(pager);

		this.refresh();

		this.initWidget(panel);
	}

	public void refresh() {
		this.loginService.getUsers(Cookies.getCookie(Consts.COOKIE), "AllCurrentUsers",
				new AsyncCallback<List<UserTO>>() {

					@Override
					public void onFailure(Throwable caught) {
						panel.remove(wait);
						Window.alert(caught.toString());
					}

					@Override
					public void onSuccess(List<UserTO> result) {
						panel.remove(wait);
						List<UserTO> list = ListUsersPanel.this.dataProvider.getList();
						list.clear();
						for (UserTO ptto : result) {
							list.add(ptto);
						}
						ListUsersPanel.this.table.redraw();
					}
				});
	}

}
