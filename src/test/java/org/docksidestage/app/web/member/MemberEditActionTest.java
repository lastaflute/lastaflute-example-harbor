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
import org.lastaflute.web.token.exception.DoubleSubmittedRequestException;

/**
 * @author jflute
 */
public class MemberEditActionTest extends UnitHarborTestCase {

    @Resource
    private MemberBhv memberBhv;

    // ===================================================================================
    //                                                                             index()
    //                                                                             =======
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
        assertTokenSaved(action.getClass());
    }

    // ===================================================================================
    //                                                                            update()
    //                                                                            ========
    public void test_update_success() {
        // ## Arrange ##
        MemberEditAction action = new MemberEditAction();
        inject(action);
        mockTokenRequested(action.getClass());
        MemberEditForm form = prepareEditForm();

        // ## Act ##
        HtmlResponse response = action.update(form);

        // ## Assert ##
        TestingHtmlData htmlData = validateHtmlData(response);
        htmlData.assertRedirect(action.getClass());
        assertTokenVerified();
    }

    public void test_update_doubleSubmitted() {
        // ## Arrange ##
        MemberEditAction action = new MemberEditAction();
        inject(action);
        mockTokenRequestedAsDoubleSubmit(action.getClass());
        MemberEditForm form = prepareEditForm();

        // ## Act ##
        // ## Assert ##
        assertException(DoubleSubmittedRequestException.class, () -> action.update(form));
    }

    private MemberEditForm prepareEditForm() {
        int memberId = 1;
        Member member = memberBhv.selectByPK(memberId).get();

        MemberEditForm form = new MemberEditForm();
        form.memberId = memberId;
        form.memberName = "sea";
        form.memberAccount = "land";
        form.memberStatus = CDef.MemberStatus.Provisional;
        form.birthdate = LocalDate.of(2016, 12, 2);
        form.versionNo = member.getVersionNo();
        form.previousStatus = member.getMemberStatusCodeAsMemberStatus();
        return form;
    }
}
