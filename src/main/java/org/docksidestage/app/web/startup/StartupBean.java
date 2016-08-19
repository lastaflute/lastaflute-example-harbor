package org.docksidestage.app.web.startup;

import org.lastaflute.web.validation.Required;

/**
 * @author iwamatsu0430
 * @author jflute
 */
public class StartupBean {

    @Required
    public final String projectPath;

    public StartupBean(String projectPath) {
        this.projectPath = projectPath;
    }
}
