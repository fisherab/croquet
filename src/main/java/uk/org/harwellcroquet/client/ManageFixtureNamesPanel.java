package uk.org.harwellcroquet.client;

import java.util.ArrayList;
import java.util.List;

import uk.org.harwellcroquet.client.service.EventServiceAsync;
import uk.org.harwellcroquet.shared.FixtureNameTO;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ManageFixtureNamesPanel extends Composite {

	private final static HTML wait = new HTML("<h2>Waiting for current information ...</h2>");

	private Button apply;

	private List<TextBox> boxes = new ArrayList<TextBox>();
	protected HorizontalPanel bs;
	private final EventServiceAsync eventService = EventServiceAsync.Util.getInstance();

	private List<FixtureNameTO> fntos;

	protected HandlerRegistration lastReg;

	private VerticalPanel panel;

	private void update() {
		List<FixtureNameTO> fntostomove = new ArrayList<FixtureNameTO>();
		for (int i = 0; i < boxes.size(); i++) {
			TextBox tb = boxes.get(i);
			FixtureNameTO fnto = fntos.get(i);
			if (!tb.getValue().equals(fnto.getName())) {
				fnto.setName(tb.getValue());
				fntostomove.add(fnto);
			}
		}
		this.eventService.updateFixtureNames(fntostomove, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.toString());
				History.newItem("home");
			}

			@Override
			public void onSuccess(Void result) {
				History.newItem("home");
			}
		});
	}

	ManageFixtureNamesPanel() {

		this.panel = new VerticalPanel();

		this.panel.add(new HTML("<h2>Manage fixture names</h2>"));
		this.panel.add(new HTML("<p>Add new names as required or overwite them. Empty names will be removed.</p>"));
		this.panel.add(ManageFixtureNamesPanel.wait);

		this.eventService.getFixtureNames(new AsyncCallback<List<FixtureNameTO>>() {

			ClickHandler newBox = new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					ManageFixtureNamesPanel.this.lastReg.removeHandler();
					ManageFixtureNamesPanel.this.panel.remove(ManageFixtureNamesPanel.this.bs);
					addEmptyBox();
					ManageFixtureNamesPanel.this.panel.add(ManageFixtureNamesPanel.this.bs);
				}
			};

			ClickHandler allBox = new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					ManageFixtureNamesPanel.this.apply.setEnabled(true);
				}
			};

			void addEmptyBox() {
				fntos.add(new FixtureNameTO(null, ""));
				TextBox tb = new TextBox();
				tb.addClickHandler(this.allBox);
				ManageFixtureNamesPanel.this.boxes.add(tb);
				tb.setVisibleLength(50);
				ManageFixtureNamesPanel.this.lastReg = tb.addClickHandler(this.newBox);
				ManageFixtureNamesPanel.this.panel.add(tb);
			}

			@Override
			public void onFailure(Throwable caught) {
				ManageFixtureNamesPanel.this.panel.remove(ManageFixtureNamesPanel.wait);
				Window.alert(caught.toString());
			}

			@Override
			public void onSuccess(List<FixtureNameTO> fntos) {
				ManageFixtureNamesPanel.this.panel.remove(ManageFixtureNamesPanel.wait);
				ManageFixtureNamesPanel.this.fntos = fntos;

				for (FixtureNameTO fnto : ManageFixtureNamesPanel.this.fntos) {
					TextBox tb = new TextBox();
					tb.addClickHandler(this.allBox);
					ManageFixtureNamesPanel.this.boxes.add(tb);
					tb.setVisibleLength(50);
					tb.setValue(fnto.getName());
					ManageFixtureNamesPanel.this.panel.add(tb);
				}
				this.addEmptyBox();

				ManageFixtureNamesPanel.this.bs = new HorizontalPanel();
				ManageFixtureNamesPanel.this.bs.setSpacing(10);
				ManageFixtureNamesPanel.this.panel.add(ManageFixtureNamesPanel.this.bs);
				ManageFixtureNamesPanel.this.apply = new Button("Apply");
				ManageFixtureNamesPanel.this.apply.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						update();
					}
				});
				ManageFixtureNamesPanel.this.apply.setEnabled(false);
				Button discard = new Button("Discard");
				ManageFixtureNamesPanel.this.bs.add(ManageFixtureNamesPanel.this.apply);
				ManageFixtureNamesPanel.this.bs.add(discard);
				discard.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						History.newItem("home");
					}
				});
			}
		});

		this.initWidget(this.panel);

	}
}
