package uk.org.harwellcroquet.server.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import uk.org.harwellcroquet.shared.UserTO;

@Entity
@NamedQueries({
		@NamedQuery(name = "UserByLogin", query = "SELECT u FROM User u WHERE u.login = :login"),
		@NamedQuery(name = "UserByLoginAndPwd", query = "SELECT u FROM User u WHERE u.login = :login AND u.pwd =:pwd"),
		@NamedQuery(name = "AdminUsers", query = "SELECT u FROM User u WHERE u.priv = uk.org.harwellcroquet.server.entity.User$Priv.SUPER"),
		@NamedQuery(name = "AllCurrentUsers", query = "SELECT u FROM User u WHERE u.priv = uk.org.harwellcroquet.server.entity.User$Priv.NONE OR u.priv = uk.org.harwellcroquet.server.entity.User$Priv.SUPER order by u.name"),
		@NamedQuery(name = "AllUsers", query = "SELECT u FROM User u order by u.name") })
public class User {

	public enum Priv {
		EMAIL_OK, NEW, NONE, SUPER, EX
	};
	
	private String login;

	private String assocHCap;

	private String email;

	private String golfHCap;

	@Id
	@GeneratedValue
	private Long id;

	private Boolean main;

	private String name;

	@Temporal(TemporalType.DATE)
	private Date paidDate;

	private Integer paidPence;

	private String phone1;

	private String phone2;

	private Priv priv;

	private String pwd;

	private Boolean scorer;

	private Boolean treasurer;

	public User(UserTO uTO) {
		this.id = uTO.getId();
		this.name = uTO.getName();
		this.login = uTO.getLogin();
		this.pwd = uTO.getPwd();
		this.email = uTO.getEmail();
		this.phone1 = uTO.getPhone1();
		this.phone2 = uTO.getPhone2();
		this.assocHCap = uTO.getAssocHCap();
		this.golfHCap = uTO.getGolfHCap();
		this.priv = User.Priv.NEW;
		this.main = uTO.isMain();
		this.paidDate = new Date();
		this.paidPence = 0;
		this.treasurer = false;
		this.scorer = false;
	}

	// For JPA
	public User() {
		this.paidDate = new Date();
		this.priv = Priv.EX;
		this.main = false;
		this.paidPence = 0;
		this.treasurer = false;
		this.scorer = false;
	}

	public String getAssocHCap() {
		return this.assocHCap;
	}

	public String getEmail() {
		return this.email;
	}

	public String getGolfHCap() {
		return this.golfHCap;
	}

	public Long getId() {
		return this.id;
	}

	public Boolean isMain() {
		return this.main;
	}

	public String getName() {
		return this.name;
	}

	public Date getPaidDate() {
		return this.paidDate;
	}

	public Integer getPaidPence() {
		return this.paidPence;
	}

	public String getPhone1() {
		return this.phone1;
	}

	public String getPhone2() {
		return this.phone2;
	}

	public Priv getPriv() {
		return this.priv;
	}

	public String getPwd() {
		return this.pwd;
	}

	public Boolean isScorer() {
		return this.scorer;
	}

	public Boolean isTreasurer() {
		return this.treasurer;
	}

	public void setAssocHCap(String assocHCap) {
		this.assocHCap = assocHCap;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setGolfHCap(String golfHCap) {
		this.golfHCap = golfHCap;
	}

	public void setId(Long key) {
		this.id = key;
	}

	public void setMain(Boolean main) {
		this.main = main;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
	}

	public void setPaidPence(Integer paidPence) {
		this.paidPence = paidPence;
	}

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	public void setPriv(Priv priv) {
		this.priv = priv;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public void setScorer(Boolean scorer) {
		this.scorer = scorer;
	}

	public void setTreasurer(Boolean treasurer) {
		this.treasurer = treasurer;
	}

	public UserTO getTransferObject() {
		return new UserTO(id, email, login, pwd, name, phone1, phone2, assocHCap, golfHCap, main, scorer, treasurer,
				priv.name(), paidDate, paidPence);
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

}
