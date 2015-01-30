package uk.org.harwellcroquet.client.event;

import uk.org.harwellcroquet.shared.UserTO;

import com.google.gwt.event.shared.GwtEvent;

public class LoginEvent extends GwtEvent<LoginEventHandler> {
	public static Type<LoginEventHandler> TYPE = new Type<LoginEventHandler>();
	private UserTO uto;
	
	public LoginEvent(UserTO uto) {
		this.uto = uto;
	}
	
	public UserTO getUserTO() {
		return uto;
	}
	
	@Override
	public Type<LoginEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LoginEventHandler handler) {
		handler.onLogin(this);
	}

}
