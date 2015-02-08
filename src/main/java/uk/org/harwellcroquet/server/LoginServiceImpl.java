package uk.org.harwellcroquet.server;

import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;

import uk.org.harwellcroquet.client.service.LoginService;
import uk.org.harwellcroquet.server.bean.LoginBean;
import uk.org.harwellcroquet.shared.AuthException;
import uk.org.harwellcroquet.shared.BadInputException;
import uk.org.harwellcroquet.shared.InternalException;
import uk.org.harwellcroquet.shared.UserTO;

@SuppressWarnings("serial")
public class LoginServiceImpl extends ContextRemoteServiceServlet implements LoginService {

	@EJB
	private LoginBean loginBean;

	@Override
	public UserTO login(String login, String password) throws AuthException {
		return loginBean.login(login, password);
	}

	@Override
	public void logout(String sessionid) {
		loginBean.logout(sessionid);
	}

	@Override
	public String register(UserTO uTO) throws InternalException, AuthException {
		HttpServletRequest req = this.getThreadLocalRequest();
		return loginBean.register(req, uTO);
	}

	@Override
	public void update(String sessionid, Set<UserTO> modified) throws InternalException,
			AuthException {
		loginBean.update(sessionid, modified);
	}

	@Override
	public List<UserTO> getUsers(String sessionid, String query) throws AuthException {
		return loginBean.getUsers(sessionid, query);
	}

	@Override
	public UserTO getUser(String sessionid) throws InternalException, AuthException {
		return loginBean.getUser(sessionid);
	}

	@Override
	public void update(String sessionid, UserTO uTO) throws InternalException, AuthException {
		loginBean.update(sessionid, uTO);
	}

	@Override
	public void setOnline(String sessionid, boolean b) throws AuthException, InternalException {
		loginBean.setOnline(sessionid, b);
	}

	@Override
	public void registerSkeletonUser(String sessionid) throws AuthException, InternalException {
		loginBean.registerSkeletonUser(sessionid);
	}

	@Override
	public void recover(String email) throws BadInputException, InternalException {
		HttpServletRequest req = this.getThreadLocalRequest();
		loginBean.recover(req, email);
	}

	@Override
	public UserTO resetPassword(long userId, String hashedPassword, String newHashedPassword)
			throws AuthException {
		return loginBean.resetPassword(userId, hashedPassword, newHashedPassword);
	}

}
