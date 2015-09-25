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
package org.docksidestage.app.web.member.purchase;

import java.util.List;

import javax.annotation.Resource;

import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalEntity;
import org.docksidestage.app.web.base.HarborBaseAction;
import org.docksidestage.dbflute.exbhv.MemberBhv;
import org.docksidestage.dbflute.exbhv.PurchaseBhv;
import org.docksidestage.dbflute.exentity.Member;
import org.docksidestage.dbflute.exentity.Purchase;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;

/**
 * @author jflute
 */
public class MemberPurchaseListAction extends HarborBaseAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                          DI Component
    //                                          ------------
    @Resource
    private MemberBhv memberBhv;

    @Resource
    private PurchaseBhv purchaseBhv;

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Execute
    public HtmlResponse index(Integer memberId, Integer pageNumber, MemberPurchaseListForm form) {
        MemberPurchaseHeaderBean headerBean = selectMember(memberId).map(member -> {
            return new MemberPurchaseHeaderBean(member);
        }).get();
        PagingResultBean<Purchase> page = selectPurchasePage(memberId, pageNumber);
        List<MemberPurchaseSearchRowBean> beans = page.mappingList(purchase -> {
            return mappingToBean(purchase);
        });
        return asHtml(path_MemberPurchase_MemberPurchaseListJsp).renderWith(data -> {
            data.register("headerBean", headerBean);
            data.register("beans", beans);
            registerPagingNavi(data, page, form);
        });
    }

    @Execute
    public HtmlResponse doDelete(MemberPurchaseListForm form) {
        validate(form, messages -> {} , () -> {
            return asHtml(path_MemberPurchase_MemberPurchaseListJsp);
        });
        Purchase purchase = new Purchase();
        purchase.setPurchaseId(form.purchaseId);
        purchaseBhv.deleteNonstrict(purchase); // no optimistic lock
        return redirect(getClass());
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    protected OptionalEntity<Member> selectMember(Integer memberId) {
        return memberBhv.selectEntity(cb -> {
            cb.query().setMemberId_Equal(memberId);
        });
    }

    protected PagingResultBean<Purchase> selectPurchasePage(Integer memberId, Integer pageNumber) {
        return purchaseBhv.selectPage(cb -> {
            cb.setupSelect_Product();
            cb.query().setMemberId_Equal(memberId);
            cb.query().addOrderBy_PurchaseDatetime_Desc();
            cb.paging(getPagingPageSize(), pageNumber);
        });
    }

    // ===================================================================================
    //                                                                             Mapping
    //                                                                             =======
    protected MemberPurchaseSearchRowBean mappingToBean(Purchase purchase) {
        MemberPurchaseSearchRowBean bean = new MemberPurchaseSearchRowBean();
        bean.purchaseId = purchase.getPurchaseId();
        bean.purchaseDatetime = purchase.getPurchaseDatetime();
        bean.productName = purchase.getProduct().get().getProductName();
        bean.purchasePrice = purchase.getPurchasePrice();
        bean.purchaseCount = purchase.getPurchaseCount();
        bean.paymentComplete = purchase.isPaymentCompleteFlgTrue();
        return bean;
    }
}
