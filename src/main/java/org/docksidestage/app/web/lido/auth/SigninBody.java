package org.docksidestage.app.web.lido.auth;

import org.lastaflute.web.validation.Required;

/**
 * @author s.tadokoro
 * @author jflute
 */
public class SigninBody {

    @Required
    public String account;
    @Required
    public String password;
}