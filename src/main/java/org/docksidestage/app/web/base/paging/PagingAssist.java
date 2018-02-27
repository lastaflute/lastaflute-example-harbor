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
package org.docksidestage.app.web.base.paging;

import java.util.List;

import org.dbflute.Entity;
import org.dbflute.cbean.result.PagingResultBean;
import org.lastaflute.web.response.render.RenderData;

/**
 * @author jflute
 */
public class PagingAssist { // #app_customize

    public static final String NAVI_KEY = "pagingNavi"; // public for e.g. unit test

    // ===================================================================================
    //                                                                       HTML Response
    //                                                                       =============
    // #delete_ifapi
    /**
     * Register the paging navigation as page-range for HTML response.
     * @param data The data object to render the HTML. (NotNull)
     * @param page The selected page as bean of paging result. (NotNull)
     * @param form The form for query string added to link. (NotNull)
     */
    public void registerPagingNavi(RenderData data, PagingResultBean<? extends Entity> page, Object form) {
        data.register(NAVI_KEY, new PagingNavi(page, op -> op.rangeSize(3).fillLimit(), form));
    }

    // ===================================================================================
    //                                                                       JSON Response
    //                                                                       =============
    /**
     * Create paging result for JSON response.
     * @param page The selected result bean of paging. (NotNull)
     * @param items The list of actual data to display. (NotNull)
     * @return The new-created result of paging. (NotNull)
     */
    public <ENTITY extends Entity, BEAN> SearchPagingResult<BEAN> createPagingResult(PagingResultBean<ENTITY> page, List<BEAN> items) {
        return new SearchPagingResult<BEAN>(page, items);
    }
}
