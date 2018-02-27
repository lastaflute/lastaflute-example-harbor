/*
 * Copyright 2015-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.docksidestage.app.web.signup;

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
import org.lastaflute.core.util.LaStringUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.login.AllowAnyoneAccess;
import org.lastaflute.web.response.HtmlResponse;

/**
 * @author annie_pocket
 * @author jflute
 */
@AllowAnyoneAccess
public class SignupAction extends HarborBaseAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private Postbox postbox;
    @Resource
    private HarborConfig config;
    @Resource
    private MemberBhv memberBhv;
    @Resource
    private MemberSecurityBhv memberSecurityBhv;
    @Resource
    private MemberServiceBhv memberServiceBhv;
    @Resource
    private HarborLoginAssist loginAssist;
    @Resource
    private SignupTokenAssist signupTokenAssist;

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Execute
    public HtmlResponse index() {
        return asHtml(path_Signup_SignupHtml).useForm(SignupForm.class);
    }

    @Execute
    public HtmlResponse signup(SignupForm form) {
        validate(form, messages -> moreValidate(form, messages), () -> {
            return asHtml(path_Signup_SignupHtml);
        });
        Member member = insertProvisionalMember(form);
        String token = signupTokenAssist.saveSignupToken(member);
        sendSignupMail(form, token);
        return redirect(MypageAction.class).afterTxCommit(() -> { // for asynchronous DB access
            loginAssist.identityLogin(member.getMemberId(), op -> {}); // #simple_for_example no remember for now
        });
    }

    private void moreValidate(SignupForm form, HarborMessages messages) {
        if (LaStringUtil.isNotEmpty(form.memberAccount)) {
            int count = memberBhv.selectCount(cb -> {
                cb.query().setMemberAccount_Equal(form.memberAccount);
            });
            if (count > 0) {
                messages.addErrorsSignupAccountAlreadyExists("memberAccount");
            }
        }
    }

    private void sendSignupMail(SignupForm form, String token) {
        WelcomeMemberPostcard.droppedInto(postbox, postcard -> {
            postcard.setFrom(config.getMailAddressSupport(), "Harbor Support"); // #simple_for_example
            postcard.addTo(form.memberAccount + "@docksidestage.org"); // #simple_for_example
            postcard.setDomain(config.getServerDomain());
            postcard.setMemberName(form.memberName);
            postcard.setAccount(form.memberAccount);
            postcard.setToken(token);
            postcard.async();
            postcard.retry(3, 1000L);
            postcard.writeAuthor(this);
        });
    }

    @Execute
    public HtmlResponse register(String account, String token) { // from mail link
        signupTokenAssist.verifySignupTokenMatched(account, token);
        updateMemberAsFormalized(account);
        return redirect(SigninAction.class);
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    private Member insertProvisionalMember(SignupForm form) {
        Member member = new Member();
        member.setMemberName(form.memberName);
        member.setMemberAccount(form.memberAccount);
        member.setMemberStatusCode_Provisional();
        memberBhv.insert(member); // #simple_for_example same-name concurrent access as application exception

        MemberSecurity security = new MemberSecurity();
        security.setMemberId(member.getMemberId());
        security.setLoginPassword(loginAssist.encryptPassword(form.password));
        security.setReminderQuestion(form.reminderQuestion);
        security.setReminderAnswer(form.reminderAnswer);
        security.setReminderUseCount(0);
        memberSecurityBhv.insert(security);

        MemberService service = new MemberService();
        service.setMemberId(member.getMemberId());
        service.setServicePointCount(0);
        service.setServiceRankCode_Plastic();
        memberServiceBhv.insert(service);
        return member;
    }

    private void updateMemberAsFormalized(String account) {
        Member member = new Member();
        member.uniqueBy(account);
        member.setMemberStatusCode_Formalized();
        memberBhv.updateNonstrict(member);
    }
}
