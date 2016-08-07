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
package org.docksidestage.app.logic.i18n;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.TimeZone;

import org.dbflute.helper.HandyDate;
import org.dbflute.optional.OptionalThing;
import org.dbflute.util.DfTypeUtil;

/**
 * @author jflute
 */
public class I18nDateLogic {

    // ===================================================================================
    //                                                                     Date Conversion
    //                                                                     ===============
    // -----------------------------------------------------
    //                                         to Local Date
    //                                         -------------
    public OptionalThing<LocalDate> toDate(Object exp, String pattern, TimeZone timeZone) {
        return OptionalThing.ofNullable(DfTypeUtil.toLocalDate(exp, timeZone, pattern), () -> {
            throw new IllegalStateException("Not found the converted local date: exp=" + exp);
        });
    }

    public OptionalThing<LocalDateTime> toDateTime(Object exp, String pattern, TimeZone timeZone) {
        return OptionalThing.ofNullable(DfTypeUtil.toLocalDateTime(exp, timeZone, pattern), () -> {
            throw new IllegalStateException("Not found the converted local date-time: exp=" + exp);
        });
    }

    // -----------------------------------------------------
    //                                        to String Date
    //                                        --------------
    public OptionalThing<String> toStringDate(LocalDate date, String pattern, TimeZone timeZone) {
        return OptionalThing.ofNullable(date, () -> {
            throw new IllegalStateException("Not found the specified local date for date expression.");
        }).map(dt -> new HandyDate(dt, timeZone).toDisp(pattern, getStandardLocale()));
    }

    public OptionalThing<String> toStringDate(LocalDateTime dateTime, String pattern, TimeZone timeZone) {
        return OptionalThing.ofNullable(dateTime, () -> {
            throw new IllegalStateException("Not found the specified local date-time for date expression.");
        }).map(dt -> new HandyDate(dt, timeZone).toDisp(pattern, getStandardLocale()));
    }

    public OptionalThing<String> toStringDateTime(LocalDateTime dateTime, String pattern, TimeZone timeZone) {
        return OptionalThing.ofNullable(dateTime, () -> {
            throw new IllegalStateException("Not found the specified local date-time for date-time expression.");
        }).map(dt -> new HandyDate(dateTime, timeZone).toDisp(pattern, getStandardLocale()));
    }

    // -----------------------------------------------------
    //                                   Conversion Resource
    //                                   -------------------
    protected Locale getStandardLocale() {
        return Locale.ENGLISH; // #app_customzie you can change or accept it as argument
    }
}
