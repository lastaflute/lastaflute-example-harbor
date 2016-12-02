package org.docksidestage.app.web.signup;

import org.dbflute.utflute.lastaflute.mock.TestingHtmlData;
import org.docksidestage.unit.UnitHarborTestCase;
import org.lastaflute.web.response.HtmlResponse;

/**
 * @author jflute
 */
public class SignupActionTest extends UnitHarborTestCase {

    public void test_index() {
        // ## Arrange ##
        SignupAction action = new SignupAction();
        inject(action);

        // ## Act ##
        HtmlResponse response = action.index();

        // ## Assert ##
        TestingHtmlData htmlData = validateHtmlData(response);
        assertTrue(htmlData.isRoutingAsHtmlForward());
    }
}
