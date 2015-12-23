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
package org.docksidestage.mylasta.direction.sponsor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.api.ApiFailureHook;
import org.lastaflute.web.api.ApiFailureResource;
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
    //     JsonBean bean = parseJsonAsSuccess(response);
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
    protected static final Map<Class<?>, TooSimpleFailureType> failureTypeMap; // for application exception

    static { // you can add mapping of failure type with exception
        final Map<Class<?>, TooSimpleFailureType> makingMap = new HashMap<Class<?>, TooSimpleFailureType>();
        makingMap.put(LoginFailureException.class, TooSimpleFailureType.LOGIN_FAILURE);
        makingMap.put(LoginRequiredException.class, TooSimpleFailureType.LOGIN_REQUIRED);
        failureTypeMap = Collections.unmodifiableMap(makingMap);
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
        final TooSimpleFailureType failureType = findAssignableValue(failureTypeMap, cause.getClass()).orElseGet(() -> {
            return TooSimpleFailureType.APPLICATION_EXCEPTION;
        });
        final TooSimpleFailureBean bean = createFailureBean(failureType, resource);
        return asJson(bean).httpStatus(BUSINESS_FAILURE_STATUS);
    }

    protected <VALUE> OptionalThing<VALUE> findAssignableValue(Map<Class<?>, VALUE> map, Class<?> key) {
        final VALUE found = map.get(key);
        if (found != null) {
            return OptionalThing.of(found);
        } else {
            return OptionalThing.migratedFrom(map.entrySet().stream().filter(entry -> {
                return entry.getKey().isAssignableFrom(key);
            }).map(entry -> entry.getValue()).findFirst(), () -> {
                throw new IllegalStateException("Not found the exception type in the map: " + map.keySet());
            });
        }
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
        return new TooSimpleFailureBean(failureType, resource.getMessageList());
    }

    public static class TooSimpleFailureBean {

        public final String notice = "[Attension] tentative JSON so you should change it: " + HarborApiFailureHook.class;
        public final TooSimpleFailureType failureType;
        public final List<String> messageList;

        public TooSimpleFailureBean(TooSimpleFailureType failureType, List<String> messageList) {
            this.failureType = failureType;
            this.messageList = messageList;
        }
    }

    public static enum TooSimpleFailureType {
        VALIDATION_ERROR // special type
        , LOGIN_FAILURE, LOGIN_REQUIRED // specific type of application exception
        , APPLICATION_EXCEPTION // default type of application exception
        , CLIENT_EXCEPTION
    }
}
