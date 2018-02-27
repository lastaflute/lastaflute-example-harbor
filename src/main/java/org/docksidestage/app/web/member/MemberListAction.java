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

import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalThing;
import org.docksidestage.app.web.base.HarborBaseAction;
import org.docksidestage.app.web.base.paging.PagingAssist;
import org.docksidestage.dbflute.exbhv.MemberBhv;
import org.docksidestage.dbflute.exentity.Member;
import org.lastaflute.core.util.LaStringUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;

/**
 * @author jflute
 */
public class MemberListAction extends HarborBaseAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private MemberBhv memberBhv;
    @Resource
    private PagingAssist pagingAssist;

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Execute
    public HtmlResponse index(OptionalThing<Integer> pageNumber, MemberSearchForm form) {
        validate(form, messages -> {}, () -> {
            return asHtml(path_Member_MemberListHtml);
        });
        PagingResultBean<Member> page = selectMemberPage(pageNumber.orElse(1), form);
        PagingResultBean<MemberSearchRowBean> beans = page.mappingList(member -> {
            return mappingToBean(member);
        });
        return asHtml(path_Member_MemberListHtml).renderWith(data -> {
            data.register("beans", beans);
            pagingAssist.registerPagingNavi(data, page, form);
        });
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    protected PagingResultBean<Member> selectMemberPage(int pageNumber, MemberSearchForm form) {
        verifyOrIllegalTransition("The pageNumber should be positive number: " + pageNumber, pageNumber > 0);
        return memberBhv.selectPage(cb -> {
            cb.setupSelect_MemberStatus();
            cb.specify().derivedPurchase().count(purchaseCB -> {
                purchaseCB.specify().columnPurchaseId();
            }, Member.ALIAS_purchaseCount);
            if (LaStringUtil.isNotEmpty(form.memberName)) {
                cb.query().setMemberName_LikeSearch(form.memberName, op -> op.likeContain());
            }
            if (LaStringUtil.isNotEmpty(form.purchaseProductName) || form.unpaid) {
                cb.query().existsPurchase(purchaseCB -> {
                    if (LaStringUtil.isNotEmpty(form.purchaseProductName)) {
                        purchaseCB.query().queryProduct().setProductName_LikeSearch(form.purchaseProductName, op -> op.likeContain());
                    }
                    if (form.unpaid) {
                        purchaseCB.query().setPaymentCompleteFlg_Equal_False();
                    }
                });
            }
            if (form.memberStatus != null) {
                cb.query().setMemberStatusCode_Equal_AsMemberStatus(form.memberStatus);
            }
            if (form.formalizedFrom != null || form.formalizedTo != null) {
                LocalDateTime fromTime = form.formalizedFrom != null ? form.formalizedFrom.atStartOfDay() : null;
                LocalDateTime toTime = form.formalizedFrom != null ? form.formalizedFrom.atStartOfDay() : null;
                cb.query().setFormalizedDatetime_FromTo(fromTime, toTime, op -> op.compareAsDate().allowOneSide());
            }
            cb.query().addOrderBy_UpdateDatetime_Desc();
            cb.query().addOrderBy_MemberId_Asc();
            cb.paging(4, pageNumber);
        });
    }

    // ===================================================================================
    //                                                                             Mapping
    //                                                                             =======
    private MemberSearchRowBean mappingToBean(Member member) {
        MemberSearchRowBean bean = new MemberSearchRowBean();
        bean.memberId = member.getMemberId();
        bean.memberName = member.getMemberName();
        member.getMemberStatus().alwaysPresent(status -> {
            bean.memberStatusName = status.getMemberStatusName();
        });
        LocalDateTime formalizedDatetime = member.getFormalizedDatetime();
        if (formalizedDatetime != null) {
            bean.formalizedDate = formalizedDatetime.toLocalDate();
        }
        bean.updateDatetime = member.getUpdateDatetime();
        bean.withdrawalMember = member.isMemberStatusCodeWithdrawal();
        bean.purchaseCount = member.getPurchaseCount();
        return bean;
    }
}
