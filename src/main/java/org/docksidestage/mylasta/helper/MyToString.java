package org.docksidestage.mylasta.helper;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author jflute
 */
public class MyToString {

    public static String of(Object obj) {
        return ToStringBuilder.reflectionToString(obj, ToStringStyle.NO_CLASS_NAME_STYLE);
    }
}
