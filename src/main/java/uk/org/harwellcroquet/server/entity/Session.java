package uk.org.harwellcroquet.server.entity;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import uk.org.harwellcroquet.shared.Consts;

@Entity
@NamedQuery(name = "Session.OLD", query = "SELECT s FROM Session s WHERE s.expireDateTime < :expiry")
public class Session {
	
	public static final String OLD = "Session.OLD";

	// For JPA
	public Session() {
	}

	@Id
	private String sessionid;
	
	@SuppressWarnings("unused")
	@Temporal(TemporalType.TIMESTAMP)
	private Date expireDateTime;

	public Session(User user) {
		this.user = user;
		this.sessionid = UUID.randomUUID().toString();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, Consts.COOKIE_MINUTES);
		this.expireDateTime = cal.getTime();
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	private User user;

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	public String getSessionid() {
		return sessionid;
	}

}
