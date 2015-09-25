<c:import url="${viewPrefix}/common/default_layout.jsp">
<c:param name="title">LastaFlute Example | SignIn</c:param>
<c:param name="contents">
<!-- <main> start main content -->
	<h2 class="pg-content-title"><la:caption key="labels.member.list.title"/></h2>
	<div class="condition">
		<la:form>
			<la:errors/>
			<la:errors property="abc.def"/>
			<table>
				<tr>
					<td><label>会員名</label></td>
					<td><la:text property="memberName"/></td>
				</tr>
				<tr>
					<td><label>購入商品名</label></td>
					<td><la:text property="purchaseProductName"/></td>
				</tr>
				<tr>
					<td><label>会員ステータス</label></td>
					<td>
						<la:select property="memberStatus">
							<c:forEach var="status" items="${memberStatusMap}">
								<la:option value="${f:h(status.key)}">${f:h(status.value)}</la:option>
							</c:forEach>
						</la:select>
					</td>
				</tr>
				<tr>
					<td><label>未払いあり</label></td>
					<td><la:checkbox property="unpaid"/></td>
				</tr>
				<tr>
					<td><label>正式会員日</label></td>
					<td><la:text property="formalizedDateFrom" size="14"/>～
					<la:text property="formalizedDateTo" size="14"/></td>
				</tr>
			</table>
			<la:submit property="doSearch" value="検索"/>
		</la:form>
	</div>
	<hr />
	<la:link href="/member/add/">新規登録</la:link>
	<table>
		<thead>
			<tr>
				<th>会員ID</th>
				<th>会員名</th>
				<th>会員ステータス</th>
				<th>正式会員日</th>
				<th>会員更新日時</th>
				<th>編集</th>
				<th>購入履歴</th>
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
						<la:link href="/member/edit/${f:h(bean.memberId)}">編集</la:link>
					</c:if>
					<c:if test="${bean.withdrawalMember}">編集不可</c:if>
				</td>
				<td>
					<c:if test="${bean.purchaseCount > 0}">
						<la:link href="/member/purchase/list/${f:h(bean.memberId)}/1">購入履歴</la:link>
					</c:if>
				</td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
	<c:import url="${viewPrefix}/common/paging_navi.jsp"/>
</div>
<!-- </main> end of main content -->
</c:param>
</c:import>

