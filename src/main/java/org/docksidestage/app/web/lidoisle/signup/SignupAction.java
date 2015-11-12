package org.docksidestage.app.web.lidoisle.signup;

import javax.annotation.Resource;

import org.docksidestage.app.web.base.HarborBaseAction;
import org.docksidestage.app.web.base.login.HarborLoginAssist;
import org.docksidestage.dbflute.exbhv.MemberBhv;
import org.docksidestage.dbflute.exbhv.MemberSecurityBhv;
import org.docksidestage.dbflute.exbhv.MemberServiceBhv;
import org.docksidestage.dbflute.exentity.Member;
import org.docksidestage.dbflute.exentity.MemberSecurity;
import org.docksidestage.dbflute.exentity.MemberService;
import org.docksidestage.mylasta.direction.HarborConfig;
import org.lastaflute.core.mail.Postbox;
import org.lastaflute.core.security.PrimaryCipher;

/**
 * @author annie_pocket
 * @author jflute
 */
public class SignupAction extends HarborBaseAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                          DI Component
    //                                          ------------
    @Resource
    private MemberBhv memberBhv;
    @Resource
    private MemberSecurityBhv memberSecurityBhv;
    @Resource
    private MemberServiceBhv memberServiceBhv;
    @Resource
    private HarborLoginAssist harborLoginAssist;
    @Resource
    private Postbox postbox;
    @Resource
    private HarborConfig harborConfig;
    @Resource
    private PrimaryCipher primaryCipher;

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======

    // TODO (s.tadokoro) implement
    //    @Execute
    //    public HtmlResponse index() {
    //        return asHtml(path_Signup_SignupJsp).useForm(SignupAction.class);
    //    }
    //
    //    @Execute
    //    public HtmlResponse signup(SignupForm form) {
    //        validate(form, messages -> {
    //            int count = memberBhv.selectCount(cb -> {
    //                cb.query().setMemberAccount_Equal(form.account);
    //            });
    //            if (count > 0) {
    //                messages.addErrorsSignupAccountAlreadyExists("account");
    //            }
    //        } , () -> {
    //            return asHtml(path_Signup_SignupJsp);
    //        });
    //        Integer memberId = newMember(form);
    //        harborLoginAssist.identityLogin(memberId.longValue(), op -> {}); // no remember-me here
    //
    //        WelcomeMemberPostcard.droppedInto(postbox, postcard -> {
    //            postcard.setFrom(harborConfig.getMailAddressSupport(), "Harbor Support");
    //            postcard.addTo(deriveMemberMailAddress(form));
    //            postcard.setDomain(harborConfig.getServerDomain());
    //            postcard.setMemberName(form.name);
    //            postcard.setToken(generateToken());
    //        });
    //        return redirect(MypageAction.class);
    //    }
    //
    //    private String deriveMemberMailAddress(SignupForm form) {
    //        return form.account + "@docksidestage.org"; // #simple_for_example
    //    }
    //
    //    private String generateToken() {
    //        return primaryCipher.encrypt(String.valueOf(new Random().nextInt())); // #simple_for_example
    //    }
    //
    //    @Execute
    //    public HtmlResponse register() {
    //        return asHtml(path_Signup_SignupJsp).useForm(SignupAction.class);
    //    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private Integer newMember(SignupForm form) {
        Member member = new Member();
        member.setMemberAccount(form.account);
        member.setMemberName(form.name);
        member.setMemberStatusCode_Provisional();
        memberBhv.insert(member);

        MemberSecurity security = new MemberSecurity();
        security.setMemberId(member.getMemberId());
        security.setLoginPassword(harborLoginAssist.encryptPassword(form.password));
        security.setReminderQuestion(form.reminderQuestion);
        security.setReminderAnswer(form.reminderAnswer);
        security.setReminderUseCount(0);
        memberSecurityBhv.insert(security);

        MemberService service = new MemberService();
        service.setMemberId(member.getMemberId());
        service.setServicePointCount(0);
        service.setServiceRankCode_Plastic();
        memberServiceBhv.insert(service);
        return member.getMemberId();
    }
}
