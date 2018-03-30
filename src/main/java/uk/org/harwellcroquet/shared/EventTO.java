package uk.org.harwellcroquet.shared;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class EventTO implements IsSerializable {

	private Long id;

	private Integer year;

	private String name;

	private String type;

	private String format;

	private int bestOf;

	private UserTO winner;
	private List<EntrantTO> entrants = new ArrayList<EntrantTO>();
	private List<ResultTO> results = new ArrayList<ResultTO>();
	// Needed for RPC stuff
	public EventTO() {
	}

	public EventTO(Long id, Integer year, String name, String type, String format, Integer bestOf, UserTO winner) {
		this.id = id;
		this.year = year;
		this.name = name;
		this.type = type;
		this.format = format;
		this.bestOf = bestOf;
		this.winner = winner;
	}

	public int getBestOf() {
		return bestOf;
	}
	public List<EntrantTO> getEntrants() {
		return entrants;
	}
	public String getFormat() {
		return this.format;
	}

	public Long getId() {
		return this.id;
	}

	public Long getKey() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public List<ResultTO> getResults() {
		return results;
	}

	public String getType() {
		return this.type;
	}

	public UserTO getWinner() {
		return this.winner;
	}

	public Integer getYear() {
		return year;
	}

	public void setBestOf(int bestOf) {
		this.bestOf = bestOf;
	}

	public void setEntrants(List<EntrantTO> entrants) {
		this.entrants = entrants;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setResults(List<ResultTO> results) {
		this.results = results;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setWinner(UserTO winner) {
		this.winner = winner;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	@Override
	public String toString() {
		return "Event " + name + " " + year + " " + entrants + " " + results;
	}
}