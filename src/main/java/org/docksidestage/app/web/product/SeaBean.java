package org.docksidestage.app.web.product;

import java.io.Serializable;

import org.lastaflute.web.validation.Required;

public class SeaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Required
    public Integer productId;
}
