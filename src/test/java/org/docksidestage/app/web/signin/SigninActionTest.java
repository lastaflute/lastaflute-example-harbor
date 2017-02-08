package org.docksidestage.app.web.signin;

import org.dbflute.utflute.lastaflute.mock.TestingHtmlData;
import org.docksidestage.app.web.mypage.MypageAction;
import org.docksidestage.mylasta.action.HarborMessages;
import org.docksidestage.unit.UnitHarborTestCase;
import org.lastaflute.core.message.UserMessages;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.validation.exception.ValidationErrorException;

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

    public void test_signin_failure() {
        // ## Arrange ##
        SigninAction action = new SigninAction();
        inject(action);
        SigninForm form = new SigninForm();
        form.account = "Pixy";
        form.password = "land";

        // ## Act ##
        try {
            action.signin(form);
            // ## Assert ##
            fail();
        } catch (ValidationErrorException e) {
            UserMessages messages = e.getMessages();
            assertTrue(messages.hasMessageOf("account", HarborMessages.ERRORS_LOGIN_FAILURE));
            HtmlResponse response = (HtmlResponse) e.getErrorHook().hook();
            validateHtmlData(response);
            assertNull(form.password);
        }
    }
}
