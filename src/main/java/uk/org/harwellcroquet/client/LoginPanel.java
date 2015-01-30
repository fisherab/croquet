package uk.org.harwellcroquet.client;

import java.util.Date;
import java.util.logging.Logger;

import uk.org.harwellcroquet.client.event.HomeChangedEvent;
import uk.org.harwellcroquet.client.event.LoginEvent;
import uk.org.harwellcroquet.client.service.LoginServiceAsync;
import uk.org.harwellcroquet.shared.Consts;
import uk.org.harwellcroquet.shared.UserTO;
import uk.org.harwellcroquet.shared.Utils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class LoginPanel extends Composite {

	interface LoginPanelUiBinder extends UiBinder<Widget, LoginPanel> {
	}

	private static LoginPanelUiBinder uiBinder = GWT.create(LoginPanelUiBinder.class);

	private final LoginServiceAsync loginService = LoginServiceAsync.Util.getInstance();
	final private static Logger logger = Logger.getLogger(LoginPanel.class.getName());

	@UiField
	TextBox email;

	@UiField
	TextBox forgotEmail;

	@UiField
	TextBox password;

	@UiHandler("loginButton")
	public void loginButtonClick(ClickEvent e) {
		String emailV = email.getValue().trim().toLowerCase();
		String pwdV = Utils.getHash(password.getValue().trim());

		logger.fine("Try logging in with " + emailV + " / " + pwdV);
		loginService.login(emailV, pwdV, new AsyncCallback<UserTO>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(UserTO uto) {
				TheEventBus.getInstance().fireEvent(new LoginEvent(uto));
				TheEventBus.getInstance().fireEvent(new HomeChangedEvent());
				History.newItem("home");
				Date now = new Date();
				Date expire = new Date(now.getTime() + Consts.COOKIE_MINUTES * 60000);
				Cookies.setCookie(Consts.COOKIE, uto.getSessionid(), expire);
				logger.fine("Cookie " + Consts.COOKIE + " set to " + Cookies.getCookie(Consts.COOKIE));
			}
		});
	}

	@UiHandler("forgotButton")
	public void forgotButtonClick(ClickEvent event) {
		String emailV = forgotEmail.getValue().trim().toLowerCase();

		logger.fine("Try recovering password for " + emailV);
		loginService.recover(emailV, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				Window.alert("You will be sent an email. Please follow the instructions there.");
			}

		});

	}

	public LoginPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
