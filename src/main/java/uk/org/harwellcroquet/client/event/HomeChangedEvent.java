package uk.org.harwellcroquet.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class HomeChangedEvent extends GwtEvent<HomeChangedEventHandler> {
	public static Type<HomeChangedEventHandler> TYPE = new Type<HomeChangedEventHandler>();

	@Override
	public Type<HomeChangedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(HomeChangedEventHandler handler) {
		handler.onChange(this);
	}

}
