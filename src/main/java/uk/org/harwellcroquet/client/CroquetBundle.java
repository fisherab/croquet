package uk.org.harwellcroquet.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

public interface CroquetBundle extends ClientBundle {
  public static final CroquetBundle INSTANCE =  GWT.create(CroquetBundle.class);

  @Source("croquet.css")
  public CroquetCss css();
}