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
    public List<PurchaseInfo> purchaseList;

    public class PurchaseInfo {
        public String productName;
        public Integer regularPrice;
        public LocalDateTime purchaseDateTime;

        public void setPurchaseInfo(String productName, Integer regularPrice, LocalDateTime purchaseDateTime) {
            this.productName = productName;
            this.regularPrice = regularPrice;
            this.purchaseDateTime = purchaseDateTime;
        }
    }
}
