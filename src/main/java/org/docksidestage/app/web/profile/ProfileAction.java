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

import javax.annotation.Resource;

import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.docksidestage.app.web.base.HarborBaseAction;
import org.docksidestage.dbflute.exbhv.MemberBhv;
import org.docksidestage.dbflute.exbhv.PurchaseBhv;
import org.docksidestage.dbflute.exentity.Member;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;

/**
 * @author jflute
 * @author deco
 */
public class ProfileAction extends HarborBaseAction {

    @Resource
    protected MemberBhv memberBhv;

    @Resource
    protected PurchaseBhv purchaseBhv;

    @Execute
    public HtmlResponse index(OptionalThing<Integer> pageNumber, ProfileForm form) {
        validate(form, messages -> {} , () -> {
            return asHtml(path_Error_ErrorMessageJsp);
        });

        OptionalEntity<Member> member = memberBhv.selectEntity(cb -> {
            cb.query().setMemberId_Equal(pageNumber.get());
        });
        memberBhv.loadPurchase(member.get(), purCB -> {});

        ProfileBean beans = new ProfileBean();
        beans.memberId = pageNumber.get();
        beans.memberName = member.get().getMemberName();
        beans.purchaseList = member.get().getPurchaseList();
        return asHtml(path_Profile_ProfileJsp).renderWith(data -> {
            data.register("beans", beans);
        });
    }
}
