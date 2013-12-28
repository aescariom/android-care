package org.androidcare.web.client.internacionalization;

import com.google.gwt.i18n.client.Constants;

public interface LocalizedConstants extends Constants {

    /**
     * return Messages
     */
    @DefaultStringValue("OK")
    String ok();
    @DefaultStringValue("Error")
    String error();
    @DefaultStringValue("Server error")
    String serverError();
}
