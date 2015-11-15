package org.docksidestage.app.web.withdrawal;

import javax.annotation.Resource;

import org.docksidestage.app.web.base.HarborBaseAction;
import org.docksidestage.app.web.signout.SignoutAction;
import org.docksidestage.dbflute.exbhv.MemberBhv;
import org.docksidestage.dbflute.exbhv.MemberWithdrawalBhv;
import org.docksidestage.dbflute.exentity.Member;
import org.docksidestage.dbflute.exentity.MemberWithdrawal;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;

/**
 * @author annie_pocket
 * @author jflute
 */
public class WithdrawalAction extends HarborBaseAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                          DI Component
    //                                          ------------
    @Resource
    private MemberBhv memberBhv;
    @Resource
    private MemberWithdrawalBhv memberWithdrawalBhv;

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Execute
    public HtmlResponse index() {
        return asHtml(path_Withdrawal_WithdrawalHtml).useForm(WithdrawalForm.class);
    }

    @Execute
    public HtmlResponse confirm(WithdrawalForm form) {
        validate(form, messages -> {
            if (form.reasonCode == null && isEmpty(form.reasonInput)) {
                messages.addConstraintsRequiredMessage("reasonCode");
            }
        } , () -> {
            return asHtml(path_Withdrawal_WithdrawalHtml);
        });
        return asHtml(path_Withdrawal_WithdrawalConfirmHtml);
    }

    @Execute
    public HtmlResponse done(WithdrawalForm form) {
        validate(form, message -> {} , () -> {
            return asHtml(path_Withdrawal_WithdrawalHtml);
        });
        Integer memberId = getUserBean().get().getMemberId();

        MemberWithdrawal withdrawal = new MemberWithdrawal();
        withdrawal.setMemberId(memberId);
        withdrawal.setWithdrawalReasonCodeAsWithdrawalReason(form.reasonCode);
        withdrawal.setWithdrawalReasonInputText(form.reasonInput);
        withdrawal.setWithdrawalDatetime(currentDateTime());
        memberWithdrawalBhv.insert(withdrawal);

        // update status of member
        Member member = new Member();
        member.setMemberId(memberId);
        member.setMemberStatusCode_Withdrawal();
        memberBhv.updateNonstrict(member);

        return redirect(SignoutAction.class);
    }
}
