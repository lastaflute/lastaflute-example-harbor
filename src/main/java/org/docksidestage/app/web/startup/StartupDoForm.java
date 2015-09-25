package org.docksidestage.app.web.startup;

import javax.validation.constraints.Size;

/**
 * @author iwamatsu0430
 */
public class StartupDoForm {

    @Size(min = 1, message = "Domainが入力されていません")
    public String domain;

    @Size(min = 1, message = "Service Nameが入力されていません")
    public String serviceName;
}
