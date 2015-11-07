package org.docksidestage.app.web.profile;

import java.util.List;

import org.docksidestage.dbflute.exentity.Product;
import org.docksidestage.dbflute.exentity.Purchase;

public class ProfileBean {

    public int memberId;
    public String memberName;
    public String memberStatusName;
    public String servicePointCount;
    public String serviceRankName;
    public List<Purchase> purchaseList;
    public List<Product> productList;
}
