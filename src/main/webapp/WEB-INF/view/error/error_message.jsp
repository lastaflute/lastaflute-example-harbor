<c:import url="${viewPrefix}/common/header.jsp">
	<c:param name="title" value="エラー通知"/>
</c:import>
<div class="contents">
	<h2><la:message key="labels.error.message.title"/></h2>
	<la:errors/>
	<div class="listback">
		<la:link href="/member/list/">to <la:caption key="labels.member.list.title"/></la:link>
	</div>
</div>
<c:import url="${viewPrefix}/common/footer.jsp"/>