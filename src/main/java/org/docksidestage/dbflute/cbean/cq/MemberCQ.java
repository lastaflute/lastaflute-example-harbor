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
package org.docksidestage.dbflute.cbean.cq;

import org.dbflute.cbean.ConditionQuery;
import org.dbflute.cbean.sqlclause.SqlClause;
import org.docksidestage.dbflute.cbean.cq.bs.BsMemberCQ;

/**
 * The condition-query of MEMBER.
 * <p>
 * You can implement your original methods here.
 * This class is NOT overridden when re-generating.
 * </p>
 * @author DBFlute(AutoGenerator)
 */
public class MemberCQ extends BsMemberCQ {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param childQuery Child query as abstract class. (Nullable: If null, this is base instance.)
     * @param sqlClause SQL clause instance. (NotNull)
     * @param aliasName My alias name. (NotNull)
     * @param nestLevel Nest level.
     */
    public MemberCQ(ConditionQuery childQuery, SqlClause sqlClause, String aliasName, int nestLevel) {
        super(childQuery, sqlClause, aliasName, nestLevel);
    }

    // ===================================================================================
    //                                                                      Arrange Method
    //                                                                      ==============
    // You can make original arrange query methods here.
    // public void arranegeXxx() {
    //     ...
    // }
    /**
     * Arrange member login query.
     * <pre>
     * o match email address
     * o match password
     * o member status is service available
     * </pre>
     * @param email The string of email address. (NotNull)
     * @param cipheredPassword The already-ciphered password. (NotNull)
     */
    public void arrangeLogin(String email, String cipheredPassword) {
        if (email == null || email.trim().length() == 0) {
            String msg = "The argument 'email' should not be null or empty: " + email;
            throw new IllegalArgumentException(msg);
        }
        if (cipheredPassword == null || cipheredPassword.trim().length() == 0) {
            String msg = "The argument 'cipheredPassword' should not be null or empty: " + cipheredPassword;
            throw new IllegalArgumentException(msg);
        }
        setMemberAccount_Equal(email); // member account (the database has no email)
        queryMemberSecurityAsOne().setLoginPassword_Equal(cipheredPassword);
        setMemberStatusCode_InScope_ServiceAvailable();
    }

    /**
     * Arrange member login query by identity.
     * <pre>
     * o match member ID
     * o member status is service available
     * </pre>
     * @param memberId The ID of the login member. (NotNull)
     */
    public void arrangeLoginByIdentity(Integer memberId) {
        if (memberId == null) {
            String msg = "The argument 'memberId' should not be null.";
            throw new IllegalArgumentException(msg);
        }
        setMemberId_Equal(memberId);
        setMemberStatusCode_InScope_ServiceAvailable();
    }
}
