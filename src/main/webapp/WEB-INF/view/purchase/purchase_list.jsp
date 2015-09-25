<c:import url="${viewPrefix}/common/default_layout.jsp">
<c:param name="contents">
<!-- <main> start main content -->
<div class="contents">
	<h2 class="pg-content-title">Purchase List</h2>
	<section class="product-result-box">
		<h3 class="pg-content-title-second">Purchase List</h3>
		<table class="product-list-tbl">
			<thead>
				<tr>
					<th>Date</th>
					<th>Product</th>
					<th>Price</th>
					<th>Amount</th>
					<th>Status</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="bean" items="${beans}">
				<tr>
					<td>${f:h(bean.purchaseDatetime)}</td>
					<td><a href="${ctx}/product/detail/${f:h(bean.productId)}">${f:h(bean.productName)}</a></td>
					<td>${f:h(bean.purchasePrice)}</td>
					<td>${f:h(bean.purchaseCount)}</td>
					<td></td>
				</tr>
				</c:forEach>
			</tbody>
		</table>
		<section class="product-list-paging-box">
			<c:import url="${viewPrefix}/common/paging_navi.jsp"/>
		</section>
	</section>
</div>
<!-- </main> end of main content -->
</c:param>
</c:import>
