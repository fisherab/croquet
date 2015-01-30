package uk.org.harwellcroquet.client;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import uk.org.harwellcroquet.client.service.FileServiceAsync;
import uk.org.harwellcroquet.shared.Consts;
import uk.org.harwellcroquet.shared.StoredFileTO;
import uk.org.harwellcroquet.shared.UserTO;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class FileManagerPanel extends Composite {

	interface FileManagerPanelUiBinder extends UiBinder<Widget, FileManagerPanel> {
	}

	private static FileManagerPanelUiBinder uiBinder = GWT.create(FileManagerPanelUiBinder.class);

	private final static FileServiceAsync fileService = FileServiceAsync.Util.getInstance();
	
	final private static Logger logger = Logger.getLogger(FileManagerPanel.class.getName());

	private Button apply;

	private ListDataProvider<StoredFileTO> dataProvider;

	private Set<StoredFileTO> modified = new HashSet<StoredFileTO>();
	private final String nameStyle = "width: 400px;";
	private final String longStyle = "width: 60px;";

	@UiField
	HTML results;

	private CellTable<StoredFileTO> table;
	private UserTO uto;

	@UiField
	VerticalPanel top;

	@UiField
	VerticalPanel main;

	@UiField
	FormPanel form;

	@UiField
	FileUpload fileupload;

	@UiField
	TextBox filename;

	@UiField
	Hidden sessionid;

	@UiHandler("submit")
	public void submit(ClickEvent event) {
		sessionid.setValue(Cookies.getCookie(Consts.COOKIE));
		form.submit();
		filename.setValue("");
	}

	public FileManagerPanel(final Harwellcroquet harwellcroquet, UserTO uto) {
		initWidget(uiBinder.createAndBindUi(this));
		this.uto = uto;

		form.setAction(GWT.getModuleBaseURL() + "fileupload");

		// Because we're going to add a FileUpload widget, we'll need to set the
		// form to use the POST method, and multipart MIME encoding.
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);

		form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				results.setHTML(event.getResults());
				refresh();
			}
		});

		// Now for the cell table
		CellTableResources resources = GWT.create(CellTableResources.class);

		this.table = new CellTable<StoredFileTO>(10, resources);
		this.dataProvider = new ListDataProvider<StoredFileTO>();
		this.dataProvider.addDataDisplay(this.table);
		this.main.add(this.table);

		HorizontalPanel bs = new HorizontalPanel();
		bs.setSpacing(10);
		this.main.add(bs);
		this.apply = new Button("Apply");
		this.apply.setEnabled(false);
		Button discard = new Button("Discard");
		bs.add(this.apply);
		bs.add(discard);
		this.apply.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for (StoredFileTO f : FileManagerPanel.this.modified) {
					logger.fine(f.toString());
					logger.fine("Delete " + f.isDelete());
					if (f.isDelete()) {
						FileManagerPanel.logger.fine("File " + f.getName() + " will be deleted");
					} else {
						FileManagerPanel.logger.fine("File " + f.getName() + " will be updated");
					}
				}
				FileManagerPanel.this.apply.setEnabled(false);
				FileManagerPanel.fileService.update(Cookies.getCookie(Consts.COOKIE), FileManagerPanel.this.modified,
						new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								Window.alert(caught.toString());
							}

							@Override
							public void onSuccess(Void result) {
								FileManagerPanel.this.refresh();
							}
						});

			}
		});
		discard.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				FileManagerPanel.this.refresh();
			}
		});

		Column<StoredFileTO, String> nameColumn = new Column<StoredFileTO, String>(new StyledTextInputCell(
				this.nameStyle)) {
			@Override
			public String getValue(StoredFileTO fto) {
				return fto.getName();
			}
		};
		nameColumn.setFieldUpdater(new FieldUpdater<StoredFileTO, String>() {
			@Override
			public void update(int index, StoredFileTO fto, String value) {
				fto.setName(value);
				FileManagerPanel.this.modified.add(fto);
				FileManagerPanel.this.apply.setEnabled(true);
			}
		});

		Column<StoredFileTO, String> userColumn = null;
		if (uto != null && uto.getPriv().equals("SUPER")) {
			userColumn = new Column<StoredFileTO, String>(new StyledTextInputCell(this.longStyle)) {
				@Override
				public String getValue(StoredFileTO fto) {
					return Long.toString(fto.getUser().getId());
				}
			};
			userColumn.setFieldUpdater(new FieldUpdater<StoredFileTO, String>() {
				@Override
				public void update(int index, StoredFileTO fto, String value) {
					UserTO uto = new UserTO();
					uto.setId(Long.parseLong(value));
					fto.setUser(uto);
					FileManagerPanel.this.modified.add(fto);
					FileManagerPanel.this.apply.setEnabled(true);
				}
			});
		}

		Column<StoredFileTO, Boolean> deleteColumn = new Column<StoredFileTO, Boolean>(new CheckboxCell()) {

			@Override
			public Boolean getValue(StoredFileTO fto) {
				return fto.isDelete();
			}
		};
		deleteColumn.setFieldUpdater(new FieldUpdater<StoredFileTO, Boolean>() {
			@Override
			public void update(int index, StoredFileTO fto, Boolean value) {
				fto.setDelete(value);
				FileManagerPanel.this.modified.add(fto);
				FileManagerPanel.this.apply.setEnabled(true);
			}
		});

		// Add the columns.
		this.table.setWidth("auto", true);
		this.table.addColumn(nameColumn, "Name");
		if (userColumn != null) {
			this.table.addColumn(userColumn, "Owner");
		}
		this.table.addColumn(deleteColumn, "Delete");

		SimplePager pager = new SimplePager();
		pager.setDisplay(table);
		this.main.add(pager);

		this.refresh();

	}

	public void refresh() {
		this.modified.clear();
		this.apply.setEnabled(false);
		FileManagerPanel.fileService.getFileNamesStarting(null, new AsyncCallback<List<StoredFileTO>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.toString());
			}

			@Override
			public void onSuccess(List<StoredFileTO> result) {
				List<StoredFileTO> list = FileManagerPanel.this.dataProvider.getList();
				list.clear();
				for (StoredFileTO fto : result) {
					if (uto != null) {
						if (uto.getPriv().equals("SUPER") || fto.getUser().getId() == uto.getId()) {
							list.add(fto);
						}
					}
				}
				FileManagerPanel.this.table.redraw();
			}
		});
	}
}
