package org.docksidestage.app.web.lido.mypage;

import org.docksidestage.dbflute.exentity.Product;

/**
 * @author jflute
 */
public class MypageProductBean {

    public final String productName;
    public final Integer regularPrice;

    public MypageProductBean(Product product) {
        this.productName = product.getProductName();
        this.regularPrice = product.getRegularPrice();
    }

    @Override
    public String toString() {
        return "{" + productName + ", " + regularPrice + "}";
    }
}
