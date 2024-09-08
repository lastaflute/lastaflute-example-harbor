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
package org.docksidestage.app.web.signup;

import java.io.IOException;
import java.util.Random;

import javax.annotation.Resource;

import org.dbflute.helper.filesystem.FileTextIO;
import org.dbflute.util.DfResourceUtil;
import org.docksidestage.dbflute.exentity.Member;
import org.lastaflute.web.servlet.request.ResponseManager;

/**
 * @author jflute
 */
public class SignupTokenAssist {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private ResponseManager responseManager;

    // ===================================================================================
    //                                                                               Save
    //                                                                              ======
    // #simple_for_example no no no no way, normally use database or KVS, and prepare time limit
    public String saveSignupToken(Member member) {
        final String token = generateToken();
        createFileTextIO().write(buildTokenFilePath(token), member.getMemberAccount());
        return token;
    }

    private String generateToken() {
        return Integer.toHexString(new Random().nextInt());
    }

    // ===================================================================================
    //                                                                              Verify
    //                                                                              ======
    public void verifySignupTokenMatched(String account, String token) {
        final String saved;
        try {
            saved = createFileTextIO().read(buildTokenFilePath(token));
        } catch (RuntimeException e) { // contains "file not found"
            throw responseManager.new404("Unmatched signup token: requested=" + token, op -> op.cause(e));
        }
        if (!saved.equals(account)) {
            throw responseManager.new404("Unmatched signup account: saved=" + saved + ", requested=" + account);
        }
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private String buildTokenFilePath(String token) {
        try {
            return DfResourceUtil.getBuildDir(getClass()).getParentFile().getCanonicalPath() + "/signup-" + token + ".txt";
        } catch (IOException e) {
            throw new IllegalStateException("Failed to get canonical path for the token: " + token, e);
        }
    }

    private FileTextIO createFileTextIO() {
        return new FileTextIO().encodeAsUTF8();
    }
}
