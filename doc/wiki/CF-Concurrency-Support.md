This page describes the technical solutions to enable Credibility Framework Concurrency Support.

[Go back to Wiki home page](home)

## Contents

[[_TOC_]]

## Global Architecture

The need of a concurrent access to the Credibility Framework (CF) will require changes to the architecture.

### Current architecture - Local

The existing architecture is described below; CF is packaged as an Eclipse plugin to be embedded into an Eclipse-based product.

![image](uploads/f0575498613b2a734d9f456cb7fc3f45/image.png)

The plugin is installed into an Eclipse instance. 
The user has a number of workspaces connected to the Eclipse instance. 
The CF database is embedded into a .cf file. The .cf extension is associated to the CF plugin. Once double-clicked, the CF plugin is launched by Eclipse. 
For each file, the user interface (UI) is started, processing the data through the local CF services.

### Concurrent access – Local and Web

Compatibility between the existing local access and the concurrent one is a prerequisite. The intended goal of this architecture is to extend the CF capabilities to a team environment.

**Important:** This architecture does not support report generation on the server side. The user will still need to install ARG and its dependencies; the evidence links must be relative to the local Eclipse workspace.

![image](uploads/7a142cedfac2abd015651eade1a528cb/image.png)

The .cf file remains the entry point into the CF application. Two options are available: local or team CF project.

The CF plugin is preserved, but a software middle layer called “Controller” connects the project to the associated data locally or remotely:
•	if associated locally, the existing CF local services are linked to the UI, and the local database is managed;
•	if associated remotely, the dispatcher connects the UI method calls to the CF web application. The webapp has an application programming interface (API) available for each authorized application as REST services.

These REST services process the data and business logic into the remote services. 

The authentication can be self-managed or delegated to another system. 

A database is installed on a MySQL server instance. The application is logically independent from the database implementation through an object relational mapping (ORM) tool and can be connected to another database server.

*Optional:* The CF webapp can serve content to a web browser if this kind of client solution is desired in the future.
This scheme does not describe the authentication mechanism.

## Software Architecture

This section describes the current software architecture and one possible implementation for the concurrency support.

Within the Eclipse OSGi mechanism, plugins are packaged into features and available from p2 repositories. When getting CF, the feature is installed with its plugins into the targeted platform.

### Current Software Architecture - Local

The current architecture is packaged into one Eclipse feature containing the Eclipse plugin jar and the needed dependencies.

![image](uploads/01387541937c3b295b4c9b5d945b6af1/image.png)

The plugin is separated into different packages:
-	**Launcher (UI)** contains the entry point of the application and launches views
-	**Parts (UI)** contains the User Interface (views and controllers)
-	**Application (application)** contains the main processes and the business logic
-	**DAO (Data Access Objects)** manages the database connection and the persistence
-	**Eclipse Preferences** package is linked to Eclipse and the UI package to manage the user preferences in the workspace
-	**Exception** package stores the different types of exceptions 
-	**Model** handles the domain objects which are a memory representation of the database objects. These are used in all the main packages.
-	**Constants** stores the application constants
-	**Logger** is used to log using the Eclipse mechanism
-	**Tools** gather useful methods for the plugin

### Concurrent Software Architecture – Local and Web

The current section introduces the technical proposal for adding to CF concurrency support and keeping the plugin local access available.

Below are described the local and server software architectures.

#### Local Architecture

The desired architecture will be to package one Eclipse feature but assembling various Eclipse plugins. The main difference with the current architecture (containing only one plugin) will be a separation between the UI and the plugin logic.

This separation will help make a distinction between one UI and different connections.
Three main plugins will be created, one for the UI, one for the logic processes and connections, and one for the shared packages.

![image](uploads/d79bd749b463199c89509825619ceec9/image.png)

##### CF Plugin UI

The CF UI plugin contains the user interface components and the **Launcher** and **Parts** packages. It also contains some specific transverse packages such as the **Eclipse Preferences UI** views and some **UI Tools** providing support for fonts and colors. 

This plugin uses shared packages from the **Shared** plugin, and connects to the **Controller** package from the CF plugin.

##### CF Plugin

The **Controller** package is the entry point of the plugin and is exposed to the UI plugin. Depending on the project type (local or remote), it will start different managers (resp. **Application** or **Web Client**).

The **Application** and **DAO** packages remain the same from the original plugin architecture. A connection is opened to the local database and the data are loaded.

The **Web Client** manages the connection between the web server and the plugin, communicating with the REST API available.

A new **Model Data Transfer Objects (DTO)** package is created, separated from the **Model entities**. The DTO are disconnected from the database and feed the UI with the backend data but are also used to transfer data to the backend. The goal is to protect the data integrity of the local database. The Model entities are directly connected to the database with an Object-Relational Mapping (ORM) library which populates the database and automatically persists the updated entities data into the database. 
**Model DTO** might be shared between client and server in order to avoid duplication. 

A **Wrapper** package is used to wrap the Model DTO into Model Entities and conversely, as well as to convert server sent data into meaningful objects for the UI.

The **Backend Tools** package provides useful tools specific to the backend.

##### CF Plugin Shared

The shared plugin contains packages and libraries used by both the UI and backend plugins.

The **Model DTO** package is available in the shared plugin to populate the views.

**Constants**, **Exceptions**, **Logger** and **Tools** packages are transverse.

The **Eclipse Preferences** package is split between the UI and shared plugins. The preference views remain into the UI plugin while the preference getters and setters should be available by all components.

#### Server Architecture

The connection between the plugin client and the server are done with HTTP requests and a REST API on the server side.

The **REST API** is the only entry point of the webserver. It relies on the **Authentication** package to secure the services and data. 

The **Authentication** package must check the user credentials. It has to include a self-managed mechanism but should also be ready to be connected to an enterprise authentication system.

The **REST API** relies on business services into the **Application** package. The **DAO** package stores the entity repositories used to access the database and allow data to persist.

Some packages are shared with the core packages (**Constants**, **Exceptions**, **Logger**, **Tools**). But the **Model DTO** and **Model Entities** packages are separated to protect the server data from the client pushes. The DTO are disconnected from the database and feed the REST API with the backend data but are also used to transfer data to the backend. The **Model entities** are directly connected to the database with an Object-Relational Mapping (ORM) library which populates the database and automatically persists the updated entities data into the database.

*Optional:* The CF webapp can serve content to a web browser, should this kind of client solution be desired in the future. This content will be separated from the REST API but remain in common packages like **core services** or **DAO**.

![image](uploads/f2a3aa571183559092556dc5defb86bf/image.png)


# Products

This section describes the resulting products.

## CF feature

The CF feature is packaged within a p2 repository with the CF plugin and dependencies. 

Each client installs the plugin from CF p2 repositories which are available from the development [iwf/cf](https://iwf.gitlab.io/cf/packages/) or the official [CredibilityFramework/cf](https://credibilityframework.gitlab.io/cf/packages/) p2 sites.

The plugin embeds local and remote capabilities. 

## CF webapp

A new CF webapp is packaged as a .war file.

This file must be installed on a Java webserver (like Tomcat). The CF database has to be created and configured on the server.
