package uk.org.harwellcroquet.client;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import uk.org.harwellcroquet.client.event.HomeChangedEvent;
import uk.org.harwellcroquet.client.service.ItemServiceAsync;
import uk.org.harwellcroquet.shared.Consts;
import uk.org.harwellcroquet.shared.ItemTO;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;

public class UpdateItemsPanel extends Composite {

	final private static Logger logger = Logger.getLogger(UpdateItemsPanel.class.getName());

	private final String smallnumStyle = "width: 30px;";

	private Button apply;

	private ListDataProvider<ItemTO> dataProvider;
	DateTimeFormat dateFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM);

	public enum Type {
		NEWS, NAV, BODY, CONTACT
	};

	private final ItemServiceAsync itemService = ItemServiceAsync.Util.getInstance();

	private Set<ItemTO> modified = new HashSet<ItemTO>();

	private CellTable<ItemTO> table;

	private String lc;

	private TextArea input;

	private PopupPanel popup;

	private ItemTO currentContent;

	UpdateItemsPanel(Type type) {
		VerticalPanel panel = new VerticalPanel();
		lc = type.name().toLowerCase();
		HTML header = new HTML("<h2>Update " + lc + " items</h2>");
		panel.add(header);
		panel.add(new HTML(
				"<p>Click on the content you wish to edit. Apply the change locally then apply the set of changes.</p>"));

		this.table = new CellTable<ItemTO>();
		this.dataProvider = new ListDataProvider<ItemTO>();
		this.dataProvider.addDataDisplay(this.table);

		panel.add(this.table);
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
				for (ItemTO f : UpdateItemsPanel.this.modified) {
					if (f.isDelete()) {
						UpdateItemsPanel.logger.fine("Item by " + f.getUserTO().getEmail() + " will be deleted");
					} else {
						UpdateItemsPanel.logger.fine("Item by " + f.getUserTO().getEmail() + " will be updated");
					}

				}
				UpdateItemsPanel.this.itemService.update(Cookies.getCookie(Consts.COOKIE),
						UpdateItemsPanel.this.modified, new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								Window.alert(caught.getMessage());
							}

							@Override
							public void onSuccess(Void result) {
								TheEventBus.getInstance().fireEvent(new HomeChangedEvent());
								refresh();

							}
						});

			}
		});
		discard.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				refresh();
			}
		});

		TextColumn<ItemTO> emailColumn = new TextColumn<ItemTO>() {

			@Override
			public String getValue(ItemTO itto) {
				return itto.getUserTO().getEmail();
			}
		};

		Column<ItemTO, String> contentColumn = new Column<ItemTO, String>(new ClickableTextCell()) {

			@Override
			public String getValue(ItemTO itto) {
				return itto.getContent();
			}
		};

		contentColumn.setFieldUpdater(new FieldUpdater<ItemTO, String>() {
			@Override
			public void update(int index, ItemTO object, String value) {
				input.setValue(value);
				currentContent = object;
				popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
					public void setPosition(int offsetWidth, int offsetHeight) {
						int left = (Window.getClientWidth() - offsetWidth) / 2;
						int top = (Window.getClientHeight() - offsetHeight) / 3;
						popup.setPopupPosition(left, top);
					}
				});
			}
		});

		Column<ItemTO, Date> dateColumn = new Column<ItemTO, Date>(new DateCell(this.dateFormat)) {
			@Override
			public Date getValue(ItemTO itto) {
				return itto.getDate();
			}
		};

		Column<ItemTO, String> seqColumn = new Column<ItemTO, String>(new StyledTextInputCell(this.smallnumStyle)) {

			@Override
			public String getValue(ItemTO itto) {
				return Integer.toString(itto.getSeq());
			}
		};
		seqColumn.setFieldUpdater(new FieldUpdater<ItemTO, String>() {
			@Override
			public void update(int index, ItemTO object, String value) {
				object.setSeq(Integer.valueOf(value));
				UpdateItemsPanel.this.modified.add(object);
				UpdateItemsPanel.this.apply.setEnabled(true);
			}
		});

		Column<ItemTO, Boolean> deleteColumn = new Column<ItemTO, Boolean>(new CheckboxCell()) {

			@Override
			public Boolean getValue(ItemTO object) {
				return object.isDelete();
			}
		};
		deleteColumn.setFieldUpdater(new FieldUpdater<ItemTO, Boolean>() {

			@Override
			public void update(int index, ItemTO object, Boolean value) {
				object.setDelete(value);
				UpdateItemsPanel.this.modified.add(object);
				UpdateItemsPanel.this.apply.setEnabled(true);
			}
		});

		// Add the columns.
		this.table.addColumn(emailColumn, "User");
		this.table.addColumn(dateColumn, "Date");
		if (!lc.equals("news")) {
			this.table.addColumn(seqColumn, "Sequence");
		}
		this.table.addColumn(contentColumn, "Content");
		this.table.addColumn(deleteColumn, "Delete");

		SimplePager pager = new SimplePager();
		pager.setDisplay(table);
		panel.add(pager);

		this.popup = new PopupPanel();
		this.popup.setGlassEnabled(true);
		VerticalPanel pvp = new VerticalPanel();
		this.popup.add(pvp);

		this.input = new TextArea();
		this.input.setHeight("10em");
		this.input.setWidth("40em");
		pvp.add(this.input);

		HorizontalPanel pbs = new HorizontalPanel();
		pbs.setSpacing(10);
		pvp.add(pbs);

		Button pApply = new Button("Apply");
		pApply.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				currentContent.setContent(input.getValue());
				dataProvider.refresh();
				UpdateItemsPanel.this.modified.add(currentContent);
				UpdateItemsPanel.this.apply.setEnabled(true);
				popup.hide();
			}
		});
		pbs.add(pApply);

		Button pDiscard = new Button("Discard");
		pDiscard.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				popup.hide();
			}
		});
		pbs.add(pDiscard);

		this.refresh();
		this.initWidget(panel);
	}

	private void refresh() {
		this.modified.clear();
		this.apply.setEnabled(false);
		this.itemService.getItems(Cookies.getCookie(Consts.COOKIE), lc, new AsyncCallback<List<ItemTO>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failure " + caught);

			}

			@Override
			public void onSuccess(List<ItemTO> result) {

				List<ItemTO> list = UpdateItemsPanel.this.dataProvider.getList();
				list.clear();
				for (ItemTO ptto : result) {
					list.add(ptto);
				}
				UpdateItemsPanel.this.table.redraw();
			}
		});

	}
}
