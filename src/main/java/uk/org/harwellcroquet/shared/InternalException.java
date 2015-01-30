package uk.org.harwellcroquet.shared;

import javax.ejb.ApplicationException;

import com.google.gwt.user.client.rpc.IsSerializable;

@SuppressWarnings("serial")
@ApplicationException(rollback = true)
public class InternalException extends Exception implements IsSerializable {

	// Needed for RPC stuff

	public InternalException() {
	}

	public InternalException(String string) {
		super(string);
	}

}
