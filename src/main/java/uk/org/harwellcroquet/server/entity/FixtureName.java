package uk.org.harwellcroquet.server.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import uk.org.harwellcroquet.shared.FixtureNameTO;

@Entity
@NamedQueries({ @NamedQuery(name = "FixtureName.All", query = "SELECT f FROM FixtureName f ORDER BY f.name") })
public class FixtureName {

	@Id
	@GeneratedValue
	private Long id;

	public Long getId() {
		return this.id;
	}

	private String name;

	public FixtureName(FixtureNameTO fto) {
		this.id = fto.getKey();
		this.name = fto.getName();
	}

	public FixtureName(Long key, String name) {
		this.id = key;
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public FixtureNameTO getTransferObject() {
		return new FixtureNameTO(this.id, this.name);
	}

	// For JPA
	public FixtureName() {
	}

	public void setName(String name) {
		this.name = name;
	}

}
