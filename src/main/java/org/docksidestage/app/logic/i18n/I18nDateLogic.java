/*
 * Copyright 2014-2015 the original author or authors.
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
import org.dbflute.util.Srl;

/**
 * @author jflute
 */
public class I18nDateLogic {

    // #app_customize if no i18n, remove time-zone from argument and change class name
    // ===================================================================================
    //                                                                     Date Conversion
    //                                                                     ===============
    // -----------------------------------------------------
    //                                         to Local Date
    //                                         -------------
    public OptionalThing<LocalDate> toDate(Object exp, TimeZone timeZone) { // application may call
        return OptionalThing.ofNullable(DfTypeUtil.toLocalDate(exp, timeZone, myDatePattern()), () -> {
            throw new IllegalStateException("Not found the converted local date: exp=" + exp);
        });
    }

    public OptionalThing<LocalDateTime> toDateTime(Object exp, TimeZone timeZone) { // application may call
        return OptionalThing.ofNullable(DfTypeUtil.toLocalDateTime(exp, timeZone, myDatePattern()), () -> {
            throw new IllegalStateException("Not found the converted local date-time: exp=" + exp);
        });
    }

    // -----------------------------------------------------
    //                                        to String Date
    //                                        --------------
    public OptionalThing<String> toStringDate(LocalDate date, TimeZone timeZone) { // application may call
        return OptionalThing.ofNullable(date, () -> {
            throw new IllegalStateException("Not found the specified local date for date expression.");
        }).map(dt -> new HandyDate(dt, timeZone).toDisp(myDatePattern(), myLocale()));
    }

    public OptionalThing<String> toStringDate(LocalDateTime dateTime, TimeZone timeZone) { // application may call
        return OptionalThing.ofNullable(dateTime, () -> {
            throw new IllegalStateException("Not found the specified local date-time for date expression.");
        }).map(dt -> new HandyDate(dt, timeZone).toDisp(myDatePattern(), myLocale()));
    }

    public OptionalThing<String> toStringDateTime(LocalDateTime dateTime, TimeZone timeZone) { // application may call
        return OptionalThing.ofNullable(dateTime, () -> {
            throw new IllegalStateException("Not found the specified local date-time for date-time expression.");
        }).map(dt -> new HandyDate(dateTime, timeZone).toDisp(myDateTimePattern(), myLocale()));
    }

    // -----------------------------------------------------
    //                                   Conversion Resource
    //                                   -------------------
    // #app_customize if pattern and locale are changed by user, move it to method arguments
    protected String myDatePattern() {
        return "yyyy-MM-dd";
    }

    protected String myDateTimePattern() {
        return "yyyy-MM-dd HH:mm:ss";
    }

    protected Locale myLocale() {
        return Locale.ENGLISH;
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected boolean isEmpty(String str) {
        return Srl.is_Null_or_Empty(str);
    }

    protected boolean isNotEmpty(String str) {
        return Srl.is_NotNull_and_NotEmpty(str);
    }
}
