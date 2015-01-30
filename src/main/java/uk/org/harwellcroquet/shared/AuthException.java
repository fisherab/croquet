package uk.org.harwellcroquet.shared;

import javax.ejb.ApplicationException;

import com.google.gwt.user.client.rpc.IsSerializable;

@SuppressWarnings("serial")
@ApplicationException(rollback = true)
public class AuthException extends Exception implements IsSerializable {

	// Needed for RPC stuff
	public AuthException() {
	}

	public AuthException(String message) {
		super(message);
	}

}
