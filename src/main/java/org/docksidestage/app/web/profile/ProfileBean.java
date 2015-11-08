package org.docksidestage.app.web.profile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author deco
 */
public class ProfileBean {

    public int memberId;
    public String memberName;
    public String memberStatusName;
    public String servicePointCount;
    public String serviceRankName;
    public List<PurchasedProductBean> purchaseList;

    public static class PurchasedProductBean {
        public String productName;
        public Integer regularPrice;
        public LocalDateTime purchaseDateTime;

        PurchasedProductBean(String productName, Integer regularPrice, LocalDateTime purchaseDateTime) {
            this.productName = productName;
            this.regularPrice = regularPrice;
            this.purchaseDateTime = purchaseDateTime;
        }
    }
}
