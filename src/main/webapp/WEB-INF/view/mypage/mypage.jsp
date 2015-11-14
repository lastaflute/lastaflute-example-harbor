<c:import url="${viewPrefix}/common/default_layout.jsp">
<c:param name="contents">
<!-- <main> start main content -->
<div class="contents">
	<h2 class="content-title"><la:caption key="labels.mypage.title"/></h2>
	<section class="recent-purchase-box">
	</section>
	<section class="follow-box">
		<h3>FOLLOW</h3>
	</section>
	<c:forEach var="bean" items="${beans}">
		${f:h(bean.productName)}, ${f:h(bean.regularPrice)}<br>
	</c:forEach>
</div>
<!-- </main> end of main content -->
</c:param>
</c:import>