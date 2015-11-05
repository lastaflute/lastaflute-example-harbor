<c:import url="${viewPrefix}/common/default_layout.jsp">
<c:param name="contents">
<div class="contents">
	<h2 class="pg-content-title"><la:caption key="labels.profile.title"/></h2>
	<h3 class="pg-content-title-second">${beans.memberName}</h3>
	<c:forEach var="bean" items="${beans.productList}">
		${bean.productName},${bean.regularPrice}<br>
	</c:forEach>
</div>
</c:param>
</c:import>