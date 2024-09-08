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
package org.docksidestage.app.web.base.paging;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.function.Consumer;

import org.dbflute.cbean.paging.numberlink.PageNumberLink;
import org.dbflute.cbean.paging.numberlink.range.PageRangeOption;
import org.dbflute.cbean.result.PagingResultBean;
import org.lastaflute.di.helper.beans.BeanDesc;
import org.lastaflute.di.helper.beans.PropertyDesc;
import org.lastaflute.di.helper.beans.factory.BeanDescFactory;
import org.lastaflute.web.util.LaRequestUtil;

/**
 * @author jflute
 */
public class PagingNavi implements Serializable { // #app_customize

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final boolean displayPagingNavi;
    protected final int allRecordCount;
    protected final int allPageCount;
    protected final int currentPageNumber;
    protected final boolean existsPreviousPage;
    protected final boolean existsNextPage;
    protected final String previousPageLinkHref;
    protected final String nextPageLinkHref;
    protected final List<PageNumberLink> pageNumberLinkList;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param page The selected page as bean of paging result. (NotNull)
     * @param opLambda The callback for option of page range. (NotNull)
     * @param queryForm The form for query string added to link. (NotNull)
     */
    public PagingNavi(PagingResultBean<?> page, Consumer<PageRangeOption> opLambda, Object queryForm) {
        assertArgumentNotNull("page", page);
        assertArgumentNotNull("opLambda", opLambda);
        displayPagingNavi = !page.isEmpty();

        allRecordCount = page.getAllRecordCount();
        allPageCount = page.getAllPageCount();
        currentPageNumber = page.getCurrentPageNumber();
        existsPreviousPage = page.existsPreviousPage();
        existsNextPage = page.existsNextPage();

        previousPageLinkHref = createTargetPageNumberLink(getCurrentPageNumber() - 1, queryForm);
        nextPageLinkHref = createTargetPageNumberLink(getCurrentPageNumber() + 1, queryForm);
        pageNumberLinkList = createPageNumberLinkList(page, opLambda, queryForm);
    }

    // ===================================================================================
    //                                                                     PageNumber Link
    //                                                                     ===============
    /**
     * Create target page number link.
     * @param pageNumber Target page number.
     * @param queryForm The form for query string added to link. (NotNull)
     * @return The link expression for target page. (NotNull)
     */
    protected String createTargetPageNumberLink(int pageNumber, Object queryForm) {
        return pageNumber + buildQueryString(queryForm);
    }

    protected String buildQueryString(Object queryForm) {
        final BeanDesc beanDesc = BeanDescFactory.getBeanDesc(queryForm.getClass());
        final int propSize = beanDesc.getPropertyDescSize();
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < propSize; i++) {
            final PropertyDesc pd = beanDesc.getPropertyDesc(i);
            final Object value = beanDesc.getPropertyDesc(i).getValue(queryForm);
            if (value == null || (value instanceof String && ((String) value).isEmpty())) {
                continue;
            }
            sb.append(sb.length() == 0 ? "?" : "&");
            sb.append(encode(pd.getPropertyName())).append("=").append(encode(value.toString()));
        }
        return sb.toString();
    }

    protected String encode(String input) {
        final String encoding = LaRequestUtil.getRequest().getCharacterEncoding();
        try {
            return URLEncoder.encode(input, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unknown encoding: " + encoding, e);
        }
    }

    /**
     * Create the list of page number link.
     * @param page The bean of paging result. (NotNull)
     * @param opLambda The callback for option of page range. (NotNull)
     * @param paramForm The form for GET parameter added to link. (NullAllowed)
     * @return The list of page number link. (NotNull)
     */
    protected List<PageNumberLink> createPageNumberLinkList(PagingResultBean<?> page, Consumer<PageRangeOption> opLambda,
            Object paramForm) {
        return page.pageRange(op -> opLambda.accept(op)).buildPageNumberLinkList((pageNumberElement, current) -> {
            return createPageNumberLink(paramForm, pageNumberElement, current);
        });
    }

    protected PageNumberLink createPageNumberLink(Object queryForm, int pageNumberElement, boolean current) {
        final String targetPageNumberLink = createTargetPageNumberLink(pageNumberElement, queryForm);
        return newPageNumberLink().initialize(pageNumberElement, current, targetPageNumberLink);
    }

    protected PageNumberLink newPageNumberLink() {
        return new PageNumberLink();
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    protected void assertArgumentNotNull(String variableName, Object value) {
        if (variableName == null) {
            String msg = "The value should not be null: variableName=null value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return "{display=" + displayPagingNavi + ", allRecordCount=" + allRecordCount + ", allPageCount=" + allPageCount
                + ", currentPageNumber=" + currentPageNumber + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public boolean isDisplayPagingNavi() {
        return displayPagingNavi;
    }

    public int getAllRecordCount() {
        return allRecordCount;
    }

    public int getAllPageCount() {
        return allPageCount;
    }

    public int getCurrentPageNumber() {
        return currentPageNumber;
    }

    public boolean isExistsPreviousPage() { // 'is' prefix for e.g. EL expression
        return existsPreviousPage;
    }

    public boolean isExistsNextPage() { // 'is' prefix for e.g. EL expression
        return existsNextPage;
    }

    public String getPreviousPageLinkHref() {
        return previousPageLinkHref;
    }

    public String getNextPageLinkHref() {
        return nextPageLinkHref;
    }

    public List<PageNumberLink> getPageNumberLinkList() {
        return pageNumberLinkList;
    }
}
