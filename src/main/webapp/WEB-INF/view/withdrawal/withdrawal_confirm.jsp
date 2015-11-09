<c:import url="${viewPrefix}/common/default_layout.jsp">
<c:param name="contents">
<!-- <main> start main content -->
<div class="contents">
	<h2 class="sg-content-title"><la:caption key="labels.withdrawal.confirm.title"/></h2>
	<section class="withdrawal-reason-select-box">
		<h3 class="sg-content-title-second">Confirm your withdrawal reason</h3>
		<la:form styleClass="withdrawal-form" action="/withdrawal/done/">
			<la:captionCls name="WithdrawalReason" value="${f:h(reasonCode)}"/>
			${f:h(reasonInput)}
			<la:hidden property="reasonCode" value="${reasonCode}" /> 
			<la:hidden property="reasonInput" value="${reasonInput}" />
			<la:submit property="done" value="labels.finish"/> 
		</la:form>
	</section>
</div>
<!-- </main> end of main content -->
<script src="${ctx}/js/common.js" ></script>
</c:param>
</c:import>