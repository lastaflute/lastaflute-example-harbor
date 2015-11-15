package org.docksidestage.app.web.lido.auth;

import org.hibernate.validator.constraints.NotBlank;

/**
 * @author annie_pocket
 */
public class SignupBody {

    // Member
    @NotBlank
    public String memberName;
    @NotBlank
    public String memberAccount;

    // Member Security
    @NotBlank
    public String password;
    @NotBlank
    public String reminderQuestion;
    @NotBlank
    public String reminderAnswer;
}
