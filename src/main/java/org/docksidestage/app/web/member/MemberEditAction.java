/*
 * Copyright 2014-2015 the original author or authors.
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

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.dbflute.cbean.result.ListResultBean;
import org.docksidestage.app.web.base.HarborBaseAction;
import org.docksidestage.dbflute.allcommon.CDef;
import org.docksidestage.dbflute.exbhv.MemberBhv;
import org.docksidestage.dbflute.exbhv.MemberStatusBhv;
import org.docksidestage.dbflute.exentity.Member;
import org.docksidestage.dbflute.exentity.MemberStatus;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;

/**
 * @author jflute
 */
public class MemberEditAction extends HarborBaseAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                          DI Component
    //                                          ------------
    @Resource
    protected MemberBhv memberBhv;

    @Resource
    protected MemberStatusBhv memberStatusBhv;

    // -----------------------------------------------------
    //                                          Display Data
    //                                          ------------
    public Map<String, String> memberStatusMap;

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Execute
    public HtmlResponse index(Integer memberId, MemberForm form) {
        Member member = selectMember(memberId);
        form.memberId = member.getMemberId();
        form.memberName = member.getMemberName();
        form.memberAccount = member.getMemberAccount();
        form.memberStatusCode = member.getMemberStatusCode();
        form.birthdate = toStringDate(member.getBirthdate()).orElse(null);
        form.formalizedDate = toStringDate(member.getFormalizedDatetime()).orElse(null);
        form.latestLoginDatetime = toStringDateTime(member.getLatestLoginDatetime()).orElse(null);
        form.updateDatetime = toStringDateTime(member.getUpdateDatetime()).get();
        form.previousStatusCode = member.getMemberStatusCode(); // to determine new formalized member
        form.versionNo = member.getVersionNo();
        return asHtml(path_Member_MemberEditJsp).renderWith(data -> {
            data.register("memberStatusMap", prepareMemberStatusMap());
        });
    }

    @Execute
    public HtmlResponse doUpdate(MemberForm form) {
        validate(form, messages -> {} , () -> {
            return asHtml(path_Member_MemberEditJsp).renderWith(data -> {
                data.register("memberStatusMap", prepareMemberStatusMap());
            });
        });
        Member member = new Member();
        member.setMemberId(form.memberId);
        member.setMemberName(form.memberName);
        member.setBirthdate(toDate(form.birthdate).orElse(null));
        member.setMemberStatusCodeAsMemberStatus(toCls(CDef.MemberStatus.class, form.memberStatusCode).get());
        member.setMemberAccount(form.memberAccount);
        if (member.isMemberStatusCodeFormalized()) {
            toCls(CDef.MemberStatus.class, form.previousStatusCode).filter(cls -> cls.isShortOfFormalized()).ifPresent(cls -> {
                member.setFormalizedDatetime(currentDateTime());
            });
        } else if (member.isMemberStatusCode_ShortOfFormalized()) {
            member.setFormalizedDatetime(null);
        }
        member.setVersionNo(form.versionNo);
        memberBhv.update(member);
        return redirectById(MemberEditAction.class, member.getMemberId());
    }

    @Execute
    public HtmlResponse doDelete(MemberForm memberForm) {
        Member member = new Member();
        member.setMemberId(memberForm.memberId);
        member.setMemberStatusCode_Withdrawal();
        member.setVersionNo(memberForm.versionNo);
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
        }).get();
    }

    protected Map<String, String> prepareMemberStatusMap() {
        ListResultBean<MemberStatus> statusList = memberStatusBhv.selectList(cb -> {
            cb.query().addOrderBy_DisplayOrder_Asc();
        });
        Map<String, String> statusMap = new LinkedHashMap<String, String>();
        statusList.forEach(status -> {
            statusMap.put(status.getMemberStatusCode(), status.getMemberStatusName());
        });
        return statusMap;
    }
}
