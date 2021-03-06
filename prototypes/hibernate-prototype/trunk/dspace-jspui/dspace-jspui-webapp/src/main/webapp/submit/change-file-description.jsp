<%--
  - change-file-description.jsp
  -
  - Version: $Revision: 2218 $
  -
  - Date: $Date: 2007-09-28 15:17:04 +0200 (ven, 28 set 2007) $
  -
  - Copyright (c) 2002, Hewlett-Packard Company and Massachusetts
  - Institute of Technology.  All rights reserved.
  -
  - Redistribution and use in source and binary forms, with or without
  - modification, are permitted provided that the following conditions are
  - met:
  -
  - - Redistributions of source code must retain the above copyright
  - notice, this list of conditions and the following disclaimer.
  -
  - - Redistributions in binary form must reproduce the above copyright
  - notice, this list of conditions and the following disclaimer in the
  - documentation and/or other materials provided with the distribution.
  -
  - - Neither the name of the Hewlett-Packard Company nor the name of the
  - Massachusetts Institute of Technology nor the names of their
  - contributors may be used to endorse or promote products derived from
  - this software without specific prior written permission.
  -
  - THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
  - ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
  - LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
  - A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
  - HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
  - INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
  - BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
  - OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
  - ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
  - TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
  - USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
  - DAMAGE.
  --%>

<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"
    prefix="fmt" %>

<%@ page import="javax.servlet.jsp.jstl.fmt.LocaleSupport" %>

<%@ page import="org.dspace.core.Context" %>
<%@ page import="org.dspace.app.webui.servlet.SubmissionController" %>
<%@ page import="org.dspace.app.util.SubmissionInfo" %>
<%@ page import="org.dspace.app.webui.util.UIUtil" %>
<%@ page import="org.dspace.content.Bitstream" %>
<%@ page import="org.dspace.content.BitstreamFormat" %>

<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>

<%
    request.setAttribute("LanguageSwitch", "hide");

    // Obtain DSpace context
    Context context = UIUtil.obtainContext(request);    

	//get submission information object
    SubmissionInfo subInfo = SubmissionController.getSubmissionInfo(context, request);
%>

<dspace:layout locbar="off"
               navbar="off"
               titlekey="jsp.submit.change-file-description.title"
               nocache="true">

    <form action="<%= request.getContextPath() %>/submit" method="post" onkeydown="return disableEnterKey(event);">

        <jsp:include page="/submit/progressbar.jsp"/>

        <%-- <h1>Submit: Change File Description</h1> --%>
		<h1><fmt:message key="jsp.submit.change-file-description.heading"/></h1>

        <%-- <p>Here are the details of the file.  
        <dspace:popup page="/help/index.html#filedescription">(More Help...)</dspace:popup></p> --%>
		<div><fmt:message key="jsp.submit.change-file-description.info1"/> 
          <dspace:popup page="<%= LocaleSupport.getLocalizedMessage(pageContext, \"help.index\") + \"#filedescription\"%>"><fmt:message key="jsp.morehelp"/></dspace:popup></div>

        <table class="miscTable" align="center" summary="Change file descripton details">
            <tr>
           <%-- <th class="oddRowOddCol">File</th>
                <th class="oddRowEvenCol">Size</th>
                <th class="oddRowOddCol">File Format</th> --%>
				<th id="t1" class="oddRowOddCol"><fmt:message key="jsp.submit.change-file-description.file"/></th>
                <th id="t2" class="oddRowEvenCol"><fmt:message key="jsp.submit.change-file-description.size"/></th>
                <th id="t3" class="oddRowOddCol"><fmt:message key="jsp.submit.change-file-description.format"/></th>
            </tr>
            <tr>
                <td headers="t1" class="evenRowOddCol"><%= subInfo.getBitstream().getName() %></td>
                <td headers="t2" class="evenRowEvenCol"><%= subInfo.getBitstream().getSize() %> bytes</td>
                <td headers="t3" class="evenRowOddCol"><%= subInfo.getBitstream().getFormatDescription() %></td>
            </tr>
        </table>

        <p><fmt:message key="jsp.submit.change-file-description.info2"/></p>
<%
    String currentDesc = subInfo.getBitstream().getDescription();
    if (currentDesc == null)
    {
        currentDesc="";
    }
%>
        <center>
            <table>
                <tr>
					<td class="submitFormLabel"><label for="tdescription"><fmt:message key="jsp.submit.change-file-description.filedescr"/></label></td>
                    <td><input type="text" name="description" id="tdescription" size="50" value="<%= currentDesc %>" /></td>
                </tr>
            </table>
        </center>

        <%-- Hidden fields needed for SubmissionController servlet to know which step is next--%>
        <%= SubmissionController.getSubmissionParameters(context, request) %>
     
		<center><p><input type="submit" name="submit" value="<fmt:message key="jsp.submit.general.submit"/>" /></p></center>
    </form>

</dspace:layout>
