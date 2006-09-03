<%@ include file="/common/taglibs.jsp"%>

<head>
	<title><fmt:message key="userProfile.title" /></title>
	<content tag="heading">
	<fmt:message key="userProfile.heading" />
	</content>
	<meta name="menu" content="UserMenu" />
	<script type="text/javascript" src="<c:url value='/scripts/selectbox.js'/>"></script>
</head>

<spring:bind path="user.*">
	<c:if test="${not empty status.errorMessages}">
		<div class="error">
			<c:forEach var="error" items="${status.errorMessages}">
				<img src="<c:url value="/images/iconWarning.gif"/>" alt="<fmt:message key="icon.warning"/>" class="icon" />
				<c:out value="${error}" escapeXml="false" />
				<br />
			</c:forEach>
		</div>
	</c:if>
</spring:bind>

<form method="post" action="<c:url value="/editUser.html"/>" id="userForm" onsubmit="return onFormSubmit(this)">
	<spring:bind path="user.id">
		<input type="hidden" name="id" value="<c:out value="${status.value}"/>" />
	</spring:bind>
	<input type="hidden" name="from" value="<c:out value="${param.from}"/>" />

	<c:if test="${cookieLogin == 'true'}">
		<spring:bind path="user.password">
			<input type="hidden" name="password" value="<c:out value="${status.value}"/>" />
		</spring:bind>
		<spring:bind path="user.confirmPassword">
			<input type="hidden" name="confirmPassword" value="<c:out value="${status.value}"/>" />
		</spring:bind>
	</c:if>

	<c:if test="${empty user.userName}">
		<input type="hidden" name="encryptPass" value="true" />
	</c:if>

	<ul>
		<li class="buttonBar center">
			<c:set var="buttons">
				<input type="submit" class="button" name="save" onclick="bCancel=false" value="<fmt:message key="button.save"/>" />

				<c:if test="${param.from == 'list' and param.method != 'Add'}">
					<input type="submit" class="button" name="delete" onclick="bCancel=true;return confirmDelete('user')"
						value="<fmt:message key="button.delete"/>" />
				</c:if>

				<input type="submit" class="button" name="cancel" onclick="bCancel=true" value="<fmt:message key="button.cancel"/>" />
			</c:set>
			<c:out value="${buttons}" escapeXml="false" />
		</li>
		<li class="info">
			<c:choose>
				<c:when test="${param.from == 'list'}">
					<p>
						<fmt:message key="userProfile.admin.message" />
					</p>
				</c:when>
				<c:otherwise>
					<p>
						<fmt:message key="userProfile.message" />
					</p>
				</c:otherwise>
			</c:choose>
		</li>
		<li>
			<Gemma:label styleClass="desc" key="user.userName" />
			<spring:bind path="user.userName">
				<c:choose>
					<c:when test="${empty user.userName}">
						<input type="text" name="userName" value="<c:out value="${status.value}"/>" id="userName" />
						<span class="fieldError"> <c:out value="${status.errorMessage}" /> </span>
					</c:when>
					<c:otherwise>
						<c:out value="${user.userName}" />
						<input type="hidden" name="userName" value="<c:out value="${status.value}"/>" id="userName" />
					</c:otherwise>
				</c:choose>
			</spring:bind>
		</li>
		<c:if test="${cookieLogin != 'true'}">
			<li>
				<div>
					<div class="left">
						<Gemma:label styleClass="desc" key="user.password" />

						<spring:bind path="user.password">
							<input type="password" id="password" name="password" size="40" value="<c:out value="${status.value}"/>"
								onchange="passwordChanged(this)" />
							<span class="fieldError"> <c:out value="${status.errorMessage}" /> </span>
						</spring:bind>
					</div>
					<div>
						<Gemma:label key="user.confirmPassword" />
						<spring:bind path="user.confirmPassword">
							<span class="fieldError"><c:out value="${status.errorMessage}" /> </span>
							<input type="password" name="confirmPassword" id="confirmPassword" value="<c:out value="${status.value}"/>"
								class="text medium" />
						</spring:bind>
					</div>
				</div>
			</li>
		</c:if>

		<li>
			<Gemma:label styleClass="desc" key="user.firstName" />

			<spring:bind path="user.firstName">
				<input type="text" name="firstName" value="<c:out value="${status.value}"/>" id="firstName" />
				<span class="fieldError"> <c:out value="${status.errorMessage}" /> </span>
			</spring:bind>
		</li>
		<li>
			<Gemma:label styleClass="desc" key="user.lastName" />

			<spring:bind path="user.lastName">
				<input type="text" name="lastName" value="<c:out value="${status.value}"/>" id="lastName" />
				<span class="fieldError"> <c:out value="${status.errorMessage}" /> </span>
			</spring:bind>
		</li>
		<li>
			<Gemma:label styleClass="desc" key="user.email" />

			<spring:bind path="user.email">
				<input type="text" name="email" value="<c:out value="${status.value}"/>" id="email" size="50" />
				<span class="fieldError"> <c:out value="${status.errorMessage}" /> </span>
			</spring:bind>
		</li>
		<li>
			<Gemma:label styleClass="desc" key="user.passwordHint" />

			<spring:bind path="user.passwordHint">
				<input type="text" name="passwordHint" value="<c:out value="${status.value}"/>" id="passwordHint" size="50" />
				<span class="fieldError"> <c:out value="${status.errorMessage}" /> </span>
			</spring:bind>
		</li>
		<c:choose>
			<c:when test="${param.from == 'list' or param.method == 'Add'}">
				<li>
					<fieldset>
						<legend>
							<fmt:message key="userProfile.accountSettings" />
						</legend>

						<spring:bind path="user.enabled">
							<input type="hidden" name="_<c:out value="${status.expression}"/>" value="visible" />
							<input type="checkbox" name="<c:out value="${status.expression}"/>"
								<c:if test="${status.value}">checked="checked"</c:if> />
						</spring:bind>
						<label for="enabled">
							<fmt:message key="user.enabled" />
						</label>
					</fieldset>
				</li>
				<li>
					<fieldset class="pickList">
						<legend>
							<fmt:message key="userProfile.assignRoles" />
						</legend>
						<table class="pickList">
							<tr>
								<th class="pickLabel">
									<Gemma:label key="user.availableRoles" colon="false" styleClass="required" />
								</th>
								<td>
								</td>
								<th class="pickLabel">
									<Gemma:label key="user.roles" colon="false" styleClass="required" />
								</th>
							</tr>
							<%-- the 'availableRoles' should be in the context. user.roles is filled in by the controller --%>
							<%-- <c:set var="leftList" value="${availableRoles}" scope="request" /> --%>
							<c:set var="rightList" value="${user.roles}" scope="request" />
							<c:import url="/WEB-INF/pages/pickList.jsp">
								<c:param name="listCount" value="1" />
								<c:param name="leftId" value="availableRoles" />
								<c:param name="rightId" value="userRoles" />
							</c:import>
						</table>
					</fieldset>
				</li>
			</c:when>
			<c:when test="${not empty user.userName}">
				<li>
					<Gemma:label key="user.roles" />
					<c:forEach var="role" items="${user.roles}" varStatus="status">
						<c:out value="${role.name}" />
						<c:if test="${!status.last}">,</c:if>
						<input type="hidden" name="userRoles" value="<c:out value="${role.name}"/>" />
					</c:forEach>
					<spring:bind path="user.enabled">
						<input type="hidden" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" />
					</spring:bind>
				</li>
			</c:when>
		</c:choose>


		<li class="buttonBar bottom">
			<c:out value="${buttons}" escapeXml="false" />
		</li>
	</ul>

</form>

<script type="text/javascript">
    Form.focusFirstElement(document.forms["userForm"]);
    highlightFormElements();

    function passwordChanged(passwordField) {
        var origPassword = "<c:out value="${user.password}"/>";
        if (passwordField.value != origPassword) {
            createFormElement("input", "hidden",
                              "encryptPass", "encryptPass",
                              "true", passwordField.form);
        }
    }

<!-- This is here so we can exclude the selectAll call when roles is hidden -->
function onFormSubmit(theForm) {
<c:if test="${param.from == 'list'}">
    selectAll('roles');
</c:if>
    return validateUser(theForm);
}
</script>

<validate:javascript formName="user" staticJavascript="false" />
<script type="text/javascript" src="<c:url value="/scripts/validator.jsp"/>"></script>

