package org.docksidestage.app.web.lido.product;

import org.lastaflute.web.validation.Required;

/**
 * @author s.tadokoro
 * @author jflute
 */
public class ProductDetailResult {

    @Required
    public Integer productId;
    @Required
    public String productName;
    @Required
    public String categoryName;
    @Required
    public Integer regularPrice;
    @Required
    public String productHandleCode;
}
