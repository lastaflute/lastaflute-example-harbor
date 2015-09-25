package org.docksidestage.app.web.signup;

import org.hibernate.validator.constraints.NotBlank;

/**
 * @author annie_pocket
 */
public class SignupForm {

    // Member
    @NotBlank
    public String name;
    @NotBlank
    public String account;

    // Member Security
    @NotBlank
    public String password;
    @NotBlank
    public String reminderQuestion;
    @NotBlank
    public String reminderAnswer;
}
