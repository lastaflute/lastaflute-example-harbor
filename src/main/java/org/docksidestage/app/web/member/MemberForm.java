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
package org.docksidestage.app.web.member;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

/**
 * 会員に対する操作の汎用ActionForm。
 * @author jflute
 */
public class MemberForm {

    @NotNull
    public Integer memberId;

    @NotBlank
    public String memberName;

    @NotBlank
    public String memberAccount;

    @NotBlank
    public String memberStatusCode;

    public String birthdate;

    public String formalizedDate;

    @NotBlank
    public String latestLoginDatetime;

    @NotBlank
    public String updateDatetime;

    @NotBlank
    public String previousStatusCode;

    @NotNull
    public Long versionNo;
}