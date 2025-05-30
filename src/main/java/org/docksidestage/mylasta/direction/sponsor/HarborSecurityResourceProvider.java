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
package org.docksidestage.mylasta.direction.sponsor;

import org.lastaflute.core.security.InvertibleCryptographer;
import org.lastaflute.core.security.OneWayCryptographer;
import org.lastaflute.core.security.SecurityResourceProvider;

/**
 * @author jflute
 */
public class HarborSecurityResourceProvider implements SecurityResourceProvider {

    protected final InvertibleCryptographer primaryInvertibleCryptographer;
    protected final OneWayCryptographer primaryOneWayCryptographer;

    public HarborSecurityResourceProvider(InvertibleCryptographer primaryInvertibleCryptographer,
            OneWayCryptographer primaryOneWayCryptographer) {
        this.primaryInvertibleCryptographer = primaryInvertibleCryptographer;
        this.primaryOneWayCryptographer = primaryOneWayCryptographer;
    }

    public InvertibleCryptographer providePrimaryInvertibleCryptographer() {
        return primaryInvertibleCryptographer;
    }

    public OneWayCryptographer providePrimaryOneWayCryptographer() {
        return primaryOneWayCryptographer;
    }
}
