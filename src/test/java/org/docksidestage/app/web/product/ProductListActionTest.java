package org.docksidestage.app.web.product;

import java.util.Map;

import org.dbflute.optional.OptionalThing;
import org.docksidestage.unit.UnitHarborTestCase;
import org.lastaflute.web.response.HtmlResponse;

/**
 * @author jflute
 */
public class ProductListActionTest extends UnitHarborTestCase {

    public void test_index_success_by_productName() throws Exception {
        // ## Arrange ##
        ProductListAction action = new ProductListAction();
        inject(action);
        ProductSearchForm form = new ProductSearchForm();
        form.productName = "a";

        // ## Act ##
        HtmlResponse response = action.index(OptionalThing.of(2), form);

        // ## Assert ##
        Map<String, Object> htmlData = validateHtmlData(response);
        requiredBeans(htmlData, ProductSearchRowBean.class).forEach(bean -> {
            log(bean);
            assertContains(bean.productName, form.productName);
        });
    }
}
