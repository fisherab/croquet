package uk.org.harwellcroquet.client;

import java.util.logging.Logger;

import uk.org.harwellcroquet.client.RegisterPanel.Op;
import uk.org.harwellcroquet.client.event.HomeChangedEvent;
import uk.org.harwellcroquet.client.event.HomeChangedEventHandler;
import uk.org.harwellcroquet.client.event.LoginEvent;
import uk.org.harwellcroquet.client.event.LoginEventHandler;
import uk.org.harwellcroquet.client.service.LoginServiceAsync;
import uk.org.harwellcroquet.shared.Consts;
import uk.org.harwellcroquet.shared.UserTO;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.logging.client.HasWidgetsLogHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Harwellcroquet implements EntryPoint {

	private static LoginServiceAsync loginService = LoginServiceAsync.Util.getInstance();
	final private static Logger logger = Logger.getLogger(Harwellcroquet.class.getName());

	private Widget body;
	private FlexTable headerGrid;

	private HomePanel home;
	private DockLayoutPanel main;

	private Button login;
	Button logout = new Button("Logout");

	private ScrollPanel logPanel;
	private FileManagerPanel uploadForm;
	private UserTO uto;

	private void modifyItems(UpdateItemsPanel.Type t) {
		UpdateItemsPanel updateItemsPanel = new UpdateItemsPanel(t);
		this.main.remove(this.body);
		this.body = new ScrollPanel(updateItemsPanel);
		this.main.add(this.body);
	}

	private void createItem(CreateItemPanel.Type t) {
		CreateItemPanel createItemPanel = new CreateItemPanel(t);
		this.main.remove(this.body);
		this.body = new ScrollPanel(createItemPanel);
		this.main.add(this.body);
	}

	private void displayHome() {
		this.main.remove(this.body);
		this.body = new ScrollPanel(this.home);
		this.main.add(this.body);
	}

	private void displayContact() {
		ContactPanel contactPanel = new ContactPanel();
		this.main.remove(this.body);
		this.body = new ScrollPanel(contactPanel);
		this.main.add(this.body);
	}

	private void displayEvent(int year, String name) {
		DisplayEventPanel displayEventPanel = new DisplayEventPanel(year, name);
		this.main.remove(this.body);
		this.body = new ScrollPanel(displayEventPanel);
		this.main.add(this.body);
	}

	private void displayFixture(int year, String name) {
		DisplayFixturePanel displayFixturePanel = new DisplayFixturePanel(year, name);
		this.main.remove(this.body);
		this.body = new ScrollPanel(displayFixturePanel);
		this.main.add(this.body);
	}

	public void register(Op op) {
		RegisterPanel registerPanel = new RegisterPanel(op);
		this.main.remove(this.body);
		this.body = new ScrollPanel(registerPanel);
		this.main.add(this.body);
	}

	private void displayEvents(int year) {
		DisplayEventsPanel displayEventsPanel = new DisplayEventsPanel(year);
		this.main.remove(this.body);
		this.body = new ScrollPanel(displayEventsPanel);
		this.main.add(this.body);

	}

	private void manageEvents() {
		ManageEventsPanel manageEventsPanel = new ManageEventsPanel();
		this.main.remove(this.body);
		this.body = new ScrollPanel(manageEventsPanel);
		this.main.add(this.body);

	}

	private void displayMeetings() {
		MeetingsPanel meetingsPanel = new MeetingsPanel();
		this.main.remove(this.body);
		this.body = new ScrollPanel(meetingsPanel);
		this.main.add(this.body);
	}

	private void displayLogin() {
		LoginPanel loginPanel = new LoginPanel();
		this.main.remove(this.body);
		this.body = new ScrollPanel(loginPanel);
		this.main.add(this.body);
	}

	private void recordResult() {
		RecordResultPanel recordResultPanel = new RecordResultPanel(uto);
		this.main.remove(this.body);
		this.body = new ScrollPanel(recordResultPanel);
		this.main.add(this.body);
	}

	private void modifyAccounts() {
		ModifyAccountsPanel modifyAccountsPanel = new ModifyAccountsPanel();
		this.main.remove(this.body);
		this.body = new ScrollPanel(modifyAccountsPanel);
		this.main.add(this.body);
	}

	private void manageSubs() {
		ManageSubsPanel manageSubsPanel = new ManageSubsPanel();
		this.main.remove(this.body);
		this.body = new ScrollPanel(manageSubsPanel);
		this.main.add(this.body);
	}

	private void listUsers() {
		ListUsersPanel listUsersPanel = new ListUsersPanel();
		this.main.remove(this.body);
		this.body = new ScrollPanel(listUsersPanel);
		this.main.add(this.body);
	}

	private void recordFixtures() {
		RecordFixturesPanel recordFixturesPanel = new RecordFixturesPanel();
		this.main.remove(this.body);
		this.body = new ScrollPanel(recordFixturesPanel);
		this.main.add(this.body);
	}

	private void manageFixtureNames() {
		ManageFixtureNamesPanel manageFixtureNamesPanel = new ManageFixtureNamesPanel();
		this.main.remove(this.body);
		this.body = new ScrollPanel(manageFixtureNamesPanel);
		this.main.add(this.body);
	}

	private void resetPassword(long userId, String hashedPassword) {
		ResetPasswordPanel resetPasswordPanel = new ResetPasswordPanel(userId, hashedPassword);
		this.main.remove(this.body);
		this.body = new ScrollPanel(resetPasswordPanel);
		this.main.add(this.body);
	}

	/**
	 * This is the entry point method.
	 */
	@Override
	public void onModuleLoad() {

		logger.fine("Module loading");

		/* Load the CSS */
		CroquetBundle.INSTANCE.css().ensureInjected();

		/* Initialise history */
		String initToken = History.getToken();
		if (initToken.length() == 0) {
			History.newItem("home");
		}

		TheEventBus.getInstance().addHandler(HomeChangedEvent.TYPE, new HomeChangedEventHandler() {
			@Override
			public void onChange(HomeChangedEvent event) {
				home.refresh();
			}
		});

		/* Find current user if present and setup */
		String sessionid = Cookies.getCookie(Consts.COOKIE);
		if (sessionid != null) {
			Harwellcroquet.loginService.getUser(sessionid, new AsyncCallback<UserTO>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Failure " + caught);
				}

				@Override
				public void onSuccess(UserTO uto) {
					Harwellcroquet.this.setup(uto);
				}
			});
		} else {
			Harwellcroquet.this.setup(null);
		}
	}

	private void setup(UserTO uto) {

		logout.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Harwellcroquet.loginService.logout(Cookies.getCookie(Consts.COOKIE),
						new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								Window.alert(caught.toString());
							}

							@Override
							public void onSuccess(Void result) {
								TheEventBus.getInstance().fireEvent(new LoginEvent(null));
							}
						});

			}
		});

		TheEventBus.getInstance().addHandler(LoginEvent.TYPE, new LoginEventHandler() {

			@Override
			public void onLogin(LoginEvent event) {
				UserTO uto = event.getUserTO();
				if (uto != null) {
					headerGrid.setWidget(0, 1, new HTML("<p>Logged in as " + uto.getName()
							+ " &nbsp;&nbsp;&nbsp;</p>"));
					Harwellcroquet.this.uto = uto;
					Harwellcroquet.this.headerGrid.setWidget(0, 2, logout);
				} else {
					Harwellcroquet.this.headerGrid.setWidget(0, 1, login);
					Harwellcroquet.this.headerGrid.clearCell(0, 2);
					History.newItem("home");
				}
			}
		});

		this.main = new DockLayoutPanel(Unit.EM);
		RootLayoutPanel.get().add(this.main);

		NavigationPanel navPan = new NavigationPanel(Harwellcroquet.this);
		this.main.addWest(new ScrollPanel(navPan), 12);

		this.headerGrid = new FlexTable();
		this.headerGrid.setWidget(0, 0, new HTML("<h1>Harwell Croquet Club</h1>"));

		login = new Button("Login");
		this.headerGrid.setWidget(0, 1, login);
		login.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				History.newItem("login");
			}
		});

		this.main.addNorth(this.headerGrid, 4);

		Harwellcroquet.this.home = new HomePanel(Harwellcroquet.this);
		this.body = new ScrollPanel(Harwellcroquet.this.home);
		this.main.add(this.body);

		if (uto != null) {
			TheEventBus.getInstance().fireEvent(new LoginEvent(uto));
		}

		Element loading = DOM.getElementById("loading");
		DOM.removeChild(RootPanel.getBodyElement(), loading);

		/* Add history listener */
		History.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				String token = event.getValue();
				logger.fine("Received history reqest for " + token);
				if (token.equals("home")) {
					displayHome();
				} else if (token.equals("contact")) {
					displayContact();
				} else if (token.equals("meetings")) {
					displayMeetings();
				} else if (token.equals("files")) {
					displayFilePanel();
				} else if (token.equals("login")) {
					displayLogin();
				} else if (token.equals("register")) {
					register(RegisterPanel.Op.NEW);
				} else if (token.equals("createNewsItem")) {
					createItem(CreateItemPanel.Type.NEWS);
				} else if (token.equals("modifyNewsItems")) {
					modifyItems(UpdateItemsPanel.Type.NEWS);
				} else if (token.equals("createNavItems")) {
					createItem(CreateItemPanel.Type.NAV);
				} else if (token.equals("modifyNavItems")) {
					modifyItems(UpdateItemsPanel.Type.NAV);
				} else if (token.equals("createContactItems")) {
					createItem(CreateItemPanel.Type.CONTACT);
				} else if (token.equals("modifyContactItems")) {
					modifyItems(UpdateItemsPanel.Type.CONTACT);
				} else if (token.equals("listUsers")) {
					listUsers();
				} else if (token.equals("modifyAccounts")) {
					modifyAccounts();
				} else if (token.equals("accountSettings")) {
					Harwellcroquet.this.register(RegisterPanel.Op.UPDATE);
				} else if (token.equals("createBodyItem")) {
					createItem(CreateItemPanel.Type.BODY);
				} else if (token.equals("modifyBodyItems")) {
					modifyItems(UpdateItemsPanel.Type.BODY);
				} else if (token.equals("manageSubs")) {
					manageSubs();
				} else if (token.equals("manageEvents")) {
					manageEvents();
				} else if (token.equals("manageFixtureNames")) {
					manageFixtureNames();
				} else if (token.equals("recordFixtures")) {
					recordFixtures();
				} else if (token.equals("recordResult")) {
					recordResult();
				} else if (token.startsWith("event")) {
					String[] tokens = token.split("/");
					int year = Integer.parseInt(tokens[1]);
					if (tokens.length == 2) {
						displayEvents(year);
					} else {
						displayEvent(year, tokens[2]);
					}
				} else if (token.startsWith("fixture")) {
					String[] tokens = token.split("/");
					int year = Integer.parseInt(tokens[1]);
					displayFixture(year, tokens[2]);
				} else if (token.startsWith("resetPassword")) {
					String[] tokens = token.split("/");
					long userId = Long.parseLong(tokens[1]);
					resetPassword(userId, tokens[2]);
				} else {
					logger.severe("Current token is unexpected " + event.getValue());
				}
			}

		});

		History.fireCurrentHistoryState();

	}

	public void displayFilePanel() {
		this.uploadForm = new FileManagerPanel(Harwellcroquet.this, uto);
		this.main.remove(this.body);
		this.body = new ScrollPanel(this.uploadForm);
		this.main.add(this.body);
	}

	public void setDebug(boolean b) {
		if (b) {
			if (logPanel == null) {
				VerticalPanel log = new VerticalPanel();
				logPanel = new ScrollPanel(log);
				Logger.getLogger("").addHandler(new HasWidgetsLogHandler(log));
			}
			this.main.remove(body);
			this.main.addNorth(logPanel, 6);
			this.main.add(body);
			logger.fine("Debug enabled");
		} else {
			this.main.remove(body);
			this.main.remove(logPanel);
			this.main.add(body);
		}
	}

}
