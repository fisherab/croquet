package uk.org.harwellcroquet.shared;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UserTO implements IsSerializable {

	private String assocHCap;

	private String email;

	private String golfHCap;

	private Boolean main;

	private String name;

	private String phone1;

	private String phone2;

	private String pwd;

	private Boolean online;

	public void setDelete(boolean delete) {
		this.delete = delete;
	}

	private boolean delete;

	public void setScorer(Boolean scorer) {
		this.scorer = scorer;
	}

	public void setTreasurer(Boolean treasurer) {
		this.treasurer = treasurer;
	}

	public void setPriv(String priv) {
		this.priv = priv;
	}

	private Boolean scorer;
	private Boolean treasurer;
	private String priv;

	public Date getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
	}

	public Integer getPaidPence() {
		return paidPence;
	}

	public void setPaidPence(Integer paidPence) {
		this.paidPence = paidPence;
	}

	private Date paidDate;
	private Integer paidPence;

	private Long id;

	private String sessionid;

	public String getSessionid() {
		return this.sessionid;
	}

	// Needed for RPC stuff
	public UserTO() {
	}

	public UserTO(Long id, String email, String pwd, String name, String phone1, String phone2, String assocHCap,
			String golfHCap, Boolean main, Boolean scorer, Boolean treasurer, String priv, Date paidDate,
			Integer paidPence) {
		this.id = id;
		this.email = email;
		this.pwd = pwd;
		this.name = name;
		this.phone1 = phone1;
		this.phone2 = phone2;
		this.assocHCap = assocHCap;
		this.golfHCap = golfHCap;
		this.main = main;
		this.paidDate = new Date();
		this.scorer = scorer;
		this.treasurer = treasurer;
		this.priv = priv;
		this.paidDate = paidDate;
		this.paidPence = paidPence;
	}

	public Boolean getMain() {
		return this.main;
	}

	public void setMain(Boolean main) {
		this.main = main;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getOnline() {
		return this.online;
	}

	public Boolean getScorer() {
		return this.scorer;
	}

	public Boolean getTreasurer() {
		return this.treasurer;
	}

	public boolean isDelete() {
		return this.delete;
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

	public String getName() {
		return this.name;
	}

	public String getPhone1() {
		return this.phone1;
	}

	public String getPhone2() {
		return this.phone2;
	}

	public String getPwd() {
		return this.pwd;
	}

	public Boolean isMain() {
		return this.main;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	public void setAssocHCap(String assocHCap) {
		this.assocHCap = assocHCap;

	}

	public void setGolfHCap(String golfHCap) {
		this.golfHCap = golfHCap;

	}

	public void setMain(boolean main) {
		this.main = main;
	}

	public Boolean isScorer() {
		return this.scorer;
	}

	public Boolean isTreasurer() {
		return this.treasurer;
	}

	public String getPriv() {
		return this.priv;
	}

	public void setOnline(Boolean online) {
		this.online = online;
	}

	public Boolean isOnline() {
		return online;
	}

	public void setEmail(String email) {
		this.email = email;

	}

	public String toString() {
		return "User id " + id + " -> " + email + " " + name;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || o.getClass() != UserTO.class) {
			return false;
		}
		UserTO uo = (UserTO) o;
		return id == uo.id || (id != null && id.equals(uo.id));
	}

	@Override
	public int hashCode() {
		if (id != null) {
			return (int) (id % Integer.MAX_VALUE);
		} else {
			return 0;
		}
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

}
