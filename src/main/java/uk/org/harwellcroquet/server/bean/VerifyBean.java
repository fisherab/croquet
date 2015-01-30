package uk.org.harwellcroquet.server.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import uk.org.harwellcroquet.server.entity.User;
import uk.org.harwellcroquet.shared.Utils;

@Stateless
public class VerifyBean {

	private String resetUrl;

	@PostConstruct
	private void init() {
		try {
			Properties props = new Properties();
			InputStream inStream = new FileInputStream(new File("croquet.properties"));
			props.load(inStream);
			resetUrl = props.getProperty("resetUrl");
			if (resetUrl == null) {
				throw new UnavailableException("resetUrl property must be set");
			}
			logger.info("Completed init of VerifyBean with resetUrl " + resetUrl);
		} catch (Exception e) {
			throw new RuntimeException(e.getClass() + " " + e.getMessage());
		}
	}

	@PersistenceContext(unitName = "croquet")
	private EntityManager em;

	final static Logger logger = Logger.getLogger(VerifyBean.class);

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		logger.debug("Verification query string is " + req.getQueryString());
		@SuppressWarnings("unchecked")
		Map<String, String[]> parms = req.getParameterMap();
		String[] keys = parms.get("key");
		String[] strings = parms.get("string");
		String[] actions = parms.get("action");
		if (keys == null || strings == null || actions == null) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"URL has bad query string  - needs key, string and action");
			return;
		}
		try {
			User u = em.find(User.class, Long.parseLong(keys[0]));
			if (!Utils.getHash(u.getPwd()).equals(strings[0])) {
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"URL has bad query string - key value not recognised");
				return;
			}
			if (actions[0].equals("register")) {
				if (u.getPriv() != User.Priv.NEW) {
					resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
							"This is no longer a new account - maybe you have tried to verify it twice?");
					return;
				}
				u.setPriv(User.Priv.EMAIL_OK);
			} else if (actions[0].equals("recover")) {
				resp.sendRedirect(resetUrl + "/" + keys[0] + "/" + u.getPwd());
			} else {
				logger.debug("redirecting");
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "action " + actions[0]
						+ " is not recognised");
				return;
			}
			logger.debug("Verification succesful");
		} catch (Exception e) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Account seems not to exist");
			return;
		}
		PrintWriter writer = resp.getWriter();
		writer.println("Verification succesful - now you must wait for a human to check");
		writer.close();
	}

}
