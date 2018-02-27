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
package org.docksidestage.app.web.product;

import javax.annotation.Resource;

import org.docksidestage.app.web.base.HarborBaseAction;
import org.docksidestage.dbflute.exbhv.ProductBhv;
import org.docksidestage.dbflute.exentity.Product;
import org.lastaflute.web.Execute;
import org.lastaflute.web.login.AllowAnyoneAccess;
import org.lastaflute.web.response.HtmlResponse;

/**
 * @author jflute
 */
@AllowAnyoneAccess
public class ProductDetailAction extends HarborBaseAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private ProductBhv productBhv;

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Execute
    public HtmlResponse index(Integer productId) {
        Product product = selectProduct(productId);
        ProductDetailBean bean = mappingToBean(product);
        return asHtml(path_Product_ProductDetailHtml).renderWith(data -> {
            data.register("product", bean);
        });
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
