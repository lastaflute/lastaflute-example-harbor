package org.docksidestage.app.web.startup;

import java.io.File;

import javax.annotation.Resource;

import org.dbflute.util.DfResourceUtil;
import org.docksidestage.app.logic.startup.StartupLogic;
import org.docksidestage.app.web.base.HarborBaseAction;
import org.lastaflute.web.Execute;
import org.lastaflute.web.login.AllowAnyoneAccess;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.render.RenderData;

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
    public HtmlResponse index() {
        return asHtml(path_Startup_StartupHtml).useForm(StartupForm.class).renderWith(data -> {
            registerBean(data, new StartupBean());
        });
    }

    @Execute
    public HtmlResponse create(StartupForm form) {
        validate(form, message -> {} , () -> {
            return asHtml(path_Startup_StartupHtml).renderWith(data -> {
                registerBean(data, new StartupBean());
            });
        });

        final String domain = form.domain;
        final String serviceName = form.serviceName;
        final File projectDir = getProjectDir();
        async(() -> startupLogic.toHarbor(projectDir, domain, serviceName));

        StartupBean bean = mappingToBean(serviceName, projectDir);
        return asHtml(path_Startup_StartupHtml).renderWith(data -> {
            registerBean(data, bean);
        });
    }

    private File getProjectDir() {
        return DfResourceUtil.getBuildDir(getClass()).getParentFile().getParentFile();
    }

    private StartupBean mappingToBean(String serviceName, File projectDir) {
        StartupBean bean = new StartupBean();
        bean.isComplete = true;
        bean.projectPath = projectDir.getPath() + "/" + serviceName;
        return bean;
    }

    private void registerBean(RenderData data, StartupBean bean) {
        data.register("bean", bean);
    }
}
