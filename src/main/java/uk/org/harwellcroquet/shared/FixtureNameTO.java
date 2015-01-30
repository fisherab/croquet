package uk.org.harwellcroquet.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FixtureNameTO implements IsSerializable {

	public Long getKey() {
		return this.key;
	}

	public String getName() {
		return this.name;
	}

	private Long key;

	private String name;

	// Needed for RPC stuff
	public FixtureNameTO() {
	}

	public FixtureNameTO(Long key, String name) {
		this.key = key;
		this.name = name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
