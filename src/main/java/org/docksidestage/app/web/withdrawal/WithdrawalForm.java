package org.docksidestage.app.web.withdrawal;

import org.docksidestage.dbflute.allcommon.CDef;
import org.hibernate.validator.constraints.Length;

/**
 * @author annie_pocket
 * @author jflute
 */
public class WithdrawalForm {

    public CDef.WithdrawalReason reasonCode;

    @Length(max = 3)
    public String reasonInput;
}
