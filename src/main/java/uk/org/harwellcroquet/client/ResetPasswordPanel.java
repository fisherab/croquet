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

public class ResetPasswordPanel extends Composite {

	interface ResetPasswordUiBinder extends UiBinder<Widget, ResetPasswordPanel> {
	}

	private static ResetPasswordUiBinder uiBinder = GWT.create(ResetPasswordUiBinder.class);

	private final LoginServiceAsync loginService = LoginServiceAsync.Util.getInstance();
	final private static Logger logger = Logger.getLogger(ResetPasswordPanel.class.getName());

	@UiField
	TextBox password;

	@UiField
	TextBox password2;

	private long userId;

	private String hashedPassword;

	@UiHandler("submitButton")
	public void submitButtonClick(ClickEvent e) {
		logger.fine("Set new password for userid " + userId);
		String passwordV = Utils.getHash(password.getValue().trim());
		String password2V = Utils.getHash(password2.getValue().trim());

		if (!passwordV.equals(password2V)) {
			Window.alert("Passwords in the two boxes must be the same");
		} else {
			loginService.resetPassword(userId, hashedPassword, passwordV, new AsyncCallback<UserTO>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Server threw error " + caught.toString());
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
	}

	public ResetPasswordPanel(long userId, String hashedPassword) {
		initWidget(uiBinder.createAndBindUi(this));
		this.userId = userId;
		this.hashedPassword = hashedPassword;
	}

}
