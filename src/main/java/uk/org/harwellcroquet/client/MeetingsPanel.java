package uk.org.harwellcroquet.client;

import java.util.List;
import java.util.logging.Logger;

import uk.org.harwellcroquet.client.service.FileServiceAsync;
import uk.org.harwellcroquet.shared.StoredFileTO;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;

public class MeetingsPanel extends HTML {

	private final FileServiceAsync fileService = FileServiceAsync.Util.getInstance();
	final private static Logger logger = Logger.getLogger(MeetingsPanel.class.getName());
	private final static String dir = "minutes/";

	public MeetingsPanel() {
		final StringBuilder sb = new StringBuilder();
		sb.append("<h2>Minutes of Meetings</h2>");
		fileService.getFileNamesStarting(dir, new AsyncCallback<List<StoredFileTO>>() {

			@Override
			public void onFailure(Throwable caught) {
				logger.severe(caught.toString());
				Window.alert("Failure " + caught);
			}

			@Override
			public void onSuccess(List<StoredFileTO> minuteDocs) {

				for (StoredFileTO minuteDoc : minuteDocs) {
					String name = minuteDoc.getName();
					sb.append("<a href=").append('"').append("harwellcroquet/filedownload?name=")
							.append(URL.encode(name)).append('"').append(" target=\"_blank\"").append(">")
							.append(name.substring(dir.length())).append("</a><br>");

				}
				setHTML(sb.toString());
			}

		});

	}

}
