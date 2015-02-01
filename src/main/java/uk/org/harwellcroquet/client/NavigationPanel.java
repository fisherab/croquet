package uk.org.harwellcroquet.client;

import java.util.Date;

import uk.org.harwellcroquet.client.event.LoginEvent;
import uk.org.harwellcroquet.client.event.LoginEventHandler;
import uk.org.harwellcroquet.client.service.ItemServiceAsync;
import uk.org.harwellcroquet.client.service.LoginServiceAsync;
import uk.org.harwellcroquet.shared.Consts;
import uk.org.harwellcroquet.shared.UserTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;

public class NavigationPanel extends Composite {

	private HTML html;
	private final ItemServiceAsync itemService = ItemServiceAsync.Util.getInstance();
	private final LoginServiceAsync loginService = LoginServiceAsync.Util.getInstance();
	private final static DateTimeFormat yearFormat = DateTimeFormat.getFormat("yyyy");
	private Harwellcroquet harwellcroquet;
	private VerticalPanel adminPanel = new VerticalPanel();
	private VerticalPanel loggedInPanel = new VerticalPanel();
	private VerticalPanel treasurerPanel = new VerticalPanel();
	private VerticalPanel scorerPanel = new VerticalPanel();

	NavigationPanel(Harwellcroquet harwellcroquet) {
		this.harwellcroquet = harwellcroquet;

		final VerticalPanel panel = new VerticalPanel();
		panel.addStyleName(CroquetBundle.INSTANCE.css().navPanel());

		VerticalPanel allwaysPanel = new VerticalPanel();
		panel.add(allwaysPanel);

		allwaysPanel.add(new InlineHyperlink("Home", "home"));
		allwaysPanel.add(new Anchor("Finding us", GWT.getHostPageBaseURL() + "finding.html"));
		allwaysPanel.add(new InlineHyperlink("Contact us", "contact"));
		allwaysPanel.add(new InlineHyperlink("Events", "events/" + yearFormat.format(new Date())));
		
		this.html = new HTML();
		panel.add(this.html);
		
		loggedInPanel.add(new InlineHyperlink("Meetings", "meetings"));
		loggedInPanel.add(new InlineHyperlink("List users", "listUsers"));
		loggedInPanel.add(new InlineHyperlink("Account settings", "accountSettings"));
		loggedInPanel.add(new InlineHyperlink("Create news item", "createNewsItem"));
		loggedInPanel.add(new InlineHyperlink("Modify news items", "modifyNewsItems"));
		loggedInPanel.add(new InlineHyperlink("Record result", "recordResult"));

		adminPanel.add(new InlineHyperlink("Create body item", "createBodyItem"));
		adminPanel.add(new InlineHyperlink("Modify body items", "modifyBodyItems"));
		adminPanel.add(new InlineHyperlink("Create nav items", "createNavItems"));
		adminPanel.add(new InlineHyperlink("Modify nav items", "modifyNavItems"));
		adminPanel.add(new InlineHyperlink("Create contact items", "createContactItems"));
		adminPanel.add(new InlineHyperlink("Modify contact items", "modifyContactItems"));
		adminPanel.add(new InlineHyperlink("Modify accounts", "modifyAccounts"));
		adminPanel.add(new InlineHyperlink("File manager", "files"));

		scorerPanel.add(new InlineHyperlink("Manage Events", "manageEvents"));
		scorerPanel.add(new InlineHyperlink("Manage Fixture Names", "manageFixtureNames"));
		scorerPanel.add(new InlineHyperlink("Record Fixtures", "recordFixtures"));

		treasurerPanel.add(new InlineHyperlink("Subscriptions", "manageSubs"));

		final Button online = new Button("Set Online");
		online.addStyleName(CroquetBundle.INSTANCE.css().red());

		final Button offline = new Button("Set Offline");

		online.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				loginService.setOnline(Cookies.getCookie(Consts.COOKIE), true,
						new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Failed " + caught);
							}

							@Override
							public void onSuccess(Void result) {
								adminPanel.remove(online);
								adminPanel.add(offline);
							}
						});
			}
		});

		offline.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				loginService.setOnline(Cookies.getCookie(Consts.COOKIE), false,
						new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Failed " + caught);
							}

							@Override
							public void onSuccess(Void result) {
								adminPanel.remove(offline);
								adminPanel.add(online);
							}
						});

			}
		});

		adminPanel.add(offline);

		final Button debug = new Button("Debug on");
		final Button nodebug = new Button("Debug off");

		debug.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				NavigationPanel.this.harwellcroquet.setDebug(true);
				adminPanel.remove(debug);
				adminPanel.add(nodebug);
			}
		});

		nodebug.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				NavigationPanel.this.harwellcroquet.setDebug(false);
				adminPanel.remove(nodebug);
				adminPanel.add(debug);
			}
		});

		adminPanel.add(debug);

		TheEventBus.getInstance().addHandler(LoginEvent.TYPE, new LoginEventHandler() {

			@Override
			public void onLogin(LoginEvent event) {
				panel.remove(loggedInPanel);
				panel.remove(adminPanel);
				panel.remove(scorerPanel);
				panel.remove(treasurerPanel);
				if (event.getUserTO() != null) {
					UserTO user = event.getUserTO();
					panel.add(loggedInPanel);
					if (user.getPriv().equals("SUPER") || user.isScorer()) {
						panel.add(scorerPanel);
					}
					if (user.getPriv().equals("SUPER") || user.isTreasurer()) {
						panel.add(treasurerPanel);
					}
					if (user.getPriv().equals("SUPER")) {
						panel.add(adminPanel);
						adminPanel.remove(online);
						adminPanel.remove(offline);
						if (user.isOnline()) {
							adminPanel.add(offline);
						} else {
							adminPanel.add(online);
						}
					}

				}
			}
		});

		this.init();
		this.initWidget(panel);
	}

	private void init() {

		this.itemService.getNavigationHtml(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.toString());
			}

			@Override
			public void onSuccess(String result) {
				NavigationPanel.this.html.setHTML(result);

			}
		});

	}

}
