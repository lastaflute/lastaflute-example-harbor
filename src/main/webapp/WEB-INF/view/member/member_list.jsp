<c:import url="${viewPrefix}/common/default_layout.jsp">
<c:param name="contents">
<!-- <main> start main content -->
<div class="contents">
	<h2 class="pg-content-title"><la:caption key="labels.member.list.title"/></h2>
	<section class="member-search-box">
		<h3 class="pg-content-title-second">Search Condition</h3>
		<la:form styleClass="member-search-form">
			<la:errors/>
			<ul class="member-search-condition-list">
				<li><span>Member Name</span><la:text property="memberName"/></li>
				<li><span>Purchase Product</span><la:text property="purchaseProductName"/></li>
				<li>
					<span>Member Status</span>
					<la:select property="memberStatus">
						<la:option value="" key="labels.listbox.caption.tell"/>
						<la:optionsCls name="MemberStatus"/>
					</la:select>
				</li>
				<li><span>has unpaid?</span><la:checkbox property="unpaid"/></li>
				<li><span>Formalized Date</span><la:text property="formalizedFrom" size="10"/>～<la:text property="formalizedTo" size="10"/></li>
			</ul>
			<la:submit value="labels.search"/>
		</la:form>
	</section>
	<section class="member-result-box">
		<h3 class="pg-content-title-second">Search Results</h3>
		<la:link href="/member/add/">add Member</la:link>
		<table class="member-list-tbl">
			<thead>
				<tr>
					<th>Member Id</th>
					<th>Member Name</th>
					<th>Member Status</th>
					<th>Formalized Date</th>
					<th>Update Datetime</th>
					<th>Editable</th>
					<th>Purchase</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="bean" items="${beans}">
				<tr>
					<td>${f:h(bean.memberId)}</td>
					<td>${f:h(bean.memberName)}</td>
					<td>${f:h(bean.memberStatusName)}</td>
					<td>${f:h(bean.formalizedDate)}</td>
					<td>${f:h(bean.updateDatetime)}</td>
					<td>
						<c:if test="${!bean.withdrawalMember}">
							<la:link href="/member/edit/${f:h(bean.memberId)}">Edit</la:link>
						</c:if>
						<c:if test="${bean.withdrawalMember}">Cannot</c:if>
					</td>
					<td>
						<c:if test="${bean.purchaseCount > 0}">
							<la:link href="/member/purchase/list/${f:h(bean.memberId)}/1">has Purchases</la:link>
						</c:if>
					</td>
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
