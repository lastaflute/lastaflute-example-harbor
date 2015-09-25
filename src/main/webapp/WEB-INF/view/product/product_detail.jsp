<c:import url="${viewPrefix}/common/default_layout.jsp">
<c:param name="contents">
<!-- <main> start main content -->
<div class="contents">
	<h2 class="pg-content-title"><la:caption key="labels.product.detail.title"/></h2>
	<table>
		<tr>
			<td><label>product</label></td>
			<td>${product.productName}</td>
		</tr>
		<tr>
			<td><label>category</label></td>
			<td>${product.categoryName}</td>
		</tr>
	</table>
</div>
<!-- </main> end of main content -->
</c:param>
</c:import>
