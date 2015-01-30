package uk.org.harwellcroquet.client.service;

import java.util.List;
import java.util.Set;

import uk.org.harwellcroquet.shared.AuthException;
import uk.org.harwellcroquet.shared.BadInputException;
import uk.org.harwellcroquet.shared.InternalException;
import uk.org.harwellcroquet.shared.UserTO;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("login")
public interface LoginService extends RemoteService {

	UserTO login(String email, String password) throws AuthException;

	void logout(String sessionid);

	String register(UserTO uTO) throws InternalException, AuthException;

	void update(String sessionid, Set<UserTO> modified) throws InternalException, AuthException;

	List<UserTO> getUsers(String sessionid, String query) throws AuthException;

	UserTO getUser(String sessionid) throws InternalException, AuthException;

	void update(String sessionid, UserTO uTO) throws InternalException, AuthException;

	void setOnline(String sessionid, boolean b) throws AuthException, InternalException;

	void registerSkeletonUser(String sessionid) throws AuthException, InternalException;

	void recover(String email) throws BadInputException, InternalException;

	UserTO resetPassword(long userId, String hashedPassword, String passwordV) throws AuthException;
}
