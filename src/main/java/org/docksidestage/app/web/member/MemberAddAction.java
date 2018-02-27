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
package org.docksidestage.app.web.member;

import javax.annotation.Resource;

import org.docksidestage.app.web.base.HarborBaseAction;
import org.docksidestage.dbflute.allcommon.CDef;
import org.docksidestage.dbflute.exbhv.MemberBhv;
import org.docksidestage.dbflute.exentity.Member;
import org.lastaflute.core.time.TimeManager;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;

/**
 * @author jflute
 */
public class MemberAddAction extends HarborBaseAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private TimeManager timeManager;
    @Resource
    private MemberBhv memberBhv;

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Execute
    public HtmlResponse index() {
        saveToken();
        return asHtml(path_Member_MemberAddHtml).useForm(MemberAddForm.class, op -> op.setup(form -> {
            form.memberStatus = CDef.MemberStatus.Provisional; // as default
        }));
    }

    @Execute
    public HtmlResponse register(MemberAddForm form) {
        validate(form, messages -> {}, () -> {
            return asHtml(path_Member_MemberAddHtml);
        });
        verifyToken(() -> {
            return asHtml(path_Error_ShowErrorsHtml);
        });
        insertMember(form);
        return redirect(MemberListAction.class);
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    private void insertMember(MemberAddForm form) {
        Member member = new Member();
        member.setMemberName(form.memberName);
        member.setMemberAccount(form.memberAccount);
        member.setBirthdate(form.birthdate);
        member.setMemberStatusCodeAsMemberStatus(form.memberStatus);
        if (member.isMemberStatusCodeFormalized()) {
            member.setFormalizedDatetime(timeManager.currentDateTime());
        }
        memberBhv.insert(member);
    }
}
