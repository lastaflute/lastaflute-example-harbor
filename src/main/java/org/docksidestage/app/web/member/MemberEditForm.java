/*
 * Copyright 2015-2024 the original author or authors.
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
import java.time.LocalDateTime;

import org.docksidestage.dbflute.allcommon.CDef;
import org.lastaflute.web.validation.ClientError;
import org.lastaflute.web.validation.Required;
import org.lastaflute.web.validation.theme.conversion.ValidateTypeFailure;

/**
 * @author jflute
 */
public class MemberEditForm {

    @Required(groups = ClientError.class)
    public Integer memberId;

    @Required
    public String memberName;

    @Required
    public String memberAccount;

    @ValidateTypeFailure // using calendar so basically unneeded but just in case
    public LocalDate birthdate;

    public LocalDate formalizedDate;

    @Required
    public CDef.MemberStatus memberStatus;

    public LocalDateTime latestLoginDatetime; // only view

    public LocalDateTime updateDatetime; // only view

    @Required(groups = ClientError.class)
    public CDef.MemberStatus previousStatus;

    @Required(groups = ClientError.class)
    public Long versionNo;
}