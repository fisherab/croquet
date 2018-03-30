package uk.org.harwellcroquet.client;

import java.util.Date;

import uk.org.harwellcroquet.client.event.HomeChangedEvent;
import uk.org.harwellcroquet.client.service.ItemServiceAsync;
import uk.org.harwellcroquet.shared.Consts;
import uk.org.harwellcroquet.shared.ItemTO;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DatePicker;

public class CreateItemPanel extends Composite {

	private Button apply;

	private TextArea content;
	private DateTimeFormat dateFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM);

	private HTML header;

	private final ItemServiceAsync itemService = ItemServiceAsync.Util.getInstance();

	private Type type;

	private DatePicker dateBox;

	private TextBox dateTextBox;

	public enum Type {
		NEWS, NAV, BODY, CONTACT
	};

	CreateItemPanel(Type t) {
		ClickHandler changeHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CreateItemPanel.this.apply.setEnabled(true);
			}
		};

		this.type = t;

		VerticalPanel panel = new VerticalPanel();
		this.header = new HTML("<h2>Create " + t + " item</h2>");
		panel.add(this.header);
		panel.add(new HTML(
				"<p>Please enter text in the box below and use the 'Apply' button. You may modify the date if you wish.</p>"));

		if (t.equals(Type.NEWS)) {
			panel.add(new HTML("<p>The date will be added to the news item as will your name.</p>"));
		}

		this.dateBox = new DatePicker();
		this.dateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {

			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				dateTextBox.setValue(dateFormat.format(event.getValue()));
				dateTextBox.setVisible(true);
				dateBox.setVisible(false);
			}
		});
		this.dateBox.setVisible(false);
		panel.add(dateBox);
		this.dateTextBox = new TextBox();
		dateTextBox.setValue(dateFormat.format(new Date()));
		panel.add(dateTextBox);
		dateTextBox.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				dateTextBox.setVisible(false);
				dateBox.setVisible(true);

			}
		});

		this.content = new TextArea();
		this.content.setWidth("40em");
		this.content.setHeight("20ex");
		this.content.addClickHandler(changeHandler);
		panel.add(this.content);

		HorizontalPanel bs = new HorizontalPanel();
		bs.setSpacing(10);
		panel.add(bs);
		this.apply = new Button("Apply");
		this.apply.setEnabled(false);
		Button discard = new Button("Discard");
		bs.add(this.apply);
		bs.add(discard);
		this.apply.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (type == Type.CONTACT) {
					String[] bits = CreateItemPanel.this.content.getValue().split(":");
					if (bits.length != 3) {
						Window.alert(CreateItemPanel.this.content.getValue() + " must have exactly two colons");
						return;
					}
				}
				Date date = dateFormat.parse(dateTextBox.getValue());
				ItemTO item = new ItemTO(null, null, date, CreateItemPanel.this.type.name(), 0,
						CreateItemPanel.this.content.getValue());
				CreateItemPanel.this.itemService.add(Cookies.getCookie(Consts.COOKIE), item, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(Void result) {
						TheEventBus.getInstance().fireEvent(new HomeChangedEvent());
						History.newItem("home");
					}
				});

			}
		});
		discard.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				content.setValue("");
				apply.setEnabled(false);
			}
		});
		refresh();

		this.initWidget(panel);
	}

	public void refresh() {
		this.apply.setEnabled(false);
		this.content.setValue("");
	}

}
