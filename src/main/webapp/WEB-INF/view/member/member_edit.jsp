<c:import url="${viewPrefix}/common/default_layout.jsp">
<c:param name="contents">
<!-- <main> start main content -->
<div class="contents">
	<h2 class="sg-content-title"><la:caption key="labels.member.edit.title"/></h2>
	<section class="member-input-box">
		<la:form styleClass="">
			<la:errors/>
			<table>
				<tr>
					<td>Member Name</td>
					<td><la:text property="memberName"/></td>
				</tr>
				<tr>
					<td>Member Account</td>
					<td><la:text property="memberAccount"/></td>
				</tr>
				<tr>
					<td>Birthdate</td>
					<td><la:text property="birthdate"/></td>
				</tr>
				<tr>
					<td>Formalized Date</td>
					<td>${f:h(formalizedDate)}</td>
				</tr>
				<tr>
					<td>Member Status</td>
					<td>
						<la:select property="memberStatus">
							<la:option value="" key="labels.listbox.caption.tell"/>
							<la:optionsCls name="MemberStatus"/>
						</la:select>
					</td>
				</tr>
				<tr>
					<td>Latest Login Datetime</td>
					<td>${f:h(latestLoginDatetime)}</td>
				</tr>
				<tr>
					<td>Update Datetime</td>
					<td>${f:h(updateDatetime)}</td>
				</tr>
			</table>
			<la:hidden property="memberId"/>
			<la:hidden property="formalizedDate"/>
			<la:hidden property="latestLoginDatetime"/>
			<la:hidden property="previousStatus"/>
			<la:hidden property="updateDatetime"/>
			<la:hidden property="versionNo"/>
			<la:submit property="update" value="labels.update"/>
			<la:submit property="withdrawal" value="forced withdraw"/>
		</la:form>
		<div class="listback">
			<la:link href="/member/list/">back to Member list</la:link>
		</div>
	</section>
</div>
<!-- </main> end of main content -->
</c:param>
</c:import>

