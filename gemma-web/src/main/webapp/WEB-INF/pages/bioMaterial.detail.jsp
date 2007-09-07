<%@ include file="/common/taglibs.jsp"%>
<jsp:useBean id="bioMaterial" scope="request"
    class="ubic.gemma.model.expression.biomaterial.BioMaterialImpl" />
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<title> <fmt:message key="bioMaterial.details" /> </title>


	<script src="<c:url value='/scripts/ext/adapter/prototype/ext-prototype-adapter.js'/>" type="text/javascript"></script>
	<script src="<c:url value='/scripts/ext/ext-all-debug.js'/>" type="text/javascript"></script>
	<script type="text/javascript" src="<c:url value='/scripts/ext/data/ListRangeReader.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/scripts/ext/data/DwrProxy.js'/>"></script>
	<script type='text/javascript' src='/Gemma/dwr/interface/BioMaterialController.js'></script>
	<script type='text/javascript' src="<c:url value='/scripts/ajax/bmAnnotations.js'/>"></script>
	<script type='text/javascript' src="<c:url value='/scripts/ajax/bmFactorValues.js'/>"></script>
	<script type='text/javascript' src='/Gemma/dwr/engine.js'></script>
	<script type='text/javascript' src='/Gemma/dwr/util.js'></script>
        <h2>
            <fmt:message key="bioMaterial.details" />
        </h2>
        <table width="100%" cellspacing="10">
            <tr>
                <td valign="top">
                    <b>
                        <fmt:message key="bioMaterial.name" />
                    </b>
                </td>
                <td>
                	<%if (bioMaterial.getName() != null){%>
                    	<jsp:getProperty name="bioMaterial" property="name" />
                    <%}else{
                    	out.print("No name available");
                    }%>
                </td>
            </tr>
        
            <tr>
                <td valign="top">
                    <b>
                        <fmt:message key="bioMaterial.description" />
                    </b>
                </td>
                <td>
                	<%if (bioMaterial.getDescription() != null){%>
                    	<jsp:getProperty name="bioMaterial" property="description" />
                    <%}else{
                    	out.print("Description unavailable");
                    }%>
                </td>
            </tr>
            
            <tr>
                <td valign="top">
                    <b>
                        <fmt:message key="taxon.title" />
                    </b>
                </td>
                <td>
                	<%if (bioMaterial.getSourceTaxon() != null){
                    	out.print(bioMaterial.getSourceTaxon().getScientificName());
                    }else{
                    	out.print("Taxon unavailable");
                    }%>
                </td>
            </tr>
            
            <tr>
                <td valign="top">
                    <b>
                        <fmt:message key="databaseEntry.title" />
                    </b>
                </td>
                <td>
                	<%if (bioMaterial.getExternalAccession() != null){
                    	out.print(bioMaterial.getExternalAccession().getAccession() + "." + bioMaterial.getExternalAccession().getAccessionVersion());
                    }else{
                    	out.print("No accession");
                    }%>
                </td>
            </tr>
                 
         </table>  

        <h3>
            <fmt:message key="treatments.title" />
        </h3>
        <display:table name="bioMaterial.treatments"  defaultsort="1" class="list" requestURI="" id="treatmentList"
         pagesize="30" decorator="ubic.gemma.web.taglib.displaytag.expression.biomaterial.BioMaterialWrapper">
            <display:column sortable="true"  property="name" maxWords="20" />
            <display:column sortable="true"  property="description" maxWords="100" />
            <display:column sortable="true"  property="orderApplied" maxWords="100" />
        </display:table>
        

        <h3>
            <fmt:message key="factorValue.title" />
        </h3>

	<div id="bmFactorValues" class="x-grid-mso" style="border: 1px solid #c3daf9; overflow: hidden; width:650px; height:150px;"></div>




<h3>Annotations</h3>
<authz:authorize ifAnyGranted="admin">
<!-- This is for the bm annotator  -->
		<div id="bmAnnotator" class="x-grid-mso" style="padding-left: 2px; padding-right: 2px; overflow: hidden; width:650px; height:30px;"></div>
		  <script type="text/javascript" src='/Gemma/dwr/interface/OntologyService.js'></script>
		  <script type='text/javascript' src='/Gemma/dwr/interface/MgedOntologyService.js'></script>
		  <script type="text/javascript" src="<c:url value='/scripts/ajax/bmAnnotator.js'/>"></script>
</authz:authorize>

	<div id="bmAnnotations" class="x-grid-mso" style="border: 1px solid #c3daf9; overflow: hidden; width:650px; height:150px;"></div>
	<input type="hidden" name="bmId" id="bmId" value="${bioMaterial.id}" />
	<input type="hidden" name="bmClass" id="bmClass" value="${bioMaterial.class.name}" />

    		
    
    <table>
    <tr>
    <td COLSPAN="2">    
            <DIV align="left"><input type="button"
            onclick="location.href='/Gemma/expressionExperiment/showAllExpressionExperiments.html'"
            value="Back"></DIV>
            </TD>
        <authz:acl domainObject="${bioMaterial}" hasPermission="1,6">
            <TD COLSPAN="2">    
            <DIV align="left"><input type="button"
            onclick="location.href='/Gemma/bioMaterial/editBioMaterial.html?id=<%=bioMaterial.getId()%>'"
            value="Edit"></DIV>
            </td>
        </authz:acl>
    </tr>
    </table>