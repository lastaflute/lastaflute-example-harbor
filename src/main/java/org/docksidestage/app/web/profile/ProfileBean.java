package org.docksidestage.app.web.profile;

import java.time.LocalDateTime;
import java.util.List;

import org.docksidestage.dbflute.exentity.Member;
import org.docksidestage.dbflute.exentity.MemberService;
import org.docksidestage.dbflute.exentity.Purchase;

/**
 * @author deco
 */
public class ProfileBean {

    public final int memberId;
    public final String memberName;
    public final String memberStatusName;
    public final Integer servicePointCount;
    public final String serviceRankName;
    public List<PurchasedProductBean> purchaseList;

    public ProfileBean(Member member) {
        this.memberId = member.getMemberId();
        this.memberName = member.getMemberName();
        this.memberStatusName = member.getMemberStatus().get().getMemberStatusName();
        MemberService memberService = member.getMemberServiceAsOne().get();
        this.servicePointCount = memberService.getServicePointCount();
        this.serviceRankName = memberService.getServiceRank().get().getServiceRankName();
    }

    public static class PurchasedProductBean {

        public final String productName;
        public final Integer regularPrice;
        public final LocalDateTime purchaseDateTime;

        public PurchasedProductBean(Purchase purchase) {
            this.productName = purchase.getProduct().get().getProductName();
            this.regularPrice = purchase.getProduct().get().getRegularPrice();
            this.purchaseDateTime = purchase.getPurchaseDatetime();
        }
    }
}
