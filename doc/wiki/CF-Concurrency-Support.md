This page describes the technical solutions to enable Credibility Framework Concurrency Support.

[Go back to Wiki home page](home)

## Contents

[[_TOC_]]

## Global Architecture

The need of a concurrent access to the Credibility Framework (CF) will require changes to the architecture.

### Current architecture - Local

The existing architecture is described below; CF is packaged as an Eclipse plugin to be embedded into an Eclipse-based product.

![image](uploads/f246d8e50aa1a6b8b189c9554d030cd5/image.png)

The plugin is installed into an Eclipse instance. 
The user has a number of workspaces connected to the Eclipse instance. 
The CF database is embedded into a .cf file. The .cf extension is associated to the CF plugin. Once double-clicked, the CF plugin is launched by Eclipse. 
For each file, the user interface (UI) is started, processing the data through the local CF services.

### Concurrent access – Local and Web

Compatibility between the existing local access and the concurrent one is a prerequisite. The intended goal of this architecture is to extend the CF capabilities to a team environment.

**Important:** This architecture does not support report generation on the server side. The user will still need to install ARG and its dependencies; the evidence links must be relative to the local Eclipse workspace.

![image](uploads/4b8b0d73e9c4ab2e97644ed4c4ac0b3d/image.png)

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

![image](uploads/89188ac9e17dabdd736001749df6d3a7/image.png)

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

![image](uploads/1df35a73735b2896eea75c386c41ab4a/image.png)

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

![image](uploads/667a74e0f459b97b7108a6899e8a30af/image.png)

# Tasks

The following planning describes the different steps needed to build the concurrency support.

![image](uploads/0dfbeaf2be8f0040dc32d5a477d21ff3/image.png)

The above tasks are presented in a sequential diagram, but the implementation of some can be parallelized. The tests are not described but are part of the different tasks.

-	**Design architecture and API:** the current analysis is part of the architecture design. 
What is the expected behavior of the concurrency support? Should we export all the logic into the web application? Or keep some on the client side (e.g. Report generation)?
What is the best and more efficient technology?
Prototyping will guide choosing the good solution.

-	**Define new CF database schema:** having several CF projects into the same database will change the defined schema. The authentication and the lock mechanism are not currently managed. These new features must be designed and will generate new entities into the database schema.

-	**Separate UI and logic:** to support two different connections, the plugin UI must be separated from the local processes and database access. The best and cleaner way is to have different plugins. The UI plugin will start the views and select the backend services depending of the project type.

-	**Create webapp:** the web application skeleton and its main components and packages are a prerequisite to the concurrency support. The connection to the database, the CF logic re-use and other features are important parts of the web app. Unit tests are part of this tasks.

-	**Create REST API:** the REST API is one major feature to develop. A prior analysis should be conducted, regarding the various methods called by the plugin views. These methods must be added to the REST API. In addition, other API routes must be created for the additional features of the concurrency support, like authentication, lock, multi-projects, etc. To be consistent, the REST API has to implement unit tests.

-	**Generate/Create Web Client:** the web client can be generated based on the REST API with some tools like OpenAPI. The generation is a possibility and can help the development process. But with or without generation, the web client will be adapted to the plugin and needs.

-	**Create CF project type selector:** the CF plugin UI will have a mechanism to select the project type (local or remote) and create the local database or the remote connection

-	**Plug views to Web client:** the CF views will receive objects from the local database or the web client. To avoid any unexpected behavior, the above architecture separates the view model objects (Model DTO) from the local database (Model Entities) and web objects. A Wrapper has to be created to transform these objects in both ways.

-	**Implement Authentication:** the authentication mechanism is mainly present on the server side but must be implemented into the client plugin to allow connections. The enterprise authentication has to be defined.

-	**Implement Lock Mechanism:** the lock mechanism is a key point of the concurrency support. Different approaches can be used (pessimistic, optimistic) depending on the usage and the desired implementation. 
What should we do if one user locks an object and loses connection? Or if the user computer stops? Should we have a timer to unlock? A super-user?
It must be defined to answer the CF plugin needs and guarantee usability.

-	**Generate API documentation:** Some tools like Swagger provide support to generate the REST API documentation. 

-	**Write concurrency documentation:** the concurrency support is a complex project that should be documented in the open source Gitlab repository wiki.

# Products

This section describes the resulting products.

## CF feature

The CF feature is packaged within a p2 repository with the CF plugin and dependencies. 

Each client installs the plugin from CF p2 repositories which are available from the development [iwf/cf](https://iwf.gitlab.io/cf/packages/) or the official [CredibilityFramework/cf](https://credibilityframework.gitlab.io/cf/packages/) p2 sites.

The plugin embeds local and remote capabilities. 

## CF webapp

A new CF webapp is packaged as a .war file.

This file must be installed on a Java webserver (like Tomcat). The CF database has to be created and configured on the server.
