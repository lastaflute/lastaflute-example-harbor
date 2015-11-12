package org.docksidestage.app.web.startup;

import org.lastaflute.web.validation.Required;

/**
 * @author iwamatsu0430
 * @author jflute
 */
public class StartupForm {

    @Required
    public String domain;

    @Required
    public String serviceName;
}
