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

import java.util.HashMap;
import java.util.Map;

import org.docksidestage.app.web.base.HarborBaseAction;
import org.lastaflute.web.Execute;
import org.lastaflute.web.login.AllowAnyoneAccess;
import org.lastaflute.web.response.JsonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jflute
 */
@AllowAnyoneAccess
public class VariousJsonParamAction extends HarborBaseAction {

    private static final Logger logger = LoggerFactory.getLogger(VariousJsonParamAction.class);

    @Execute
    public JsonResponse<Map<String, Object>> index(VariousJsonParamForm form) {
        logger.debug("param = " + form.param);
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("form", form);
        return asJson(map);
    }
}
