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
package org.docksidestage.app.web.various;

import org.dbflute.optional.OptionalThing;
import org.docksidestage.app.web.base.HarborBaseAction;
import org.lastaflute.web.Execute;
import org.lastaflute.web.login.AllowAnyoneAccess;
import org.lastaflute.web.response.JsonResponse;

/**
 * @author jflute
 */
@AllowAnyoneAccess
public class VariousMappingCheckAction extends HarborBaseAction {

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    // cannot use both index() and named execute use URL parameter in same action
    //@Execute
    //public JsonResponse<CheckJsonBean> index(String first) {
    //    return asJson(new CheckJsonBean("index()", first, null));
    //}

    @Execute
    public JsonResponse<CheckJsonBean> maihama() {
        return asJson(new CheckJsonBean("maihama()", null, null));
    }

    @Execute
    public JsonResponse<CheckJsonBean> sea(String first) {
        return asJson(new CheckJsonBean("sea()", first, null));
    }

    // Cannot define overload method of action execute
    //@Execute
    //public JsonResponse<CheckJsonBean> sea(String first, String second) {
    //    return asJson(new CheckJsonBean("sea()", first, second));
    //}

    @Execute
    public JsonResponse<CheckJsonBean> land(String first, String second) {
        return asJson(new CheckJsonBean("land()", first, second));
    }

    @Execute
    public JsonResponse<CheckJsonBean> iks(OptionalThing<String> first) {
        return asJson(new CheckJsonBean("iks()", first.orElse("*first"), null));
    }

    @Execute
    public JsonResponse<CheckJsonBean> amba(OptionalThing<String> first, OptionalThing<String> second) {
        return asJson(new CheckJsonBean("amba()", first.orElse("*first"), second.orElse("*second")));
    }

    @Execute
    public JsonResponse<CheckJsonBean> dstore(String first, OptionalThing<String> second) {
        return asJson(new CheckJsonBean("dstore()", first, second.orElse("*second")));
    }

    @Execute
    public JsonResponse<CheckJsonBean> bonvo(String first, String second, OptionalThing<String> third) {
        return asJson(new CheckJsonBean("bonvo()", first, second + " :: " + third.orElse("*third")));
    }

    @Execute
    public JsonResponse<CheckJsonBean> miraco(String first, String second, String third, OptionalThing<String> fourth) {
        return asJson(new CheckJsonBean("miraco()", first, second + " :: " + third + " :: " + fourth.orElse("*fourth")));
    }

    public static class CheckJsonBean {

        public final String method;
        public final String first;
        public final String second;

        public CheckJsonBean(String method, String first, String second) {
            this.method = method;
            this.first = first;
            this.second = second;
        }
    }
}
