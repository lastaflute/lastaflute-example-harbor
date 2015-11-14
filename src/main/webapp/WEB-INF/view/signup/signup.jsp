<c:import url="${viewPrefix}/common/default_layout.jsp">
<c:param name="title">LastaFlute Example | SignIn</c:param>
<c:param name="contents">
<!-- <main> start main content -->
<div class="">
	<h2 class="content-title"><la:caption key="labels.signup.title"/></h2>
	<la:errors/>
	<section class="sign-in-box">
		<la:form styleClass="signin-form">
			<ul>
				<li>
					<p><la:message key="labels.account"/></p>
					<la:text property="email" placeholder="labels.signin.input.account"/>
				</li>
				<li>
					<p><la:message key="labels.password"/></p>
					<la:password property="password" placeholder="labels.signin.input.password"/>
				</li>
				<li>
					<p><label><la:checkbox property="autoLogin"/> Remember Account</label></p>
					<la:submit property="doSignin" value="labels.signin.button"/>
				</li>
			</ul>
		</la:form>
	</section>
</div>
<!-- </main> end of main content -->
</c:param>
</c:import>