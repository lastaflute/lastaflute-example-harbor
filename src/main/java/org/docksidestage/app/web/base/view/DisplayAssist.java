/*
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.docksidestage.app.web.base.view;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.TimeZone;

import javax.annotation.Resource;

import org.dbflute.optional.OptionalThing;
import org.docksidestage.app.logic.i18n.I18nDateLogic;
import org.lastaflute.web.servlet.request.RequestManager;

/**
 * @author jflute
 */
public class DisplayAssist { // #change_it

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private RequestManager requestManager;
    @Resource
    private I18nDateLogic i18nDateLogic;

    // ===================================================================================
    //                                                                               Date
    //                                                                              ======
    // -----------------------------------------------------
    //                                         to Local Date
    //                                         -------------
    public OptionalThing<LocalDate> toDate(Object exp) {
        return i18nDateLogic.toDate(exp, getStandardDatePattern(), getStandardTimeZone());
    }

    public OptionalThing<LocalDateTime> toDateTime(Object exp) {
        return i18nDateLogic.toDateTime(exp, getStandardDateTimePattern(), getStandardTimeZone());
    }

    // -----------------------------------------------------
    //                                        to String Date
    //                                        --------------
    public OptionalThing<String> toStringDate(LocalDate date) {
        return i18nDateLogic.toStringDate(date, getStandardDatePattern(), getStandardTimeZone());
    }

    public OptionalThing<String> toStringDate(LocalDateTime dateTime) {
        return i18nDateLogic.toStringDate(dateTime, getStandardDatePattern(), getStandardTimeZone());
    }

    public OptionalThing<String> toStringDateTime(LocalDateTime dateTime) {
        return i18nDateLogic.toStringDateTime(dateTime, getStandardDateTimePattern(), getStandardTimeZone());
    }

    // -----------------------------------------------------
    //                                   Conversion Resource
    //                                   -------------------
    protected String getStandardDatePattern() {
        return "yyyy-MM-dd";
    }

    protected String getStandardDateTimePattern() {
        return "yyyy-MM-dd HH:mm:ss";
    }

    protected TimeZone getStandardTimeZone() {
        return requestManager.getUserTimeZone();
    }
}
