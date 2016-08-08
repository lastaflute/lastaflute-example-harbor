package org.docksidestage.app.web.withdrawal;

import javax.annotation.Resource;

import org.docksidestage.app.web.base.HarborBaseAction;
import org.docksidestage.app.web.signout.SignoutAction;
import org.docksidestage.dbflute.exbhv.MemberBhv;
import org.docksidestage.dbflute.exbhv.MemberWithdrawalBhv;
import org.docksidestage.dbflute.exentity.Member;
import org.docksidestage.dbflute.exentity.MemberWithdrawal;
import org.docksidestage.mylasta.action.HarborMessages;
import org.lastaflute.core.time.TimeManager;
import org.lastaflute.core.util.LaStringUtil;
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
    @Resource
    private TimeManager timeManager;
    @Resource
    private MemberBhv memberBhv;
    @Resource
    private MemberWithdrawalBhv memberWithdrawalBhv;

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Execute
    public HtmlResponse index() {
        return asEntryHtml();
    }

    @Execute
    public HtmlResponse confirm(WithdrawalForm form) {
        validate(form, messages -> moreValidation(form, messages), () -> {
            return asEntryHtml();
        });
        return asConfirmHtml();
    }

    private void moreValidation(WithdrawalForm form, HarborMessages messages) {
        if (form.selectedReason == null && LaStringUtil.isEmpty(form.reasonInput)) {
            messages.addConstraintsRequiredMessage("selectedReason");
        }
    }

    @Execute
    public HtmlResponse done(WithdrawalForm form) {
        validate(form, message -> {}, () -> {
            return asEntryHtml();
        });
        Integer memberId = getUserBean().get().getMemberId();
        insertWithdrawal(form, memberId);
        updateStatusWithdrawal(memberId);
        return redirect(SignoutAction.class);
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    private void insertWithdrawal(WithdrawalForm form, Integer memberId) {
        MemberWithdrawal withdrawal = new MemberWithdrawal();
        withdrawal.setMemberId(memberId);
        withdrawal.setWithdrawalReasonCodeAsWithdrawalReason(form.selectedReason);
        withdrawal.setWithdrawalReasonInputText(form.reasonInput);
        withdrawal.setWithdrawalDatetime(timeManager.currentDateTime());
        memberWithdrawalBhv.insert(withdrawal);
    }

    private void updateStatusWithdrawal(Integer memberId) {
        Member member = new Member();
        member.setMemberId(memberId);
        member.setMemberStatusCode_Withdrawal();
        memberBhv.updateNonstrict(member);
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private HtmlResponse asEntryHtml() {
        return asHtml(path_Withdrawal_WithdrawalEntryHtml);
    }

    private HtmlResponse asConfirmHtml() {
        return asHtml(path_Withdrawal_WithdrawalConfirmHtml);
    }
}
