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
package org.docksidestage.app.web.signin;

import javax.annotation.Resource;

import org.docksidestage.app.web.base.HarborBaseAction;
import org.docksidestage.app.web.base.login.HarborLoginAssist;
import org.docksidestage.app.web.mypage.MypageAction;
import org.docksidestage.dbflute.exbhv.MemberBhv;
import org.lastaflute.web.Execute;
import org.lastaflute.web.response.HtmlResponse;

/**
 * @author jflute
 */
public class SigninAction extends HarborBaseAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private HarborLoginAssist harborLoginAssist;
    @Resource
    private MemberBhv memberBhv;

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Execute
    public HtmlResponse index() {
        if (getUserBean().isPresent()) {
            return redirect(MypageAction.class);
        }
        return asHtml(path_Signin_SigninJsp).useForm(SigninForm.class);
    }

    @Execute
    public HtmlResponse doSignin(SigninForm form) {
        validate(form, messages -> {} , () -> {
            form.clearSecurityInfo();
            return asHtml(path_Signin_SigninJsp);
        });
        String email = form.email;
        String password = form.password;
        boolean useAutoLogin = form.isRememberMeTrue();
        return harborLoginAssist.loginRedirect(email, password, op -> op.rememberMe(useAutoLogin), () -> {
            return redirect(MypageAction.class);
        });
    }
}