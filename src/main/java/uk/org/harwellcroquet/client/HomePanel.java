package uk.org.harwellcroquet.client;

import uk.org.harwellcroquet.client.service.ItemServiceAsync;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HomePanel extends Composite {

	private HTML html;
	private final ItemServiceAsync itemService = ItemServiceAsync.Util.getInstance();
	private Calendar calendar;

	HomePanel(Harwellcroquet top) {
		final VerticalPanel panel = new VerticalPanel();
		panel.add(new HTML(
				"<img style=\"border: 1px solid ; margin: 0pt 1cm 0pt 0pt; width: 301px; height: 200px; float: left;\" "
						+ "alt=\"The Harwell Croquet Lawns\" title=\"The lawns\" src=\"Lawns.jpg\" /> "
						+ "<p style=\"padding: 1cm 1cm 0pt 0pt;\">The Harwell Croquet Club is a small but friendly club with two well "
						+ "maintained lawns and a  pavilion shared with other sports clubs. The club has a few mallets for players "
						+ "who do not have their own.<p>We would welcome new members - with or without previous experience of the game. "
						+ "Just come along (in flat footware) and have a go. See below for times.<br clear=\"all\" /> </p>"));
		this.calendar = new Calendar();
		panel.add(this.calendar);
		this.html = new HTML();
		panel.add(this.html);
		this.initWidget(panel);
		this.refresh();
	}

	public void refresh() {
		this.itemService.getHomeHtml(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.toString());
			}

			@Override
			public void onSuccess(String result) {
				HomePanel.this.html.setHTML(result);
			}
		});

	}

}
