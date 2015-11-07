<c:import url="${viewPrefix}/common/default_layout.jsp">
<c:param name="contents">
<!-- <main> start main content -->
<div class="contents">
	<h2 class="pg-content-title"><la:caption key="labels.profile.title"/></h2>
	<article class="profile-contents-box">
		<section class="profile-info-box">
			<h3 class="pg-content-title-second">${beans.memberName}</h3>
			<ul class="profile-info-detail">
				<li>${beans.memberStatusName}</li>
				<li>${beans.servicePointCount}</li>
				<li>${beans.serviceRankName}</li>
			</ul>
		</section>
		<section class="profile-product-box">
			<h3 class="pg-content-title-second">PurchaseHistory</h3>
			<table class="product-list-tbl">
				<thead>
					<tr>
						<th>ProductName</th>
						<th>Price</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="bean" items="${beans.productList}">
						<tr>
							<td>${bean.productName}</td>
							<td>${bean.regularPrice}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</section>
	</article>
</div>
</c:param>
</c:import>