package uk.org.harwellcroquet.server.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import uk.org.harwellcroquet.shared.FixtureTO;

@Entity
@NamedQueries({
		@NamedQuery(name = "Fixture.ByYear", query = "SELECT f FROM Fixture f WHERE f.date > :date1 AND f.date < :date2 ORDER BY f.date, f.name"),
		@NamedQuery(name = "Fixture.NameByYear", query = "SELECT f.name FROM Fixture f WHERE f.date > :date1 AND f.date < :date2 ORDER BY f.date, f.name"),
		@NamedQuery(name = "Fixture.Date", query = "SELECT f.date FROM Fixture f"),
		@NamedQuery(name = "Fixture.NamedEvent", query = "SELECT f FROM Fixture f WHERE f.date > :date1 AND f.date < :date2 AND f.name = :name ORDER BY f.date") })
public class Fixture {

	@Id
	@GeneratedValue
	private Long id;

	public Long getId() {
		return this.id;
	}

	private String name;

	@Temporal(TemporalType.TIMESTAMP)
	private Date date;
	private String location;
	private String players;
	private String opponents;
	private String result;

	public Fixture(FixtureTO fto) {
		this.id = fto.getKey();
		this.name = fto.getName();
		this.date = fto.getDate();
		this.location = fto.getLocation();
		this.players = fto.getPlayers();
		this.opponents = fto.getOpponents();
		this.result = fto.getResult();
	}

	public Fixture(Long key, String name, Date date, String location, String players,
			String opponents, String result) {
		this.id = key;
		this.name = name;
		this.date = date;
		this.location = location;
		this.players = players;
		this.opponents = opponents;
		this.result = result;
	}

	public String getName() {
		return this.name;
	}

	// For JPA
	public Fixture() {
	}

	public FixtureTO getTransferObject() {
		return new FixtureTO(this.id, this.name, this.date, this.location, this.players,
				this.opponents, this.result);
	}

}
