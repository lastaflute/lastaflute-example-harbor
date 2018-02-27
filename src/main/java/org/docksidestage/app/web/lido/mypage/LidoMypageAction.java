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
package org.docksidestage.app.web.lido.mypage;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.dbflute.cbean.result.ListResultBean;
import org.docksidestage.app.web.base.HarborBaseAction;
import org.docksidestage.dbflute.exbhv.ProductBhv;
import org.docksidestage.dbflute.exentity.Product;
import org.lastaflute.web.Execute;
import org.lastaflute.web.login.AllowAnyoneAccess;
import org.lastaflute.web.response.JsonResponse;

// the 'lido' package is example for JSON API in simple project
// client application is riot.js in lidoisle directory
/**
 * @author s.tadokoro
 * @author jflute
 */
public class LidoMypageAction extends HarborBaseAction {

    @Resource
    private ProductBhv productBhv;

    @AllowAnyoneAccess // #for_now s.tadokoro Remove this when JSON Login feature is implemented.
    @Execute
    public JsonResponse<List<MypageProductResult>> index() {
        ListResultBean<Product> memberList = productBhv.selectList(cb -> {
            cb.query().addOrderBy_RegularPrice_Desc();
            cb.fetchFirst(3);
        });
        List<MypageProductResult> beans = memberList.stream().map(member -> {
            return new MypageProductResult(member);
        }).collect(Collectors.toList());
        return asJson(beans);
    }
}
