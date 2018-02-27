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
package org.docksidestage.app.web.lido.auth;

import java.util.Random;

import javax.annotation.Resource;

import org.docksidestage.app.web.base.HarborBaseAction;
import org.docksidestage.app.web.base.login.HarborLoginAssist;
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
import org.lastaflute.core.util.LaStringUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.login.AllowAnyoneAccess;
import org.lastaflute.web.login.credential.UserPasswordCredential;
import org.lastaflute.web.response.JsonResponse;
import org.lastaflute.web.servlet.request.ResponseManager;
import org.lastaflute.web.servlet.session.SessionManager;

// the 'lido' package is example for JSON API in simple project
// client application is riot.js in lidoisle directory
/**
 * @author s.tadokoro
 * @author jflute
 */
public class LidoAuthAction extends HarborBaseAction {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final String SIGNUP_TOKEN_KEY = "signupToken";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private PrimaryCipher primaryCipher;
    @Resource
    private Postbox postbox;
    @Resource
    private ResponseManager responseManager;
    @Resource
    private SessionManager sessionManager;
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

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    // -----------------------------------------------------
    //                                           Sign in/out
    //                                           -----------
    @Execute
    @AllowAnyoneAccess
    public JsonResponse<Void> signin(SigninBody body) {
        validateApi(body, messages -> {});
        loginAssist.login(new UserPasswordCredential(body.account, body.password), op -> {});
        return JsonResponse.asEmptyBody();
    }

    @Execute
    public JsonResponse<Void> signout() {
        loginAssist.logout();
        return JsonResponse.asEmptyBody();
    }

    // -----------------------------------------------------
    //                                               Sign up
    //                                               -------
    @Execute
    public JsonResponse<Void> signup(SignupBody body) {
        validateApi(body, messages -> {
            moreValidate(body, messages);
        });
        Integer memberId = newMember(body);
        loginAssist.identityLogin(memberId, op -> {}); // no remember-me here

        String signupToken = saveSignupToken();
        sendSignupMail(body, signupToken);
        return JsonResponse.asEmptyBody();
    }

    private void moreValidate(SignupBody body, HarborMessages messages) {
        if (LaStringUtil.isNotEmpty(body.memberAccount)) {
            int count = memberBhv.selectCount(cb -> {
                cb.query().setMemberAccount_Equal(body.memberAccount);
            });
            if (count > 0) {
                messages.addErrorsSignupAccountAlreadyExists("memberAccount");
            }
        }
    }

    private String saveSignupToken() {
        String token = primaryCipher.encrypt(String.valueOf(new Random().nextInt())); // #simple_for_example
        sessionManager.setAttribute(SIGNUP_TOKEN_KEY, token);
        return token;
    }

    private void sendSignupMail(SignupBody body, String signupToken) {
        WelcomeMemberPostcard.droppedInto(postbox, postcard -> {
            postcard.setFrom(config.getMailAddressSupport(), "Harbor Support"); // #simple_for_example
            postcard.addTo(body.memberAccount + "@docksidestage.org"); // #simple_for_example
            postcard.setDomain(config.getServerDomain());
            postcard.setMemberName(body.memberName);
            postcard.setAccount(body.memberAccount);
            postcard.setToken(signupToken);
        });
    }

    @Execute
    public JsonResponse<Void> register(String account, String token) { // from mail link
        verifySignupTokenMatched(account, token);
        updateStatusFormalized(account);
        return JsonResponse.asEmptyBody();
    }

    private void verifySignupTokenMatched(String account, String token) {
        String saved = sessionManager.getAttribute(SIGNUP_TOKEN_KEY, String.class).orElseTranslatingThrow(cause -> {
            return responseManager.new404("Not found the signupToken in session: " + account, op -> op.cause(cause));
        });
        if (!saved.equals(token)) {
            throw responseManager.new404("Unmatched signupToken in session: saved=" + saved + ", requested=" + token);
        }
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    private Integer newMember(SignupBody body) {
        Member member = new Member();
        member.setMemberAccount(body.memberAccount);
        member.setMemberName(body.memberName);
        member.setMemberStatusCode_Provisional();
        memberBhv.insert(member); // #simple_for_example same-name concurrent access as application exception

        MemberSecurity security = new MemberSecurity();
        security.setMemberId(member.getMemberId());
        security.setLoginPassword(loginAssist.encryptPassword(body.password));
        security.setReminderQuestion(body.reminderQuestion);
        security.setReminderAnswer(body.reminderAnswer);
        security.setReminderUseCount(0);
        memberSecurityBhv.insert(security);

        MemberService service = new MemberService();
        service.setMemberId(member.getMemberId());
        service.setServicePointCount(0);
        service.setServiceRankCode_Plastic();
        memberServiceBhv.insert(service);
        return member.getMemberId();
    }

    private void updateStatusFormalized(String account) {
        Member member = new Member();
        member.setMemberAccount(account);
        member.setMemberStatusCode_Formalized();
        memberBhv.updateNonstrict(member);
    }
}