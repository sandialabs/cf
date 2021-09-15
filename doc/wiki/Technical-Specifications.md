This page describes credibility framework technical choices and software architecture.

[Go back to Wiki home page](home)

## Contents

[[_TOC_]]

## General Description

[Go back to Contents](#contents)

CF plugin is a local **Eclipse RCP plugin** which can be installed on `Eclipse >= Oxygen 3a` based platforms.

## Software Architecture

[Go back to Contents](#contents)

### Eclipse RCP Java Projects

The CF project is dispatched in multiple projects. This Java projects are managed by Maven Tycho which is Maven implementation for Eclipse RCP projects (see https://maven.apache.org/ and https://www.eclipse.org/tycho/sitedocs/).

Here is the CF project folder structure:
```
gov.sandia.cf.root
├── gov.sandia.cf.bundles
│   ├── gov.sandia.cf.plugin
│   ├── gov.sandia.cf.plugin.help
├── gov.sandia.cf.features
│   ├── gov.sandia.cf.feature
├── gov.sandia.cf.releng
│   ├── gov.sandia.cf.configuration
│   ├── gov.sandia.cf.update
├── gov.sandia.cf.tests
    └── gov.sandia.cf.plugin.tests
```

- `gov.sandia.cf.root`: is the main project. It is used to define the global configuration and run commands on the entire project.
- `gov.sandia.cf.bundles`: contains all the plugin projects.
- `gov.sandia.cf.plugin`: is the main CF plugin. It contains all the logic and GUI.
- `gov.sandia.cf.plugin.help`: is the CF plugin help module. It contains the help files used in the main CF plugin.
- `gov.sandia.cf.features`: contains all the features
- `gov.sandia.cf.feature`: is the CF plugin feature. The feature is used to package CF as a standalone plugin, importable into an Eclipse product. It includes all the necessary dependencies.
- `gov.sandia.cf.releng`: is the global release project (releng: Release Engineering). This project is responsible for the builds, configuration and automated tasks.
- `gov.sandia.cf.configuration`: contains the global configuration. It is the logical main project whereas `gov.sandia.cf.root` is the Maven main project.
- `gov.sandia.cf.update`: contains the releases products. It is used to package the CF feature and create the p2 repository.
- `gov.sandia.cf.tests`: is the main tests project.
- `gov.sandia.cf.plugin.tests`: contains the CF plugin tests. The CF plugin packages structure is reused.


### Multi-tier architecture (multi-layer)

The CF plugin is developed following the `multi-tier` architecture design pattern. The main content is present into `gov.sandia.cf.plugin` project. 

You can see below the architecture:

<div align="center">
![cf_software_architecture.svg](uploads/8eb1066e5ac2bfe7b04d83d9b3d17b52/cf_software_architecture.svg)
</div>

The software architecture is based on multiple layers:
- `Launcher`: this layer is specific to Eclipse RCP. This Java classes are used to start the plugin. It loads the yml file describing the project and its configuration.
- `Presentation Tier`: it contains the GUI of the plugin, with view classes, widgets, components, parts (Eclipse RCP GUI components). It is a generic layer that is only dependent of the Business Logic Tier. It is inspired by the **MVC** design pattern. The views are dispatched by features and stored into `gov.sandia.cf.parts.ui` packages. Classes contained on top of the `gov.sandia.cf.parts` package are UI global classes and interfaces.
- `Business Logic Tier`: it contains the business logic of the plugin. This layer coordinates the plugin behavior, performs calculations, makes logical decisions and processes commands.
- `Data Tier`: This layer is in charge of querying the persistent data from database or files. In our case, it contains the persistence classes to access HSQLDB database and persist data as model objects. The result of queries can be returned to the business logical tier and then to the user interface.
- `Model`: This layer is transverse. It can be called from all the other tier. It is used to transmit data (in memory) to the other layers. For example, data from database are stored in memory and structured as model classes. It is like a wrap for data.
- `Preferences`: This layer is transverse. It is based on the Eclipse Preferences mechanism. It contains constants and methods to retrieve the Eclipse stored local preferences. 
- `Tools, Constants, Logger, Exceptions`: This layer contains functions and methods that are not specific to a tier. This methods can be useful for all the project. It is also a transverse layer.
  - `Constants`: Contains the project global constants
  - `Exceptions`: Contains the exceptions used for the classes
  - `Logger`: Contains the logger mechanism
  - `Tools`: Contains the project tools


### Interface design pattern

All the tiers are designed following the `interface design pattern`. That means that every call to a class and a method is made through an interface. There can be multiple implementation of the same interface. The "caller" tier do not know the implementation but only the interface. The association between the interface and the implementation is made by a specific class called `Manager`. Every tier has a Loader class known by the "caller" tier.

<div align="center">
![cf_interface_design_pattern](uploads/a65356de55d025c15b58b7a5fa168267/cf_interface_design_pattern.png)
</div>

### Layer managers

The `IManager` interface defines manager's methods to implement. Each layer must have its own implementation of the manager to launch.

Here is a schema of the actual implementation:

<div align="center">
![CF-UI_manager.svg](uploads/78e468927cad13506e98c619f33f9bb3/CF-UI_manager.svg)
</div>

`CredibilityEditor`: The main entry point of the plugin is the class `CredibilityEditor` which is called when the `<filename>.cf` file is opened. 
It manages its instance of the `MainViewManager` and the `ApplicationManager`.

`MainViewManager`: The `MainViewManager` class calls the `ApplicationManager` from the CredibilityEditor to retrieve data and compute business logic.

`ApplicationManager`: The `ApplicationManager` class manages its own instance of `DaoManager` to get data from the database. 

`DaoManager`: The `DaoManager` class has its instance of `IDBManager` which is in our case a HSQLDB implementation with `HSQLDBDaoManager` class.


#### Application layer Services

`app-services.yml`: To add/change the application services available, a service configuration file is available at `src/main/resources/configuration/app-services.yml`.

This file contains the definition of all the interfaces implementing the interface `gov.sandia.cf.appplication.IApplication` used to reference the class and methods for the application layer services (e.g. gov.sandia.cf.appplication.IUserApplication).

To add a new service, add the full name (with package) of your service interface. Create the implementation of your interface extending `gov.sandia.cf.appplication.AApplication` class. The implementation will be automatically found and implemented.

#### DAO layer Repositories

`dao-services.yml`: To add/change the DAO repositories available, a dao configuration file is available at `src/main/resources/configuration/dao-services.yml`.

This file contains the definition of all the interfaces implementing the interface `gov.sandia.cf.dao.ICRUDRepository` used to reference the class and methods for the dao layer repositories.

To add a new repository, add the full name (with package) of your dao repository interface (e.g. gov.sandia.cf.dao.IModelRepository). Create the implementation of your interface extending `gov.sandia.cf.dao.AbstractCRUDRepository` class. The implementation will be automatically found and implemented.

## Database Model

[Go back to Contents](#contents)

CF data are stored into a HSQLDB database, integrated into the `<filename>.cf` file. A database folder is created to store HSQLDB database files.

### Database Diagram
This diagram describes the data structure by feature (Authentication, Global, PIRT, QoI Planning, PCMM, PCMM Planning, Decision, System Requirements, Uncertainty, Reporting, Settings).

<div align="center">
![credibility-framework-db-schema.svg](uploads/b3ab15ee481c0e64264acd8abe4ad74c/credibility-framework-db-schema.svg)
</div>

You can find the original data model schema in [doc/data model](https://gitlab.com/iwf/cf/-/tree/develop/doc/data%20model) folder. It contains the `.mwb` file to edit with MySQL Workbench, and the exported schemas in PNG and SVG.

### Database Migration from version A to B

The plugin integrates a Java/SQL **migration management engine**. It allows to **execute SQL and/or Java tasks** just **after establishing the connection** with the database and **after the ORM loading** (Object-Relational Mapping) based on `JPA (Java Persistence API)` which is `EclipseLink`. At this step before the migration tasks execution, the java datamodel objects have been initialized, updated (if necessary) and checked.

#### SQL migration engine behavior

The engine is launched just after establishing the connection with the database and after launching the scan of the ORM on the java model objects.

The tasks to execute are described into the `gov.sandia.cf.dao.migration.EclipseLinkMigrationManager` class and must extend  `gov.sandia.cf.dao.migration.IMigrationTask` interface. Each class has a name as a unique identifier and a method `boolean execute(DaoManager daoManager) throws CredibilityMigrationException` to implement.

The `EclipseLinkMigrationManager` will get the ordered tasks and execute each of them one after the other. Each task is executed only once.

A datamodel object `MigrationLog` associated to the table `MIGRATION_LOG` stores the script execution log. It contains one line per execution.

Each task is executed in Java, but it is possible to execute SQL requests and SQL scripts directly from Java. A folder called [src/gov.sandia.cf/bundles/gov.sandia.cf.plugin/src/main/resources/sql/migration](https://gitlab.com/iwf/cf/-/tree/develop/src/gov.sandia.cf/bundles/gov.sandia.cf.plugin/src/main/resources/sql/migration) contains the SQL script to execute (if necessary).

##### <i class="fa fa-gear fa-spin fa-2x" style="color: firebrick"></i> Engine process:
- <i class="fas fa-check" aria-hidden="true"></i> If the task is executed **successfully**: the changes are committed and one line is added into the `MIGRATION_LOG` table with successful execution.
- <i class="fas fa-times" aria-hidden="true"></i> If the task is **in error**: the task changes are rollbacked (data only, **HSQLDB does not support transactions on database schema**), and an error line with the error log is added into the `MIGRATION_LOG` table. 
An **error log** file will be generated at the same level as the `<filename>.cf`. The file will be named `<cf-filename.cf>-yyyyMMddmmhhss-err.log`. If the last task executed was in error, the credibility plugin will display an error and lock the file opening. The `<filename>.cf` file will not be affected by the migration if it is not saved. The migration is stopped.

> <i class="fas fa-exclamation-triangle" aria-hidden="true"></i> **Warning**: 
HSQLDB does not support transactions on database schema. Each schema change executed is irremediably committed.

> <i class="fas fa-magic" aria-hidden="true"></i> **Tip 1**: The migration process is executed into the CF temporary folder, not directly into the `<filename>.cf`. The changes are impacting the `<filename>.cf` file once the user saves the changes in the CF Editor (into Eclipse). 

> <i class="fas fa-magic" aria-hidden="true"></i> **Tip 2**: It is possible to **reexecute a script in error** by launching the `<filename>.cf` again. A new script line is added into the `MIGRATION_LOG` table with the last execution date, log, status. If the script is successful, the last script marked as successfully executed.

#### Restore a `<filename>.cf` file in error

The `<filename>.cf` file is unzipped under a temporary folder to be opened. The migration is executed during the opening of the `<filename>.cf` file and affect the database contained in the temporary folder. If there is an error with a migration script, take a look at the error log file at the same level as the `<filename>.cf` file. To open the `<filename>.cf` file after fixing the error, just delete the cf temporary folder. The database will be regenerated from the original`<filename>.cf` file and the migration script will be reexecuted on the temporary folder database.

#### Tasks naming convention

##### Java Tasks convention

Add the migration tasks under package `gov.sandia.cf.dao.migration.tasks` in the project `gov.sandia.cf.plugin`.

> Please do not change the existing tasks order and add your new task after the latest one.

For a better understanding in the task order and description, please follow this naming convention:

```
Task_xxx_SHORTDESCRIPTION
```

In the task Java class, define the task name (unique identifier) as follow:

```
TASK_NAME = "x.y.z-GITLAB_REPO-ISSUE_NUMBER-SHORTDESCRIPTION-task"
```

**Legend:**
xxx: the task number
x: CF major version number
y: CF medium version number
z: CF minor version number
GITLAB_REPO: the repository abbreviation (for CF, iwfcf)
ISSUE_NUMBER: the issue number (e.g. 216)
SHORTDESCRIPTION: a short description of the migration task

Examples:
- class name: Task_005_ConfigurationFileTable -> TASK_NAME = "0.6.0-iwfcf-384-confile-task"
- class name: Task_009_EvidenceValueToGson -> TASK_NAME = "0.6.0-iwfcf-425-evidenceValuesToGson-task9"

##### SQL Scripts convention

For a better understanding in the script order, please follow this naming convention:

```
x.y.z-GITLAB_REPO-ISSUE_NUMBER
```

**Legend:**
x: CF major version number
y: CF medium version number
z: CF minor version number
GITLAB_REPO: the repository abbreviation (for CF, iwfcf)
ISSUE_NUMBER: the issue number (e.g. 216)

Examples:
- 0.2.0-iwfcf-216
- 0.2.1-iwfcf-187
- 0.3.0-iwfcf-211

#### Database integrity check at launch

At launch, the plugin version is checked and compared with the database version. 

If `database version <= plugin version`, the plugin is launched. 
If `database version > plugin version`, the plugin stops the loading and displays an error message to force the user to update its plugin.

<div align="center">
![203-plugin-version-behind](/uploads/d15827971ae760ca90bd3eb9192a89fd/203-plugin-version-behind.png)
</div>

<div align="center">
![203-plugin-version-behind-error](/uploads/b3b9955f83af8b823bb99d96a26398fc/203-plugin-version-behind-error.png)
</div>

After the database version check validated, the current plugin version is setted to the database.

## Credibility Files

### Requirements (Configuration Files)

This files describe the features for a specific CF process. They are not embedded into the credibility plugin. They are created by the user or with an external tool. 

They are not mandatory and can be provided during the credibility process creation to enable the associated feature:

<div align="center">
![image](uploads/1f4f0feafcddca31e45dd681ce257b81/image.png)
</div>

> An examplar of each of the following files is present at [doc/Configuration](https://gitlab.com/iwf/cf/-/tree/develop/doc/Configuration).

> This files can be generated with the Requirements Excel spreadsheets from [doc/Requirements](https://gitlab.com/iwf/cf/-/tree/develop/doc/Requirements). 

> See [Requirements page](Requirements) for furthermore information.

#### PIRT: 

`<PIRT_schema>.yml`: The PIRT schema describes the settings and configuration of the PIRT feature. This file is imported into the database. 

#### QoI Planning: 

`<QoIPlanning_schema>.yml`: The Quantity of Interest Planning schema lists the configuration of the Quantity of Interest feature. It is linked to the PIRT feature. This file is imported into the database.

#### PCMM and PCMM Planning: 

`<PCMM_schema>.yml`: The PCMM schema describes the settings and configuration of the PCMM feature and the PCMM Planning. This file is imported into the database.

#### Uncertainty: 

`<Uncertainty_schema>.yml`: The Uncertainty schema describes the settings of the Uncertainty feature. This file is imported into the database.

#### System Requirement: 

`<SystemRequirement_schema>.yml`: The System Requirement schema describes the configuration of the System Requirement feature. This file is imported into the database.

#### Decision: 

`<Decision_schema>.yml`: The Decision schema describes the settings for the analyst decision feature. This file is imported into the database.

#### PIRT queries:

`<PIRT_queries>.yml` (experimental and optional): This file lists some PIRT queries. It is optional. If activated, it would be specified in the Credibility Preferences. It is not imported into the database is specified for a specific Eclipse environment.

This option is not currently activated.

### Embedded files/folders

This files are created and managed by the credibility plugin.

#### Credibility process file

`<filename>.cf`: This file is used to launch the credibility editor into Eclipse. It is a zip of the temporary folder `.cftmp-<filename>.cf` which contains all the needed data for the plugin. 
There can be multiple `<filename>.cf` files at the same level or project/folder but not with the same name.

#### Temporary folder

`.cftmp-<filename>.cf`: This is a temporary folder created at launch of the credibility editor to store the execution/run data. It does not contain links to an Eclipse project except that it is stored itself into a project or folder of an eclipse workspace. It is deleted after closing the Credibility Editor.

Because there can be several `<filename>.cf` files in the same folder, each `.cftmp-<filename>.cf` folder is suffixed by the name of the `<filename>.cf` file associated. (e.g. `test.cf` will generate a `.cftmp-test.cf` folder in its project/parent folder path).

The included files/folders are:
- `data`: This folder contains the HSQLDB folder with HSQL files
- `cf-schema.yml`: this file was present in an oldest version of CF. It contained the configuration of the all requirements files in one. The file was generated during the CF project creation. it is **not used** anymore and should be automatically deleted after the importation  of its data into the database. 

#### File management operations and actions

##### Create a new Credibility process:

During the creation phase, the Credibility plugin do the following tasks:
- Create a CF temporary folder `.cftmp-<filename>.cf` at the same level as the `<filename>.cf` file
- Create empty HSQLDB database
- Apply all the migration tasks
- Import the Requirements file selected configuration into the database
- Zip the temporary folder `.cftmp-<filename>.cf` into `<filename>.cf` file
- Delete the `.cftmp-<filename>.cf` temporary folder

##### Open an existing Credibility process file:

During the opening phase, the Credibility plugin do the following tasks:
- Create a CF temporary folder `.cftmp-<filename>.cf` at the same level as the `<filename>.cf` file
- Unzip `<filename>.cf` file to `.cftmp-<filename>.cf` temporary folder 
- Apply the migration tasks not already applied
- Open the database connection on the `.cftmp-<filename>.cf` temporary folder and load data

<div align="center">
![Annotation_2020-03-10_121942](uploads/f031ad28d592b407584cf857e545ad18/Annotation_2020-03-10_121942.png)
</div>

**Warning**: If `there is already an existing cf temporary folder` during loading, the plugin **tests the integrity** of this folder. 

If it is not recoverable, the plugin will inform the user and continue loading the `<filename>.cf` file:

<div align="center">
![Screen_Shot_2019-11-20_at_12.33.46_PM](/uploads/4e456f0b0117ca855b1cc340ef6ce5fe/Screen_Shot_2019-11-20_at_12.33.46_PM.png)
</div>

If it is recoverable, it **asks the user** if he wants to recover the previous data or not:

<div align="center">
![Screen_Shot_2019-11-20_at_12.33.18_PM](/uploads/352bd26f8af2bff0232a4f581b354c19/Screen_Shot_2019-11-20_at_12.33.18_PM.png)
</div>

If the user confirms recovering, the temporary folder is used and not replaced. The migration tasks needed are applied. The temporary folder and the `<filename>.cf` file will be in a different state, so the plugin will put the credibility editor in a **dirty state**. That means that the user has to save the credibility editor to keep the previous data. 
If the user doesn't want to keep the previous data, he can exit without saving.

##### Save a Credibility process file:

During the save phase, the Credibility plugin do the following tasks:
- Rename `<filename>.cf` file before starting the save process to keep a working copy
- Close the database connection
- Zip `.cftmp-<filename>.cf` temporart folder into `<filename>.cf` file
- If all this steps are successful, remove the previous renamed `<filename>.cf` file

<div align="center">
![Screen_Shot_2019-11-20_at_12.34.43_PM](/uploads/1638bbcc9355e8d1c0f79592cbd28bf9/Screen_Shot_2019-11-20_at_12.34.43_PM.png)
</div>

##### Close a Credibility process editor:

During the close phase, the Credibility plugin do the following tasks:
- Save the `<filename>.cf` file (see [Save `<filename>.cf` file](#save-filenamecf-file))
- Remove the `.cftmp-<filename>.cf` temporary folder

<div align="center">
![Screen_Shot_2019-11-20_at_12.35.48_PM](/uploads/cb9a9030efd8ec0e93acc257a18a6bd6/Screen_Shot_2019-11-20_at_12.35.48_PM.png)
</div>

##### Delete a Credibility process file:

The class `ResourceDeltaModifier` is listening for a resource deleted event and trigger the following actions if a `<filename>.cf` file is deleted:
- Delete `<filename>.cf` file
- Close the opened database connection
- If it exists, delete the `.cftmp-<filename>.cf` temporary folder

<div align="center">
![Annotation_2020-03-10_122310](uploads/d5aeb367f518987d895c437171df3c3b/Annotation_2020-03-10_122310.png)
</div>
<div align="center">
![Annotation_2020-03-10_122331](uploads/230f658d575618db250e0ca257943514/Annotation_2020-03-10_122331.png)
</div>

##### Rename a Credibility process file:

The class `ResourceDeltaModifier` is listening for a resource moved event and trigger the following actions if a `<filename>.cf` file is renamed:
- If the associated **cf editor is closed**, rename `<filename>.cf` in the Eclipse project explorer.
- If the associated **cf editor is opened**:
  - Rename `<filename>.cf` in the Eclipse project explorer
  - Rename the opened associated cf editor
  - Close the opened database connection
  - Rename `.cftmp-<filename>.cf` temporary folder
  - Close and reopen the associated cf editor to load the data

##### Move a Credibility process file:

The class `ResourceDeltaModifier` is listening for a resource moved event and trigger the following actions if a `<filename>.cf` file is moved:
- If the associated cf editor is closed, move/rename `<filename>.cf`
- If the associated cf editor is opened:
  - Move `<filename>.cf` in the Eclipse project explorer
  - Close the opened database connection
  - Move `.cftmp-<filename>.cf` temporary folder
  - Close and reopen the associated cf editor to load the data

## GUI Views Architecture

This section describes the architecture of the cf plugin views. The entry point of the plugin is the `CredibilityEditor`. It is launched when a `<filename>.cf` file is opened in Eclipse RCP. 

It loads the `MainViewManager` class. This view manages the behavior of all the other views.

### Managers

#### Credibility Editor Views

There is other view managers which are managed by the `MainViewManager`:
- `ConfigurationViewManager`
- `DecisionViewManager`
- `PIRTViewManager`
- `PCMMViewManager`
- `QoIPlanningViewManager`
- `ReportViewManager`
- `SystemRequirementViewManager`
- `UncertaintyViewManager`

They implement the `IViewManager` interface. This interface is used to set to a sub-view manager its own manager.

You can see below a diagram representing the view manager's interactions:

<div align="center">
![CF-UI_view-manager.svg](uploads/c9f73da697266340d02e136e330ff709/CF-UI_view-manager.svg)
</div>

The view managers manage its own view. You can find below a diagram per view manager:

- `HomeViewManager`:

<div align="center">
![CF-UI_interactions_home.svg](uploads/0ce41701908865014d7895c89c27b583/CF-UI_interactions_home.svg)
</div>

- `ConfigurationViewManager`:

<div align="center">
![CF-UI_interactions_configuration.svg](uploads/940955521ef55a44191f8feef2da6e44/CF-UI_interactions_configuration.svg)
</div>

- `DecisionViewManager`:

<div align="center">
![CF-UI_interactions_decision.svg](uploads/b58cbaf4fb2c2d31483649ca51f657b4/CF-UI_interactions_decision.svg)
</div>

- `PIRTViewManager`:

<div align="center">
![CF-UI_interactions_pirt.svg](uploads/b32e1834da869c8bef7d84dd0512b694/CF-UI_interactions_pirt.svg)
</div>

- `PCMMViewManager`:

<div align="center">
![CF-UI_interactions_pcmm.svg](uploads/96ea2b0b61428f3784bb423f832d3d96/CF-UI_interactions_pcmm.svg)
</div>

- `QoIPlanningViewManager`:

<div align="center">
![CF-UI_interactions_qoi-planning.svg](uploads/59eac20ae19c95b76f735c5f755a0ffb/CF-UI_interactions_qoi-planning.svg)
</div>

- `ReportViewManager`:

<div align="center">
![CF-UI_interactions_report.svg](uploads/c7ff18f552aaf91a5c9f6d9cc8d3cf89/CF-UI_interactions_report.svg)
</div>

- `SystemRequirementViewManager`:

<div align="center">
![CF-UI_interactions_system-requirement.svg](uploads/dd53c3499eeb33e7a0b5d341c99d4bc5/CF-UI_interactions_system-requirement.svg)
</div>

- `UncertaintyViewManager`:

<div align="center">
![CF-UI_interactions_uncertainty.svg](uploads/6ab3d20b4efa7bdec83a475437b73283/CF-UI_interactions_uncertainty.svg)
</div>

#### Guidance View

The guidance view is apart from the credibility editor but is listening for the credibility editor view selection and properties. 

The properties are in the `CredibilityFrameworkConstants` file and start with `PART_PROPERTY_ACTIVEVIEW`.

The guidance view manager is `CFGuidanceViewManager` class and managed the following views:
- `PIRTGuidanceLevelView`: this class displays the PIRT guidelines
- `PCMMGuidanceLevelView`: this class displays the PCMM guidelines

<div align="center">
![CF-UI_interactions_guidance.svg](uploads/a4b6e34166fcaede86ab88d359c4c81a/CF-UI_interactions_guidance.svg)
</div>

### Views Inheritance

Each CF view is divided in multiple components. Each view java class is displaying a piece of the global view. The following lists all the view classes:
- `ACredibilityView` displays the title of the plugin. This view is abstract, it can not be implemented but only extended
- `ACredibilitySubView` displays the subtitle of the plugin. This view is abstract, it can not be implemented but only extended
- `HomeView` is the plugin home and first view
- `ImportConfigurationView` allows the user to import a new configuration and to activate CF new features
- `ExportConfigurationView` displays export options for the configuration and data
- `DecisionView` shows the analyst decision tree
- `QoIPlanningView` permits to edit the Quantity of Interest and add planning informations
- `SystemRequirementView` displays the system requirements tree
- `UncertaintyView` lists the uncertainties of the CF process
- `PIRTTabFolder` is used to display the PIRT tabs
- `PIRTQoIView` is another view to edit the Quantity of Interest. It is the first PIRT view
- `PIRTPhenomenaView` is the PIRT Phenomenon description view
- `PIRTQueryResultView` is used to display the results of a PIRT query
- `ACredibilityPCMMView` is the main PCMM view to display the current role of the user. This view is abstract, it can not be implemented but only extended
- `PCMMHomeView` is the PCMM home view. It contains the PCMM wheel and progression
- `PCMMHPlanningView` is the view to edit the PCMM planning. The user input informations about the PCMM element and subelements associated
- `PCMMEvidenceView` is the evidence view and it permits to manage the evidence
- `PCMMAssessView` is used to assess PCMM subelements
- `PCMMStampView` displays a Kiviat or spider diagram of the aggregation results
- `PCMMAggregateView` aggregates and displays the result of the assessments
- `PCMMGuidanceLevelView` shows the PCMM level descriptions
- `ReportView` allows to generate a report for the CF process. Different options are available to choose the data to export

Below is a diagram showing the inheritance between all the views:

<div align="center">
![CF-UI_inheritance.svg](uploads/365542474619b5444e4f5ff61ab0f734/CF-UI_inheritance.svg)
</div>

## NGW Integration
[Go back to Contents](#contents)

NGW is the **Next Generation Workflow** project. It includes an Eclipse-based workflow editor and a portable Java runtime system. It is used to help mechanical engineers in their work.

Credibility Framework plugin can be installed into NGW as an Eclipse feature. See [How to install CF plugin](https://gitlab.com/iwf/cf/-/wikis/how-to-install-cf-plugin#install-credibility-framework-plugin) for furthermore information.

See [NGW on Gitlab](https://gitlab.com/iwf/ngw).

[Go back to the top of the page](#content-body)