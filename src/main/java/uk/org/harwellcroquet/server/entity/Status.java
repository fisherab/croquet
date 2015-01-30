package uk.org.harwellcroquet.server.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Status {

	@Id
	private Long id;

	// For JPA
	public Status() {
	}

	private Boolean online;

	public Long getId() {
		return this.id;
	}

	public Boolean isOnline() {
		return this.online;
	}

	public void setOnline(Boolean online) {
		this.online = online;
	}

	public void setId(long id) {
		this.id = id;
	}

}
