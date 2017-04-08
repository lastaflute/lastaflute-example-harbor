package org.docksidestage.app.web.signup;

import javax.annotation.Resource;

import org.dbflute.utflute.lastaflute.mock.TestingHtmlData;
import org.docksidestage.app.web.base.login.HarborLoginAssist;
import org.docksidestage.app.web.mypage.MypageAction;
import org.docksidestage.dbflute.exbhv.MemberBhv;
import org.docksidestage.dbflute.exentity.MemberLogin;
import org.docksidestage.dbflute.exentity.MemberSecurity;
import org.docksidestage.mylasta.action.HarborHtmlPath;
import org.docksidestage.mylasta.mail.member.WelcomeMemberPostcard;
import org.docksidestage.unit.UnitHarborTestCase;
import org.lastaflute.web.response.HtmlResponse;

/**
 * @author jflute
 */
public class SignupActionTest extends UnitHarborTestCase {

    @Resource
    private MemberBhv memberBhv;
    @Resource
    private HarborLoginAssist loginAssist;

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
        // for login history registration
        changeAsyncToNormalSync();
        changeRequiresNewToRequired();

        SignupAction action = new SignupAction();
        inject(action);
        SignupForm form = new SignupForm();
        form.memberName = "sea";
        form.memberAccount = "land";
        form.password = "piari";
        form.reminderQuestion = "bonvo?";
        form.reminderAnswer = "dstore!";

        reserveMailAssertion(mailData -> {
            mailData.required(WelcomeMemberPostcard.class).forEach(message -> {
                message.requiredToList().forEach(addr -> {
                    assertContains(addr.getAddress(), form.memberAccount); // e.g. land@docksidestage.org
                });
                message.assertPlainTextContains(form.memberName);
                message.assertPlainTextContains(form.memberAccount);
            });
        });

        // ## Act ##
        HtmlResponse response = action.signup(form);
        response.getAfterTxCommitHook().alwaysPresent(hook -> {
            hook.hook(); // login
        });

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
    }
}
