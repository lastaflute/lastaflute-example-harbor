<c:import url="${viewPrefix}/common/header.jsp">
	<c:param name="title" value="会員編集"/>
</c:import>
<div class="contents">
	<h2>NowTry upload</h2>
	<la:form action="abc" enctype="multipart/form-data">
		<la:errors/>
		<la:text property="birthdate" value="2015/05/03"/>
		<input type="file" name="file">
		<la:submit property="doUpload" value="upup"/>
	</la:form>
</div>
<c:import url="${viewPrefix}/common/footer.jsp"/>
