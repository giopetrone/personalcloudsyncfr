<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<f:view>
    <html>
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
            <title>Listing Anima Items</title>
            <link rel="stylesheet" type="text/css" href="/ZooApp/faces/jsfcrud.css" />
        </head>
        <body>
            <h:panelGroup id="messagePanel" layout="block">
                <h:messages errorStyle="color: red" infoStyle="color: green" layout="table"/>
            </h:panelGroup>
            <h1>Listing Anima Items</h1>
            <h:form styleClass="jsfcrud_list_form">
                <h:outputText escape="false" value="(No Anima Items Found)<br />" rendered="#{anima.pagingInfo.itemCount == 0}" />
                <h:panelGroup rendered="#{anima.pagingInfo.itemCount > 0}">
                    <h:outputText value="Item #{anima.pagingInfo.firstItem + 1}..#{anima.pagingInfo.lastItem} of #{anima.pagingInfo.itemCount}"/>&nbsp;
                    <h:commandLink action="#{anima.prev}" value="Previous #{anima.pagingInfo.batchSize}" rendered="#{anima.pagingInfo.firstItem >= anima.pagingInfo.batchSize}"/>&nbsp;
                    <h:commandLink action="#{anima.next}" value="Next #{anima.pagingInfo.batchSize}" rendered="#{anima.pagingInfo.lastItem + anima.pagingInfo.batchSize <= anima.pagingInfo.itemCount}"/>&nbsp;
                    <h:commandLink action="#{anima.next}" value="Remaining #{anima.pagingInfo.itemCount - anima.pagingInfo.lastItem}"
                                   rendered="#{anima.pagingInfo.lastItem < anima.pagingInfo.itemCount && anima.pagingInfo.lastItem + anima.pagingInfo.batchSize > anima.pagingInfo.itemCount}"/>
                    <h:dataTable value="#{anima.animaItems}" var="item" border="0" cellpadding="2" cellspacing="0" rowClasses="jsfcrud_odd_row,jsfcrud_even_row" rules="all" style="border:solid 1px">
                        <h:column>
                            <f:facet name="header">
                                <h:outputText value="Kind"/>
                            </f:facet>
                            <h:outputText value=" #{item.kind}"/>
                        </h:column>
                        <h:column>
                            <f:facet name="header">
                                <h:outputText value="Name"/>
                            </f:facet>
                            <h:outputText value=" #{item.name}"/>
                        </h:column>
                        <h:column>
                            <f:facet name="header">
                                <h:outputText value="Weight"/>
                            </f:facet>
                            <h:outputText value=" #{item.weight}"/>
                        </h:column>
                        <h:column>
                            <f:facet name="header">
                                <h:outputText value="Id"/>
                            </f:facet>
                            <h:outputText value=" #{item.id}"/>
                        </h:column>
                        <h:column>
                            <f:facet name="header">
                                <h:outputText escape="false" value="&nbsp;"/>
                            </f:facet>
                            <h:commandLink value="Show" action="#{anima.detailSetup}">
                                <f:param name="jsfcrud.currentAnima" value="#{jsfcrud_class['entity.util.JsfUtil'].jsfcrud_method['getAsConvertedString'][item][anima.converter].jsfcrud_invoke}"/>
                            </h:commandLink>
                            <h:outputText value=" "/>
                            <h:commandLink value="Edit" action="#{anima.editSetup}">
                                <f:param name="jsfcrud.currentAnima" value="#{jsfcrud_class['entity.util.JsfUtil'].jsfcrud_method['getAsConvertedString'][item][anima.converter].jsfcrud_invoke}"/>
                            </h:commandLink>
                            <h:outputText value=" "/>
                            <h:commandLink value="Destroy" action="#{anima.destroy}">
                                <f:param name="jsfcrud.currentAnima" value="#{jsfcrud_class['entity.util.JsfUtil'].jsfcrud_method['getAsConvertedString'][item][anima.converter].jsfcrud_invoke}"/>
                            </h:commandLink>
                        </h:column>
                    </h:dataTable>
                </h:panelGroup>
                <br />
                <h:commandLink action="#{anima.createSetup}" value="New Anima"/>
                <br />
                <h:commandLink value="Index" action="welcome" immediate="true" />
                
            </h:form>
        </body>
    </html>
</f:view>
