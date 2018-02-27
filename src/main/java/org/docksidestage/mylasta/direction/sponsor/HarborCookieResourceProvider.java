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
package org.docksidestage.mylasta.direction.sponsor;

import org.docksidestage.mylasta.direction.HarborConfig;
import org.lastaflute.core.security.InvertibleCryptographer;
import org.lastaflute.web.servlet.cookie.CookieResourceProvider;

/**
 * @author jflute
 */
public class HarborCookieResourceProvider implements CookieResourceProvider {

    protected final HarborConfig config;
    protected final InvertibleCryptographer cookieCipher;

    public HarborCookieResourceProvider(HarborConfig config, InvertibleCryptographer cookieCipher) {
        this.config = config;
        this.cookieCipher = cookieCipher;
    }

    public String provideDefaultPath() {
        return config.getCookieDefaultPath();
    }

    public Integer provideDefaultExpire() {
        return config.getCookieDefaultExpireAsInteger();
    }

    public InvertibleCryptographer provideCipher() {
        return cookieCipher;
    }
}
