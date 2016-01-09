/*
 * Copyright 2015-2016 the original author or authors.
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

import javax.annotation.Resource;

import org.docksidestage.app.web.base.HarborBaseAction;
import org.docksidestage.app.web.base.login.HarborLoginAssist;
import org.docksidestage.dbflute.exbhv.MemberBhv;
import org.docksidestage.dbflute.exbhv.MemberSecurityBhv;
import org.docksidestage.dbflute.exbhv.MemberServiceBhv;
import org.docksidestage.dbflute.exentity.Member;
import org.docksidestage.dbflute.exentity.MemberSecurity;
import org.docksidestage.dbflute.exentity.MemberService;
import org.lastaflute.core.mail.Postbox;
import org.lastaflute.web.Execute;
import org.lastaflute.web.login.AllowAnyoneAccess;
import org.lastaflute.web.response.JsonResponse;

/**
 * @author jflute
 * @author iwamatsu0430
 * @author s.tadokoro
 */
public class LidoAuthAction extends HarborBaseAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private HarborLoginAssist harborLoginAssist;
    @Resource
    private MemberBhv memberBhv;
    @Resource
    private MemberSecurityBhv memberSecurityBhv;
    @Resource
    private MemberServiceBhv memberServiceBhv;
    @Resource
    private Postbox postbox;

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======

    @Execute
    @AllowAnyoneAccess
    public JsonResponse<Object> signin(SigninBody body) {
        validateApi(body, messages -> {});
        String account = body.account;
        String password = body.password;
        harborLoginAssist.login(account, password, op -> {});
        return JsonResponse.asEmptyBody();
    }

    @Execute
    @AllowAnyoneAccess
    public JsonResponse<Object> signout() {
        harborLoginAssist.logout();
        return JsonResponse.asEmptyBody();
    }

    @Execute
    public JsonResponse<Object> register(SignupBody body) {
        validateApi(body, messages -> {
            int count = memberBhv.selectCount(cb -> {
                cb.query().setMemberAccount_Equal(body.memberAccount);
            });
            if (count > 0) {
                messages.addErrorsSignupAccountAlreadyExists("account");
            }
        });
        Integer memberId = newMember(body);
        harborLoginAssist.identityLogin(memberId.longValue(), op -> {}); // no remember-me here
        return JsonResponse.asEmptyBody();
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private Integer newMember(SignupBody body) {
        Member member = new Member();
        member.setMemberAccount(body.memberAccount);
        member.setMemberName(body.memberName);
        member.setMemberStatusCode_Provisional();
        memberBhv.insert(member);

        MemberSecurity security = new MemberSecurity();
        security.setMemberId(member.getMemberId());
        security.setLoginPassword(harborLoginAssist.encryptPassword(body.password));
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
}