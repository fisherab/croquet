package uk.org.harwellcroquet.server.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import uk.org.harwellcroquet.shared.EntrantTO;
import uk.org.harwellcroquet.shared.EventTO;
import uk.org.harwellcroquet.shared.ResultTO;

@Entity
@NamedQueries({
		@NamedQuery(name = "EventsByYear", query = "SELECT e FROM Event e WHERE e.year = :year ORDER BY e.name"),
		@NamedQuery(name = "AllEvents", query = "SELECT e FROM Event e"),
		@NamedQuery(name = "IncompleteEvents", query = "SELECT e FROM Event e WHERE e.winner IS NULL"),
		@NamedQuery(name = "Event.Year", query = "SELECT e.year FROM Event e"),
		@NamedQuery(name = "NamedEvent", query = "SELECT e FROM Event e WHERE e.year = :year AND e.name = :name"),
		@NamedQuery(name = "RollOfHonour", query = "SELECT e FROM Event e WHERE e.winner IS NOT NULL ORDER BY e.year DESC") })
public class Event {

	public enum Format {
		ALLPLAYALL, DRAWANDPROCESS, TWOBLOCKS, KNOCKOUT
	}

	public enum Type {
		ASSOCIATION, GOLF
	}

	@Id
	@GeneratedValue
	private Long id;

	public Long getId() {
		return this.id;
	}

	public List<Entrant> getEntrants() {
		return this.entrants;
	}

	public void setResults(List<Result> results) {
		this.results = results;
	}

	public Format getFormat() {
		return this.format;
	}

	public Type getType() {
		return this.type;
	}

	public List<Result> getResults() {
		return this.results;
	}

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "event", orphanRemoval = true)
	private List<Entrant> entrants = new ArrayList<Entrant>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "event", orphanRemoval = true)
	private List<Result> results = new ArrayList<Result>();

	private Format format;

	private String name;

	private Type type;

	private int year;

	private User winner;

	public Event(EventTO eto) {
		this.id = eto.getKey();
		this.year = eto.getYear();
		this.name = eto.getName();
		this.type = Type.valueOf(eto.getType());
		this.format = Format.valueOf(eto.getFormat());
		this.winner = eto.getWinner() == null ? null
				: new User(eto.getWinner());
		for (EntrantTO ento : eto.getEntrants()) {
			Entrant e = new Entrant(ento);
			e.setEvent(this);
			this.entrants.add(e);
		}
		for (ResultTO rto : eto.getResults()) {
			Result r = new Result(rto);
			r.setEvent(this);
			this.results.add(r);
		}
	}

	public String getName() {
		return this.name;
	}

	public EventTO getTransferObject() {
		EventTO eto = new EventTO(this.id, this.year, this.name,
				this.type.name(), this.format.name(),
				this.winner == null ? null : this.winner.getTransferObject());
		for (Entrant entrant : entrants) {
			eto.getEntrants().add(entrant.getTransferObject());
		}
		for (Result result : results) {
			eto.getResults().add(result.getTransferObject());

		}
		return eto;
	}

	public int getYear() {
		return this.year;
	}

	// For JPA
	public Event() {
	}

	public User getWinner() {
		return winner;
	}

	public void setWinner(User winner) {
		this.winner = winner;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

}
