package uk.org.harwellcroquet.shared;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FixtureTO implements IsSerializable {

	private Date date;

	private boolean delete;

	private Long key;

	private String location;

	private String name;

	private String opponents;

	private String players;

	private String result;

	// Needed for RPC stuff
	public FixtureTO() {
	}

	public FixtureTO(Long key, String name, Date date, String location, String players, String opponents, String result) {
		this.key = key;
		this.name = name;
		this.date = date;
		this.location = location;
		this.players = players;
		this.opponents = opponents;
		this.result = result;
	}

	public Date getDate() {
		return this.date;
	}

	public Long getKey() {
		return this.key;
	}

	public String getLocation() {
		return this.location;
	}

	public String getName() {
		return this.name;
	}

	public String getOpponents() {
		return this.opponents;
	}

	public String getPlayers() {
		return this.players;
	}

	public String getResult() {
		return this.result;
	}

	public boolean isDelete() {
		return this.delete;
	}

	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOpponents(String opponents) {
		this.opponents = opponents;
	}

	public void setPlayers(String players) {
		this.players = players;
	}

	public void setResult(String result) {
		this.result = result;
	}
}
