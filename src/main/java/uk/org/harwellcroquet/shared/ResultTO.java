package uk.org.harwellcroquet.shared;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ResultTO implements IsSerializable {

	private UserTO user1TO;
	private UserTO user2TO;
	private String type;
	private Integer user1Score1;
	private Integer user1Score2;
	private Integer user1Score3;
	private Integer user2Score1;
	private Integer user2Score2;
	private Integer user2Score3;

	public Integer getUser1Score1() {
		return this.user1Score1;
	}

	public Integer getUser1Score2() {
		return this.user1Score2;
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

	private UserTO recorder;

	public UserTO getRecorder() {
		return this.recorder;
	}

	public void setRecorder(UserTO recorder) {
		this.recorder = recorder;
	}

	private Date date;
	private Long id;

	// Needed for RPC stuff
	public ResultTO() {
	}

	public ResultTO(Long id, String type, UserTO user1, Integer user1Score1, Integer user1Score2, Integer user1Score3,
			UserTO user2, Integer user2Score1, Integer user2Score2, Integer user2Score3, UserTO recorder, Date date) {
		this.id = id;
		this.type = type;
		this.user1TO = user1;
		this.user1Score1 = user1Score1;
		this.user1Score2 = user1Score2;
		this.user1Score3 = user1Score3;
		this.user2TO = user2;
		this.user2Score1 = user2Score1;
		this.user2Score2 = user2Score2;
		this.user2Score3 = user2Score3;
		this.recorder = recorder;
		this.date = date;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UserTO getUser1TO() {
		return this.user1TO;
	}

	public void setUser1TO(UserTO user1to) {
		this.user1TO = user1to;
	}

	public UserTO getUser2TO() {
		return this.user2TO;
	}

	public void setUser2TO(UserTO user2to) {
		this.user2TO = user2to;
	}

	public void setType(String type) {
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

	public void setDate(Date date) {
		this.date = date;
	}

	public String getType() {
		return this.type;
	}

	public Date getDate() {
		return this.date;
	}

}
