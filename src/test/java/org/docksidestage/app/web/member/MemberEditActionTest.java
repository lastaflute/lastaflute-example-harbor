package org.docksidestage.app.web.member;

import java.time.LocalDate;

import javax.annotation.Resource;

import org.dbflute.utflute.lastaflute.mock.TestingHtmlData;
import org.docksidestage.dbflute.allcommon.CDef;
import org.docksidestage.dbflute.exbhv.MemberBhv;
import org.docksidestage.dbflute.exentity.Member;
import org.docksidestage.mylasta.action.HarborHtmlPath;
import org.docksidestage.unit.UnitHarborTestCase;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.token.DoubleSubmitManager;
import org.lastaflute.web.token.TokenErrorHook;

/**
 * @author jflute
 */
public class MemberEditActionTest extends UnitHarborTestCase {

    @Resource
    private MemberBhv memberBhv;
    @Resource
    private DoubleSubmitManager doubleSubmitManager;

    public void test_index_success() {
        // ## Arrange ##
        MemberEditAction action = new MemberEditAction();
        inject(action);
        int memberId = 1;
        Member member = memberBhv.selectByPK(memberId).get();

        // ## Act ##
        HtmlResponse response = action.index(memberId);

        // ## Assert ##
        TestingHtmlData htmlData = validateHtmlData(response);
        htmlData.assertHtmlForward(HarborHtmlPath.path_Member_MemberEditHtml);
        MemberEditForm form = htmlData.requiredPushedForm(MemberEditForm.class);
        assertEquals(member.getMemberName(), form.memberName);
        assertTrue(doubleSubmitManager.isDoubleSubmittedRequest());
    }

    public void test_update_success() {
        // ## Arrange ##
        MemberEditAction action = new MemberEditAction() {
            @Override
            protected void verifyToken(TokenErrorHook errorResponseLambda) {
                markHere("called");
            }
        };
        inject(action);
        int memberId = 1;
        Member member = memberBhv.selectByPK(memberId).get();

        MemberEditForm form = new MemberEditForm();
        form.memberId = memberId;
        form.memberName = "seaName";
        form.memberAccount = "seaAccount";
        form.memberStatus = CDef.MemberStatus.Provisional;
        form.birthdate = LocalDate.of(2016, 12, 2);
        form.versionNo = member.getVersionNo();
        form.previousStatus = member.getMemberStatusCodeAsMemberStatus();

        // ## Act ##
        HtmlResponse response = action.update(form);

        // ## Assert ##
        TestingHtmlData htmlData = validateHtmlData(response);
        htmlData.assertRedirect(MemberEditAction.class);
        assertMarked("called");
    }
}
