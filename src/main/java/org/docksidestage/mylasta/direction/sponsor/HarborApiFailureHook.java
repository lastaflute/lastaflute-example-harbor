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
import javax.validation.constraints.NotNull;

import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.api.ApiFailureHook;
import org.lastaflute.web.api.ApiFailureResource;
import org.lastaflute.web.api.BusinessFailureMapping;
import org.lastaflute.web.login.exception.LoginFailureException;
import org.lastaflute.web.login.exception.LoginRequiredException;
import org.lastaflute.web.response.ApiResponse;
import org.lastaflute.web.response.JsonResponse;
import org.lastaflute.web.validation.Required;

/**
 * @author jflute
 */
public class HarborApiFailureHook implements ApiFailureHook { // #change_it for handling API failure

    // _/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_/
    // [Front-side Implementation Example]
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
    protected static final BusinessFailureMapping<UnifiedFailureType> failureTypeMapping; // for application exception

    static { // you can add mapping of failure type with exception
        failureTypeMapping = new BusinessFailureMapping<UnifiedFailureType>(failureMap -> {
            failureMap.put(LoginFailureException.class, UnifiedFailureType.LOGIN_FAILURE);
            failureMap.put(LoginRequiredException.class, UnifiedFailureType.LOGIN_REQUIRED);
        });
    }

    // ===================================================================================
    //                                                                    Business Failure
    //                                                                    ================
    @Override
    public ApiResponse handleValidationError(ApiFailureResource resource) {
        final UnifiedFailureResult result = createFailureResult(UnifiedFailureType.VALIDATION_ERROR, resource);
        return asJson(result).httpStatus(BUSINESS_FAILURE_STATUS);
    }

    @Override
    public ApiResponse handleApplicationException(ApiFailureResource resource, RuntimeException cause) {
        final UnifiedFailureType failureType = failureTypeMapping.findAssignable(cause).orElseGet(() -> {
            return UnifiedFailureType.APPLICATION_EXCEPTION;
        });
        final UnifiedFailureResult result = createFailureResult(failureType, resource);
        return asJson(result).httpStatus(BUSINESS_FAILURE_STATUS);
    }

    // ===================================================================================
    //                                                                      System Failure
    //                                                                      ==============
    @Override
    public OptionalThing<ApiResponse> handleClientException(ApiFailureResource resource, RuntimeException cause) {
        final UnifiedFailureResult result = createFailureResult(UnifiedFailureType.CLIENT_EXCEPTION, resource);
        return OptionalThing.of(asJson(result)); // HTTP status will be automatically sent as client error for the cause
    }

    @Override
    public OptionalThing<ApiResponse> handleServerException(ApiFailureResource resource, Throwable cause) {
        return OptionalThing.empty(); // means empty body, HTTP status will be automatically sent as server error
    }

    // ===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    protected JsonResponse<UnifiedFailureResult> asJson(UnifiedFailureResult result) {
        return new JsonResponse<UnifiedFailureResult>(result);
    }

    protected UnifiedFailureResult createFailureResult(UnifiedFailureType failureType, ApiFailureResource resource) {
        return new UnifiedFailureResult(failureType, resource.getPropertyMessageMap());
    }

    public static class UnifiedFailureResult {

        @Required
        public final UnifiedFailureType failureType;
        @NotNull
        public final Map<String, List<String>> messageMap;

        public UnifiedFailureResult(UnifiedFailureType failureType, Map<String, List<String>> messageMap) {
            this.failureType = failureType;
            this.messageMap = messageMap;
        }
    }

    public static enum UnifiedFailureType {
        VALIDATION_ERROR // special type
        , LOGIN_FAILURE, LOGIN_REQUIRED // specific type of application exception
        , APPLICATION_EXCEPTION // default type of application exception
        , CLIENT_EXCEPTION // e.g. 404 not found
    }
}
