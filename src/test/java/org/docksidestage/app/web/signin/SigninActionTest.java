package org.docksidestage.app.web.signin;

import org.dbflute.utflute.lastaflute.mock.TestingHtmlData;
import org.docksidestage.app.web.mypage.MypageAction;
import org.docksidestage.mylasta.action.HarborHtmlPath;
import org.docksidestage.mylasta.action.HarborMessages;
import org.docksidestage.unit.UnitHarborTestCase;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.validation.Required;

/**
 * @author jflute
 */
public class SigninActionTest extends UnitHarborTestCase {

    public void test_signin_success() {
        // ## Arrange ##
        SigninAction action = new SigninAction();
        inject(action);
        SigninForm form = new SigninForm();
        form.account = "Pixy";
        form.password = "sea";

        // ## Act ##
        HtmlResponse response = action.signin(form);

        // ## Assert ##
        TestingHtmlData htmlData = validateHtmlData(response);
        htmlData.assertRedirect(MypageAction.class);
    }

    public void test_signin_validationError_required() {
        // ## Arrange ##
        SigninAction action = new SigninAction();
        inject(action);
        SigninForm form = new SigninForm();

        // ## Act ##
        // ## Assert ##
        assertValidationError(() -> action.signin(form)).handle(data -> {
            data.requiredMessageOf("account", Required.class);
            data.requiredMessageOf("password", Required.class);
            TestingHtmlData htmlData = validateHtmlData(data.hookError());
            htmlData.assertHtmlForward(HarborHtmlPath.path_Signin_SigninHtml);
        });
    }

    public void test_signin_validationError_loginFailure() {
        // ## Arrange ##
        SigninAction action = new SigninAction();
        inject(action);
        SigninForm form = new SigninForm();
        form.account = "Pixy";
        form.password = "land";

        // ## Act ##
        // ## Assert ##
        assertValidationError(() -> action.signin(form)).handle(data -> {
            data.requiredMessageOf("account", HarborMessages.ERRORS_LOGIN_FAILURE);
            TestingHtmlData htmlData = validateHtmlData(data.hookError());
            htmlData.assertHtmlForward(HarborHtmlPath.path_Signin_SigninHtml);
            assertNull(form.password); // should cleared for security
        });
    }
}
