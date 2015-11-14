package org.docksidestage.app.web.signup;

import org.lastaflute.web.validation.Required;

/**
 * @author annie_pocket
 */
public class SignupForm {

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
