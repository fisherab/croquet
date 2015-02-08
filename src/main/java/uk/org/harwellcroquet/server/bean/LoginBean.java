package uk.org.harwellcroquet.server.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import uk.org.harwellcroquet.server.entity.Session;
import uk.org.harwellcroquet.server.entity.Status;
import uk.org.harwellcroquet.server.entity.User;
import uk.org.harwellcroquet.server.entity.User.Priv;
import uk.org.harwellcroquet.shared.AuthException;
import uk.org.harwellcroquet.shared.BadInputException;
import uk.org.harwellcroquet.shared.InternalException;
import uk.org.harwellcroquet.shared.UserTO;
import uk.org.harwellcroquet.shared.Utils;

@Stateless
public class LoginBean {

	@PersistenceContext(unitName = "croquet")
	private EntityManager entityManager;

	private String verifyUrl;

	private Properties mailProps;

	private String mailUserName;

	private String mailPassword;

	private String mailStmpHost;

	private String mailUserDesc;

	final static Logger logger = Logger.getLogger(LoginBean.class);

	final private static long statusKey = 1L;

	public static User getUser(EntityManager entityManager, String sessionid)
			throws AuthException {
		if (sessionid == null) {
			throw new AuthException(
					"Please log in again - your session has expired");
		}
		Session session = entityManager.find(Session.class, sessionid);
		if (session == null) {
			throw new AuthException(
					"Please log in again - your session has expired");
		}
		User u = session.getUser();
		logger.debug("Access by " + u.getName() + " requested");
		return u;

	}

	static User getUserTolerant(EntityManager entityManager, String sessionid)
			throws AuthException {
		if (sessionid == null) {
			logger.debug("Access by ?? - no session");
			return null;
		}
		Session session = entityManager.find(Session.class, sessionid);
		if (session == null) {
			logger.debug("Access by " + sessionid + " session not found");
			return null;
		}
		User u = session.getUser();
		logger.debug("Access by " + u.getName() + " requested");
		return u;
	}

	public UserTO getUser(String sessionid) throws InternalException,
			AuthException {
		logger.info("Finding current user from LoginBean");
		User u = LoginBean.getUserTolerant(entityManager, sessionid);

		if (u == null) {
			return null;
		}

		UserTO itto = u.getTransferObject();
		Status status = entityManager.find(Status.class, statusKey);
		if (!status.isOnline() && u.getPriv() != User.Priv.SUPER) {
			throw new AuthException("Sorry the site is currently offline");
		}
		itto.setOnline(status.isOnline());
		logger.info("Current user is " + u.getName());
		return itto;
	}

	public List<UserTO> getUsers(String sessionid, String queryString)
			throws AuthException {
		User ui = LoginBean.getUserTolerant(entityManager, sessionid);
		List<UserTO> ittos = new ArrayList<UserTO>();
		TypedQuery<User> query = entityManager.createNamedQuery(queryString,
				User.class);
		for (User u : query.getResultList()) {
			UserTO itto = null;
			if (ui == null) {
				itto = new UserTO();
				itto.setId(u.getId());
				itto.setName(u.getName());
			} else {
				itto = u.getTransferObject();
				if (ui.getPriv() != User.Priv.SUPER && !ui.isTreasurer()) {
					itto.setPaidDate(null);
					itto.setPaidPence(null);
				}
			}
			ittos.add(itto);
		}

		logger.info("LoginBean.getUsers found " + ittos.size() + " users");
		return ittos;
	}

	@PostConstruct
	private void init() {
		try {
			Properties props = new Properties();
			InputStream inStream = new FileInputStream(new File(
					"croquet.properties"));
			props.load(inStream);

			verifyUrl = getStringProperty(props, "verifyUrl");
			mailStmpHost = getStringProperty(props, "mail.smtp.host");
			mailUserDesc = getStringProperty(props, "mail.userDesc");

			mailProps = new Properties();
			mailProps.put("mail.smtp.host", mailStmpHost);
			mailProps.put("mail.smtp.socketFactory.port", "587");
			mailProps.put("mail.smtp.auth", "true");
			mailProps.put("mail.smtp.port", "587");
			mailProps.put("mail.smtp.starttls.enable", "true");
			mailProps.put("mail.smtp.ssl.trust", "*");

			mailUserName = getStringProperty(props, "mail.userName");
			mailPassword = getStringProperty(props, "mail.password");

			logger.info("Completed init of LoginBean");
		} catch (Exception e) {
			throw new RuntimeException(e.getClass() + " " + e.getMessage());
		}
	}

	private String getStringProperty(Properties props, String key)
			throws UnavailableException {
		String value = props.getProperty(key);
		if (value == null) {
			throw new UnavailableException(key + " property must be set");
		}
		return value;
	}

	public UserTO login(String login, String pwd) throws AuthException {
		logger.info("LoginBean.login request for " + login);
		TypedQuery<User> query = entityManager.createNamedQuery(
				"UserByLoginAndPwd", User.class);
		query.setParameter("login", login);
		query.setParameter("pwd", pwd);
		List<User> us = query.getResultList();
		if (us.size() != 1) {
			throw new AuthException("Login name and password don't match");
		}
		User u = us.get(0);
		Priv priv = u.getPriv();
		if (priv == User.Priv.NEW) {
			throw new AuthException("This account needs e-mail verification");
		}
		if (priv == User.Priv.EMAIL_OK) {
			throw new AuthException(
					"This account is waiting for a human to approve it");
		}
		if (priv == User.Priv.EX) {
			throw new AuthException("This account is not currently active");
		}
		Status status = entityManager.find(Status.class, statusKey);
		if (!status.isOnline() && u.getPriv() != User.Priv.SUPER) {
			throw new AuthException("Sorry the site is currently offline");
		}
		Session session = new Session(u);
		entityManager.persist(session);
		UserTO itto = u.getTransferObject();
		itto.setOnline(status.isOnline());
		String sessionid = session.getSessionid();
		itto.setSessionid(sessionid);
		logger.info(login + " has logged in with sessionid " + sessionid);
		return itto;
	}

	public void logout(String sessionid) {
		logger.info("LoginBean.logout request");

		if (sessionid == null) {
			return;
		}
		Session session = entityManager.find(Session.class, sessionid);
		if (session == null) {
			return;
		} else {
			entityManager.remove(session);
			logger.info(session.getUser().getName() + " has logged out");
		}
	}

	public String register(HttpServletRequest req, UserTO uTO)
			throws InternalException, AuthException {
		logger.info("Request registration of " + uTO.getName() + " as "
				+ uTO.getEmail());

		User u = new User(uTO);

		/* If there are no admins accept registration immediately */
		String result = "Please check your e-mail and follow the instructions";

		if (entityManager.createNamedQuery("AdminUsers").getResultList().size() == 0) {
			u.setPriv(User.Priv.SUPER);
			result = "You may now login";

			Status s = new Status();
			s.setId(statusKey);
			s.setOnline(false);
			entityManager.persist(s);
		}
		Query query = entityManager.createNamedQuery("UserByLogin")
				.setParameter("login", u.getLogin());
		if (!query.getResultList().isEmpty()) {
			throw new AuthException("The login name " + u.getLogin()
					+ " is already in use");
		}
		entityManager.persist(u);

		if (u.getPriv() != User.Priv.SUPER) {

			StringBuffer url = new StringBuffer(verifyUrl);

			url.append("?key=").append(u.getId());
			url.append("&string=").append(Utils.getHash(u.getPwd()));
			url.append("&action=register");

			String msgBody = "You, or somebody pretending to be you, has signed up for the "
					+ "Harwell Croquet Club web site. If it is you, then please click on the following "
					+ "link to alert a human to approve your application. Humans are not as "
					+ "quick as computers so please be patient.\n\n"
					+ url
					+ "\n\n"
					+ "If it is not you then you need do nothing."
					+ "\n\nHarwell Croquet Web Admin.";

			String subject = "Harwell Croquet Club website account creation";

			sendMail(u.getEmail(), u.getName(), subject, msgBody);

		}
		return result;

	}

	private void sendMail(String email, String name, String subject,
			String msgBody) throws InternalException {

		javax.mail.Session session = javax.mail.Session.getInstance(mailProps,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(mailUserName,
								mailPassword);
					}
				});

		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(mailUserName + "@" + mailStmpHost,
					mailUserDesc));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
					email, name));
			msg.setSubject(subject);
			msg.setText(msgBody);
			Transport.send(msg);
			logger.info("Message sent");

		} catch (Exception e) {
			logger.error(e);
			throw new InternalException(e.getMessage());
		}

	}

	public void setOnline(String sessionid, boolean b) throws AuthException,
			InternalException {
		logger.info("LoginBean.setOnline request");
		User ui = LoginBean.getUser(entityManager, sessionid);
		if (ui.getPriv() != User.Priv.SUPER) {
			throw new AuthException("You must be admin to make this call");
		}
		Status status = entityManager.find(Status.class, statusKey);
		status.setOnline(b);
		logger.info("LoginBean.setOnline done");
	}

	public void update(String sessionid, Set<UserTO> modified)
			throws InternalException, AuthException {
		logger.debug("LoginBean.update " + modified.size() + " items");
		User ui = LoginBean.getUser(entityManager, sessionid);
		boolean supeR = ui.getPriv() == User.Priv.SUPER;
		if (!supeR && !ui.isTreasurer()) {
			throw new AuthException(
					"You must be admin or treasurer to make this call");
		}
		for (UserTO to : modified) {
			User usero = entityManager.find(User.class, to.getId());
			if (to.isDelete()) {
				if (supeR) {
					entityManager.remove(usero);
				}
			} else {
				if (supeR) {
					usero.setEmail(to.getEmail());
					usero.setName(to.getName());
					usero.setPhone1(to.getPhone1());
					usero.setPhone2(to.getPhone2());
					usero.setAssocHCap(to.getAssocHCap());
					usero.setGolfHCap(to.getGolfHCap());
					usero.setMain(to.isMain());
					usero.setScorer(to.isScorer());
					usero.setTreasurer(to.isTreasurer());
					usero.setPriv(User.Priv.valueOf(to.getPriv()));
				}
				usero.setPaidDate(to.getPaidDate());
				usero.setPaidPence(to.getPaidPence());
			}
			logger.debug("LoginBean.update completed");
		}
	}

	public void update(String sessionid, UserTO to) throws InternalException,
			AuthException {
		logger.debug("LoginBean.update " + to.getName());
		User ui = LoginBean.getUser(entityManager, sessionid);
		long uid = ui.getId().longValue();
		if (uid != to.getId().longValue()) {
			throw new AuthException(
					"You may only modify information about your own account");
		}
		User usero = entityManager.find(User.class, to.getId());
		if (to.isDelete()) {
			entityManager.remove(usero);
		} else {

			List<User> users = entityManager
					.createNamedQuery("UserByLogin", User.class)
					.setParameter("login", to.getLogin()).getResultList();
			if (!users.isEmpty()) {
				if (uid != users.get(0).getId().longValue()) {
					throw new AuthException("The login name " + to.getLogin()
							+ " is already in use");
				}
			}

			usero.setEmail(to.getEmail());
			usero.setLogin(to.getLogin());
			String newPwd = to.getPwd();
			if (!newPwd.isEmpty()) {
				usero.setPwd(to.getPwd());
			}
			usero.setName(to.getName());
			usero.setPhone1(to.getPhone1());
			usero.setPhone2(to.getPhone2());
			usero.setAssocHCap(to.getAssocHCap());
			usero.setGolfHCap(to.getGolfHCap());
			usero.setMain(to.isMain());
		}
		logger.debug("LoginBean.updated " + to.getName());
	}

	public void registerSkeletonUser(String sessionid) throws AuthException,
			InternalException {
		logger.info("Request new skeleton user");
		User ui = LoginBean.getUser(entityManager, sessionid);
		if (ui.getPriv() != User.Priv.SUPER) {
			throw new AuthException("You must be admin to make this call");
		}
		User u = new User();
		entityManager.persist(u);
		logger.info("New skeleton user created");
	}

	@Schedule(minute = "*/30", hour = "*")
	private void cleanup() {
		logger.info("Look to clean up old sessions");
		for (Session session : entityManager
				.createNamedQuery(Session.OLD, Session.class)
				.setParameter("expiry", new Date()).getResultList()) {
			logger.info("Cleaning out session for "
					+ session.getUser().getName());
			entityManager.remove(session);
		}

	}

	public void recover(HttpServletRequest req, String login)
			throws BadInputException, InternalException {
		logger.info("Try recovering login for " + login);
		List<User> results = entityManager
				.createNamedQuery("UserByLogin", User.class)
				.setParameter("login", login).getResultList();

		if (results.isEmpty()) {
			throw new BadInputException("The login name " + login
					+ " is not registered");
		}

		User u = results.get(0);
		StringBuffer url = new StringBuffer(verifyUrl);

		url.append("?key=").append(u.getId());
		url.append("&string=").append(Utils.getHash(u.getPwd()));
		url.append("&action=recover");

		String msgBody = "You, or somebody pretending to be you, has requested password reset for the "
				+ "Harwell Croquet Web site. If it is you, then please click on the following "
				+ "link to verify that it is you. You will be taken to the web page where you can change your password.\n\n"
				+ url
				+ "\n\n"
				+ "If it is not you then you need do nothing."
				+ "\n\nHarwell Croquet Club web Admin.";

		String subject = "Harwell Croquet Club website password reset";

		sendMail(u.getEmail(), u.getName(), subject, msgBody);
	}

	public UserTO resetPassword(long userId, String hashedPassword,
			String newHashedPassword) throws AuthException {

		logger.info("About to change password for user " + userId);
		User u = entityManager.find(User.class, userId);
		if (u == null) {
			throw new AuthException("User with id " + userId + " is not found");
		}
		if (!u.getPwd().equals(hashedPassword)) {
			throw new AuthException("User with id " + userId
					+ " does not have the specified password");
		}

		Status status = entityManager.find(Status.class, statusKey);
		if (!status.isOnline() && u.getPriv() != User.Priv.SUPER) {
			throw new AuthException("Sorry the site is currently offline");
		}

		u.setPwd(newHashedPassword);
		Session session = new Session(u);
		entityManager.persist(session);
		UserTO uto = u.getTransferObject();
		uto.setOnline(status.isOnline());
		String sessionid = session.getSessionid();
		uto.setSessionid(sessionid);
		logger.info("New password set for user " + u.getName());
		return uto;
	}

}
