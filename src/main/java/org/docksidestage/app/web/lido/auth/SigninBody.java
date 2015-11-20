package org.docksidestage.app.web.lido.auth;

import org.hibernate.validator.constraints.NotBlank;

/**
 * The form of member's Login.
 * @author jflute
 * @author iwamatsu0430
 */
public class SigninBody {

    @NotBlank
    public String account;

    @NotBlank
    public String password;

    public void clearSecurityInfo() {
        password = null;
    }
}