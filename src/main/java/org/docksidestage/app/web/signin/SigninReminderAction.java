package org.docksidestage.app.web.signin;

import org.docksidestage.app.web.base.HarborBaseAction;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;

/**
 * @author masaki.kamachi
 * @author jflute
 */
public class SigninReminderAction extends HarborBaseAction {

    // #pending now making...
    @Execute
    public HtmlResponse index() {
        return asHtml(path_Signin_SigninReminderHtml);
    }
}
