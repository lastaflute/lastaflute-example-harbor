<%@page pageEncoding="UTF-8"%>
<c:if test="${pagingNavi != null}">
<c:if test="${pagingNavi.displayPagingNavi}">
<div class="paging-navi">
	${pagingNavi.currentPageNumber}/${pagingNavi.allPageCount} (${pagingNavi.allRecordCount})
	<c:if test="${pagingNavi.existsPreviousPage}">
		<la:link href="${pagingNavi.previousPageLinkHref}">pre</la:link>
	</c:if>
	<c:forEach var="link" items="${pagingNavi.pageNumberLinkList}">
		<c:if test="${!link.current}">
			<span id="pageNumberElement"><la:link href="${link.pageNumberLinkHref}">${link.pageNumberElement}</la:link></span>
		</c:if>
		<c:if test="${link.current}">
			<span id="pageNumberElement">${link.pageNumberElement}</span>
		</c:if>
	</c:forEach>
	<c:if test="${pagingNavi.existsNextPage}">
		<la:link href="${pagingNavi.nextPageLinkHref}">next</la:link>
	</c:if>
</div>
</c:if>
</c:if>
