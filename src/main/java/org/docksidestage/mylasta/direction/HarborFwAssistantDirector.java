/*
 * Copyright 2015-2018 the original author or authors.
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
package org.docksidestage.mylasta.direction;

import javax.annotation.Resource;

import org.docksidestage.mylasta.direction.sponsor.HarborActionAdjustmentProvider;
import org.docksidestage.mylasta.direction.sponsor.HarborApiFailureHook;
import org.docksidestage.mylasta.direction.sponsor.HarborCookieResourceProvider;
import org.docksidestage.mylasta.direction.sponsor.HarborCurtainBeforeHook;
import org.docksidestage.mylasta.direction.sponsor.HarborJsonResourceProvider;
import org.docksidestage.mylasta.direction.sponsor.HarborListedClassificationProvider;
import org.docksidestage.mylasta.direction.sponsor.HarborMailDeliveryDepartmentCreator;
import org.docksidestage.mylasta.direction.sponsor.HarborMultipartRequestHandler;
import org.docksidestage.mylasta.direction.sponsor.HarborSecurityResourceProvider;
import org.docksidestage.mylasta.direction.sponsor.HarborTimeResourceProvider;
import org.docksidestage.mylasta.direction.sponsor.HarborUserLocaleProcessProvider;
import org.docksidestage.mylasta.direction.sponsor.HarborUserTimeZoneProcessProvider;
import org.lastaflute.core.direction.CachedFwAssistantDirector;
import org.lastaflute.core.direction.CurtainBeforeHook;
import org.lastaflute.core.direction.FwAssistDirection;
import org.lastaflute.core.direction.FwCoreDirection;
import org.lastaflute.core.json.JsonResourceProvider;
import org.lastaflute.core.security.InvertibleCryptographer;
import org.lastaflute.core.security.OneWayCryptographer;
import org.lastaflute.core.security.SecurityResourceProvider;
import org.lastaflute.core.time.TimeResourceProvider;
import org.lastaflute.db.dbflute.classification.ListedClassificationProvider;
import org.lastaflute.db.direction.FwDbDirection;
import org.lastaflute.thymeleaf.ThymeleafRenderingProvider;
import org.lastaflute.web.api.ApiFailureHook;
import org.lastaflute.web.direction.FwWebDirection;
import org.lastaflute.web.path.ActionAdjustmentProvider;
import org.lastaflute.web.ruts.multipart.MultipartResourceProvider;
import org.lastaflute.web.ruts.renderer.HtmlRenderingProvider;
import org.lastaflute.web.servlet.cookie.CookieResourceProvider;
import org.lastaflute.web.servlet.filter.cors.CorsHook;
import org.lastaflute.web.servlet.request.UserLocaleProcessProvider;
import org.lastaflute.web.servlet.request.UserTimeZoneProcessProvider;

/**
 * @author jflute
 */
public class HarborFwAssistantDirector extends CachedFwAssistantDirector {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private HarborConfig config;

    // ===================================================================================
    //                                                                              Assist
    //                                                                              ======
    @Override
    protected void prepareAssistDirection(FwAssistDirection direction) {
        direction.directConfig(nameList -> nameList.add("harbor_config.properties"), "harbor_env.properties");
    }

    // ===================================================================================
    //                                                                               Core
    //                                                                              ======
    @Override
    protected void prepareCoreDirection(FwCoreDirection direction) {
        // this configuration is on harbor_env.properties because this is true only when development
        direction.directDevelopmentHere(config.isDevelopmentHere());

        // titles of the application for logging are from configurations
        direction.directLoggingTitle(config.getDomainTitle(), config.getEnvironmentTitle());

        // this configuration is on sea_env.properties because it has no influence to production
        // even if you set true manually and forget to set false back
        direction.directFrameworkDebug(config.isFrameworkDebug()); // basically false

        // you can add your own process when your application is booting
        direction.directCurtainBefore(createCurtainBeforeHook());

        direction.directSecurity(createSecurityResourceProvider());
        direction.directTime(createTimeResourceProvider());
        direction.directJson(createJsonResourceProvider());
        direction.directMail(createMailDeliveryDepartmentCreator().create());
    }

    protected CurtainBeforeHook createCurtainBeforeHook() {
        return new HarborCurtainBeforeHook();
    }

    protected SecurityResourceProvider createSecurityResourceProvider() { // #change_it_first
        final InvertibleCryptographer inver = InvertibleCryptographer.createAesCipher("harbor:dockside:");
        final OneWayCryptographer oneWay = OneWayCryptographer.createSha256Cryptographer();
        return new HarborSecurityResourceProvider(inver, oneWay);
    }

    protected TimeResourceProvider createTimeResourceProvider() {
        return new HarborTimeResourceProvider(config);
    }

    protected JsonResourceProvider createJsonResourceProvider() {
        return new HarborJsonResourceProvider();
    }

    protected HarborMailDeliveryDepartmentCreator createMailDeliveryDepartmentCreator() {
        return new HarborMailDeliveryDepartmentCreator(config);
    }

    // ===================================================================================
    //                                                                                 DB
    //                                                                                ====
    @Override
    protected void prepareDbDirection(FwDbDirection direction) {
        direction.directClassification(createListedClassificationProvider());
    }

    protected ListedClassificationProvider createListedClassificationProvider() {
        return new HarborListedClassificationProvider();
    }

    // ===================================================================================
    //                                                                                Web
    //                                                                               =====
    @Override
    protected void prepareWebDirection(FwWebDirection direction) {
        direction.directRequest(createUserLocaleProcessProvider(), createUserTimeZoneProcessProvider());
        direction.directCookie(createCookieResourceProvider());
        direction.directAdjustment(createActionAdjustmentProvider());
        direction.directMessage(nameList -> nameList.add("harbor_message"), "harbor_label");
        direction.directApiCall(createApiFailureHook());
        direction.directCors(new CorsHook("http://localhost:5000")); // #change_it
        direction.directHtmlRendering(createHtmlRenderingProvider());
        direction.directMultipart(createMultipartResourceProvider());
    }

    protected UserLocaleProcessProvider createUserLocaleProcessProvider() {
        return new HarborUserLocaleProcessProvider();
    }

    protected UserTimeZoneProcessProvider createUserTimeZoneProcessProvider() {
        return new HarborUserTimeZoneProcessProvider();
    }

    protected CookieResourceProvider createCookieResourceProvider() { // #change_it_first
        final InvertibleCryptographer cr = InvertibleCryptographer.createAesCipher("dockside:harbor:");
        return new HarborCookieResourceProvider(config, cr);
    }

    protected ActionAdjustmentProvider createActionAdjustmentProvider() {
        return new HarborActionAdjustmentProvider();
    }

    protected ApiFailureHook createApiFailureHook() {
        return new HarborApiFailureHook();
    }

    protected HtmlRenderingProvider createHtmlRenderingProvider() {
        return new ThymeleafRenderingProvider().asDevelopment(config.isDevelopmentHere());
    }

    protected MultipartResourceProvider createMultipartResourceProvider() {
        return () -> new HarborMultipartRequestHandler();
    }
}
