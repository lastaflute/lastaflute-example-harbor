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
package org.docksidestage.app.web.member;

import javax.annotation.Resource;

import org.docksidestage.app.web.base.HarborBaseAction;
import org.docksidestage.dbflute.exbhv.MemberBhv;
import org.docksidestage.dbflute.exentity.Member;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;

/**
 * @author jflute
 */
public class MemberEditAction extends HarborBaseAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private MemberBhv memberBhv;

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Execute
    public HtmlResponse index(Integer memberId) {
        saveToken();
        Member member = selectMember(memberId);
        return asHtml(path_Member_MemberEditHtml).useForm(MemberEditForm.class, op -> op.setup(form -> {
            form.memberId = member.getMemberId();
            form.memberName = member.getMemberName();
            form.memberAccount = member.getMemberAccount();
            form.memberStatus = member.getMemberStatusCodeAsMemberStatus();
            form.birthdate = member.getBirthdate();
            form.formalizedDate = toDate(member.getFormalizedDatetime()).orElse(null); // to yyyyMMdd
            form.latestLoginDatetime = member.getLatestLoginDatetime();
            form.updateDatetime = member.getUpdateDatetime();
            form.previousStatus = member.getMemberStatusCodeAsMemberStatus(); // to determine new formalized member
            form.versionNo = member.getVersionNo();
        }));
    }

    @Execute
    public HtmlResponse update(MemberEditForm form) {
        verifyToken(() -> {
            return asHtml(path_Member_MemberEditHtml);
        });
        validate(form, messages -> {} , () -> {
            return asHtml(path_Member_MemberEditHtml);
        });
        Member member = new Member();
        member.setMemberId(form.memberId);
        member.setMemberName(form.memberName);
        member.setBirthdate(toDate(form.birthdate).orElse(null));
        member.setMemberStatusCodeAsMemberStatus(form.memberStatus);
        member.setMemberAccount(form.memberAccount);
        if (member.isMemberStatusCodeFormalized()) {
            if (form.previousStatus.isShortOfFormalized()) {
                member.setFormalizedDatetime(currentDateTime());
            }
        } else if (member.isMemberStatusCode_ShortOfFormalized()) {
            member.setFormalizedDatetime(null);
        }
        member.setVersionNo(form.versionNo);
        memberBhv.update(member);
        return redirectById(MemberEditAction.class, member.getMemberId());
    }

    @Execute
    public HtmlResponse withdrawal(MemberEditForm form) {
        validate(form, messages -> {} , () -> {
            return asHtml(path_Member_MemberEditHtml);
        });
        Member member = new Member();
        member.setMemberId(form.memberId);
        member.setMemberStatusCode_Withdrawal();
        member.setVersionNo(form.versionNo);
        memberBhv.update(member);
        return redirect(MemberListAction.class);
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    protected Member selectMember(Integer memberId) {
        return memberBhv.selectEntity(cb -> {
            cb.specify().derivedMemberLogin().max(loginCB -> {
                loginCB.specify().columnLoginDatetime();
            } , Member.ALIAS_latestLoginDatetime);
            cb.query().setMemberId_Equal(memberId);
            cb.query().setMemberStatusCode_InScope_ServiceAvailable();
        }).get(); // exclusive control if not found
    }
}
