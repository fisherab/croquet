package uk.org.harwellcroquet.server.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;

import uk.org.harwellcroquet.shared.EntrantTO;

@NamedQuery(name = "AllEntrants", query = "SELECT e FROM Entrant e")
@Entity
public class Entrant {

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	private Event event;
	private Integer block;

	public Event getEvent() {
		return this.event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public Entrant(EntrantTO ento) {
		id = ento.getId();
		user = new User(ento.getUserTO());
		drawPos = ento.getDrawPos();
		processPos = ento.getProcessPos();
		block = ento.getBlock();
	}

	public User getUser() {
		return this.user;
	}

	public Integer getDrawPos() {
		return this.drawPos;
	}

	public Integer getProcessPos() {
		return this.processPos;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setDrawPos(Integer drawPos) {
		this.drawPos = drawPos;
	}

	public void setProcessPos(Integer processPos) {
		this.processPos = processPos;
	}

	@Id
	@GeneratedValue
	private Long id;

	// For JPA
	public Entrant() {
	}

	private User user;
	private Integer drawPos;
	private Integer processPos;

	public EntrantTO getTransferObject() {
		return new EntrantTO(id, user.getTransferObject(), drawPos, processPos, block);
	}

}
