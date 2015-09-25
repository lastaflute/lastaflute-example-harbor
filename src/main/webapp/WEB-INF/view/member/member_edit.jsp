<c:import url="${viewPrefix}/common/header.jsp">
	<c:param name="title" value="会員編集"/>
</c:import>
<div class="contents">
	<h2>会員編集</h2>
	<la:form>
		<la:errors/>
		<table>
			<tr>
				<td>会員名</td>
				<td><la:text property="memberName"/></td>
			</tr>
			<tr>
				<td>誕生日</td>
				<td><la:text property="birthdate"/></td>
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
				<td><la:text property="memberAccount"/></td>
			</tr>
			<tr>
				<td>正式会員日</td>
				<td>${f:h(formalizedDate)}</td>
			</tr>
			<tr>
				<td>最終ログイン日時</td>
				<td>${f:h(latestLoginDatetime)}</td>
			</tr>
			<tr>
				<td>更新日時</td>
				<td>${f:h(updateDatetime)}</td>
			</tr>
		</table>
		<la:hidden property="memberId"/>
		<la:hidden property="formalizedDate"/>
		<la:hidden property="latestLoginDatetime"/>
		<la:hidden property="updateDatetime"/>
		<la:hidden property="previousStatusCode"/>
		<la:hidden property="versionNo"/>
		<la:submit property="doUpdate" value="更新"/>
		<la:submit property="doDelete" value="強制退会"/>
	</la:form>
	<div class="listback">
		<la:link href="/member/list/">会員一覧へ</la:link>
	</div>
</div>
<c:import url="${viewPrefix}/common/footer.jsp"/>
