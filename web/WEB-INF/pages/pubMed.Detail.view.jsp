<%@ include file="/common/taglibs.jsp"%>

<jsp:useBean id="bibliographicReference" scope="request"
    class="edu.columbia.gemma.common.description.BibliographicReferenceImpl" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML>

<HEAD>
<title>pubMed.Detail.view</title>
<SCRIPT LANGUAGE="JavaScript">
    function selectButton(target){
        if(target == 1 && confirm("Are you sure you want to delete this reference from the system?")){
                document.actionForm.action="<c:url value="/bibRef/deleteBibRef.html" />"
        }
        if(target == 2){
            document.actionForm._eventId.value="edit"
            document.actionForm._flowId.value="pubMed.Edit"
            document.actionForm.action="<c:url value="/flowController.htm"/>"           
        }
     
        document.actionForm.submit();
    }
    </SCRIPT>
</HEAD>
<BODY>

<FORM name="actionForm" action=""><input type="hidden" name="_eventId"
    value=""> <input type="hidden" name="_flowId" value=""> <input
    type="hidden" name="accession"
    value="<%=request.getAttribute("accession")%>"></FORM>

<TABLE width="100%">
    <TR>
        <TD colspan="2"><b>Bibliographic Reference Details</b></TD>
    </TR>
    <TR>
        <TD COLSPAN="2">
        <HR>
        </TD>
    </TR>

    <tr>
        <td colspan="2"><Gemma:bibref
            bibliographicReference="<%=bibliographicReference%>" />
        <td>
    </tr>


    <TR>
        <TD COLSPAN="2">
        <HR>
        </TD>
    </TR>
    <TR>
        <TD COLSPAN="2">
        <table cellpadding="4">
            <tr>
                <TD><authz:acl domainObject="${bibliographicReference}"
                    hasPermission="1,6">

                    <DIV align="right"><INPUT type="button"
                        onclick="javascript:selectButton(1)"
                        value="Delete from Gemma"></DIV>

                </authz:acl></TD>
                <TD><authz:acl domainObject="${bibliographicReference}"
                    hasPermission="1,6">

                    <DIV align="right"><INPUT type="button"
                        onclick="javascript:selectButton(2)"
                        value="Edit"></DIV>

                </authz:acl></td>

            </tr>
        </table>
        </td>
    </TR>
</TABLE>

<h2>New Gemma search:</h2>


<form action=<c:url value="/bibRef/searchBibRef.html"/> method="get"><spring:bind
    path="bibliographicReference.pubAccession.accession">
    <input type="text" name="${status.expression}"
        value="${status.value}">
</spring:bind></form>
<hr />
<DIV align="left"><INPUT type="button"
    onclick="location.href='showAllBibRef.html'"
    value="View all references"></DIV>
<hr />
<a href="<c:url value="/flowController.htm?_flowId=pubMed.Search"/>"><fmt:message
    key="menu.flow.PubMedSearch" /></a>
</BODY>
</HTML>
