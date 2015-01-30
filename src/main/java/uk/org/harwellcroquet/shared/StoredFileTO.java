package uk.org.harwellcroquet.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/** File Transfer Object */
public class StoredFileTO implements IsSerializable {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private Boolean delete;
	private UserTO user;
	private Long id;

	// Needed for RPC stuff
	public StoredFileTO() {
	}

	public StoredFileTO(Long id, String name, UserTO user) {
		this.id = id;
		this.name = name;
		this.user = user;
		this.delete = false;
	}

	public UserTO getUser() {
		return this.user;
	}

	public void setUser(UserTO user) {
		this.user = user;
	}

	public Boolean getDelete() {
		return this.delete;
	}

	public Boolean isDelete() {
		return delete;
	}

	public void setDelete(Boolean delete) {
		this.delete = delete;

	}

	public Long getId() {
		return this.id;
	}

}
