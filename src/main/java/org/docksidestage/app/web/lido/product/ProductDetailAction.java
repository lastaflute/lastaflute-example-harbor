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
package org.docksidestage.app.web.lido.product;

import javax.annotation.Resource;

import org.docksidestage.app.web.base.HarborBaseAction;
import org.docksidestage.dbflute.exbhv.ProductBhv;
import org.docksidestage.dbflute.exentity.Product;
import org.lastaflute.web.login.AllowAnyoneAccess;

/**
 * @author jflute
 */
@AllowAnyoneAccess
public class ProductDetailAction extends HarborBaseAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                          DI Component
    //                                          ------------
    @Resource
    private ProductBhv productBhv;

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======

    // TODO (s.tadokoro) implement
    //    @Execute
    //    public HtmlResponse index(Integer productId) {
    //        validate(productId, messages -> {} , () -> {
    //            return asHtml(path_Product_ProductListJsp);
    //        });
    //        Product product = selectProduct(productId);
    //        return asHtml(path_Product_ProductDetailJsp).renderWith(data -> {
    //            data.register("product", mappingToBean(product));
    //        });
    //    }

    // ===================================================================================
    //                                                                              Select
    //                                                                              ======
    private Product selectProduct(int productId) {
        return productBhv.selectEntity(cb -> {
            cb.setupSelect_ProductCategory();
            cb.query().setProductId_Equal(productId);
        }).orElseThrow(() -> {
            return of404("Not found the product: " + productId); // mistake or user joke
        });
    }

    // ===================================================================================
    //                                                                             Mapping
    //                                                                             =======
    private ProductDetailBean mappingToBean(Product product) {
        ProductDetailBean bean = new ProductDetailBean();
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
