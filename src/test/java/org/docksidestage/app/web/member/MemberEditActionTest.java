package org.docksidestage.app.web.member;

import javax.annotation.Resource;

import org.dbflute.utflute.lastaflute.mock.TestingHtmlData;
import org.docksidestage.dbflute.exbhv.MemberBhv;
import org.docksidestage.dbflute.exentity.Member;
import org.docksidestage.unit.UnitHarborTestCase;
import org.lastaflute.web.response.HtmlResponse;

/**
 * @author jflute
 */
public class MemberEditActionTest extends UnitHarborTestCase {

    @Resource
    private MemberBhv memberBhv;

    public void test_index() {
        // ## Arrange ##
        MemberEditAction action = new MemberEditAction() {
            @Override
            protected Class<?> myTokenGroupType() {
                return getClass();
            }
        };
        inject(action);
        int memberId = 1;
        Member member = memberBhv.selectByPK(memberId).get();

        // ## Act ##
        HtmlResponse response = action.index(memberId);

        // ## Assert ##
        TestingHtmlData htmlData = validateHtmlData(response);
        assertTrue(htmlData.isRoutingAsHtmlForward());
        MemberEditForm form = htmlData.requiredPushedForm(MemberEditForm.class);
        assertEquals(member.getMemberName(), form.memberName);
    }
}
