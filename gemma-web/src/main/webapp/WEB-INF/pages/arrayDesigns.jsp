<%-- $Id$ --%>
<%@ include file="/common/taglibs.jsp"%>
<html>
	<head>
		<content tag="heading">
		Array designs
		</content>
		<title>Array designs</title>
	</head>
	<body>
	<form name="ArrayDesignFilter" action="filterArrayDesigns.html" method="POST">
			<input type="text" name="filter" />
			<input type="submit" value="filter">
		</form>
	
		<h2>
			Search results
		</h2>
		<h3>
			<c:out value="${numArrayDesigns }" />
			Array Designs found.
		</h3>

				<display:table name="arrayDesigns" sort="list" class="list" requestURI="" id="arrayDesignList"
				pagesize="30" decorator="ubic.gemma.web.taglib.displaytag.expression.arrayDesign.ArrayDesignWrapper">
					<display:column property="name" sortable="true" href="showArrayDesign.html" paramId="id" paramProperty="id"
						titleKey="arrayDesign.name" />
					<display:column property="shortName" sortable="true" titleKey="arrayDesign.shortName" />
					<display:column property="expressionExperimentCountLink" sortable="true" title="Expts" />
					<authz:authorize ifAnyGranted="admin">
						<display:column property="color" sortable="true" titleKey="arrayDesign.technologyType" />
					</authz:authorize>
					<authz:authorize ifAnyGranted="admin">
						<display:column property="delete" sortable="false" titleKey="arrayDesign.delete" />
					</authz:authorize>
					<display:setProperty name="basic.empty.showtable" value="true" />
				</display:table>

	</body>
</html>



