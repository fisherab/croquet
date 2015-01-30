package uk.org.harwellcroquet.client;

import com.google.gwt.user.cellview.client.CellTable;

public interface CellTableResources extends CellTable.Resources {
	@Source({CellTable.Style.DEFAULT_CSS, "CellTable.css"})
	CellTableStyle cellTableStyle();
}
