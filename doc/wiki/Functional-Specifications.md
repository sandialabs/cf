This page describes the Credibility Framework functional specifications.

[Go back to Wiki home page](home)

## Contents

[[_TOC_]]

## General Description
[Go back to Contents](#contents)

This section defines Credibility Framework plugin context.

### Product Scope

The Credibility Framework shall provide intuitive and coherent environment for ModSim teams to:
- define a Credibility Framework project to track evolution the credibility evidence associated with a particular ModSim task
- communicate credibility of computational simulation models
- provide and maintain links to configuration controlled artifacts constituting the evidence package
- tag the evidence package at significant stages of the ModSim program
- quantitatively assess the state of credibility if they choose
- support internal and external peer reviews
- conduct a historical review of credibility throughout the life of the ModSim project including understanding diverse input from different team and review panel members

It is important that all behavior of the Credibility Framework are configurable by non-programmers without having to touch source code.

### Product Perspective

Engineering credibility has been defined in many ways over the past two decades. Fundamentally credibility is a set of tools and processes for decision makers to understand why (or whether) they should trust the ModSim model to make decisions as well as develop a sense risk associated with making such decisions.

The Credibility Framework is an initial implementation of the credibility tools for mechanical engineers. It is largely a stand-alone Eclipse application intended to work seamlessly with the analysis data management back-end to identify whether artifacts referenced by the credibility evidence package are under configuration control by the Analysis Data Management layer.

### Product Functions

On the high-level Credibility framework implements a document management system and a guided information harvesting and ranking tool providing members of the ModSim team and reviewers to collect, disseminate and optionally rank groups of attributes of the ModSim activity. 

The Credibility Framework is not a physical experiment management of computational model execution platform, but it provides links to artifacts from those processes managed and executed by the workflow engine. 

For example, definition, execution and post processing of a UQ study on a complex analysis workflow is managed by the workflow engine, the Dakota wizard and results visualization tools. 

Consequently, the Credibility Framework under the Uncertainty Quantification sub element of PCMM maintains links to instances of the UQ studies and their documentation intended to provide evidence for making statements about margin uncertainties.

### Acronyms:
- `PIRT`: `P`henomena `I`dentification `R`anking `T`able
- `QoI`: `Q`uantity `o`f `I`nterest
- `PCMM`: `P`redictive `C`apability `M`aturity `M`odel

## Configuration
[Go back to Contents](#contents)

Every credibility feature must be configurable by a non-programmer user. To do so, the choosen solution is to use YAML files (`*.yaml` or `*.yml`) to configure each CF project. 

This configuration files can be created or generated. The CF project contains Excel files with a macro that converts the spreadsheets content into YAML files. See [Requirements](Requirements) page for more implementation details.

## Preferences
[Go back to Contents](#contents)

It is possible to configure the cf plugin with the preference page under ```Eclipse Preferences```, then ```Credibility Framework```. Different options are available:

### Global Preferences

![image](uploads/2aad0a7682c1907da840f9ecbc958754/image.png)

- `Display Credibility Framework Version Number`: If set to true (default value), this option displays the version number in the bottom left corner of the Home view.

![image](uploads/51dbbe81f07709b49abd92e8b454c1ab/image.png)

- `Display Credibility File Version Origin Number`: If set to true (default value), this option displays the CF file version origin number in the bottom right corner of the Home view.

![image](uploads/4b26927b5d3786fdff96f1c0caa2599f/image.png)

- `Python executable path`: to execute python scripts (used for reporting)
- `ARG executable path`: the ARG installation path (used for reporting)
- `ARG setenv script path`: the python preexecution scripts (used to set ARG environment before report execution)

- Open link with (Evidence, References...): this preference is used to open link into a web browser

### Developer Options

![image](uploads/350f56f70b81ced0e151681f28216c0f/image.png)

- Enable `Inline Word Document` option in the Report View: enable the word document inlining option into the [Report View](#credibility-report). The option will be available here:

![image](/uploads/539654389d249a5de742a73b3110f44a/image.png)

- Enable `Concurrency Support` allows the CF plugin to create/open web projects and connect to a remote server

## Getting Started
[Go back to Contents](#contents)

The user must be able to create a new Credibility Process **from an existing modSim project**. CF can be associated with any point of an eclipse project. 

For example, a project may have multiple ModSim models within a deeply hierarchical folder structure. Conceptually an analyst would navigate to a particular model to create a CF process.

If you don't have an existing Eclipse project please create one with section [How to create a new Eclipse project](#how-to-create-a-new-eclipse-project). Otherwise, you can go to section [New Credibility Process](#new-credibility-process).

### How to create a new Eclipse project

If you don't have an existing project, create one:
- In the menu, click on `File > New > Project`:

![image](uploads/601d15950fcb30bcb090fac02925b9a1/image.png)

- Select `General > Project`:

![image](uploads/26df7546e164695c70b0c118e6b813b3/image.png)

- Enter a **name** for your project and click `Finish`:

![image](uploads/94fd7161b06ed698811231b52bdecda8/image.png)

### New Credibility Process
[Go back to Contents](#contents)

Create the Credibility Process for an existing Eclipse project:

- **Right-click** on the desired project, the plugin should provide a credibility creation wizard. The plugin must use Eclipse current interface to create a new project.

![2019-07-23_fspecs-new-cf-project](uploads/4dd810887c804cfafd35b5b179954d16/2019-07-23_fspecs-new-cf-project.png)

#### Credibility Process

Select `Credibility Process` under `Credibility Framework` section:

![Annotation_2020-03-17_181307](uploads/792f94bdd231796251d7fbeeb180d72f/Annotation_2020-03-17_181307.png)

During creation, the interface should ask the user to complete the following information:
- `Parent folder`: the file location in the project explorer.
- `File base name`: the name of the credibility process (file extension will be `<filename>.cf` and is automatically generated).

![Annotation_2020-03-17_180905](uploads/2a045f24c294c6f08a81cd299227c90d/Annotation_2020-03-17_180905.png)

#### Credibility Setup

The next window will ask for the configuration of the credibility process. This is related to[Configuration](#Configuration) section. 

This dialog has two different pages:

- `Simplified`:

Opened by default, the configuration directory location will be asked. The folder will be parsed searching for configuration files. By default, the found files will be automatically associated to the corresponding feature:

![image](uploads/bdc1f67d7eac8807e6dff3dc3e3e27f8/image.png)

If there is multiple files available for one feature, a drop-down combo will be displayed with different options:

![image](uploads/5f085e2f45069d69c68a3d6a09f1fd5f/image.png)

- `Advanced`:

All configuration files location will be asked:

![image](uploads/acbef0dfc57bace60cf37a028236a39f/image.png)

- `Generate Credibility Evidence Folder Structure`: this checkbox is checked by default. It enables the generation of the default Credibility Evidence Folder Structure at the same location as the .cf file. See section [Credibility Evidence Folder Structure](#credibility-evidence-folder-structure) for further information.

Just after creation, the new project wiil be opened in a new editor.

The new credibility project data are stored in a database inside the  `<filename>.cf` file(see [Technical Specifications](#technical-specifications)).

### Credibility Evidence Folder Structure
[Go back to Contents](#contents)

This option enables the generation of the default Credibility Evidence Folder Structure at the same location as the `<filename>.cf` file. This folder structure is useful to store the different evidence needed for PCMM. If a folder already exists, it will not be replaced or deleted.

- **Right-click** a project or folder in the project explorer and select `Credibility Evidence Folder Structure`, then `Next`:

![image](uploads/913d88d73ef86b5c70f03eca0ac98a44/image.png)

- Select the **location** and click `Finish`:

![image](uploads/b2fd3e493b073970068ba11bdda4f26b/image.png)

The folder structure will be generated under the location you selected before.

### Open Existing Credibility Process
[Go back to Contents](#contents)

To open an existing credibility process, the user should **double-click on the** `<filename>.cf` **file** under the desired modSim project in the Project Explorer. The plugin will open a new editor with the Home View.

### Save Credibility Editor

The credibility editor manages a temporary folder to store the processing data. Every modification is instantly saved in this temporary folder but not in the `<filename>.cf` file. 

#### Save your modifications

After each change, the editor indicates there is unsaved data by adding a `\*` beside the editor name and with a yellow label `Not saved` in the top left corner:

![image](uploads/1a2a748bbc7f0b63d4d33daaec030fae/image.png)

The user has to save the editor to keep the modifications across sessions by clicking the `Save` button in the Credibility Editor:

![image](uploads/6cb1792f2a83581443b40c3af7abe021/image.png)

Or by clicking in the Eclipse menu `File > Save`:

![image](uploads/9ba43af8a4166d4dd3e030c1ed3d895b/image.png)

If the user **closes the editor with unsaved modifications**, the editor **prompts the user to save** or quit the editor:

![image](uploads/cb5dc98c2534d5da3cd690180ddc63a7/image.png)

The editor is saved once the `\*` character disapears aside the file name and when the green `Up to date` label is displayed in the top left corner:

![image](uploads/8ac4a7116b1379eb2f0f83d5363ac62c/image.png)

#### Recover after a crash

If the Eclipse based application crashed, a temporary folder will survive in the project folder. 

When the user opens the `<filename>.cf` file, if the folder is in a recoverable state, the plugin asks the user to recover data or not:

![image](uploads/a05955aac1d6c86b3dab4464f96b1588/image.png)

If the folder is not recoverable, the plugin will delete it and open the `<filename>.cf` file data.

### Navigation
[Go back to Contents](#contents)

From all views (except the Home View), the user can navigate by clicking on an element in the `breadcrumb` on top of the view:

![image](uploads/b9b027e6b3829aa1b4386ec027dc4e35/image.png)

The `breadcrumb` and title changes depending of the current view. The current view item is selected (grayed out).

![image](uploads/3b8f4035d170e593f42a664610b5033d/image.png)

![image](uploads/11bd81ce3fdb9d3cdf0aa329cc0ab046/image.png)


## Home View
[Go back to Contents](#contents)

The home view describes the credibility process and contains links and buttons to open the credibility features.

The available features are:
- [Planning - ModSim Intended Purpose](#modsim-intended-purpose)
- [Planning - System Requirements](#system-requirements)
- [Planning - QoI Planner](#qoi-planner)
- [Planning - Uncertainty Inventory](#uncertainty-inventory)
- [Planning - PCMM Planning](#pcmm-planning)
- [Planning - Analyst Decisions](#analyst-decisions)
- [Phenomena, PIRT](#phenomena-pirt)
- [Credibility, PCMM](#credibility-pcmm)
- [Communication - Credibility Report](#credibility-report)
- [CF Project Configuration](#cf-project-configuration)

The home page must be opened by clicking on a credibility file (`<filename>.cf` file) in the eclipse project explorer.

![image](uploads/0108dfdabe80b0d703e246378512c70c/image.png)

### Planning card

The Planning card contains different tools to set out the Credibility project. Each tool has a clickable card to access the feature:
- [ModSim Intended Purpose](#modsim-intended-purpose)
- [System Requirements](#system-requirements)
- [QoI Planner](#qoi-planner)
- [Uncertainty Inventory](#uncertainty-inventory)
- [PCMM Planning](#pcmm-planning)
- [Analyst Decisions](#analyst-decisions)

The buttons trigger the following actions:
- Click on the card: Opens the associated feature
- `Informations`: Opens the contextual Help to describe the Planning tools:

![image](uploads/f44d792dec6907b19447eae5b21511d5/image.png)

### PIRT card

The PIRT card contains a description of the PIRT feature, Quantities of Interest and the PIRT tables. 

This card has some indicators:
- `Quantities of Interest`: This indicator displays the number of Quantities of Interest created in PIRT.

The buttons trigger the following actions:
- Click on the `PIRT table`: Opens the PIRT home view (see section [Phenomena, PIRT](#phenomena-pirt))
- `Reference`: Opens a PDF document that describing the PIRT feature
- `Open`: Opens the PIRT home view (see section [Phenomena, PIRT](#phenomena-pirt))
- `Informations`: Opens the contextual Help to describe PIRT:

![image](uploads/31fdf5e8648d02353234d21507248b3f/image.png)

### PCMM card

The PCMM card contains a description of the PCMM feature. It also gives an overview of the PCMM wheel and its PCMM elements available. 

This card has some indicators:
- `Evidence errors`: This indicator displays the number of errors for the PCMM evidence (evidence file not found,...). This indicator is only visible if an error occured. The tagged PCMM errors are not counted.
- `Evidence warnings`: This indicator displays the number of warnings for the PCMM evidence (evidence file changed, an evidence is associated to multiple elements/subelements,...). This indicator is only visible if there is a warning. The tagged PCMM warnings are not counted.
- `Progress`: The progress bar displays the progress of the overall PCMM. It checks if at least an evidence is associated to each PCMM element or subelement and if there is at least an assessment for each of them.

The buttons trigger the following actions:
- Click on the `PCMM wheel`: Opens the PCMM home view (see section [Credibility, PCMM](#credibility-pcmm))
- `Reference`: Opens a PDF document that describing the PCMM feature
- `Open`: Opens the PCMM home view (see section [Credibility, PCMM](#credibility-pcmm))
- `Informations`: Opens the contextual Help to describe PCMM:

![image](uploads/042157dd8d4ab6a4acba0189fbf99d38/image.png)

### Communication card

The Communication card contains the tools to communicate around the project. 

The only available tool is [Credibility Report](#credibility-report). See [Communication](#communication) section for furthermore informations.

The buttons trigger the following actions:
- Click on the card: Opens the associated feature
- `Informations`: Opens the contextual Help to describe the card:

![image](uploads/4529be96707468456feb8f28f8a5d974/image.png)

## Planning
[Go back to Contents](#contents)

Planning is intended to help analysts to set out V&V activities before model development commences with the intention that its contents are kept up to date as model development progresses.

The available tools are:
- ModSim Intended Purpose
- System Requirements
- QoI Planner
- Uncertainty Inventory
- PCMM Planning
- Analyst Decisions

### ModSim Intended Purpose

The ModSim Intended Purpose goal is to firstly describe the project and its purpose. A reference can be associated. 

![image](uploads/104137f3e5818c8ca3bed36f2fe80c54/image.png)

### System Requirements

The System Requirements goal is to enumerate and describe the initial requirements of the project. This is a **prerequisite to Quantities of Interest**.

![image](uploads/f5ab0abab7db493e3ad33e53070f13c7/image.png)

There is no limit in the number of groups, and sub-requirements:

![image](uploads/064fac75a43bb77c6a452e5c902e5b10/image.png)

#### System Requirements Buttons and Actions

##### Add Requirement Group

![image](uploads/67cff536075fff3cf67d57d8d4e25bcc/image.png)

This button opens a new dialog to add a requirement group. The configured fields will be available into the dialog:

![image](uploads/ed21143e8c1e798ecf0ec8f43dab6868/image.png)

##### Back

![image](uploads/6288fc7442ceac731344769bfe8f41ff/image.png)

Return to Home page (see [Home View](#home-view)).

##### Information

![image](uploads/d86ad7c7cccc2251e923d45a9e75971b/image.png)

It opens the contextual help for Requirements view:

![image](uploads/a1345d014c5580f5dba3e369c912b5a1/image.png)

#### System Requirements Table Columns

- `ID`: the logical ID generated by CF
- `Statement`: the requirement statement used to identify the requirement

The other columns are managed with the configuration files and can be removed:
- `Type`: the type of requirement
- `Factor of Safety`: a numerical indicator of the safety required
- `Verification/Acceptance method`: the level of rigor for the verification 
- `Source Requirement Reference`: the document to give more details about the requirement

#### System Requirements Table Actions

![image](uploads/1dcc5d5749aea3fa2316b1323ad602a6/image.png)

##### Add System Requirement

The user can add a requirement under an existing requirement group or a requirement by clicking on the `Add` button:

![image](uploads/9a24e67b0f5fd7b8a0722d2dc99094dd/image.png)

A new dialog will popup and ask the user to input the necessary informations:

![image](uploads/2bfe2fc99066abaa85dc789c716a157a/image.png)

##### Open System Requirement References

The `Source Requirement Reference` field stores a link to the source document associated. Clicking on the `Open` button will open this document (url, document inside the workspace):

![image](uploads/5011b304ebe94ee31f53d86071ac586e/image.png)

##### View System Requirement

It is possible to view the requirement informations by clicking on the `View` button:

![image](uploads/52c3151b29ef363598a00b6c93832c07/image.png)

A new dialog will popup with the requirement informations in read-only mode:

![image](uploads/ae0f1ea7ca43c807a7eb77a70dd1b6af/image.png)

##### Edit System Requirement

To edit the requirement informations click the `Edit` button:

![image](uploads/34c5643f296f9e75801fdd03046cc9e8/image.png)

A new dialog will popup in edition mode:

![image](uploads/47b8637615520ba83afa691f3026a19f/image.png)

Click `Update` to save changes.

##### Delete System Requirement

To delete a requirement click the `Delete` button:

![image](uploads/cfee80c98f67d44cc4a1a7acdebfe958/image.png)

A confirm box will popup:

![image](uploads/9d437bfcf5dadf351ef0348ebc58dabd/image.png)

All the requirements and sub-requirements associated will be deleted.

### QoI Planner

This view lists all the quantities of interest associated to the current credibility process. The intended goal of this tool is to define quantities of interest and to configure their limits and conditions.

The definition of the PIRT tables will be done later in PIRT section (see [QoI Home View](#qoi-home-view)).

System requirements defintion is **a pre-requisite** to QoI Planner.

![image](uploads/f679514a671873e3807440b07ea48c9b/image.png)

#### QoI Buttons and Actions

##### Add Quantity of Interest

![image](uploads/ab853fa377c8acb9e510c200d280a45b/image.png)

This button opens a new dialog to add a quantity of interest. The QoI planner fields will only be available in this tool. The PIRT view of the QoI is more PIRT table oriented. The fields are configurable with the configuration process:

![image](uploads/51f3bb2ae6b162f224a6f85f4a27d750/image.png)

The **QoI name must be unique.**

##### Back

![image](uploads/6288fc7442ceac731344769bfe8f41ff/image.png)

Return to Home page (see [Home View](#home-view)).

##### Information

![image](uploads/d86ad7c7cccc2251e923d45a9e75971b/image.png)

It opens the contextual help for QoI Planner view:

![image](uploads/fd60ecae316314da458faabf3d87735a/image.png)

#### QoI Table Columns

- `ID`: the logical ID generated by CF
- `Symbol`: the symbol used to identify the QoI
- `Description`: the QoI description

The other columns are managed with the configuration files and can be removed:
- `System Requirement`: the system requirement to associate with
- `Lower Limit`: a numerical indicator for the QoI lower limit
- `Upper Limit`: a numerical indicator for the QoI upper limit
- `Model Reliability Facotr`: a numerical indicator for the reliability of the QoI
- `Is Extractor Available?`
- `Basic SQE for Extractor?`

#### QoI Table Actions

![image](uploads/aa8229263d7c00349b21c416ed33f0c8/image.png)

##### View Quantity of Interest

It is possible to view the QoI informations by clicking on the `View` button:

![image](uploads/52c3151b29ef363598a00b6c93832c07/image.png)

A new dialog will popup with the QoI informations in read-only mode:

![image](uploads/3541d00cc8bd5ab76e16d52703641db3/image.png)

##### Edit Quantity of Interest

To edit the QoI informations click the `Edit` button:

![image](uploads/34c5643f296f9e75801fdd03046cc9e8/image.png)

A new dialog will popup in edition mode:

![image](uploads/490529e2ed4b0ac78d9e3fbaf295cc99/image.png)

Click `Update` to save changes.

A tagged QoI is only visible and can not be edited.

##### Copy Quantity of Interest

The user can make a copy of the QoI by clicking on the `Copy` button:

![image](uploads/62638bec663871d21c692d4b0132cadc/image.png)

A confirmation box will popup, click `Ok`:

![image](uploads/e4a1afdc9e1598988e22c33278188120/image.png)

A new dialog will popup and ask the user to input the necessary informations:

![image](uploads/a19bb9f62f4bafe3b96d0024af9f81d7/image.png)

The **QoI name must be unique.**

A tagged QoI can not be copied.

##### Tag Quantity of Interest

The user can tag a QoI. A tag is a copy that can not be edited. It is used to kept the state of a QoI. It helps the user to see the progress and take decision. Each associated phenomenon groups, phenomena and criteria will be copied and tagged (see [Tag QoI](#tag-qoi)). 
To tag a QoI click on the `Tag` button:

![image](uploads/9a24e67b0f5fd7b8a0722d2dc99094dd/image.png)

A confirmation box will popup, click `Ok`:

![image](uploads/75a526702c61b1515b7bada29c7814f7/image.png)

A new dialog will popup asking the user to input a description for the tag:

![image](uploads/4be2b009e80091dc8ecfe92ddc07e0a1/image.png)

A succes box will be displayed to confirm the tag operation:

![image](uploads/77ac538ec9205166b84b779c6fc375d2/image.png)

A tagged QoI can not be tagged again.

##### Delete Quantity of Interest

To delete a QoI click the `Delete` button:

![image](uploads/cfee80c98f67d44cc4a1a7acdebfe958/image.png)

A confirm box will popup:

![image](uploads/433bdbf868466e3e9887b3b6906197ef/image.png)

If the user confirms deletion, the selected QoI is deleted with all the associated definition, ranking, phenomenon groups, phenomena and criteria. The associated tags will be deleted too.

### Uncertainty Inventory

Uncertainty Inventory is a tool to define the known uncertainties of the current modSim and to associate its references. The current view lets you add groups and uncertainties.

![image](uploads/20a8c78e280d198a45d00d2a03e9aca1/image.png)

#### Uncertainty Buttons and Actions

##### Add Uncertainty Group

![image](uploads/095cc0dd4b83d8dcf00b0f5f5b6a7c87/image.png)

This button opens a new dialog to add an uncertainty group:

![image](uploads/8943ad43ac3a13f59707e9f14b4cdec7/image.png)

##### Back

![image](uploads/6288fc7442ceac731344769bfe8f41ff/image.png)

Return to Home page (see [Home View](#home-view)).

##### Information

![image](uploads/d86ad7c7cccc2251e923d45a9e75971b/image.png)

It opens the contextual help for Uncertainty view:

![image](uploads/7a11729325b2912d82f4a288a1ff297d/image.png)

#### Uncertainty Table Columns

- `ID`: the logical ID generated by CF
- `Uncertainty group name`: the uncertainty group name

The other columns are managed with the configuration files and can be removed:
- `Variable Name`: the uncertainty variable name
- `Description`: the uncertainty description
- `Type`: the uncertainty type
- `Characterization`
- `Parameters`: the input parameters
- `Model feature`
- `Comments`
- `Feasible Physical Range`: the physical range of the uncertainty
- `References`: the document to associate to the uncertainty

#### Uncertainty Table Actions

![image](uploads/d10e01cd136652fa22bd375a6139370e/image.png)

##### Add Uncertainty

The user can add an uncertainty under an existing uncertainty group by clicking on the `Add` button:

![image](uploads/9a24e67b0f5fd7b8a0722d2dc99094dd/image.png)

A new dialog will popup and ask the user to input the necessary informations:

![image](uploads/a5dbae6ce88113e5a9f64c6b5405198a/image.png)

##### Open Uncertainty

The `References` field stores a link to a document associated to the uncertainty. Clicking on the `Open` button will open this document (url, document inside the workspace):

![image](uploads/5011b304ebe94ee31f53d86071ac586e/image.png)

##### View Uncertainty

It is possible to view the uncertainty informations by clicking on the `View` button:

![image](uploads/52c3151b29ef363598a00b6c93832c07/image.png)

A new dialog will popup with the uncertainty informations in read-only mode:

![image](uploads/740a6cbcca62571293bfb229eeab2f13/image.png)

##### Edit Uncertainty

To edit the uncertainty or group informations click the `Edit` button:

![image](uploads/34c5643f296f9e75801fdd03046cc9e8/image.png)

A new dialog will popup in edition mode:

![image](uploads/e5c49511b1b6b508061e27b1ff1c85a7/image.png)

Click `Update` to save changes.

##### Delete Uncertainty

To delete an uncertainty or a group click the `Delete` button:

![image](uploads/cfee80c98f67d44cc4a1a7acdebfe958/image.png)

A confirm box will popup:

![image](uploads/ff881cc324145d1b0003208574897b0e/image.png)

For a group, all the associated uncertainties will be deleted.


### PCMM Planning

PCMM is a tool to help analysts to classify and assess evidence. The planning tool's goal is to prepare further  discussions and describe each element and/or sub-element with questions and predefined fields.

![image](uploads/a24ed92b6afac16d6eaf9041d62a1517/image.png)

#### PCMM Wheel

The PCMM wheel shows the different PCMM elements in one chart. 

To start planning, **click on a PCMM Element**.

![image](uploads/28b33808c40e388309b1ebb86793c13d/image.png)

**Important**: This section requires a role to be defined for the user before it an be used.

If there is no role defined, the user is prompted to select a role (see [Change PCMM Role](#change-pcmm-role)).

**Important**: The PCMM Element PMMF (Physics and Material Model Fidelity) needs to define PIRT quantities of interest first:

![image](uploads/a0dbba74bda5af4941633a0f0aa8c109/image.png)

#### PCMM Planning Progress

This section displays the overall progress of the PCMM process including the Planning, Assess and Evidence phases.

See [PCMM Progress](#pcmm-progress) for furthermore informations.

#### PCMM Planning Tags

A tag saves the state of the PCMM process at one point. By default, the tag `Latest version (working)` is selected. It is the current working state of the PCMM process.

A `Tags` part is available on the PCMM Home view, below the `Progress` one:

![image](uploads/655552de279ed3939438188ccfca1321/image.png)

Different actions are available for the tags. See how to:
- [add a new Tag](#new-tag)
- [select an existing Tag](#select-a-tag)
- [manage existing tags](#manage-tags)

#### PCMM Planning Buttons and Actions

##### Role

In the top right corner, the user has to choose a role to start planning (see [Change PCMM Role](#change-pcmm-role)).

![image](uploads/4f0069f1c6cd238e1d0798db3f4095e1/image.png)

##### Back

![image](uploads/6288fc7442ceac731344769bfe8f41ff/image.png)

Return to Home page (see [Home View](#home-view)).

##### Guidance

![image](uploads/e11ab8339eacd7eefb90c53e377a836b/image.png)

This button opens the `Credibility Framework Guidance` view with the PCMM Guidance in it. See [PCMM Guidance](#pcmm-guidance).

##### Information

![image](uploads/d86ad7c7cccc2251e923d45a9e75971b/image.png)

It opens the contextual help for the PCMM Planning view:

![image](uploads/42448f5155948bebdee5e545021d5b19/image.png)

#### PCMM Planning Form

Clicking on a PCMM element opens the PCMM Planning form. All the planning fields are configurable. See [Requirements page for PCMM Planning fields](Requirements#pcmm-planning-data-model-tab) to change the configuration. 

Depending of the project configuration, the rendering will change:

**In default mode:** All the subelements associated to the element are displayed. 

![image](uploads/7ec79d94d3758efbe6fd8879b496a333/image.png)

To start planning, click on a subelement. The available fields will be displayed:

![image](uploads/43089e4e880fac84352673a23f5aff38/image.png)

**In simplified mode:** The planning informations are associated to the element:

![image](uploads/f95c67a48cda12917700deb49df85bcb/image.png)

##### PCMM Planning item types

The PCMM Planning fields can be of the following types (see [Generic Parameters configuration](Requirements#generic-parameters-configuration) for a complete definition):
- Number
- Text
- RichText: text with colors, titles, styles... (converted into html)
- Link: a link to a file or a url
- Date
- Select: a select box with predefined values
- Custom (table): a table with configurable columns of the types defined above

##### PCMM Planning Form Buttons and Actions

###### Role

In the top right corner, the user can change its role (see [Change PCMM Role](#change-pcmm-role)).

![image](uploads/4f0069f1c6cd238e1d0798db3f4095e1/image.png)

###### Back

![image](uploads/6288fc7442ceac731344769bfe8f41ff/image.png)

Return to the PCMM Planning Home page.

###### Information

![image](uploads/d86ad7c7cccc2251e923d45a9e75971b/image.png)

Opens the contextual help for the PCMM Planning view:

![image](uploads/131b5a55ef8e9136bb5be6ff1c46d2c1/image.png)


### Analyst Decisions

Analyst Decision is a minitool to record analyst decisions and their characteristics (consequence, techniques maturity, description...).

![image](uploads/110f20a012be209189e63f740e5a063f/image.png)

There is no limit in the number of levels for the groups, decisions, sub-decisions...

#### Analyst Decisions Buttons and Actions

##### Add Decision Group

![image](uploads/ac82fe1a7af85a043a7769e4f99b6063/image.png)

This button opens a new dialog to add a decision group. The configured fields will be available into the dialog:

![image](uploads/ed21143e8c1e798ecf0ec8f43dab6868/image.png)

##### Back

![image](uploads/6288fc7442ceac731344769bfe8f41ff/image.png)

Return to Home page (see [Home View](#home-view)).

##### Information

![image](uploads/d86ad7c7cccc2251e923d45a9e75971b/image.png)

It opens the contextual help for the Decision view:

![image](uploads/554b17f2eff9754b436bce257c1f223d/image.png)

#### Analyst Decisions Table Columns

- `ID`: the logical ID generated by CF
- `Title`: the title used to identify the decision

The other columns are managed with the configuration files and can be removed:
- `Description`: the type of requirement
- `Rationale`
- `Conservatism`
- `Credibility Element`: the PCMM element to associate with
- `Consequence of Wrong Decision`
- `Likelihood of Wrong Decision`
- `Maturity of Techniques`: are the techniques mature? 
- `Analyst Training`: what is the level of training of the analyst?
- `References`: the document (url or file) to support the decision

#### Analyst Decisions Table Actions

![image](uploads/04f247b9def672fd8cfe2855343c6462/image.png)

##### Add Analyst Decision

The user can add a decision under an existing decision group or a decision by clicking on the `Add` button:

![image](uploads/9a24e67b0f5fd7b8a0722d2dc99094dd/image.png)

A new dialog will popup and ask the user to input the necessary informations:

![image](uploads/e9d85e0875112e531c25694cf85894d6/image.png)

##### Open Analyst Decision

The `References` field stores a link to a document associated to the decision. Clicking on the `Open` button will open this document (url, document inside the workspace):

![image](uploads/5011b304ebe94ee31f53d86071ac586e/image.png)

##### View Analyst Decision

It is possible to view the decision informations by clicking on the `View` button:

![image](uploads/52c3151b29ef363598a00b6c93832c07/image.png)

A new dialog will popup with the decision informations in read-only mode:

![image](uploads/45edfa99eac9a1911c0565c8065d72a9/image.png)

##### Edit Analyst Decision

To edit the decision informations click the `Edit` button:

![image](uploads/34c5643f296f9e75801fdd03046cc9e8/image.png)

A new dialog will popup in edition mode:

![image](uploads/2e318217e192d0ead9a73618bd74d74a/image.png)

Click `Update` to save changes.

##### Delete Analyst Decision

To delete a decision click the `Delete` button:

![image](uploads/cfee80c98f67d44cc4a1a7acdebfe958/image.png)

A confirm box will popup:

![image](uploads/67e9ac8250ada9ac9e2b0c0249e68e2c/image.png)

All the decisions and sub-decisions associated will be deleted.

## Phenomena, PIRT
[Go back to Contents](#contents)

PIRT feature allows to describe quantities of interest and to associate phenomena and criteria with a created quantity of interest. The PIRT view is a stack of tabs starting with the QoI Home View. After that, multiple PIRT tables can be added for each quantity of interest opened.

PIRT content persists across sessions in `<filename>.cf` file.

### QoI Home View

This view lists all the quantities of interest associated to the current credibility process. It is possible to add, edit/open, copy, tag or delete a QoI (Quantity of Interest).

![image](uploads/373f412d5f787979383b370cbba54fa0/image.png)

#### Model Header Table

![image](uploads/95b0ae43d48711c482d6141721477a66/image.png)

The Model Header table contains model information:
- `Application`: the application of this model.
- `Contact`: the person(s) to contact for further information about the model.

The Model Header table can be hidden by clicking on the topbar containing *Model Description*.

#### Table QoI columns:

![image](uploads/a036135cd82d74ffb89e2c708a39fcff/image.png)

The table has four columns:
- `<Tagged?>`: is QoI tagged? The first column displays a tag icon if the QoI is tagged, otherwise nothing. A tagged QoI is always displayed under its parent untagged QoI. Its line background color is brown.
- `Name`: the QoI name
- `Description`: the summary of the description of the QoI
- `Creation Date`: QoI creation date
- `Action columns`: 

#### Table QoI actions:
- **Double-click**: opens the selected QoI in a new tab in edition. A tagged QoI is only visible and can not be edited.

#### Table QoI action buttons:

![image](uploads/9e4146bc722ff3dfd6b9b8e1385036b1/image.png)

- `Open`: opens the selected QoI in a new tab in edition. A tagged QoI is only visible and can not be edited.
- `Copy`: makes a copy of the QoI and of all its associated phenomenon groups, phenomena and criteria.
- `Tag`: creates a tag for this QoI. A tag is a copy that can not be edited. It is used to kept the state of a QoI. It helps the user to see the progress and take decision. Each associated phenomenon groups, phenomena and criteria will be copied and tagged (see [Tag QoI](#tag-qoi)).
- `Delete`: deletes the QoI. A deletion confirmation dialog is opened on click:

![image](uploads/2196b1dd077a61221cad985588a61661/image.png)

If the user confirms deletion, the selected QoI is deleted with all the associated phenomenon groups, phenomena and criteria.

#### QoI Buttons

##### Add Quantity of Interest

![image](uploads/2de0bd17af6b0a6337c99a3063eb163f/image.png)

This button creates a new quantity of interest with a new PIRT table. It throws a new QoI creation wizard.

- `Name`: The user can enter a **name** for the new QoI. The name can not be null.
- `Description`: The QoI can be described with a RichText editor with tools to layout the description as a document.
- The **creation date** will be automatically stored on the new QoI.

![image](uploads/5f8026d4bfd3586db58b476754119491/image.png)

##### Back

![image](uploads/6288fc7442ceac731344769bfe8f41ff/image.png)

`Back` button opens the cf-plugin `Home View`.

##### Guidance

![image](uploads/e11ab8339eacd7eefb90c53e377a836b/image.png)

This button opens the `Credibility Framework Guidance` view with the PIRT Guidance in it. See [PIRT Guidance](#pirt-guidance).

##### Information

![image](uploads/d86ad7c7cccc2251e923d45a9e75971b/image.png)

It opens the contextual help for Quantities of Interest:

![image](uploads/1ced04bfc9f3214418f04e07cdc59150/image.png)

### Phenomena View

The Phenomena view displays the information of a QoI. This view is splitted in two tables, quantity of interest header containing the QoI information and phenomena table to assess criteria credibility.

![image](uploads/e8b6ecc203edfb83389d660bc7a99665/image.png)

#### Phenomena Buttons and Actions

##### Add Phenomenon Group

![image](uploads/a8387e8e52b82d75e674c9403146179c/image.png)

Opens a new dialog to create a new phenomenon group:

![image](uploads/a3d1e7ee088897633e5a458fcd2b0021/image.png)

Required field for phenomenon group is:
- `Description`: this is the group label or name

##### Tag QoI

![image](uploads/549667352b30818f546fdb458c1fe2ed/image.png)

Creates a tag for this QoI. A tag is a copy that can not be edited. It is used to kept the state of a QoI. It helps the user to see the progress and take decision. Each associated phenomenon groups, phenomena and criteria will be copied and tagged.

The user is prompted to confirm the tag action:

![image](uploads/d19f58a3186db05ed94b8b5a00050246/image.png)

And is encouraged to enter a `Description` for this tag:

![image](uploads/49ee519ab43af2f055992625e412fcc0/image.png)

##### Reset

![image](uploads/33adfff400a207266d4bd75d89e1bd7a/image.png)

Resets the current Quantity of Interest:
- Delete all variable QoI Headers
- Delete all Phenomenon groups
- Delete all Phenomena
- Delete all Criteria

The user has to confirm the reset action:

![image](uploads/29cb5eaa53c74614ea97e1ea5af588a8/image.png)

##### Back

![image](uploads/6288fc7442ceac731344769bfe8f41ff/image.png)

`Back` button opens the cf-plugin `Home View`.

##### Close

![image](uploads/09464311eb104543e904616d06f01a38/image.png)

This button closes the current Quantity of Interest tab.

##### Guidance

![image](uploads/e11ab8339eacd7eefb90c53e377a836b/image.png)

This button opens the `Credibility Framework Guidance` view with the PIRT Guidance in it. See [PIRT Guidance](#pirt-guidance).

##### Information

![image](uploads/d86ad7c7cccc2251e923d45a9e75971b/image.png)

It opens the contextual help for PIRT table:

![image](uploads/cdc5afaf90711df40e8e4ba292c34208/image.png)

#### QoI Header Table

The QoI Header table contains QoI information. The information is separated in two types, the fixed fields and the variable ones.

![image](uploads/51a65ce944c77c91fa8e859c61849c33/image.png)

The **fixed fields** are:
- `Name`: the QoI name
- `Creation Date`: the creation date
- `Tagged`: is QoI tagged? This field is computed depending on tag date.
- `Tag Date`: the tag date
- `Tag Description`: The QoI description

The **variable fields** are added according to the project configuration file. All variable fields are *Text* or *RichText* fields.

The QoI Header table can be hidden by clicking on the topbar containing the *name of the QoI*.

##### Edit a RichText field
The field of type *RichText* are editable y clicking on the following button:

![image](uploads/ef95e4fd72b6952eda2be9db8e366bc4/image.png)

A new dialog will popup with the RichText tools:

![image](uploads/e286f241808fd971d0c56cb2fc199f7a/image.png)

##### View a RichText field
This type of field can be viewed by clicking on the view icon:

![image](uploads/1e5ae7233b12d621f8112383afce4129/image.png)

A new dialog will popup with the full content:

![image](uploads/6bb80953aff6d843d1138292ac68ddf7/image.png)

#### Phenomena Table

This table presents the different phenomenon groups with their associated phenomena. The phenomena has several criteria. Information is separated in two types, the fixed columns and variable.

![image](uploads/738063f61b5143753723516e634e5c29/image.png)

#### Phenomena Table columns:

**Fixed columns** are:
- `ID`: group or phenomenon ID to display.
- `Description`: the name of the group or phenomenon.
- `Importance`: the reference Level column to be compared with other Levels criteria.
- `Action columns`: contains the action buttons for each row.

**Variable columns** are coming from PIRT configuration file and are called criteria. The criteria can be of different types: *Levels*, *Text* or *RichText*. Criteria of *Level* type will be compared with the level of the *Importance* column. Like the other criteria, *Levels* criteria are configurable. 

#### Phenomena Table actions:
- **Double-click**: opens a new dialog to display the group or phenomenon ranking information, see [Table Phenomena action buttons](#table-phenomena-action-buttons).
- **Quick Edit columns** are:
  - For phenomenon:
    - *Importance*
    - *All the criteria columns*

![image](uploads/a0209b4f33ed032a1916890766f417bf/image.png)

The criteria columns of *Levels* type are colored. Their color changes depending of the *Importance* column. All columns of *Level* type has a numerical value. The numerical value is used to be compared with the *Importance* column.

The difference is splitted in three level differencing colors. This colors are configured in the project configuration file.

`Importance Numerical Value* - *Level Criterion Numerical Value`:
- `>= 0`
- `= -1`
- `<= -2`

See [Generate Configuration for Credibility Project](https://gitlab.com/iwf/cf/wikis/Generate-Configuration-for-Credibility-Project) for more details.

#### Phenomena Table action buttons:

![image](uploads/14875d343672763000580c3f6c58cc32/image.png)

##### Add Phenomenon

![image](uploads/15c18f48be11537baa83403cc3c0be5b/image.png)

Opens a dialog to add a new phenomenon:

![image](uploads/5395ccffd76421a99bd95ed6707c97d8/image.png)

Fixed fields for phenomenon are:
- `Phenomenon group`: the phenomenon group to associate with.
- `Description`: this is the phenomenon label or name.
- `Importance`: the importance level of the QoI to compare with adequacy columns.

Fields under `Adequacy` are the configurable criteria columns to assess the QoI.

Text or RichText criteria fields will be added at the bottom.

##### View Phenomenon/Group

![image](uploads/de16b4fc6278fd48e5aa57031fb58ffe/image.png)

Opens a new dialog to display the group or phenomenon ranking information:

![image](uploads/e37d2da508555b05bde8c029ed6143e5/image.png)

##### Edit Phenomenon/Group

![image](uploads/323624f36d60bc5db39dfa11e78b0c1a/image.png)

Opens a new dialog to edit the group or phenomenon:

![image](uploads/149fe356a1155b690646c56eac1166b1/image.png)

##### Delete Phenomenon/Group

![image](uploads/228abf24424fa1536c026fe762035d06/image.png)

Deletes the group or phenomenon. A deletion confirmation dialog is opened on click:

![image](uploads/cba997e996be95dd51de796c00253d58/image.png)

If the user confirms deletion, the selected group or phenomenon is deleted with all the associated phenomena and criteria.


## Credibility, PCMM
[Go back to Contents](#contents)

PCMM is a tool to help analysts to classify evidence, and assess this evidence. This will be used to start discussion and detect lack in the process, not to assess the analysts.

PCMM also gives tools to summarize the assessments and display the state and progress of the model with an aggregated score for the different tested elements. This aggregation can be shown as a table or as a radar chart.

**PIRT is a prerequisite to PCMM**, please firstly fill in QoI and PIRT tables:

![image](uploads/847dc26469bc22b4841b55c5232f9f3f/image.png)

To start adding evidence and assessments, please select a role in the top-right corner drop-down.

#### Assessor and Role

**Assessor:** Every user can manage evidence and assess but it has to define his role before. The host machine user id is saved as the user id for the PCMM feature.
 
**Role:** The role is used to determine the implication and the credibility of the assessor. Roles are defined in the PCMM configuration file. To manage evidence and assess, each user needs to define his role in the PCMM. 

**Important rule**: A user can only give one assessment per role, but he can select another role to assess several times.

#### PCMM Mode 

The PCMM feature is available in different modes:
- **DEFAULT**: In this mode, the user can **assess** and **manage evidence** at the **subelement level**.
- **SIMPLIFIED**: In the simplified mode, the user **assess** and **manage evidence** at the **element level**. There is no subelements.

This mode is activated **depending of the PCMM configuration file** specified while creating the new credibility process. 

If the configuration file associates levels to the PCMM element, even if the PCMM element has subelements, the simplified mode is activated. Otherwise the default mode is activated.

This **mode can not be changed** once the credibility process is created.

#### Change PCMM Role

Each user can change his role. If the user wants to, he has to click in the `Role` dropdown list section in the top-right corner. The role dialog will be displayed. 

![image](uploads/4f0069f1c6cd238e1d0798db3f4095e1/image.png)

If not defined, the user is prompted to select his role when he tries to access to the evidence or the asses views:

![image](uploads/d86db9a73631e9add3025c68189a5969/image.png)

If the user cancels, the following message box is opened:

![image](uploads/e877f91d532067e663ac15db43c3b19d/image.png)

### PCMM Home View

The PCMM Home view displays the progress of the PCMM and the PCMM wheel with the different elements. 

The PCMM wheel text is adaptive to the view, by default the full PCMM element name is displayed:

![image](uploads/e769282ab99a060e94f81619ca3dcfae/image.png)

But if the available space for the wheel is too small, the PCMM element acronym is displayed:

![image](uploads/afd46051c38d402fdd4a8852b336449a/image.png)

#### PCMM Progress

This section displays the overall progress of the PCMM process including the PCMM Planning, Assess and Evidence phases.

![image](uploads/5b952111155f3cc6851cec721a1dd4fd/image.png)

The progress is indexed by PCMM element. The progress is divided in two major parts, the evidence and the assessment.

**In default mode:** The progress of each PCMM element is divided by the number of PCMM subelements of this PCMM element.

Once an evidence is added or an assessment is made on a PCMM subelement, the progress is marked as done.

For example, a PCMM Element has 5 subelements. The progress bar will be divided in 10 parts (5 for the evidence and 5 for the assessment).

**In simplified mode:** The progress is divided by two. Once an evidence is added and an assessment is made on an element, the progress is marked as done.

#### PCMM Tags

It is possible to tag the PCMM process to a specific state. A `Tags` part is available on the PCMM Home view, below the `Progress` one:

![image](uploads/655552de279ed3939438188ccfca1321/image.png)

##### New Tag

To create a new tag, click on the `Tag` button:

![image](uploads/6f349c1861ee90882907009ddb63cffe/image.png)

A new dialog will popup to enter the tag information:

![image](uploads/6b10f84b939437cd2d48232ea2334de0/image.png)

The plugin will inform the user of the success of the tag operation:

![image](uploads/ecae0cdf4ae8501700f4e772d38a16df/image.png)

##### Select a Tag

By default, the tag `Latest version (working)` is selected. It is the current working state of the PCMM process.

The user can select an existing tag in the `Tag` list:

![image](uploads/2b8279da81b983297efd39f304a4b1eb/image.png)

When a tag is selected, the associated evidence and assessments are loaded. All the PCMM views are available. But **the user can not alter** (create/update/delete) the evidence or assessments once tagged.

##### Manage Tags

To manage the existing tags, click on the `Manage Tag` button:

![image](uploads/ef7f9b3e89534a58330933f7ea8436ef/image.png)

A new dialog will popup with a table containing all the existing tags. It is possible to sort the table by each column:

![image](uploads/23fc7eeffd62a3850735684ec4739c95/image.png)

It is also possible to delete a tag by clicking the `Delete` button:

![image](uploads/069feacf9fa973e9160806d30815cae6/image.png)

or pressing **DEL** key on the keyboard:

![image](uploads/711a9330607a14facde919cca3091cb6/image.png)

The plugin will confirm the deletion success:

![image](uploads/6e3d453017a2255679c141007c54a2c5/image.png)

#### PCMM Wheel

The PCMM wheel shows the different PCMM elements in one chart. 

To start adding evidence and/or add assessment:
- **Click on a PCMM Element**, a contextual menu with two options will popup:
  - `Evidence`: manage evidence for this PCMM element. It opens the evidence view (see [PCMM Evidence View](#pcmm-evidence-view)).
  - `Assess`: manage assessments for this PCMM element. It opens the assessment view (see [PCMM Assess View](#pcmm-assess-view)).

![image](uploads/2f243d64698fd0aecfbe7f4cdbabb491/image.png)

**Important**: This sections requires a role to be defined for the user before it an be used. If there is no role defined, the user is prompted to select a role (see [Change PCMM Role](#change-pcmm-role)).

**Important**: The PCMM Element PMMF (Physics and Material Model Fidelity) needs to define PIRT quantities of interest first:

![image](uploads/a0dbba74bda5af4941633a0f0aa8c109/image.png)

#### PCMM Home Buttons

##### Back

![image](uploads/6288fc7442ceac731344769bfe8f41ff/image.png)

Return to home page (see [Home section](#home)).

##### Guidance

![image](uploads/e11ab8339eacd7eefb90c53e377a836b/image.png)

This button opens the `Credibility Framework Guidance` view with the PCMM Guidance in it. See [PCMM Guidance](#pcmm-guidance).

##### Information

![image](uploads/d86ad7c7cccc2251e923d45a9e75971b/image.png)

It opens the contextual help for PCMM Home view:

![image](uploads/6002de283a870c5952521dbdae4266ea/image.png)

##### Aggregate Individual Assessments

![image](uploads/a936d9ed13d1f4ea71eaf94bad482dba/image.png)

Open the aggregate view (see [PCMM Aggregate View](#pcmm-aggregate-view)).

##### PCMM Radar Plot/Quality Stamp

![image](uploads/a200957ee8ea35409971f4a3e82d9fba/image.png)

Open the PCMM stamp view (see [PCMM Radar Plot/Quality Stamp View](#pcmm-radar-plotquality-stamp-view)).

### PCMM Evidence View

The evidence view helps analysts to manage evidence. All evidence are available, but only the ones associated to the selected PCMM element can be managed. 

The user and role which created the evidence is referenced in the `User` and `Role` columns. It is useful to analyze the credibility of the evidence.

**In default mode:** The evidence is associated to the subelements:

![image](uploads/2740e7bd036e2e8bd77d494294409245/image.png)

**In simplified mode:** The evidence is associated to the elements:

![image](uploads/290f39574a6ff52abfb2a5f6089ba37c/image.png)

#### PCMM Evidence Table Columns:

- `File Name`: the file name or URL
- `Path`: the file path in the workspace or the URL
- `Description`: the user's description of the evidence
- `User`: the creation user
- `Role`: the creation role
- `Add`: add button to open the add evidence dialog
- `Open`: open button to open the evidence
- `Edit`: edit button to edit the evidence
- `Delete`: delete button to delete the evidence

#### PCMM Evidence Table Warnings and Errors

Some evidence may have an error or warning label:

![image](uploads/004d1979c605cda0002aaffa4f216094/image.png)

A tooltip is available to have further information about the warning or error:

![image](uploads/cdd80e0cb9a3677013609986d5936b0d/image.png)

This may occur if the evidence has the following problems:
- **Errors**:
  - The evidence file `doesn't exist anymore`
  - The evidence file is `already associated with the same PCMM element/subelement`.
- **Warnings**:
  - The evidence file is `already associated with another PCMM element/subelement`.
  - The evidence `file changed`. It is possible for the evidence creator to delete this warning message by clicking on the `Edit` button, check the **Remove "file changed" notification**, and click **Update**.
  
  ![image](uploads/6398bae7f7078edc4d6a8a6c2a8ba2ea/image.png)

#### PCMM Evidence Table Actions

![image](uploads/2c7cbc7af8ff5b712d3c8b57e8c94c9c/image.png)

##### Add Evidence

To add an evidence:

- **Click** on the `Add` button:

![image](uploads/9a24e67b0f5fd7b8a0722d2dc99094dd/image.png)

**Select/Browse** the `file` or `URL` and enter a `Description`:

![image](uploads/f7592981d7ac02e680d42642e177d643/image.png)

- **Drag and drop** a document from the Eclipse explorator to the destination subelement or element. If the element dragged is not valid or if the source is not valid, the interface will show the forbidden icon on the dropped item.

![image](uploads/3ac3415013eb075204d3e2cbd8ed8c7d/image.png)

**Evidence Type**: An evidence can be a file in the Eclipse project or a URL. To select the type, click on the radio button in top of the add evidence dialog:

![image](uploads/3f83e89a1c3eacf4b85735e7775c36ec/image.png)

![image](uploads/420f092aa0dd3eb5fba24f70a042e763/image.png)

##### Open Evidence

It is possible to open one or more evidence by:
- **Double-clicking** on the evidence row.
- **Selecting** the evidence to open and typing **Enter key** on the keyboard.
- **Selecting** the evidence to open and clicking the `Open` button:

![image](uploads/42e5fbcfad3ce662c45620df6f6d0efb/image.png)

By default, the evidence is opened by the Eclipse product. It is possible to change this behavior into the Eclipse  preferences (see [Eclipse File Associations](#eclipse-file-associations))

##### Edit Evidence

Only the user which created the Evidence can edit it.

To edit the Evidence description, click the `Edit` button:

![image](uploads/34c5643f296f9e75801fdd03046cc9e8/image.png)

A new dialog will popup with the **current errors and warnings** and the `Description`:

![image](uploads/3322fab802dd366cb60f4a5a10f97877/image.png)

Click `Ok` to save changes.

##### Delete Evidence

Only the user which created the Evidence can delete it.

To delete it, do the following:
- `Click` the `Delete` button:

![image](uploads/cfee80c98f67d44cc4a1a7acdebfe958/image.png)

- **Select** the evidence to delete and type **"Del" key** on the keyboard.

A confirm box will popup:

![image](uploads/75b18748d7eaaf2d4d19876a269a49ce/image.png)

#### PCMM Evidence Buttons and Actions

##### Back

![image](uploads/6288fc7442ceac731344769bfe8f41ff/image.png)

Return to PCMM Home page (see [PCMM Home View](#pcmm-home-view)).

##### Guidance

![image](uploads/e11ab8339eacd7eefb90c53e377a836b/image.png)

This button opens the `Credibility Framework Guidance` view with the PCMM Guidance in it. See [PCMM Guidance](#pcmm-guidance).

##### Information

![image](uploads/d86ad7c7cccc2251e923d45a9e75971b/image.png)

It opens the contextual help for PCMM Evidence view:

![image](uploads/5f2afc117663762452ade155f4a59c59/image.png)

### PCMM Assess View

The Assess view is used to evaluate each element and subelement depending of the evidence associated. The analyst is invited to take a role to assess.

An analyst can only assess once a subelement/element per role selected. The analyst has to change his role to assess several times.

__In default mode:__ The assessment are done on the subelement:

![image](uploads/8e70e77c380f28b94ed99e8ed8457dec/image.png)

__In simplified mode:__ The assessment are done on the element:

![image](uploads/a72a4ca913963cccf1e4f00430cea7c8/image.png)

#### PCMM Assess Table Columns:

- `ID`: the first column is for the PCMM element/subelement code
- `Element/Sub-element`: the PCMM element/subelement name
- `Level Achieved`: the assessment level 
- `Evidence`: the number of evidence
- `Open`: open button to examine the evidence
- `Comments`: An abstract of the assessment comment 
- `Assess`: assess button to open the assess dialog
- `Delete`: delete button to delete the assessment

#### PCMM Assess Table Actions

![image](uploads/9a413853b47da62e4b0470a7a639f259/image.png)

##### Open/Examine Evidence

![image](uploads/5aa9d5fe816a9a8819f58167e1044119/image.png)

To examine evidence click the `Open` button.

If there is **only one evidence**, the **evidence will be opened** inside or outside of Eclipse according to the preferences. For further information see section [Open Evidence](#open-evidence).

Otherwise, a **list of evidence dialog** will be opened containing all the added evidence for this PCMM element/subelement:

![image](uploads/65b0ce8037d141dc45c75b1cdb408ad4/image.png)

The user has different actions:
- `Open`: open the evidence
- `View`: view the evidence description

##### Assess/Edit

There is different ways to assess:
- **Double-click** on the PCMM element/subelement row.
- Click `Add` or `Edit` button:
  - **Add** assessment: by default if there is no assessment for this role and user:
![image](uploads/ba580c9edb9d6451a3b9bc946d05c146/image.png)

  - **Edit** assessment: present if there is already an assessment for this role and user:
![image](uploads/fd6ecb737a94acb435a35b7e05cb120f/image.png)

![image](uploads/a043097b464061258c62a0c3596eba0b/image.png)

The `Level achieved` and `Comment` fields are mandatory.

**Important Rule:** An analyst can only assess once a PCMM subelement/element per role selected. The analyst has to change his role to assess several times.

**Important Rule:** It is possible to assess every subelement/element, but it is only possible to give a the lowest level if there is no evidence associated to subelement/element. A warning message is displayed if the user tries to assess with a higher level than permitted:

![image](uploads/b9bd864c845dd73c4607d73b7c1d76ac/image.png)

##### Delete

![image](uploads/de27f8417b4dedbcd67b546e62ddccda/image.png)

The `Delete` button removes the assessment for this user and role. 

A confirmation dialog is opened:

![image](uploads/137d08af2274b25d7fcf8750cfc10eb2/image.png)

#### PCMM Assess Buttons and Actions

##### Back

![image](uploads/6288fc7442ceac731344769bfe8f41ff/image.png)

Return to PCMM Home page (see [PCMM Home View](#pcmm-home-view)).

##### Guidance

![image](uploads/e11ab8339eacd7eefb90c53e377a836b/image.png)

This button opens the `Credibility Framework Guidance` view with the PCMM Guidance in it. See [PCMM Guidance](#pcmm-guidance).

##### Information

![image](uploads/d86ad7c7cccc2251e923d45a9e75971b/image.png)

It opens the contextual help for PCMM Assess view:

![image](uploads/abc8bbe4836a2b6fb2421959cd7e6113/image.png)

### PCMM Aggregate View

The aggregate view aggregates all the assessment by subelement and element. It gives a score level to the element based on the level configured. 

**Tip**: There is as many levels as there are subelements/elements. A numerical code is stored on the level to have a correspondance between the subelement/element level and the aggregation level. This gives a numerical score for the aggregation. This numerical score is then converted to a level score.

__In default mode:__ The aggregation is made for the subelements and the elements.

![image](uploads/50b96bd10cd6424ae4d808f050677783/image.png)

__In simplified mode:__ The aggregation is only made for the elements.

![image](uploads/6fcaa6606493adc92d8061172f91393a/image.png)

**Warning:** If the PCMM overall progress is not complete, the plugin shows a warning to the user:

![image](uploads/88736fe4f36e240c559d3be86f9b3312/image.png)

#### PCMM Aggregate Table Columns:

- `ID`: the first column is for the PCMM element/subelement code
- `Element/Sub-element`: the PCMM element/subelement name
- `Level Achieved`: the aggregate level
- `Evidence`: the number of evidence
- `Open`: open button to examine all the evidence
- `Comments`: An abstract of the assessment comments
- `Details`: details button to open the list of assessment dialog

#### PCMM Aggregate Table Filter

![image](uploads/efbc712924c1e04de9dba3e45772e94b/image.png)

The aggregate list can be filtered by:
- `Role`: the role of the assessment creator

The aggregate table data will change depending of the filter selection.

#### PCMM Aggregate Table Actions

##### Open/Examine Evidence

![image](uploads/5aa9d5fe816a9a8819f58167e1044119/image.png)

To examine evidence click the `Open` button.

If there is **only one evidence**, the **evidence will be opened** inside or outside of Eclipse according to the preferences. For further information see section [Open Evidence](#open-evidence).

Otherwise, a **list of evidence dialog** will be opened containing all the added evidence for this PCMM element/subelement:

![image](uploads/65b0ce8037d141dc45c75b1cdb408ad4/image.png)

The user has different actions:
- `Open`: open the evidence
- `View`: view the evidence description

##### Details

![image](uploads/297d18795eacfa5be7e8de2710b1ca97/image.png)

The user can inspect all the assessments done with:
- **Double-click** the subelement/element.
- **Click** the `Details` button.

A new dialog is opened containing all the assessments per user and role:
![image](uploads/90ee57b8f1c93bc8f97c4f97979a26b4/image.png)

**Tip**: By default all assessments are expanded. But the list of assessments can be very long. It is possible to **collapse** an assessment by **clicking the title** of the assessment description:

![image](uploads/7a75dcb1194520ba8d0bd92e004510d6/image.png)

#### PCMM Aggregate Buttons and Actions

##### Back

![image](uploads/6288fc7442ceac731344769bfe8f41ff/image.png)

Return to PCMM Home page (see [PCMM Home View](#pcmm-home-view)).

##### Guidance

![image](uploads/e11ab8339eacd7eefb90c53e377a836b/image.png)

This button opens the `Credibility Framework Guidance` view with the PCMM Guidance in it. See [PCMM Guidance](#pcmm-guidance).

##### Information

![image](uploads/d86ad7c7cccc2251e923d45a9e75971b/image.png)

It opens the contextual help for PCMM Aggregate view:

![image](uploads/6b90e9d3dcf8ae3aa82dd328ec58dfe8/image.png)

### PCMM Radar Plot/Quality Stamp View

The PCMM Radar Plot/Quality Stamp View is the **aggregation and the average** of all the assessments over team members. 

In a **low rigor** ModSim project, the aggregation will be computed from the assessments done on each PCMM elements. 

In a **high rigor** project, the aggregation is firstly computed at the PCMM subelement level, then computed from the result of the subelement aggregation to the PCMM element.

![image](uploads/b457b7b753d76f649a5be1563b3afe6e/image.png)

**Warning:** If the progress is not complete, the plugin shows a warning:

![image](uploads/35a3d6f5dda37571aff5c222bea1bfcf/image.png)

#### PCMM Stamp Filter

![image](uploads/050a9179f47fe3547579a0b9d05fdbdc/image.png)

The aggregate list can be filtered by:
- `Role`: the role of the assessment creator

The PCMM stamp data will change depending of the filter selection.

#### PCMM Stamp Buttons and Actions

##### Back

![image](uploads/6288fc7442ceac731344769bfe8f41ff/image.png)

Return to PCMM Home page (see [PCMM Home View](#pcmm-home-view)).

##### Guidance

![image](uploads/e11ab8339eacd7eefb90c53e377a836b/image.png)

This button opens the `Credibility Framework Guidance` view with the PCMM Guidance in it. See [PCMM Guidance](#pcmm-guidance).

##### Information

![image](uploads/d86ad7c7cccc2251e923d45a9e75971b/image.png)

It opens the contextual help for PCMM Aggregate view:

![image](uploads/ea7bb0a0942bf048db648ff722769e4c/image.png)



## Communication
[Go back to Contents](#contents)

This feature main goal is to stow the documents associated to the other features and make it available for peer review and discussion about the current Credibility process.

The following reference is managed:
- [Credibility Report](#credibility-report)

The following references will be managed in the future:
- Credibility Evidence Package (Overview)
- Credibility Evidence Package
- Plausible Margin Bounds
- Peer Review

### Credibility Report

To access the report view, click on the `Credibility Report` tile on the Home view:

![image](uploads/80077f15adfc35afa933d53da42120f1/image.png)

The Report home view appears with its different sections:

![image](uploads/1fe69f7eb3f176b24ba49578c46470e1/image.png)

**Important**: the Credibility Report feature is using ARG (Automati Report Generator) library to create the reports. Please see [How to install ARG](How-to-install-CF-plugin#arg) page.

#### ARG Setup

This section describes the ARG setup including the executable path, the python pre-script path and python command.

![image](uploads/a08ac508c51a1f2826dc171da7e315a9/image.png)

- `ARG executable path`: the location of the ARG executable. This is mandatory and setted empty by default.
- `ARG setenv script path`: this field locates the environment script to execute before generating the report. Setted empty by default, this script is optional.
- `Use local configuration from Preferences`: by default, the configuration is saved into the `<filename.cf>` file. But it is possible to overwrite the file configuration by the local one with this option. The configuration has to be setted directly into the plugin Preferences.

##### Open Preferences Button

![image](uploads/a810c6226a81836f7ddceaba6da7e302/image.png)

This button opens the local Preferences. See [Preferences](#preferences) to have furthermore informations about the local configuration.

#### ARG Parameters

This section is used to configure ARG and the report generation:

![image](uploads/f2092a6197deb4922d245f1f83fa46e3/image.png)

- `Parameters File`: the parameters file stores the report options like the filename, the report title, the author... This file is **generated by CF** to be executed by ARG. CF variables can be used to define the value (see [Variables](#Variables)). By default the `<filename.cf>` folder path is setted.
- `Structure file`: the structure file stores the report data. The data to be included are defined in the other sections Planning, PIRT and PCMM. This file is **generated by CF** to be executed by ARG. CF variables can be used to define the value (see [Variables](#Variables)). By default the `<filename.cf>` folder path is setted.
- `Output folder`: the generated report location. By default the `<filename.cf>` folder path is setted. This field can not be empty.
- `File name`: the report file name. By default `<filename.cf>-Report`. This field can not be empty.
- `Report title`: the report title. By default `<filename.cf> Report`. This field can not be empty.
- `Author`: this is the author name. By default empty into CF. If empty, the system user name is used. This field is optional.
- `Backend type`: this is the backend type. The ARG installation defines the different types. The minimal backend types are `LaTek` and `Word`. By default `Word` is filled. This field can not be empty.
- `Inline Word documents if possible`: this option allows to inline all the document mentioned or associated to the CF features (e.g. PCMM evidence, links...). This option is experimental and only available with the `Word` backend. It needs ARG 1.1.7 at least. To enable inlining go to [Preferences Developer Options](#developer-options).

#### Planning Parameters

This section allows to select the Planning data to embed into the report. The following mini-tools are available:
- System Requirements
- Uncertainty Inventory
- Analyst Decisions

The QoI Planning and PCMM Planning mini-tools are available directly within the associated feature respectively PIRT and PCMM.

![image](uploads/32b2c2c3157f74081c1cded6dfbce4b9/image.png)

#### PIRT Parameters

The PIRT Parameters section lets select the PIRT data to include into the report. Each Quantity of Interest can be added/removed with its associated tag (into the combobox).

![image](uploads/1a5ae5ee5412d67ab48f7aff11bbcefb/image.png)

#### PCMM Parameters

The PCMM Parameters section permits to include the PCMM data into the report. The PCMM tag to include is setted by default to the working version.

The PCMM features can be selected:
- PCMM Planning defintion
- Evidence with their associated documents
- Assessments

![image](uploads/a4bbf2f5202f1738b8173898e65ca6b0/image.png)

#### Custom Report Ending

Custom Report Ending is an optional feature to allow to append some content to the generated report.

This content has to be added as an ARG core structure file `.yml` containing at least one `chapters` tag. Please see [ARG documentation](https://automaticreportgenerator.gitlab.io/arg/) for furthermore informations.

![image](uploads/1faf1d800f33461e90650507e87029a5/image.png)

The content will be added to the end of the report:

![image](/uploads/704186f26099ca50a1e68538b5dd190d/image.png)

#### ARG Console

The ARG console is used to display the ARG execution logs:

![image](uploads/aaf175c77c44ff2dc8dc038f32d0a848/image.png)

#### Document Inlining

CF uses ARG to inline evidence and references documents.

- **Images** are automatically inlined as images into the final report:

![image](/uploads/a5406c6f34b08fbb1939b0090bcb4868/image.png)

A caption can be added directly in the user interface. If the caption is left empty, the file name will be used:

![image](/uploads/1ae4e781e3944aec483429cf3b228434/GifMaker_20220106170329052.gif)

- **Word** documents is still in a bêta mode. This option can be activated for PCMM evidence in the [Preferences - Developer Options](#developer-options) and on the Report view [ARG Parameters - Inline Word document](#arg-parameters) section.

#### RichText rendering (bêta)

Some of the CF features have RichText editors available:

![image](uploads/df89342ce8c0c9f36563fa42a12e69b3/image.png)

ARG is able to render the RichText editors content (based on html) in the final report:

![image](uploads/ccea52d93c7a9c2353f95b8ff367e9f4/image.png)



## CF Project Configuration
[Go back to Contents](#contents)

![image](uploads/108e6e8f5bed2ecc96ea6a180fc48051/image.png)

In the top right corner of each page, the `Configuration` button allows to access CF configuration options:

![image](uploads/657c2aee28f2261fd6b26b14da1c327e/image.png)

The configuration has two tabs:
- Import Configuration
- Export Configuration

![image](uploads/a6e6da08ca8faacb327cfe18f162b4ae/image.png)

### Import Configuration

This tab permits to change the current configuration of each CF feature. It is used to enable/disable CF features too.

![image](uploads/657c2aee28f2261fd6b26b14da1c327e/image.png)

Each CF feature can be imported. It contains two buttons:

#### Browse

![image](uploads/ad308359db41dbb1141c821f655c83df/image.png)

The browse button lets select the `.yml` configuration file for the desired feature.

#### Import

![image](uploads/990b9d5d5aa911e024bf350cf4a3938b/image.png)

The `Import` button opens a new dialog. This dialog compares the current configuration with the new one. The user can select the changes to apply. To apply the changes, click on `Confirm Import`:

![image](uploads/91cff13a40f7e2b922938e999ce92466/image.png)

**Important**: if the user decides to delete an existing field, all the data associated will be deleted.

**Tip**: Once confirmed, the changes will be applied to the current CF project. The changes are made locally but not saved until the user saves the CF editor. To revert a change in the configuration, close the current CF editor without saving. 

### Export Configuration

This tab is used to export the current configuration of each CF feature.

![image](uploads/bfcfefabeb5c6c20ceec1e409e620f0c/image.png)

Each CF feature can be exported. It contains two buttons:

#### Browse

![image](uploads/ad308359db41dbb1141c821f655c83df/image.png)

The browse button lets select the output `.yml` configuration file for the desired feature.

#### Export

![image](uploads/662200e15aa6cc5a277039fc35611365/image.png)

The `Export` button create a new `.yml` file with the configuration of the selected feature. This file can be used to create a new CF file. A confirmation message pops up:

![image](uploads/c0db3dbef6d4397ed0e5478f88e7719a/image.png)

**Tip**: use this option to copy a CF project file without data



## Credibility Framework Guidance view
[Go back to Contents](#contents)

A guidance view called `Credibility Framework Guidance` is available within the CF plugin. 

The guidance view is a separated Eclipse view and can be moved into the Eclipse plugin or outside:

![image](uploads/7e25bfc096f514b3d38a96c534b839fa/image.png)

![image](uploads/92ca7c333e4c7ed98614babfceedcb5d/image.png)

The guidance view also changes according to the credibility process opened and the current view (PIRT, PCMM,...) chosen by the user. The view is opened once for all the credibility process and if closed, disappears for all processes.

The currently implemented guidelines are:
- [PIRT Guidance](#pirt-guidance)
- [PCMM Guidance](#pcmm-guidance)

### Open the Credibility Framework Guidance view:
It can be opened by:
- clicking the `Guidance` button on each view:

![image](uploads/e11ab8339eacd7eefb90c53e377a836b/image.png)

- within **Eclipse menu**:
  - click `Window > Show View > Others...`:

  ![image](uploads/04dee089f92e2e32f0a3033967dacc56/image.png)

  - then select `Credibility Framework Guidance` under `Credibility Framework`, and click `Open`:
  
  ![image](uploads/0142ce26680e11eabb767cf4932bfde7/image.png)

### PIRT Guidance

The PIRT Guidance is separated in two sections:
- `PIRT Guidance Levels`: displays the PIRT Guidance for each adequacy column and level. This comes from the PIRT configuration schema file.
- `PIRT Level Difference Colors`: lists the difference colors to help the user to fill in the PIRT table.

![image](uploads/d87bb699cea64ed60ebdd940a0f6bc3c/image.png)

### PCMM Guidance

The guidance level view displays the description of the levels of each subelement/element to help the user to assess:

By default, the PCMM level view is shown in columns:

![image](uploads/186d1d00ae440f1156d524b546e8e415/image.png)

But if the available size is less than 500 pixels, the PCMM level view is shown in row:

![image](uploads/59e8391ffc3aeceda78ad15d191f68ac/image.png)

To increase the readability of this table, a tooltip is available with the full content of the row:

![image](uploads/f941870721ec2d973b1ae30d9489d40d/image.png)


## Concurrency Support

### Enable Developer Option

To enable concurrency support into CF plugin, enable the option into Window > Preferences > Credibility Framework > Developer Options:

![image](uploads/350f56f70b81ced0e151681f28216c0f/image.png)

### Create a new web project

You will be able to create an existing project with the "New Credibility Process" wizard:

- Right click the project and select New > Other...:

![image](uploads/bed2e12ed3cad9b7b38ea0cac15faf96/image.png)

- Select Credibility Process:

![image](uploads/a2d27ee89ab9e58fbbff47686a150687/image.png)

- Enter the credibility process name:

![image](uploads/cb849f77b306b479c7b55f84d7c99eb8/image.png)

- Select the project type (Web):

![image](uploads/01e1be432f0515c26c0de7ff26dbca5c/image.png)

- Enter the server URL and click "Test Connection":

![image](uploads/46df00614dabfe5d0e0c07f9f4c245ec/image.png)

- Select "New Web Project":

![image](uploads/b74daef28b434be1d93daac2a699e44f/image.png)

- Enter the project information and click "Finish":

![image](uploads/90973975e276ddac46aa7f31fa05f554/image.png)

- The project will be created and will be prompted to enter your credentials, just click "Connect":

![image](uploads/e085af25a6b00a62bfd5e9c2d8a45ce3/image.png)

The web project is open:

![image](uploads/39d835be2aa989c7cdb46fd317220e0f/image.png)


### Connect to an existing web project

You will be able to connect to an existing project with the "New Credibility Process" wizard:

- Right click the project and select New > Other...:

![image](uploads/bed2e12ed3cad9b7b38ea0cac15faf96/image.png)

- Select Credibility Process:

![image](uploads/a2d27ee89ab9e58fbbff47686a150687/image.png)

- Enter the credibility process name:

![image](uploads/cb849f77b306b479c7b55f84d7c99eb8/image.png)

- Select the project type (Web):

![image](uploads/01e1be432f0515c26c0de7ff26dbca5c/image.png)

- Enter the server URL and click "Test Connection":

![image](uploads/19f1d6e8dc23981823ba85e527175319/image.png)

- Select "Existing Web Project":

![image](uploads/2821a566cefe9f6190b39aad378822ad/image.png)

- Select the project and click "Finish":

![image](uploads/ea1ae3e90098da83a3c457477b49b159/image.png)

- The project will open and you will be prompted to enter your credentials, just click "Connect":

![image](uploads/ae83b4b5cc01ff8535ce6fb4f6e98ea7/image.png)

The web project is open:

![image](uploads/39d835be2aa989c7cdb46fd317220e0f/image.png)


## Eclipse File Associations
[Go back to Contents](#contents)

By default, files are opened by the Eclipse product. It is possible to change this behavior into the Eclipse preferences:

In the toolbar, Go to `Window > Preferences`:

![eclispe-pref](uploads/5797e280fb189de42d75ea08e72fe7d3/eclispe-pref.png)

Then in the left menu, go to `General > Editors > File Associations`:

![eclispe-pref-screen](uploads/4c4dabdfa9c720d836f3c3dad329330b/eclispe-pref-screen.png)

In this screen you can add unknown file extensions and associate them to an editor (internal or external).
It is also possible to associate the editor by Content-Type. For that, Go to `General > Content Types`:

![eclispe-pref-screen2](uploads/0876713f86e865fe02991785ce178178/eclispe-pref-screen2.png)



## Eclipse Web Browser
[Go back to Contents](#contents)

Eclipse has its own preferences to open a web page or a link. 

![image](uploads/d0618e096ef0e0e828973eb5ef20ff4e/image.png)

To change the Eclipse behavior then follow the below steps:
- Go to `Windows` -> `Preferences` if on Windows OS (`Eclipse` -> `Preferences` on Mac OS X)
- Go to `General` -> `Web Browser`

Here you would see the Default system web Browser being selected. Click on New and Select your browser (Safari, Firefox, Chrome or IE) from Applications.

- Click `OK`


[Go back to the top of the page](#content-body)
