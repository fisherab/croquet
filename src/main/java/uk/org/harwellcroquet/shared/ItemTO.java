package uk.org.harwellcroquet.shared;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/** Item Transfer Object */
public class ItemTO implements IsSerializable {

	private String content;

	private Date date;

	private boolean delete;

	private UserTO userTO;

	private Long id;

	private int seq;

	private String type;

	// Needed for RPC stuff
	public ItemTO() {
	}

	public ItemTO(Long id, UserTO userTO, Date date, String type, int seq, String content) {
		this.id = id;
		this.date = date;
		this.type = type;
		this.content = content;
		this.userTO = userTO;
		this.seq = seq;
	}

	public String getContent() {
		return this.content;
	}

	public Date getDate() {
		return this.date;
	}

	public Long getId() {
		return this.id;
	}

	public int getSeq() {
		return this.seq;
	}

	public String getType() {
		return this.type;
	}

	public boolean isDelete() {
		return this.delete;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setDelete(Boolean delete) {
		this.delete = delete;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public UserTO getUserTO() {
		return userTO;
	}

}
