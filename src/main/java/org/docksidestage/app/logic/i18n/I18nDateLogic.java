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
    public OptionalThing<LocalDate> toDate(String exp, TimeZone timeZone) { // application may call
        if (isNotEmpty(exp)) {
            return OptionalThing.of(new HandyDate(exp, timeZone, myDatePattern(), myLocale()).getLocalDate());
        } else {
            return OptionalThing.ofNullable(null, () -> {
                throw new IllegalStateException("The specified expression for local date was null or empty: " + exp);
            });
        }
    }

    public OptionalThing<LocalDateTime> toDateTime(String exp, TimeZone timeZone) { // application may call
        if (isNotEmpty(exp)) {
            return OptionalThing.of(new HandyDate(exp, timeZone, myDatePattern(), myLocale()).getLocalDateTime());
        } else {
            return OptionalThing.ofNullable(null, () -> {
                throw new IllegalStateException("The specified expression for local date was null or empty: " + exp);
            });
        }
    }

    // -----------------------------------------------------
    //                                        to String Date
    //                                        --------------
    public OptionalThing<String> toStringDate(LocalDate date, TimeZone timeZone) { // application may call
        if (date != null) {
            return OptionalThing.of(new HandyDate(date, timeZone).toDisp(myDatePattern(), myLocale()));
        } else {
            return OptionalThing.ofNullable(null, () -> {
                throw new IllegalStateException("The specified local date was null.");
            });
        }
    }

    public OptionalThing<String> toStringDate(LocalDateTime dateTime, TimeZone timeZone) { // application may call
        if (dateTime != null) {
            return OptionalThing.of(new HandyDate(dateTime, timeZone).toDisp(myDatePattern(), myLocale()));
        } else {
            return OptionalThing.ofNullable(null, () -> {
                throw new IllegalStateException("The specified local date-time was null.");
            });
        }
    }

    public OptionalThing<String> toStringDateTime(LocalDateTime dateTime, TimeZone timeZone) { // application may call
        if (dateTime != null) {
            return OptionalThing.of(new HandyDate(dateTime, timeZone).toDisp(myDateTimePattern(), myLocale()));
        } else {
            return OptionalThing.ofNullable(null, () -> {
                throw new IllegalStateException("The specified local date-time was null.");
            });
        }
    }

    // -----------------------------------------------------
    //                                   Conversion Resource
    //                                   -------------------
    // #app_customize if pattern and locale are changed by user, move it to method arguments
    protected String myDatePattern() {
        return "yyyy/MM/dd";
    }

    protected String myDateTimePattern() {
        return "yyyy/MM/dd HH:mm:ss";
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
