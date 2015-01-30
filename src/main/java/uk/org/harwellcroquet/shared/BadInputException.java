package uk.org.harwellcroquet.shared;

import javax.ejb.ApplicationException;

import com.google.gwt.user.client.rpc.IsSerializable;

@SuppressWarnings("serial")
@ApplicationException(rollback = true)
public class BadInputException extends Exception implements IsSerializable {

	// Needed for RPC stuff
	public BadInputException() {
		super();
	}

	public BadInputException(String message) {
		super(message);
	}

}
