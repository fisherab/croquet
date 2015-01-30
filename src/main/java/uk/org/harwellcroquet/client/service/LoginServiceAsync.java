package uk.org.harwellcroquet.client.service;

import java.util.List;
import java.util.Set;

import uk.org.harwellcroquet.shared.UserTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoginServiceAsync {

	public static final class Util {
		private static LoginServiceAsync instance;

		public static final LoginServiceAsync getInstance() {
			if (instance == null) {
				instance = (LoginServiceAsync) GWT.create(LoginService.class);
			}
			return instance;
		}

		private Util() {
		}
	}

	void getUser(String sessionid, AsyncCallback<UserTO> callback);

	void getUsers(String sessionid, String query, AsyncCallback<List<UserTO>> callback);

	void login(String email, String password, AsyncCallback<UserTO> callback);

	void logout(String sessionid, AsyncCallback<Void> callback);

	void recover(String email, AsyncCallback<Void> callback);

	void register(UserTO uTO, AsyncCallback<String> callback);

	void registerSkeletonUser(String sessionid, AsyncCallback<Void> callback);

	void resetPassword(long userId, String hashedPassword, String passwordV,
			AsyncCallback<UserTO> callback);

	void setOnline(String sessionid, boolean b, AsyncCallback<Void> callback);

	void update(String sessionid, Set<UserTO> modified, AsyncCallback<Void> callback);

	void update(String sessionid, UserTO uTO, AsyncCallback<Void> callback);

}
