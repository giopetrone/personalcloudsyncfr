<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<f:view>
    <html>
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
            <title>Anima Detail</title>
            <link rel="stylesheet" type="text/css" href="/ZooApp/faces/jsfcrud.css" />
        </head>
        <body>
            <h:panelGroup id="messagePanel" layout="block">
                <h:messages errorStyle="color: red" infoStyle="color: green" layout="table"/>
            </h:panelGroup>
            <h1>Anima Detail</h1>
            <h:form>
                <h:panelGrid columns="2">
                    <h:outputText value="Kind:"/>
                    <h:outputText value="#{anima.anima.kind}" title="Kind" />
                    <h:outputText value="Name:"/>
                    <h:outputText value="#{anima.anima.name}" title="Name" />
                    <h:outputText value="Weight:"/>
                    <h:outputText value="#{anima.anima.weight}" title="Weight" />
                    <h:outputText value="Id:"/>
                    <h:outputText value="#{anima.anima.id}" title="Id" />
                </h:panelGrid>
                <br />
                <h:commandLink action="#{anima.destroy}" value="Destroy">
                    <f:param name="jsfcrud.currentAnima" value="#{jsfcrud_class['entity.util.JsfUtil'].jsfcrud_method['getAsConvertedString'][anima.anima][anima.converter].jsfcrud_invoke}" />
                </h:commandLink>
                <br />
                <br />
                <h:commandLink action="#{anima.editSetup}" value="Edit">
                    <f:param name="jsfcrud.currentAnima" value="#{jsfcrud_class['entity.util.JsfUtil'].jsfcrud_method['getAsConvertedString'][anima.anima][anima.converter].jsfcrud_invoke}" />
                </h:commandLink>
                <br />
                <h:commandLink action="#{anima.createSetup}" value="New Anima" />
                <br />
                <h:commandLink action="#{anima.listSetup}" value="Show All Anima Items"/>
                <br />
                <h:commandLink value="Index" action="welcome" immediate="true" />
            </h:form>
        </body>
    </html>
</f:view>
