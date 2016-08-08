package org.docksidestage.app.web.lido.product;

import org.lastaflute.web.validation.Required;

/**
 * @author jflute
 * @author iwamatsu0430
 */
public class ProductDetailBean {

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
