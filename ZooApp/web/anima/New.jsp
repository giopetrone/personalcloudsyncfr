<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<f:view>
    <html>
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
            <title>New Anima</title>
            <link rel="stylesheet" type="text/css" href="/ZooApp/faces/jsfcrud.css" />
        </head>
        <body>
            <h:panelGroup id="messagePanel" layout="block">
                <h:messages errorStyle="color: red" infoStyle="color: green" layout="table"/>
            </h:panelGroup>
            <h1>New Anima</h1>
            <h:form>
                <h:inputHidden id="validateCreateField" validator="#{anima.validateCreate}" value="value"/>
                <h:panelGrid columns="2">
                    <h:outputText value="Kind:"/>
                    <h:inputText id="kind" value="#{anima.anima.kind}" title="Kind" />
                    <h:outputText value="Name:"/>
                    <h:inputText id="name" value="#{anima.anima.name}" title="Name" />
                    <h:outputText value="Weight:"/>
                    <h:inputText id="weight" value="#{anima.anima.weight}" title="Weight" />
                </h:panelGrid>
                <br />
                <h:commandLink action="#{anima.create}" value="Create"/>
                <br />
                <br />
                <h:commandLink action="#{anima.listSetup}" value="Show All Anima Items" immediate="true"/>
                <br />
                <h:commandLink value="Index" action="welcome" immediate="true" />
            </h:form>
        </body>
    </html>
</f:view>
