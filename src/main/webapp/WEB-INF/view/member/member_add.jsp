<c:import url="${viewPrefix}/common/header.jsp">
	<c:param name="title" value="会員追加"/>
</c:import>
<div class="contents">
	<h2>会員追加</h2>
	<la:form>
		<la:errors/>
		<table>
			<tr>
				<td>会員名</td>
				<td><la:text property="memberName" /></td>
			</tr>
			<tr>
				<td>誕生日</td>
				<td><la:text property="birthdate" /></td>
			</tr>
			<tr>
				<td>会員ステータス</td>
				<td>
					<la:select property="memberStatusCode">
						<c:forEach var="status" items="${memberStatusMap}">
							<la:option value="${f:h(status.key)}">${f:h(status.value)}</la:option>
						</c:forEach>
					</la:select>
				</td>
			</tr>
			<tr>
				<td>会員アカウント</td>
				<td><la:text property="memberAccount" /></td>
			</tr>
		</table>
		<la:submit property="doAdd" value="登録" />
	</la:form>
	<div class="listback">
		<la:link href="/member/list/">会員一覧へ</la:link>
	</div>
</div>
<c:import url="${viewPrefix}/common/footer.jsp"/>
