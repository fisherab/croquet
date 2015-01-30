package uk.org.harwellcroquet.server.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import uk.org.harwellcroquet.shared.ResultTO;

@NamedQuery(name = "AllResults", query = "SELECT r FROM Result r")
@Entity
public class Result {

	public Integer getUser1Score1() {
		return this.user1Score1;
	}

	public Integer getUser1Score2() {
		return this.user1Score2;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	private Event event;

	public Event getEvent() {
		return this.event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public long getId() {
		return this.id;
	}

	public Integer getUser1Score3() {
		return this.user1Score3;
	}

	public Integer getUser2Score1() {
		return this.user2Score1;
	}

	public Integer getUser2Score2() {
		return this.user2Score2;
	}

	public Integer getUser2Score3() {
		return this.user2Score3;
	}

	public enum Type {
		DRAW, FINAL, PROCESS, NA
	}

	public Result(Type type, User user1, Integer user1Score1, Integer user1Score2, Integer user1Score3, User user2,
			Integer user2Score1, Integer user2Score2, Integer user2Score3, User recorder, Date date) {
		this.type = type;
		this.user1 = user1;
		this.user1Score1 = user1Score1;
		this.user1Score2 = user1Score2;
		this.user1Score3 = user1Score3;
		this.user2 = user2;
		this.user2Score1 = user2Score1;
		this.user2Score2 = user2Score2;
		this.user2Score3 = user2Score3;
		this.recorder = recorder;
		this.date = date;
	}

	// For JPA
	public Result() {
	}

	@Temporal(TemporalType.DATE)
	private Date date;

	@Id
	@GeneratedValue
	private long id;

	private Type type;
	private User recorder;

	private User user1;

	public User getRecorder() {
		return this.recorder;
	}

	public void setRecorder(User recorder) {
		this.recorder = recorder;
	}

	public User getUser1() {
		return this.user1;
	}

	public void setUser1(User user1) {
		this.user1 = user1;
	}

	public User getUser2() {
		return this.user2;
	}

	public void setUser2(User user2) {
		this.user2 = user2;
	}

	private Integer user1Score1;
	private Integer user1Score2;
	private Integer user1Score3;
	private User user2;
	private Integer user2Score1;
	private Integer user2Score2;
	private Integer user2Score3;

	public void setDate(Date date) {
		this.date = date;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setUser1Score1(Integer user1Score1) {
		this.user1Score1 = user1Score1;
	}

	public void setUser1Score2(Integer user1Score2) {
		this.user1Score2 = user1Score2;
	}

	public void setUser1Score3(Integer user1Score3) {
		this.user1Score3 = user1Score3;
	}

	public void setUser2Score1(Integer user2Score1) {
		this.user2Score1 = user2Score1;
	}

	public void setUser2Score2(Integer user2Score2) {
		this.user2Score2 = user2Score2;
	}

	public void setUser2Score3(Integer user2Score3) {
		this.user2Score3 = user2Score3;
	}

	public Result(ResultTO rto) {
		this.type = Type.valueOf(rto.getType());
		this.user1 = new User(rto.getUser1TO());
		this.user1Score1 = rto.getUser1Score1();
		this.user1Score2 = rto.getUser1Score2();
		this.user1Score3 = rto.getUser1Score3();
		this.user2 = new User(rto.getUser2TO());
		this.user2Score1 = rto.getUser2Score1();
		this.user2Score2 = rto.getUser2Score2();
		this.user2Score3 = rto.getUser2Score3();
		this.recorder = new User(rto.getRecorder());
		this.date = rto.getDate();
	}

	public Date getDate() {
		return this.date;
	}

	public Type getType() {
		return this.type;
	}

	public ResultTO getTransferObject() {
		return new ResultTO(id, type.name(), user1.getTransferObject(), user1Score1, user1Score2, user1Score3,
				user2.getTransferObject(), user2Score1, user2Score2, user2Score3, recorder.getTransferObject(), date);
	}

}
