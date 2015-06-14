package uk.org.harwellcroquet.client;

import java.util.logging.Logger;

import uk.org.harwellcroquet.client.service.LoginServiceAsync;
import uk.org.harwellcroquet.shared.Consts;
import uk.org.harwellcroquet.shared.UserTO;
import uk.org.harwellcroquet.shared.Utils;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RegisterPanel extends Composite {

	enum Op {
		NEW, UPDATE
	}

	final private static Logger logger = Logger.getLogger(RegisterPanel.class.getName());

	private final LoginServiceAsync loginService = LoginServiceAsync.Util.getInstance();
	protected UserTO uto;;

	public RegisterPanel(Op op) {

		logger.info("RegisterPanel being created for " + op);
		final VerticalPanel panel = new VerticalPanel();
		ScrollPanel sp = new ScrollPanel(panel);

		if (op == Op.NEW) {
			panel.add(new HTML("<h2>Registration</h2>"
					+ "<p>This form is for registration of prospective members of the Harwell Croquet Club. "
					+ "Once registered you will be able to record your scores in matches, post news items and see "
					+ "contact information for other members.</p>"
					+ "<p>After completing the form below and submitting it you will be sent an e-mail "
					+ "which you must act upon. The purpose of this e-mail is to ensure that nobody "
					+ "else can register on your behalf. Having followed the instructions in the e-mail "
					+ "your registration request will be examined by one of the committee and if you are "
					+ "known your registration will be completed.</p>"
					+ "<p>We are careful with the registration process because as a registered member, after "
					+ "logging in, you will be able to see some information about other members. You are "
					+ "asked below to provide a password. This should not be one you use for services "
					+ "such as on-line banking.</p>"));
		} else {
			panel.add(new HTML("<h2>Update account information</h2>"
					+ "<p>This form allows you to update your information. Just change those fields you want "
					+ "to modify. The password field is shown empty - if it is left empty it will not be changed.</p>"));
		}

		if (op == Op.UPDATE) {
			this.loginService.getUser(Cookies.getCookie(Consts.COOKIE), new AsyncCallback<UserTO>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert(caught.toString());
				}

				@Override
				public void onSuccess(UserTO uto) {
					RegisterPanel.this.uto = uto;
					RegisterPanel.this.displayTable(panel);
				}
			});
		} else {
			this.displayTable(panel);
		}
		this.initWidget(sp);
	}

	void displayTable(VerticalPanel panel) {
		final FlexTable t = new FlexTable();
		panel.add(t);
		int n = 0;

		t.setWidget(n, 0, new Label("e-mail"));
		final TextBox email = new TextBox();
		t.setWidget(n, 1, email);
		email.setTitle("Your preferred e-mail address");
		final int nEmail = n++;
		if (this.uto != null) {
			email.setValue(this.uto.getEmail());
		}

		t.setWidget(n, 0, new Label("login"));
		final TextBox login = new TextBox();
		t.setWidget(n, 1, login);
		login.setTitle("Your login name");
		final int nLogin = n++;
		if (this.uto != null) {
			login.setValue(this.uto.getLogin());
		}

		t.setWidget(n, 0, new Label("Password"));
		final TextBox pwd = new PasswordTextBox();
		t.setWidget(n, 1, pwd);
		pwd.setTitle("This password will be fairly secure but not to on-line banking standards");
		final int nPwd = n++;

		t.setWidget(n, 0, new Label("Password - repeat"));
		final TextBox pwd2 = new PasswordTextBox();
		t.setWidget(n, 1, pwd2);
		pwd2.setTitle("This must match the one above");
		final int nPwd2 = n++;

		t.setWidget(n, 0, new Label("Name"));
		final TextBox name = new TextBox();
		t.setWidget(n, 1, name);
		name.setTitle("Your full name");
		final int nName = n++;
		if (this.uto != null) {
			name.setValue(this.uto.getName());
		}

		t.setWidget(n, 0, new Label("Phone number"));
		final TextBox phone1 = new TextBox();
		t.setWidget(n, 1, phone1);
		phone1.setTitle("Your principal phone number");
		final int nPhone1 = n++;
		if (this.uto != null) {
			phone1.setValue(this.uto.getPhone1());
		}

		t.setWidget(n, 0, new Label("2nd phone number"));
		final TextBox phone2 = new TextBox();
		t.setWidget(n, 1, phone2);
		t.setWidget(n, 2, new Label("(optional)"));
		phone2.setTitle("Optional alternative phone number");
		final int nPhone2 = n++;
		if (this.uto != null) {
			phone2.setValue(this.uto.getPhone2());
		}

		t.setWidget(n, 0, new Label("Assoc Handicap"));
		final TextBox assocHCap = new TextBox();
		t.setWidget(n, 1, assocHCap);
		assocHCap.setTitle("Your assocation handicap - leave blank if you don't have one");
		final int nAssocHCap = n++;
		if (this.uto != null) {
			assocHCap.setValue(this.uto.getAssocHCap());
		}

		t.setWidget(n, 0, new Label("Golf Handicap"));
		final TextBox golfHCap = new TextBox();
		t.setWidget(n, 1, golfHCap);
		golfHCap.setTitle("Your golf handicap - leave blank if you don't have one");
		final int nGolfHCap = n++;
		if (this.uto != null) {
			golfHCap.setValue(this.uto.getGolfHCap());
		}

		t.setWidget(n, 0, new Label("Primary affiliation"));
		final CheckBox main = new CheckBox();
		main.setValue(true);
		t.setWidget(n, 1, main);
		main.setTitle("Please select if this is your only or main croquet club");
		n++;
		if (this.uto != null) {
			main.setValue(this.uto.isMain());
		}
		StringBuilder sb = new StringBuilder();
		if (this.uto != null) {
			if (this.uto.getPriv().equals("SUPER")) {
				sb.append("<li>You have full admin rights.</li>");
			}
			if (this.uto.isScorer()) {
				sb.append("<li>You have scorer rights.</li>");
			}
			if (this.uto.isTreasurer()) {
				sb.append("<li>You have treasurer rights.</li>");
			}
		}
		if (sb.length() > 0) {
			panel.add(new HTML("<h3>Rights associated with the account</h3><ul>" + sb + "</ul>"));
		}
		if (this.uto != null && this.uto.getPaidPence() != null) {
			int pounds = this.uto.getPaidPence() / 100;
			int pence = this.uto.getPaidPence() % 100;
			panel.add(new HTML("<h3>Subscription</h3><p>Subscription of &pound;" + pounds + "." + pence
					+ " was paid on " + this.uto.getPaidDate()));
		}

		Button submit = new Button("Submit");

		t.setWidget(n, 1, submit);

		submit.addClickHandler(new ClickHandler() {

			boolean err;

			private void check(int n, boolean condition, String message) {
				if (!condition) {
					t.setWidget(n, 3, new HTML("<font color=red>" + message + "</font>"));
					this.err = true;
				} else {
					if (t.getCellCount(n) > 3) {
						t.clearCell(n, 3);
					}
				}
			}

			@Override
			public void onClick(ClickEvent event) {
				this.err = false;

				String ts = email.getValue().trim();
				this.check(nEmail, ts.length() >= 7 && ts.contains("@") && ts.contains("."),
						"Please enter you full e-mail address");

				ts = login.getValue().trim();
				this.check(nLogin, !ts.isEmpty(), "Login name cannot be empty");

				if (RegisterPanel.this.uto == null) {
					this.check(nPwd, pwd.getValue().trim().length() >= 6, "Must be at least six characters");
				}
				this.check(nPwd2, pwd.getValue().trim().equals(pwd2.getValue().trim()), "Passwords must match");

				ts = name.getValue().trim();
				this.check(nName, ts.length() >= 7 && ts.contains(" "), "Please enter you full name");

				ts = phone1.getValue().trim();
				this.check(nPhone1, ts.length() >= 10 && ts.matches("^0[ 0-9]*$"),
						"Please enter you principal phone number within the UK starting with 0 and with just digits and spaces");

				ts = phone2.getValue().trim();
				this.check(nPhone2, ts.length() == 0 || ts.matches("^0[0-9]*$"),
						"Please enter you secondary phone number or leave blank");

				ts = assocHCap.getValue().trim();
				this.check(nAssocHCap, ts.length() == 0 || ts.matches("^[0-9\\.-]*$"),
						"Please enter you assocation handicap (e.g -0.5 or leave blank");

				ts = golfHCap.getValue().trim();
				this.check(nGolfHCap, ts.length() == 0 || ts.matches("^[0-9\\.-]*$"),
						"Please enter you golf handicap (e.g 4 or leave blank");

				if (!this.err) {
					String lpwd = pwd.getValue().trim();
					if (RegisterPanel.this.uto == null || !pwd.getValue().trim().isEmpty()) {
						lpwd = Utils.getHash(lpwd);
					}
					UserTO uTO = new UserTO(null, email.getValue().trim().toLowerCase(), login.getValue().trim()
							.toLowerCase(), lpwd, name.getValue().trim(), phone1.getValue().trim(), phone2.getValue()
							.trim(), assocHCap.getValue().trim(), golfHCap.getValue().trim(), main.getValue(), null,
							null, null, null, null);
					if (RegisterPanel.this.uto == null) {
						RegisterPanel.this.loginService.register(uTO, new AsyncCallback<String>() {

							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Failed: " + caught);
							}

							@Override
							public void onSuccess(String result) {
								Window.alert(result);
							}
						});
					} else {
						uTO.setId(uto.getId());
						RegisterPanel.this.loginService.update(Cookies.getCookie(Consts.COOKIE), uTO,
								new AsyncCallback<Void>() {

									@Override
									public void onFailure(Throwable caught) {
										Window.alert("Failed: " + caught);
									}

									@Override
									public void onSuccess(Void result) {
										History.newItem("home");
									}
								});
					}

				}

			}
		});

		panel.add(new HTML(
				"<h3>Technical note about passwords</h3><p>The web site does not use https. An MD5 digest is "
						+ "computed for the id/password and transmitted to, and stored on, the server.</p>"));

	}
}
