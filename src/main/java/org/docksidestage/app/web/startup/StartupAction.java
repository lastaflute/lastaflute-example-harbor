package org.docksidestage.app.web.startup;

import java.io.File;

import javax.annotation.Resource;

import org.dbflute.util.DfResourceUtil;
import org.docksidestage.app.logic.startup.StartupLogic;
import org.docksidestage.app.web.base.HarborBaseAction;
import org.lastaflute.web.Execute;
import org.lastaflute.web.login.AllowAnyoneAccess;
import org.lastaflute.web.response.HtmlResponse;

/**
 * @author iwamatsu0430
 * @author jflute
 */
@AllowAnyoneAccess
public class StartupAction extends HarborBaseAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private StartupLogic startupLogic;

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Execute
    public HtmlResponse index(StartupDoForm form) {
        StartupResult result = new StartupResult();
        return asHtml(path_Startup_StartupJsp).renderWith(data -> {
            data.register("bean", result);
        });
    }

    @Execute
    public HtmlResponse doStartup(StartupDoForm form) {
        final String domain = form.domain;
        final String serviceName = form.serviceName;
        StartupResult result = new StartupResult();

        validate(form, message -> {} , () -> {
            return asHtml(path_Startup_StartupJsp).renderWith(data -> {
                data.register("bean", result);
            });
        });

        final File projectDir = DfResourceUtil.getBuildDir(getClass()).getParentFile().getParentFile();
        async(() -> startupLogic.toHarbor(projectDir, domain, serviceName));

        result.isComplete = true;
        result.projectPath = projectDir.getPath() + File.separator + serviceName;
        return asHtml(path_Startup_StartupJsp).renderWith(data -> {
            data.register("bean", result);
        });
    }
}
