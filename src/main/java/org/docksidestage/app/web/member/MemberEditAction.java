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

import java.time.LocalDateTime;

import javax.annotation.Resource;

import org.docksidestage.app.web.base.HarborBaseAction;
import org.docksidestage.dbflute.exbhv.MemberBhv;
import org.docksidestage.dbflute.exentity.Member;
import org.lastaflute.core.time.TimeManager;
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
    private TimeManager timeManager;
    @Resource
    private MemberBhv memberBhv;

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Execute
    public HtmlResponse index(Integer memberId) {
        Member member = selectMember(memberId);
        saveToken();
        return asHtml(path_Member_MemberEditHtml).useForm(MemberEditForm.class, op -> op.setup(form -> {
            mappingToForm(member, form);
        }));
    }

    @Execute
    public HtmlResponse update(MemberEditForm form) {
        validate(form, messages -> {}, () -> {
            return asHtml(path_Member_MemberEditHtml);
        });
        verifyToken(() -> {
            return asHtml(path_Error_ShowErrorsHtml);
        });
        Member member = updateMember(form);
        return redirectById(MemberEditAction.class, member.getMemberId());
    }

    @Execute
    public HtmlResponse withdrawal(MemberEditForm form) {
        validate(form, messages -> {}, () -> {
            return asHtml(path_Member_MemberEditHtml);
        });
        updateMemberToWithdrawal(form);
        return redirect(MemberListAction.class);
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    private Member selectMember(Integer memberId) {
        return memberBhv.selectEntity(cb -> {
            cb.specify().derivedMemberLogin().max(loginCB -> {
                loginCB.specify().columnLoginDatetime();
            }, Member.ALIAS_latestLoginDatetime);
            cb.query().setMemberId_Equal(memberId);
            cb.query().setMemberStatusCode_InScope_ServiceAvailable();
        }).get(); // automatically exclusive controlled if not found
    }

    // ===================================================================================
    //                                                                              Update
    //                                                                              ======
    private Member updateMember(MemberEditForm form) {
        Member member = new Member();
        member.setMemberId(form.memberId);
        member.setMemberName(form.memberName);
        member.setBirthdate(form.birthdate); // may be updated as null
        member.setMemberStatusCodeAsMemberStatus(form.memberStatus);
        member.setMemberAccount(form.memberAccount);
        if (member.isMemberStatusCodeFormalized()) {
            if (form.previousStatus.isShortOfFormalized()) {
                member.setFormalizedDatetime(timeManager.currentDateTime());
            }
        } else if (member.isMemberStatusCode_ShortOfFormalized()) {
            member.setFormalizedDatetime(null);
        }
        member.setVersionNo(form.versionNo);
        memberBhv.update(member);
        return member;
    }

    private void updateMemberToWithdrawal(MemberEditForm form) {
        Member member = new Member();
        member.setMemberId(form.memberId);
        member.setMemberStatusCode_Withdrawal();
        member.setVersionNo(form.versionNo);
        memberBhv.update(member);
    }

    // ===================================================================================
    //                                                                             Mapping
    //                                                                             =======
    private void mappingToForm(Member member, MemberEditForm form) {
        form.memberId = member.getMemberId();
        form.memberName = member.getMemberName();
        form.memberAccount = member.getMemberAccount();
        form.memberStatus = member.getMemberStatusCodeAsMemberStatus();
        form.birthdate = member.getBirthdate();
        LocalDateTime formalizedDatetime = member.getFormalizedDatetime();
        if (formalizedDatetime != null) {
            form.formalizedDate = formalizedDatetime.toLocalDate();
        }
        form.latestLoginDatetime = member.getLatestLoginDatetime();
        form.updateDatetime = member.getUpdateDatetime();
        form.previousStatus = member.getMemberStatusCodeAsMemberStatus(); // to determine new formalized member
        form.versionNo = member.getVersionNo();
    }
}
