<%@ tag display-name="go-section" body-content="empty"%>
<%@ attribute name="featureCvTerms" type="java.util.Collection" required="true" %>
<%@ attribute name="organism" type="java.lang.String" required="true" %>
<%@ attribute name="title" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="db" uri="db" %>

<c:if test="${fn:length(featureCvTerms) > 0}">
      <c:forEach items="${featureCvTerms}" var="featureCvTerm" varStatus="status">
        <tr>
        <td class="label"><c:if test="${status.first}">${title}</c:if></th>
        <td class="value name"><a href="http://www.genedb.org/amigo-cgi/term-details.cgi?term=GO%3A${featureCvTerm.type.dbXRef.accession}">${featureCvTerm.type.name}</a></td>
        <td class="value qualifiers">
        	<db:filtered-loop items="${featureCvTerm.featureCvTermProps}" cvTerm="qualifier" var="qualifier" varStatus="st">
        		<c:if test="${!st.first}"> | </c:if>
        		${qualifier.value}
            </db:filtered-loop>
        </td>
        <td class="value evidence">
            <db:filtered-loop items="${featureCvTerm.featureCvTermProps}" cvTerm="evidence" var="evidence">
            		${evidence.value}
            </db:filtered-loop>&nbsp;
            <c:forEach items="${featureCvTerm.pubs}" var="pub">
            	(${pub.uniqueName})
            </c:forEach>
        </td>
        <td class="value accession">
        	<c:forEach items="${featureCvTerm.featureCvTermDbXRefs}" var="fctdbx">
        		<a href="${fctdbx.dbXRef.db.urlPrefix}${fctdbx.dbXRef.accession}">${fctdbx.dbXRef.db.name}:${fctdbx.dbXRef.accession}</a>
        	</c:forEach>
        	<c:if test="${featureCvTerm.pub.uniqueName != 'null'}">
        		<a href="${PMID}${featureCvTerm.pub.uniqueName}">${featureCvTerm.pub.uniqueName}</a>
        	</c:if>
        </td>
        </tr>
      </c:forEach>
 </c:if>