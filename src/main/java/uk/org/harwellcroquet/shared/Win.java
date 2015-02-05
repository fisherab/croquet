package uk.org.harwellcroquet.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Win implements IsSerializable {

	private int year;
	private String eventName;
	private String personName;

	// Needed for RPC stuff
	public Win() {
	}

	public Win(int year, String eventName, String personName) {
		this.year = year;
		this.eventName = eventName;
		this.personName = personName;

	}

	public int getYear() {
		return year;
	}

	public String getEventName() {
		return eventName;
	}

	public String getPersonName() {
		return personName;
	}

}
