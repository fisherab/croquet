package uk.org.harwellcroquet.client.service;

import java.util.List;
import java.util.Set;

import uk.org.harwellcroquet.shared.StoredFileTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FileServiceAsync {

	public static final class Util {
		private static FileServiceAsync instance;

		public static final FileServiceAsync getInstance() {
			if (instance == null) {
				instance = (FileServiceAsync) GWT.create(FileService.class);
			}
			return instance;
		}

		private Util() {
		}
	}

	void getFileNamesStarting(String string, AsyncCallback<List<StoredFileTO>> callback);

	void update(String sessionid, Set<StoredFileTO> modified, AsyncCallback<Void> callback);

}
