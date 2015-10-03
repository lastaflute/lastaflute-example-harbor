<c:import url="${viewPrefix}/common/default_layout.jsp">
<c:param name="contents">
<!-- <main> start main content -->
<div class="contents">
	<h2 class="pg-content-title"><la:caption key="labels.member.add.title"/></h2>
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
					<td>Member Status</td>
					<td>
						<la:select property="memberStatus">
							<la:option value="" key="labels.listbox.caption.tell"/>
							<la:optionsCls name="MemberStatus"/>
						</la:select>
					</td>
				</tr>
			</table>
			<la:submit property="register" value="labels.register"/>
		</la:form>
		<div class="listback">
			<la:link href="/member/list/">back to Member list</la:link>
		</div>
	</section>
</div>
<!-- </main> end of main content -->
</c:param>
</c:import>

