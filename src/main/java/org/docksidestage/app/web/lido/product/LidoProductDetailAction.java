/*
 * Copyright 2015-2024 the original author or authors.
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

import javax.annotation.Resource;

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
@AllowAnyoneAccess
public class LidoProductDetailAction extends HarborBaseAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private ProductBhv productBhv;

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Execute
    public JsonResponse<ProductDetailResult> index(Integer productId) {
        Product product = selectProduct(productId);
        return asJson(mappingToBean(product));
    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    private Product selectProduct(int productId) {
        return productBhv.selectEntity(cb -> {
            cb.setupSelect_ProductCategory();
            cb.query().setProductId_Equal(productId);
        }).get();
    }

    // ===================================================================================
    //                                                                             Mapping
    //                                                                             =======
    private ProductDetailResult mappingToBean(Product product) {
        ProductDetailResult bean = new ProductDetailResult();
        bean.productId = product.getProductId();
        bean.productName = product.getProductName();
        bean.regularPrice = product.getRegularPrice();
        bean.productHandleCode = product.getProductHandleCode();
        product.getProductCategory().alwaysPresent(category -> {
            bean.categoryName = category.getProductCategoryName();
        });
        return bean;
    }
}
