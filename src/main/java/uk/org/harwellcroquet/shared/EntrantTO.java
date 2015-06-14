package uk.org.harwellcroquet.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class EntrantTO implements IsSerializable {
	private UserTO userTO;
	private Integer drawPos;

	public Integer getDrawPos() {
		return this.drawPos;
	}

	public Integer getProcessPos() {
		return this.processPos;
	}

	private Integer processPos;

	// Needed for RPC stuff
	public EntrantTO() {
	}
	
	public String toString() {
		return "Entrant: " + userTO;
	}

	public EntrantTO(Long id, UserTO userTO, Integer drawPos, Integer processPos, Integer block) {
		this.id = id;
		this.userTO = userTO;
		this.drawPos = drawPos;
		this.processPos = processPos;
		this.block = block;
	}

	public UserTO getUserTO() {
		return this.userTO;
	}

	public void setUserTO(UserTO userTO) {
		this.userTO = userTO;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Long id;
	private Integer block;

	public Long getId() {
		return id;
	}

	public void setDrawPos(Integer drawPos) {
		this.drawPos = drawPos;
	}

	public void setProcessPos(Integer processPos) {
		this.processPos = processPos;
	}

	public void setBlock(Integer block) {
		this.block = block;
	}

	public Integer getBlock() {
		return block;
	}

}
