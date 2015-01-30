package uk.org.harwellcroquet.client.service;

import java.util.List;
import java.util.Set;

import uk.org.harwellcroquet.shared.AuthException;
import uk.org.harwellcroquet.shared.BadInputException;
import uk.org.harwellcroquet.shared.InternalException;
import uk.org.harwellcroquet.shared.StoredFileTO;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("file")
public interface FileService extends RemoteService {

	List<StoredFileTO> getFileNamesStarting(String string) throws InternalException;

	void update(String sessionid, Set<StoredFileTO> modified) throws InternalException, AuthException,
			BadInputException;
}
