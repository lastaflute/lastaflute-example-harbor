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
package org.docksidestage.mylasta.direction.sponsor;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.api.ApiFailureHook;
import org.lastaflute.web.api.ApiFailureResource;
import org.lastaflute.web.api.BusinessFailureMapping;
import org.lastaflute.web.login.exception.LoginFailureException;
import org.lastaflute.web.login.exception.LoginRequiredException;
import org.lastaflute.web.response.ApiResponse;
import org.lastaflute.web.response.JsonResponse;

/**
 * @author jflute
 */
public class HarborApiFailureHook implements ApiFailureHook { // #change_it for handling API failure

    // _/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/
    // [Front-side Implementation Image]
    //
    // if (HTTP Status: 200) { // success
    //     XxxJsonBean bean = parseJsonAsSuccess(response);
    //     ...(do process per action)
    // } else if (HTTP Status: 400) { // e.g. validation error, application exception, client exception
    //     FailureBean bean = parseJsonAsFailure(response);
    //     ...(show bean.messageList or do process per bean.failureType)
    // } else if (HTTP Status: 404) { // e.g. real not found, invalid parameter
    //     showNotFoundError();
    // } else { // basically 500, server exception
    //     showSystemError();
    // }
    // _/_/_/_/_/_/_/_/_/_/

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final int BUSINESS_FAILURE_STATUS = HttpServletResponse.SC_BAD_REQUEST;
    protected static final BusinessFailureMapping<TooSimpleFailureType> failureTypeMapping; // for application exception

    static { // you can add mapping of failure type with exception
        failureTypeMapping = new BusinessFailureMapping<TooSimpleFailureType>(failureMap -> {
            failureMap.put(LoginFailureException.class, TooSimpleFailureType.LOGIN_FAILURE);
            failureMap.put(LoginRequiredException.class, TooSimpleFailureType.LOGIN_REQUIRED);
        });
    }

    // ===================================================================================
    //                                                                    Business Failure
    //                                                                    ================
    @Override
    public ApiResponse handleValidationError(ApiFailureResource resource) {
        final TooSimpleFailureBean bean = createFailureBean(TooSimpleFailureType.VALIDATION_ERROR, resource);
        return asJson(bean).httpStatus(BUSINESS_FAILURE_STATUS);
    }

    @Override
    public ApiResponse handleApplicationException(ApiFailureResource resource, RuntimeException cause) {
        final TooSimpleFailureType failureType = failureTypeMapping.findAssignable(cause).orElseGet(() -> {
            return TooSimpleFailureType.APPLICATION_EXCEPTION;
        });
        final TooSimpleFailureBean bean = createFailureBean(failureType, resource);
        return asJson(bean).httpStatus(BUSINESS_FAILURE_STATUS);
    }

    // ===================================================================================
    //                                                                      System Failure
    //                                                                      ==============
    @Override
    public OptionalThing<ApiResponse> handleClientException(ApiFailureResource resource, RuntimeException cause) {
        final TooSimpleFailureBean bean = createFailureBean(TooSimpleFailureType.CLIENT_EXCEPTION, resource);
        return OptionalThing.of(asJson(bean)); // HTTP status will be automatically sent as client error for the cause
    }

    @Override
    public OptionalThing<ApiResponse> handleServerException(ApiFailureResource resource, Throwable cause) {
        return OptionalThing.empty(); // means empty body, HTTP status will be automatically sent as server error
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected JsonResponse<TooSimpleFailureBean> asJson(TooSimpleFailureBean bean) {
        return new JsonResponse<TooSimpleFailureBean>(bean);
    }

    protected TooSimpleFailureBean createFailureBean(TooSimpleFailureType failureType, ApiFailureResource resource) {
        return new TooSimpleFailureBean(failureType, resource.getPropertyMessageMap());
    }

    public static class TooSimpleFailureBean {

        public final String notice = "[Attension] tentative JSON so you should change it: " + HarborApiFailureHook.class;
        public final TooSimpleFailureType failureType;
        public final Map<String, List<String>> messageMap;

        public TooSimpleFailureBean(TooSimpleFailureType failureType, Map<String, List<String>> messageMap) {
            this.failureType = failureType;
            this.messageMap = messageMap;
        }
    }

    public static enum TooSimpleFailureType {
        VALIDATION_ERROR // special type
        , LOGIN_FAILURE, LOGIN_REQUIRED // specific type of application exception
        , APPLICATION_EXCEPTION // default type of application exception
        , CLIENT_EXCEPTION
    }
}
