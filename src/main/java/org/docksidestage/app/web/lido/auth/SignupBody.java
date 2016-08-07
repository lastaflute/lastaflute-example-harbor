package org.docksidestage.app.web.lido.auth;

import org.lastaflute.web.validation.Required;

/**
 * @author s.tadokoro
 * @author jflute
 */
public class SignupBody {

    // member
    @Required
    public String memberName;
    @Required
    public String memberAccount;

    // security
    @Required
    public String password;
    @Required
    public String reminderQuestion;
    @Required
    public String reminderAnswer;
}
