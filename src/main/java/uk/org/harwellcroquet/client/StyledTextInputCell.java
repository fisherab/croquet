package uk.org.harwellcroquet.client;

import com.google.gwt.cell.client.AbstractInputCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;

public class StyledTextInputCell extends AbstractInputCell<String, StyledTextInputCell.ViewData> {
	interface Template extends SafeHtmlTemplates {
		@Template("<input type=\"text\" value=\"{0}\" style=\"{1}\" tabindex=\"-1\"></input>")
		SafeHtml input(String value, String cssClass);
	}

	/**
	 * The {@code ViewData} for this cell.
	 */
	public static class ViewData {
		/**
		 * The current value.
		 */
		private String curValue;

		/**
		 * The last value that was updated.
		 */
		private String lastValue;

		/**
		 * Construct a ViewData instance containing a given value.
		 * 
		 * @param value
		 *            a String value
		 */
		public ViewData(String value) {
			this.lastValue = value;
			this.curValue = value;
		}

		/**
		 * Return true if the last and current values of this ViewData object
		 * are equal to those of the other object.
		 */
		@Override
		public boolean equals(Object other) {
			if (!(other instanceof ViewData)) {
				return false;
			}
			ViewData vd = (ViewData) other;
			return this.equalsOrNull(this.lastValue, vd.lastValue) && this.equalsOrNull(this.curValue, vd.curValue);
		}

		private boolean equalsOrNull(Object a, Object b) {
			return (a != null) ? a.equals(b) : ((b == null) ? true : false);
		}

		/**
		 * Return the current value of the input element.
		 * 
		 * @return the current value String
		 * @see #setCurrentValue(String)
		 */
		public String getCurrentValue() {
			return this.curValue;
		}

		/**
		 * Return the last value sent to the {@link ValueUpdater}.
		 * 
		 * @return the last value String
		 * @see #setLastValue(String)
		 */
		public String getLastValue() {
			return this.lastValue;
		}

		/**
		 * Return a hash code based on the last and current values.
		 */
		@Override
		public int hashCode() {
			return (this.lastValue + "_*!@HASH_SEPARATOR@!*_" + this.curValue).hashCode();
		}

		/**
		 * Set the current value.
		 * 
		 * @param curValue
		 *            the current value
		 * @see #getCurrentValue()
		 */
		protected void setCurrentValue(String curValue) {
			this.curValue = curValue;
		}

		/**
		 * Set the last value.
		 * 
		 * @param lastValue
		 *            the last value
		 * @see #getLastValue()
		 */
		protected void setLastValue(String lastValue) {
			this.lastValue = lastValue;
		}
	}

	private static Template template;

	private String cssClass;

	private final SafeHtmlRenderer<String> renderer;

	/**
	 * Constructs a TextInputCell that renders its text without HTML markup.
	 */
	public StyledTextInputCell() {
		this(SimpleSafeHtmlRenderer.getInstance());
	}

	/**
	 * Constructs a TextInputCell that renders its text using the given
	 * {@link SafeHtmlRenderer}.
	 * 
	 * @param renderer
	 *            a non-null SafeHtmlRenderer
	 */
	public StyledTextInputCell(SafeHtmlRenderer<String> renderer) {
		super("change", "keyup");
		if (StyledTextInputCell.template == null) {
			StyledTextInputCell.template = GWT.create(Template.class);
		}
		if (renderer == null) {
			throw new IllegalArgumentException("renderer == null");
		}
		this.renderer = renderer;
	}

	/**
	 * Constructs a TextInputCell that renders its text without HTML markup.
	 */
	public StyledTextInputCell(String cssClass) {
		this(SimpleSafeHtmlRenderer.getInstance());
		this.cssClass = cssClass;
	}

	@Override
	protected void finishEditing(Element parent, String value, Object key, ValueUpdater<String> valueUpdater) {
		String newValue = this.getInputElement(parent).getValue();

		// Get the view data.
		ViewData vd = this.getViewData(key);
		if (vd == null) {
			vd = new ViewData(value);
			this.setViewData(key, vd);
		}
		vd.setCurrentValue(newValue);

		// Fire the value updater if the value has changed.
		if (valueUpdater != null && !vd.getCurrentValue().equals(vd.getLastValue())) {
			vd.setLastValue(newValue);
			valueUpdater.update(newValue);
		}

		// Blur the element.
		super.finishEditing(parent, newValue, key, valueUpdater);
	}

	@Override
	protected InputElement getInputElement(Element parent) {
		return super.getInputElement(parent).<InputElement> cast();
	}

	@Override
	public void onBrowserEvent(Context context, Element parent, String value, NativeEvent event,
			ValueUpdater<String> valueUpdater) {
		super.onBrowserEvent(context, parent, value, event, valueUpdater);

		// Ignore events that don't target the input.
		InputElement input = this.getInputElement(parent);
		Element target = event.getEventTarget().cast();
		if (!input.isOrHasChild(target)) {
			return;
		}

		String eventType = event.getType();
		Object key = context.getKey();
		if ("change".equals(eventType)) {
			this.finishEditing(parent, value, key, valueUpdater);
		} else if ("keyup".equals(eventType)) {
			// Record keys as they are typed.
			ViewData vd = this.getViewData(key);
			if (vd == null) {
				vd = new ViewData(value);
				this.setViewData(key, vd);
			}
			vd.setCurrentValue(input.getValue());
		}
	}

	@Override
	public void render(Context context, String value, SafeHtmlBuilder sb) {
		// Get the view data.
		Object key = context.getKey();
		ViewData viewData = this.getViewData(key);
		if (viewData != null && viewData.getCurrentValue().equals(value)) {
			this.clearViewData(key);
			viewData = null;
		}

		String s = (viewData != null) ? viewData.getCurrentValue() : value;
		if (s != null) {
			SafeHtml html = this.renderer.render(s);
			// Note: template will not treat SafeHtml specially
			sb.append(StyledTextInputCell.template.input(html.asString(), this.cssClass));
		} else {
			sb.appendHtmlConstant("<input type=\"text\" style=\"" + this.cssClass + "\" tabindex=\"-1\"></input>");
		}
	}
}
