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
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;

/**
 * 会員追加アクション。
 * @author jflute
 */
public class MemberAddAction extends HarborBaseAction {

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

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Execute
    public HtmlResponse index() {
        return asHtml(path_Member_MemberAddJsp);
    }

    @Execute
    public HtmlResponse doAdd(MemberForm form) {
        Member member = new Member();
        member.setMemberId(form.memberId);
        member.setMemberName(form.memberName);
        member.setBirthdate(toDate(form.birthdate).orElse(null));
        member.setMemberStatusCodeAsMemberStatus(CDef.MemberStatus.codeOf(form.memberStatusCode));
        member.setMemberAccount(form.memberAccount);
        if (member.isMemberStatusCodeFormalized()) { // 区分値の判定は Entity の isなんとか() メソッドで by jflute
            member.setFormalizedDatetime(currentDateTime()); // 現在日時はTimeManagerから by jflute
        }
        member.setVersionNo(form.versionNo);
        memberBhv.insert(member);
        return redirect(MemberListAction.class);
    }

    // ===================================================================================
    //                                                                            Callback
    //                                                                            ========
    @Override
    public void hookFinally(ActionRuntime runtime) {
        if (runtime.isForwardToHtml()) {
            prepareListBox(runtime); // 会員ステータスなどリストボックスの構築
        }
        super.hookFinally(runtime);
    }

    // ===================================================================================
    //                                                                               Logic
    //                                                                               =====
    protected void prepareListBox(ActionRuntime runtime) { // ここはアプリによって色々かと by jflute
        ListResultBean<MemberStatus> statusList = memberStatusBhv.selectList(cb -> {
            cb.query().addOrderBy_DisplayOrder_Asc();
        });
        Map<String, String> statusMap = new LinkedHashMap<String, String>();
        statusList.forEach(status -> statusMap.put(status.getMemberStatusCode(), status.getMemberStatusName()));
        runtime.registerData("memberStatusMap", statusMap);
    }
}
