package org.docksidestage.app.web.lido.mypage;

import org.docksidestage.dbflute.exentity.Product;
import org.lastaflute.web.validation.Required;

/**
 * @author jflute
 */
public class MypageProductResult {

    @Required
    public final String productName;
    @Required
    public final Integer regularPrice;

    public MypageProductResult(Product product) {
        this.productName = product.getProductName();
        this.regularPrice = product.getRegularPrice();
    }

    @Override
    public String toString() {
        return "{" + productName + ", " + regularPrice + "}";
    }
}
