package org.docksidestage.app.web.signup;

import java.util.Random;

import javax.annotation.Resource;

import org.docksidestage.app.web.base.HarborBaseAction;
import org.docksidestage.app.web.base.login.HarborLoginAssist;
import org.docksidestage.app.web.mypage.MypageAction;
import org.docksidestage.app.web.signin.SigninAction;
import org.docksidestage.dbflute.exbhv.MemberBhv;
import org.docksidestage.dbflute.exbhv.MemberSecurityBhv;
import org.docksidestage.dbflute.exbhv.MemberServiceBhv;
import org.docksidestage.dbflute.exentity.Member;
import org.docksidestage.dbflute.exentity.MemberSecurity;
import org.docksidestage.dbflute.exentity.MemberService;
import org.docksidestage.mylasta.action.HarborMessages;
import org.docksidestage.mylasta.direction.HarborConfig;
import org.docksidestage.mylasta.mail.member.WelcomeMemberPostcard;
import org.lastaflute.core.mail.Postbox;
import org.lastaflute.core.security.PrimaryCipher;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;

/**
 * @author annie_pocket
 * @author jflute
 */
public class SignupAction extends HarborBaseAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
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
    @Execute
    public HtmlResponse index() {
        return asHtml(path_Signup_SignupHtml).useForm(SignupForm.class);
    }

    @Execute
    public HtmlResponse signup(SignupForm form) {
        validate(form, messages -> {
            moreValidate(form, messages);
        } , () -> {
            return asHtml(path_Signup_SignupHtml);
        });
        Integer memberId = newMember(form);
        harborLoginAssist.identityLogin(memberId, op -> {}); // no remember-me here

        WelcomeMemberPostcard.droppedInto(postbox, postcard -> {
            postcard.setFrom(harborConfig.getMailAddressSupport(), "Harbor Support");
            postcard.addTo(deriveMemberMailAddress(form));
            postcard.setDomain(harborConfig.getServerDomain());
            postcard.setMemberName(form.memberName);
            postcard.setAccount(form.memberAccount);
            postcard.setToken(generateToken());
        });
        return redirect(MypageAction.class);
    }

    private void moreValidate(SignupForm form, HarborMessages messages) {
        if (isNotEmpty(form.memberAccount)) {
            int count = memberBhv.selectCount(cb -> {
                cb.query().setMemberAccount_Equal(form.memberAccount);
            });
            if (count > 0) {
                messages.addErrorsSignupAccountAlreadyExists("memberAccount");
            }
        }
    }

    @Execute
    public HtmlResponse register(String account, String token) { // from mail link
        Member member = new Member();
        member.setMemberAccount(account);
        member.setMemberStatusCode_Formalized();
        memberBhv.update(member);
        return redirect(SigninAction.class);
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private Integer newMember(SignupForm form) {
        Member member = new Member();
        member.setMemberName(form.memberName);
        member.setMemberAccount(form.memberAccount);
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

    private String deriveMemberMailAddress(SignupForm form) {
        return form.memberAccount + "@harborstage.org"; // #simple_for_example
    }

    private String generateToken() {
        return primaryCipher.encrypt(String.valueOf(new Random().nextInt())); // simple for example
    }
}
