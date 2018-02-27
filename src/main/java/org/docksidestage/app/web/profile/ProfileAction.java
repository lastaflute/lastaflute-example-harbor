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
package org.docksidestage.app.web.profile;

import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.docksidestage.app.web.base.HarborBaseAction;
import org.docksidestage.app.web.profile.ProfileBean.PurchasedProductBean;
import org.docksidestage.dbflute.exbhv.MemberBhv;
import org.docksidestage.dbflute.exentity.Member;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;

/**
 * @author jflute
 * @author deco
 */
public class ProfileAction extends HarborBaseAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private MemberBhv memberBhv;

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Execute
    public HtmlResponse index() {
        Member member = selectMember();
        ProfileBean bean = mappingToBean(member);

        return asHtml(path_Profile_ProfileHtml).renderWith(data -> {
            data.register("bean", bean);
        });
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    private Member selectMember() {
        Integer memberId = getUserBean().get().getMemberId();
        Member member = memberBhv.selectEntity(cb -> {
            cb.setupSelect_MemberStatus();
            cb.setupSelect_MemberServiceAsOne().withServiceRank();
            cb.query().setMemberId_Equal(memberId);
        }).get();
        memberBhv.loadPurchase(member, purCB -> {
            purCB.setupSelect_Product();
            purCB.query().addOrderBy_PurchaseDatetime_Desc();
        });
        return member;
    }

    // ===================================================================================
    //                                                                             Mapping
    //                                                                             =======
    private ProfileBean mappingToBean(Member member) {
        ProfileBean bean = new ProfileBean(member);
        bean.purchaseList = member.getPurchaseList().stream().map(purchase -> {
            return new PurchasedProductBean(purchase);
        }).collect(Collectors.toList());
        return bean;
    }
}
