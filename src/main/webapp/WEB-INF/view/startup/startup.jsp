<c:import url="${viewPrefix}/common/default_layout.jsp">
<c:param name="title">LastaFlute Example | Start up</c:param>
<c:param name="contents">
<!-- <main> start main content -->
<div class="contents">
	<h2 class="pg-content-title">Start up!!</h2>
	<section class="sign-in-box">
		<la:form styleClass="signin-form">
			<c:if test="${bean.isComplete}">
				<p>new Project created!: ${bean.projectPath}</p>
			</c:if>
			<la:errors/>
			<ul>
				<li>
					<p>Domain:</p>
					<la:text property="domain" placeholder="input domain e.g. dancingdb.org" />
				</li>
				<li>
					<p>Service Name</p>
					<la:text property="serviceName"  placeholder="input service name e.g. dancers" />
				</li>
				<li>
					<la:submit property="doStartup" value="Start up"/>
				</li>
			</ul>
		</la:form>
	</section>
</div> 
<!-- </main> end of main content -->
</c:param>
</c:import>