<%@ page language="java" %>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ taglib prefix="html" uri="http://jakarta.apache.org/struts/tags-html"%>
<%@ taglib prefix="bean" uri="http://jakarta.apache.org/struts/tags-bean"%>
<%@ taglib prefix="logic" uri="http://jakarta.apache.org/struts/tags-logic"%>
<html:xhtml/>
<tr>
	<td class="separador" colspan="2"><bean:message key="mensajePlataforma.datosTextos"/></td>
</tr>
<tr>
    <td class="labelo"><bean:message key="mensajePlataforma.descripcion"/></td>
    <td class="input"><html:text styleClass="text" tabindex="9" property="traduccion.descripcion" maxlength="1000" /></td>
</tr>