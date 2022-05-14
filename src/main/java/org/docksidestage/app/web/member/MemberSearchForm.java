/*
 * Copyright 2015-2022 the original author or authors.
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
package org.docksidestage.app.web.member;

import java.time.LocalDate;

import org.docksidestage.dbflute.allcommon.CDef;
import org.hibernate.validator.constraints.Length;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

/**
 * @author jflute
 */
public class MemberSearchForm {

    @Length(max = 5) // #simple_for_example just for validtion example
    public String memberName;

    public CDef.MemberStatus memberStatus; // select-box so no validation (client error if invalid code)

    @Length(max = 10) // #simple_for_example just for validtion example
    public String purchaseProductName;

    public boolean unpaid; // checkbox cannot send 'off' status so primitive here

    @ValidateTypeFailure // you can customize message by "constraints.TypeLocalDate.message"
    public LocalDate formalizedFrom;

    @ValidateTypeFailure
    public LocalDate formalizedTo;
}
