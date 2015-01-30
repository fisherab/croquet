package uk.org.harwellcroquet.shared;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class EventTO implements IsSerializable {

	private Long id;

	public Long getKey() {
		return this.id;
	}

	private Integer year;

	public String getName() {
		return this.name;
	}

	public String getType() {
		return this.type;
	}

	public String getFormat() {
		return this.format;
	}

	private String name;
	private String type;
	private String format;
	private Boolean complete;
	private List<EntrantTO> entrants = new ArrayList<EntrantTO>();
	private List<ResultTO> results = new ArrayList<ResultTO>();

	// Needed for RPC stuff
	public EventTO() {
	}

	public EventTO(Long id, Integer year, String name, String type, String format, Boolean complete) {
		this.id = id;
		this.year = year;
		this.name = name;
		this.type = type;
		this.format = format;
		this.complete = complete;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getComplete() {
		return this.complete;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public void setEntrants(List<EntrantTO> entrants) {
		this.entrants = entrants;
	}

	public void setResults(List<ResultTO> results) {
		this.results = results;
	}

	public List<EntrantTO> getEntrants() {
		return entrants;
	}

	public Integer getYear() {
		return year;
	}

	public List<ResultTO> getResults() {
		return results;
	}

	public Boolean isComplete() {
		return complete;
	}

	public void setComplete(Boolean complete) {
		this.complete = complete;
	}

}
