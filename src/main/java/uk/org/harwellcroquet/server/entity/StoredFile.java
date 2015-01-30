package uk.org.harwellcroquet.server.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import uk.org.harwellcroquet.shared.StoredFileTO;

@Entity
@NamedQueries({
		@NamedQuery(name = StoredFile.LIKE, query = "SELECT f FROM StoredFile f WHERE f.name LIKE :name ORDER BY f.name"),
		@NamedQuery(name = StoredFile.ALL, query = "SELECT f FROM StoredFile f"),
		@NamedQuery(name = StoredFile.GET, query = "SELECT f FROM StoredFile f WHERE f.name = :name") })
public class StoredFile {

	public static final String GET = "StoredFile.GET";
	public static final String ALL = "StoredFile.ALL";
	public static final String LIKE = "StoredFile.LIKE";

	@Id
	@GeneratedValue
	private Long id;

	private String name;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private User user;

	public StoredFile(String name, User user) {
		this.name = name;
		this.user = user;
	}

	public String getName() {
		return this.name;
	}

	// For JPA
	public StoredFile() {
	}

	public void setName(String name) {
		this.name = name;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public StoredFileTO getTransferObject() {
		return new StoredFileTO(id, name, user.getTransferObject());
	}

}
