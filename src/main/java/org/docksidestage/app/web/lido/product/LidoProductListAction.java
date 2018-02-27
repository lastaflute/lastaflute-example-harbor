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
package org.docksidestage.app.web.lido.product;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.dbflute.cbean.result.PagingResultBean;
import org.dbflute.optional.OptionalThing;
import org.docksidestage.app.web.base.HarborBaseAction;
import org.docksidestage.app.web.base.paging.PagingAssist;
import org.docksidestage.app.web.base.paging.SearchPagingResult;
import org.docksidestage.dbflute.exbhv.ProductBhv;
import org.docksidestage.dbflute.exbhv.ProductStatusBhv;
import org.docksidestage.dbflute.exentity.Product;
import org.lastaflute.core.util.LaStringUtil;
import org.lastaflute.web.Execute;
import org.lastaflute.web.login.AllowAnyoneAccess;
import org.lastaflute.web.response.JsonResponse;

// the 'lido' package is example for JSON API in simple project
// client application is riot.js in lidoisle directory
/**
 * @author s.tadokoro
 * @author jflute
 */
@AllowAnyoneAccess
public class LidoProductListAction extends HarborBaseAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private ProductBhv productBhv;
    @Resource
    private ProductStatusBhv productStatusBhv;
    @Resource
    private PagingAssist pagingAssist;

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Execute
    public JsonResponse<SearchPagingResult<ProductRowResult>> index(OptionalThing<Integer> pageNumber, ProductSearchBody body) {
        validateApi(body, messages -> {});

        PagingResultBean<Product> page = selectProductPage(pageNumber.orElse(1), body);
        List<ProductRowResult> items = page.stream().map(product -> {
            return mappingToBean(product);
        }).collect(Collectors.toList());

        SearchPagingResult<ProductRowResult> result = pagingAssist.createPagingResult(page, items);
        return asJson(result);
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    private PagingResultBean<Product> selectProductPage(int pageNumber, ProductSearchBody body) {
        verifyOrClientError("The pageNumber should be positive number: " + pageNumber, pageNumber > 0);
        return productBhv.selectPage(cb -> {
            cb.setupSelect_ProductStatus();
            cb.setupSelect_ProductCategory();
            cb.specify().derivedPurchase().count(purchaseCB -> {
                purchaseCB.specify().columnPurchaseId();
            }, Product.ALIAS_purchaseCount);
            if (LaStringUtil.isNotEmpty(body.productName)) {
                cb.query().setProductName_LikeSearch(body.productName, op -> op.likeContain());
            }
            if (LaStringUtil.isNotEmpty(body.purchaseMemberName)) {
                cb.query().existsPurchase(purchaseCB -> {
                    purchaseCB.query().queryMember().setMemberName_LikeSearch(body.purchaseMemberName, op -> op.likeContain());
                });
            }
            if (body.productStatus != null) {
                cb.query().setProductStatusCode_Equal_AsProductStatus(body.productStatus);
            }
            cb.query().addOrderBy_ProductName_Asc();
            cb.query().addOrderBy_ProductId_Asc();
            cb.paging(Integer.MAX_VALUE, pageNumber); // #later: waiting for client side implementation by jflute
        });
    }

    // ===================================================================================
    //                                                                             Mapping
    //                                                                             =======
    private ProductRowResult mappingToBean(Product product) {
        ProductRowResult bean = new ProductRowResult();
        bean.productId = product.getProductId();
        bean.productName = product.getProductName();
        product.getProductStatus().alwaysPresent(status -> {
            bean.productStatusName = status.getProductStatusName();
        });
        bean.regularPrice = product.getRegularPrice();
        return bean;
    }
}
