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
package org.docksidestage.app.web.profile;

import java.util.ArrayList;

import javax.annotation.Resource;

import org.docksidestage.app.web.base.HarborBaseAction;
import org.docksidestage.app.web.profile.ProfileBean.PurchaseInfo;
import org.docksidestage.dbflute.exbhv.MemberBhv;
import org.docksidestage.dbflute.exentity.Member;
import org.docksidestage.dbflute.exentity.Product;
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
    protected MemberBhv memberBhv;

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Execute
    public HtmlResponse index(ProfileForm form) {
        validate(form, messages -> {} , () -> {
            return asHtml(path_Error_ErrorMessageJsp);
        });

        Integer memberId = getUserBean().get().getMemberId();
        Member member = memberBhv.selectEntity(cb -> {
            cb.setupSelect_MemberStatus();
            cb.setupSelect_MemberServiceAsOne().withServiceRank();
            cb.query().setMemberId_Equal(memberId);
        }).get();
        memberBhv.loadPurchase(member, purCB -> {
            purCB.setupSelect_Product();
        });

        ProfileBean bean = new ProfileBean();
        bean.memberName = member.getMemberName();
        bean.memberStatusName = member.getMemberStatus().get().getMemberStatusName();
        bean.servicePointCount = String.valueOf(member.getMemberServiceAsOne().get().getServicePointCount());
        bean.serviceRankName = member.getMemberServiceAsOne().get().getServiceRankCode();
        bean.purchaseList = new ArrayList<>();
        member.getPurchaseList().forEach(purchase -> {
            Product product = purchase.getProduct().get();
            PurchaseInfo purchaseInfo = bean.new PurchaseInfo();
            purchaseInfo.setPurchaseInfo(product.getProductName(), product.getRegularPrice(), purchase.getPurchaseDatetime());
            bean.purchaseList.add(purchaseInfo);
        });

        return asHtml(path_Profile_ProfileJsp).renderWith(data -> {
            data.register("beans", bean);
        });
    }
}
