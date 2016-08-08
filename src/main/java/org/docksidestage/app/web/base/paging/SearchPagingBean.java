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
package org.docksidestage.app.web.base.paging;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.dbflute.Entity;
import org.dbflute.cbean.result.PagingResultBean;
import org.lastaflute.web.validation.Required;

/**
 * @param <BEAN> The type of row bean
 * @author iwamatsu0430
 * @author jflute
 */
public class SearchPagingBean<BEAN> { // #app_customize

    /** page size */
    @Required
    public final Integer itemsPerPage;

    /** current page number */
    @Required
    public final Integer currentPage;

    /** count of total items (records) */
    @Required
    public final Integer totalItems;

    /** count of total pages */
    @Required
    public final Integer totalPages;

    /** paging data */
    @NotNull
    @Valid
    public final List<BEAN> items;

    public SearchPagingBean(PagingResultBean<? extends Entity> page, List<BEAN> items) {
        this.itemsPerPage = page.getPageSize();
        this.currentPage = page.getCurrentPageNumber();
        this.totalItems = page.getAllRecordCount();
        this.totalPages = page.getAllPageCount();
        this.items = items;
    }
}
