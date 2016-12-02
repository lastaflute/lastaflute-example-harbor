package org.docksidestage.app.web.signup;

import javax.annotation.Resource;

import org.dbflute.utflute.lastaflute.mock.TestingHtmlData;
import org.docksidestage.app.web.mypage.MypageAction;
import org.docksidestage.dbflute.exbhv.MemberBhv;
import org.docksidestage.dbflute.exentity.MemberLogin;
import org.docksidestage.dbflute.exentity.MemberSecurity;
import org.docksidestage.mylasta.action.HarborHtmlPath;
import org.docksidestage.unit.UnitHarborTestCase;
import org.lastaflute.web.response.HtmlResponse;

/**
 * @author jflute
 */
public class SignupActionTest extends UnitHarborTestCase {

    @Resource
    private MemberBhv memberBhv;

    public void test_index_success() {
        // ## Arrange ##
        SignupAction action = new SignupAction();
        inject(action);

        // ## Act ##
        HtmlResponse response = action.index();

        // ## Assert ##
        TestingHtmlData htmlData = validateHtmlData(response);
        htmlData.assertHtmlForward(HarborHtmlPath.path_Signup_SignupHtml);
    }

    public void test_signup_success() {
        // ## Arrange ##
        // for login histroy registration
        changeAsyncToNormalSync();
        changeRequiresNewToRequired();

        SignupAction action = new SignupAction();
        inject(action);
        SignupForm form = new SignupForm();
        form.memberName = "seaName";
        form.memberAccount = "seaAccount";
        form.password = "seaPassword";
        form.reminderQuestion = "sea?";
        form.reminderAnswer = "sea!";

        // ## Act ##
        HtmlResponse response = action.signup(form);

        // ## Assert ##
        TestingHtmlData htmlData = validateHtmlData(response);
        htmlData.assertRedirect(MypageAction.class);

        memberBhv.selectEntity(cb -> {
            cb.setupSelect_MemberLoginAsLatest();
            cb.setupSelect_MemberSecurityAsOne();
            cb.query().setMemberAccount_Equal(form.memberAccount);
        }).alwaysPresent(member -> {
            assertEquals(form.memberName, member.getMemberName());
            assertTrue(member.isMemberStatusCodeProvisional());

            MemberSecurity security = member.getMemberSecurityAsOne().get();
            assertEquals(form.reminderQuestion, security.getReminderQuestion());

            MemberLogin login = member.getMemberLoginAsLatest().get();
            assertTrue(login.isLoginMemberStatusCodeProvisional());
        });

        // how I assert mail sending?
    }
}
