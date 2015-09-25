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

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.api.ApiFailureHook;
import org.lastaflute.web.api.ApiFailureResource;
import org.lastaflute.web.login.exception.LoginUnauthorizedException;
import org.lastaflute.web.response.ApiResponse;
import org.lastaflute.web.response.JsonResponse;

/**
 * @author jflute
 */
public class HarborApiFailureHook implements ApiFailureHook { // #change_it for handling API failure

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final int HTTP_BAD_REQUEST = HttpServletResponse.SC_BAD_REQUEST;
    protected static final int HTTP_UNAUTHORIZED = HttpServletResponse.SC_UNAUTHORIZED;

    // ===================================================================================
    //                                                                    Business Failure
    //                                                                    ================
    @Override
    public ApiResponse handleValidationError(ApiFailureResource resource) {
        final TooSimpleFailureBean bean = createFailureBean(TooSimpleBizStatus.VALIDATION_ERROR, resource);
        return asJson(bean).httpStatus(HTTP_BAD_REQUEST);
    }

    @Override
    public ApiResponse handleApplicationException(ApiFailureResource resource, RuntimeException cause) {
        final TooSimpleFailureBean bean = createFailureBean(TooSimpleBizStatus.APPLICATION_EXCEPTION, resource);
        return asJson(bean).httpStatus(chooseApplicationExceptionHttpStatus(cause));
    }

    protected int chooseApplicationExceptionHttpStatus(RuntimeException cause) {
        return cause instanceof LoginUnauthorizedException ? HTTP_UNAUTHORIZED : HTTP_BAD_REQUEST;
    }

    // ===================================================================================
    //                                                                      System Failure
    //                                                                      ==============
    @Override
    public OptionalThing<ApiResponse> handleClientException(ApiFailureResource resource, RuntimeException cause) {
        final TooSimpleFailureBean bean = createFailureBean(TooSimpleBizStatus.CLIENT_EXCEPTION, resource);
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

    protected TooSimpleFailureBean createFailureBean(TooSimpleBizStatus bizStatus, ApiFailureResource resource) {
        return new TooSimpleFailureBean(bizStatus, resource.getMessageList());
    }

    public static enum TooSimpleBizStatus {
        VALIDATION_ERROR, APPLICATION_EXCEPTION, CLIENT_EXCEPTION
    }

    public static class TooSimpleFailureBean {

        public final String notice = "[Attension] tentative JSON so you should change it: " + HarborApiFailureHook.class;
        public final TooSimpleBizStatus bizStatus;
        public final List<String> messageList;

        public TooSimpleFailureBean(TooSimpleBizStatus bizStatus, List<String> messageList) {
            this.bizStatus = bizStatus;
            this.messageList = messageList;
        }
    }
}
