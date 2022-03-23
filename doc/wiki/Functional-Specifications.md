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

![image](uploads/49a292cb60a565260d5284bf4b3c10de/image.png)

- `Display Credibility Framework Version Number`: If set to true (default value), this option displays the version number in the bottom left corner of the Home view.

![image](uploads/f9d53a83a7b0df02ca01dbaf6095d4bf/image.png)

- `Display Credibility File Version Origin Number`: If set to true (default value), this option displays the CF file version origin number in the bottom right corner of the Home view.

![image](uploads/1ccdd6a74b0d7ad3a133fc9a50caeb5e/image.png)

- `Python executable path`: to execute python scripts (used for reporting)
- `ARG executable path`: the ARG installation path (used for reporting)
- `ARG setenv script path`: the python preexecution scripts (used to set ARG environment before report execution)

- Open link with (Evidence, References...): this preference is used to open link into a web browser

### Developer Options

![image](uploads/22ec0cbb209322c00a96b2fadab0c4e7/image.png)

- Enable `Inline Word Document` option in the Report View: enable the word document inlining option into the [Report View](#credibility-report). The option will be available here:

![image](uploads/4f85457eac98a71406c8887fa2e762e2/image.png)

- Enable `Concurrency Support` allows the CF plugin to create/open web projects and connect to a remote server

## Getting Started
[Go back to Contents](#contents)

The user must be able to create a new Credibility Process **from an existing modSim project**. CF can be associated with any point of an eclipse project. 

For example, a project may have multiple ModSim models within a deeply hierarchical folder structure. Conceptually an analyst would navigate to a particular model to create a CF process.

If you don't have an existing Eclipse project please create one with section [How to create a new Eclipse project](#how-to-create-a-new-eclipse-project). Otherwise, you can go to section [New Credibility Process](#new-credibility-process).

### How to create a new Eclipse project

If you don't have an existing project, create one:
- In the menu, click on `File > New > Project`:

![image](uploads/1d798e43359a58e9e0c7f9beab0ffc49/image.png)

- Select `General > Project`:

![image](uploads/194372b2eae73129a56922548a5e8a3f/image.png)

- Enter a **name** for your project and click `Finish`:

![image](uploads/58dbeb136928007f76d55f686725d689/image.png)

### New Credibility Process
[Go back to Contents](#contents)

Create the Credibility Process for an existing Eclipse project:

- **Right-click** on the desired project, the plugin should provide a credibility creation wizard. The plugin must use Eclipse current interface to create a new project.

![image](uploads/f97daab3ef8bb1ea19f21550bac63f46/image.png)

- Select `Credibility Process` under `Credibility Framework` section:

![image](uploads/a5aec002c9321d5ad69303f2f717071b/image.png)

During creation, the interface should ask the user to complete the following information:
- `Parent folder`: the file location in the project explorer.
- `File base name`: the name of the credibility process (file extension will be `<filename>.cf` and is automatically generated).

![image](uploads/9c5365fa6b1882de3ba2378d4f5f0390/image.png)


#### Credibility Setup

The next window will ask for the configuration of the credibility process. This is related to[Configuration](#Configuration) section. 

This dialog has two different pages:

- `Simplified`:

Opened by default, the configuration directory location will be asked. The folder will be parsed searching for configuration files. By default, the found files will be automatically associated to the corresponding feature:

![image](uploads/cc561da7d70c5b36b2ce5cb3efc196fe/image.png)

If there is multiple files available for one feature, a drop-down combo will be displayed with different options:

![image](uploads/17691a0d2a09e5ca56f83deb8a0bc326/image.png)

- `Advanced`:

All configuration files location will be asked:

![image](uploads/d89cf10975cb5ec1557a0cfaab60ee0b/image.png)

- `Generate Credibility Evidence Folder Structure`: this checkbox is checked by default. It enables the generation of the default Credibility Evidence Folder Structure at the same location as the .cf file. See section [Credibility Evidence Folder Structure](#credibility-evidence-folder-structure) for further information.


Just after creation, the new project wiil be opened in a new editor.

The new credibility project data are stored in a database inside the  `<filename>.cf` file(see [Technical Specifications](#technical-specifications)).

### Credibility Evidence Folder Structure
[Go back to Contents](#contents)

This option enables the generation of the default Credibility Evidence Folder Structure at the same location as the `<filename>.cf` file. This folder structure is useful to store the different evidence needed for PCMM. If a folder already exists, it will not be replaced or deleted.

- **Right-click** a project or folder in the project explorer and select `Credibility Evidence Folder Structure`, then `Next`:

![image](uploads/c36680fe07d618d1772074e1a4bdb39f/image.png)

- Select the **location** and click `Finish`:

![image](uploads/2b0eeb728aa59f556a128c5d1aab3ed2/image.png)

The folder structure will be generated under the location you selected before.

### Open Existing Credibility Process
[Go back to Contents](#contents)

To open an existing credibility process, the user should **double-click on the** `<filename>.cf` **file** under the desired modSim project in the Project Explorer. The plugin will open a new editor with the Home View.

### Save Credibility Editor

The credibility editor manages a temporary folder to store the processing data. Every modification is instantly saved in this temporary folder but not in the `<filename>.cf` file. 

#### Save your modifications

After each change, the editor indicates there is unsaved data by adding a `\*` beside the editor name and with a yellow label `Not saved` in the top left corner:

![image](uploads/5a52fc64d92eed5a9a779ac6d49f4818/image.png)

The user has to save the editor to keep the modifications across sessions by clicking the `Save` button in the Credibility Editor:

![image](uploads/99920c7594e53ae20a7db14db48fe5ac/image.png)

Or by clicking in the Eclipse menu `File > Save`:

![image](uploads/469291df0ec9f17bb79a3131ac52952b/image.png)

If the user **closes the editor with unsaved modifications**, the editor **prompts the user to save** or quit the editor:

![image](uploads/4a9a9fd86804aee7f8b63b38fb3ec490/image.png)

The editor is saved once the `\*` character disapears aside the file name and when the green `Up to date` label is displayed in the top left corner:

![image](uploads/73a3c04b8ffd23e0ef906bbcf7156c65/image.png)

#### Recover after a crash

If the Eclipse based application crashed, a temporary folder will survive in the project folder. 

When the user opens the `<filename>.cf` file, if the folder is in a recoverable state, the plugin asks the user to recover data or not:

![image](uploads/8a29ca5eafadb1c980eefb5f18864c4f/image.png)

If the folder is not recoverable, the plugin will delete it and open the `<filename>.cf` file data.

### Navigation
[Go back to Contents](#contents)

From all views (except the Home View), the user can navigate by clicking on an element in the `breadcrumb` on top of the view:

![image](uploads/6e374b61e7d40b391982eccd42216446/image.png)

The `breadcrumb` and title changes depending of the current view. The current view item is selected (grayed out).

![image](uploads/0236397ddf63fac66faac89f6d645a8e/image.png)

![image](uploads/50be70f549acdd6b6a05459a19346504/image.png)

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

![image](uploads/6c8b80e7b34291783ae1c1d512a2bec5/image.png)

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

![image](uploads/66ebf57a7f544c052eb263c74f088c44/image.png)

### PIRT card

The PIRT card contains a description of the PIRT feature, Quantities of Interest and the PIRT tables. 

This card has some indicators:
- `Quantities of Interest`: This indicator displays the number of Quantities of Interest created in PIRT.

The buttons trigger the following actions:
- Click on the `PIRT table`: Opens the PIRT home view (see section [Phenomena, PIRT](#phenomena-pirt))
- `Reference`: Opens a PDF document that describing the PIRT feature
- `Open`: Opens the PIRT home view (see section [Phenomena, PIRT](#phenomena-pirt))
- `Informations`: Opens the contextual Help to describe PIRT:

![image](uploads/373f445302c9925214f847391b8a6a0e/image.png)

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

![image](uploads/5648406bf7b6af2d536de93b104bee8a/image.png)

### Communication card

The Communication card contains the tools to communicate around the project. 

The only available tool is [Credibility Report](#credibility-report). See [Communication](#communication) section for furthermore informations.

The buttons trigger the following actions:
- Click on the card: Opens the associated feature
- `Informations`: Opens the contextual Help to describe the card:

![image](uploads/f7361b1ffd309478b12557a5d30a1b93/image.png)

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

![image](uploads/bbf231c7f4ec053970a3f81170f598b9/image.png)

### System Requirements

The System Requirements goal is to enumerate and describe the initial requirements of the project. This is a **prerequisite to Quantities of Interest**.

![image](uploads/97ed1c55d20c83b8d12608a533159e57/image.png)

There is no limit in the number of groups, and sub-requirements:

![image](uploads/403b176fa4386528aecf7f83b3550363/image.png)

#### System Requirements Buttons and Actions

##### Add Requirement Group

![image](uploads/fd3d25c79d8d28c287664a1c083f0bae/image.png)

This button opens a new dialog to add a requirement group. The configured fields will be available into the dialog:

![image](uploads/4307be61c49b5a9dc0f48833066dada1/image.png)

##### Back

![image](uploads/bfb6dbe6d1cfd61698b50ef3af18f64a/image.png)

Return to Home page (see [Home View](#home-view)).

##### Information

![image](uploads/06f5c8848621e8713894df2bb4f2e5e3/image.png)

It opens the contextual help for Requirements view:

![image](uploads/3b3043f7d8b5b82c49ab709e16e60810/image.png)

#### System Requirements Table Columns

- `ID`: the logical ID generated by CF
- `Statement`: the requirement statement used to identify the requirement

The other columns are managed with the configuration files and can be removed:
- `Type`: the type of requirement
- `Factor of Safety`: a numerical indicator of the safety required
- `Verification/Acceptance method`: the level of rigor for the verification 
- `Source Requirement Reference`: the document to give more details about the requirement

#### System Requirements Table Actions

![image](uploads/49b2137d4465ada5c6b110dd757712e4/image.png)

##### Add System Requirement

The user can add a requirement under an existing requirement group or a requirement by clicking on the `Add` button:

![image](uploads/a9216d12b2106183fd8b005294ea1acc/image.png)

A new dialog will popup and ask the user to input the necessary informations:

![image](uploads/6a303a85d88aee05bf61ba337a0b757b/image.png)

##### Open System Requirement References

The `Source Requirement Reference` field stores a link to the source document associated. Clicking on the `Open` button will open this document (url, document inside the workspace):

![image](uploads/06b9d30c67f6f4efc9f9b2a07c3fb5e2/image.png)

##### View System Requirement

It is possible to view the requirement informations by clicking on the `View` button:

![image](uploads/fa145cd49dd8e57346e0e575ab882e1b/image.png)

A new dialog will popup with the requirement informations in read-only mode:

![image](uploads/513e8737dac54a21ce7996b1c07247a3/image.png)

##### Edit System Requirement

To edit the requirement informations click the `Edit` button:

![image](uploads/2bad758ee684c248b0a5d9b2af246b82/image.png)

A new dialog will popup in edition mode:

![image](uploads/e019f1397bd8ecd7bbe0fc3da7e1854a/image.png)

Click `Update` to save changes.

##### Delete System Requirement

To delete a requirement click the `Delete` button:

![image](uploads/736a12bafe0225af1bc6f44872e0cd2f/image.png)

A confirm box will popup:

![image](uploads/063e8190e81722ea5b818e15892d841c/image.png)

All the requirements and sub-requirements associated will be deleted.

### QoI Planner

This view lists all the quantities of interest associated to the current credibility process. The intended goal of this tool is to define quantities of interest and to configure their limits and conditions.

The definition of the PIRT tables will be done later in PIRT section (see [QoI Home View](#qoi-home-view)).

System requirements defintion is **a pre-requisite** to QoI Planner.

![image](uploads/80c44a1d376d282cca9ec6b316efb784/image.png)

#### QoI Buttons and Actions

##### Add Quantity of Interest

![image](uploads/ac26373dd6d8efeb2bfe83f3e1f7a669/image.png)

This button opens a new dialog to add a quantity of interest. The QoI planner fields will only be available in this tool. The PIRT view of the QoI is more PIRT table oriented. The fields are configurable with the configuration process:

![image](uploads/bd91cd970ed338ed0da65165966b8756/image.png)

The **QoI name must be unique.**

##### Back

![image](uploads/bfb6dbe6d1cfd61698b50ef3af18f64a/image.png)

Return to Home page (see [Home View](#home-view)).

##### Information

![image](uploads/3122311027699f6f40b6ed9c2063021a/image.png)

It opens the contextual help for QoI Planner view:

![image](uploads/3420abba2056f1836198d7c368042a4b/image.png)

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

![image](uploads/4a1fff9e7cb2323df80e2669bb47e6c1/image.png)

##### View Quantity of Interest

It is possible to view the QoI informations by clicking on the `View` button:

![image](uploads/1731be60956677d4d67a6e7de00a4657/image.png)

A new dialog will popup with the QoI informations in read-only mode:

![image](uploads/a30c335628f43c9f716d63c7c585899b/image.png)

##### Edit Quantity of Interest

To edit the QoI informations click the `Edit` button:

![image](uploads/e889b23d5f2a6345002ff6238a8c634f/image.png)

A new dialog will popup in edition mode:

![image](uploads/e058ec5d93214c220946ad828496c729/image.png)

Click `Update` to save changes.

A tagged QoI is only visible and can not be edited.

##### Copy Quantity of Interest

The user can make a copy of the QoI by clicking on the `Copy` button:

![image](uploads/201c05cac68f9d6ccc3371e7a2aa48fb/image.png)

A confirmation box will popup, click `Ok`:

![image](uploads/f09d3305b834c44650b5aa9c2a693ed2/image.png)

A new dialog will popup and ask the user to input the necessary informations:

![image](uploads/3374aa0c557d44aa76fa6af0e91a9d14/image.png)

The **QoI name must be unique.**

A tagged QoI can not be copied.

##### Tag Quantity of Interest

The user can tag a QoI. A tag is a copy that can not be edited. It is used to kept the state of a QoI. It helps the user to see the progress and take decision. Each associated phenomenon groups, phenomena and criteria will be copied and tagged (see [Tag QoI](#tag-qoi)). 
To tag a QoI click on the `Tag` button:

![image](uploads/63e6ed55b1358e80d3dd0df2861d3d05/image.png)

A confirmation box will popup, click `Ok`:

![image](uploads/63f771655ae4fb79118e62a12790130f/image.png)

A new dialog will popup asking the user to input a description for the tag:

![image](uploads/e0bb4264a20aaf560852dcffab9e3f76/image.png)

A succes box will be displayed to confirm the tag operation:

![image](uploads/3e20262ca591362d3907a92049b19a86/image.png)

A tagged QoI can not be tagged again.

##### Delete Quantity of Interest

To delete a QoI click the `Delete` button:

![image](uploads/b68a94d0b71ac40af1988bef41855edd/image.png)

A confirm box will popup:

![image](uploads/c583c88db2b0e23ddd6940b86898ce8c/image.png)

If the user confirms deletion, the selected QoI is deleted with all the associated definition, ranking, phenomenon groups, phenomena and criteria. The associated tags will be deleted too.

### Uncertainty Inventory

Uncertainty Inventory is a tool to define the known uncertainties of the current modSim and to associate its references. The current view lets you add groups and uncertainties.

![image](uploads/b846575a207016d21388eb635edbc619/image.png)

#### Uncertainty Buttons and Actions

##### Add Uncertainty Group

![image](uploads/e1033f061caaf0e3567b7c939f788e48/image.png)

This button opens a new dialog to add an uncertainty group:

![image](uploads/fb6cdf56aadb995cc7de813f231c6d12/image.png)

##### Back

![image](uploads/bfb6dbe6d1cfd61698b50ef3af18f64a/image.png)

Return to Home page (see [Home View](#home-view)).

##### Information

![image](uploads/84aec3b741cf7c3a33248704c032fdd5/image.png)

It opens the contextual help for Uncertainty view:

![image](uploads/8b41683e0de2a2dd91a4c3150ad762ef/image.png)

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

![image](uploads/2b9e0f2db8a4d6bde2ad62053cb4c267/image.png)

##### Add Uncertainty

The user can add an uncertainty under an existing uncertainty group by clicking on the `Add` button:

![image](uploads/4daee41e1f692aa1cd5d46683aec636b/image.png)

A new dialog will popup and ask the user to input the necessary informations:

![image](uploads/0c2f16765518c8c69b63e5bfc96551ff/image.png)

##### Open Uncertainty

The `References` field stores a link to a document associated to the uncertainty. Clicking on the `Open` button will open this document (url, document inside the workspace):

![image](uploads/bbca3ba476df7dd397edece12bd9712d/image.png)

##### View Uncertainty

It is possible to view the uncertainty informations by clicking on the `View` button:

![image](uploads/6d630592e854a8c20fed882159908aa4/image.png)

A new dialog will popup with the uncertainty informations in read-only mode:

![image](uploads/360ba554004f58ea29a866df099d3447/image.png)

##### Edit Uncertainty

To edit the uncertainty or group informations click the `Edit` button:

![image](uploads/6ccd842a5f7b607b505deca805a5c857/image.png)

A new dialog will popup in edition mode:

![image](uploads/c6bd346ff329e7ba647c87b5cbbffec4/image.png)

Click `Update` to save changes.

##### Delete Uncertainty

To delete an uncertainty or a group click the `Delete` button:

![image](uploads/7ffef679c92150b32739b765b3a78c23/image.png)

A confirm box will popup:

![image](uploads/1b958c8eab80601a164ee995e449aac1/image.png)

For a group, all the associated uncertainties will be deleted.


### PCMM Planning

PCMM is a tool to help analysts to classify and assess evidence. The planning tool's goal is to prepare further  discussions and describe each element and/or sub-element with questions and predefined fields.

![image](uploads/27a5be611f56ce688b9f6d070f36dd9e/image.png)

#### PCMM Wheel

The PCMM wheel shows the different PCMM elements in one chart. 

To start planning, **click on a PCMM Element**.

![image](uploads/75fdb443cfca0d322bf9e6fd213bd54a/image.png)

**Important**: This section requires a role to be defined for the user before it an be used.

If there is no role defined, the user is prompted to select a role (see [Change PCMM Role](#change-pcmm-role)).

**Important**: The PCMM Element PMMF (Physics and Material Model Fidelity) needs to define PIRT quantities of interest first:

![image](uploads/6cf43332652107b7be84a0a2f38ea793/image.png)

#### PCMM Planning Progress

This section displays the overall progress of the PCMM process including the Planning, Assess and Evidence phases.

See [PCMM Progress](#pcmm-progress) for furthermore informations.

#### PCMM Planning Tags

A tag saves the state of the PCMM process at one point. By default, the tag `Latest version (working)` is selected. It is the current working state of the PCMM process.

A `Tags` part is available on the PCMM Home view, below the `Progress` one:

![image](uploads/d0bd100689eb6b4e266feeccd592ddc2/image.png)

Different actions are available for the tags. See how to:
- [add a new Tag](#new-tag)
- [select an existing Tag](#select-a-tag)
- [manage existing tags](#manage-tags)

#### PCMM Planning Buttons and Actions

##### Role

In the top right corner, the user has to choose a role to start planning (see [Change PCMM Role](#change-pcmm-role)).

![image](uploads/5a4886e5c1745e1c5258e758e174c133/image.png)

##### Back

![image](uploads/bfb6dbe6d1cfd61698b50ef3af18f64a/image.png)

Return to Home page (see [Home View](#home-view)).

##### Guidance

![image](uploads/2dc9d990372c821da1b88776a1f832bb/image.png)

This button opens the `Credibility Framework Guidance` view with the PCMM Guidance in it. See [PCMM Guidance](#pcmm-guidance).

##### Information

![image](uploads/814fb9d46a32f90f1fc33c921d5a264a/image.png)

It opens the contextual help for the PCMM Planning view:

![image](uploads/b93566d2d3edd3fb61e81afcb5845218/image.png)

#### PCMM Planning Form

Clicking on a PCMM element opens the PCMM Planning form. All the planning fields are configurable. See [Requirements page for PCMM Planning fields](Requirements#pcmm-planning-data-model-tab) to change the configuration. 

Depending of the project configuration, the rendering will change:

**In default mode:** All the subelements associated to the element are displayed. 

![image](uploads/bf7102b6753fc1c040fa20b6e59ca918/image.png)

To start planning, click on a subelement. The available fields will be displayed:

![image](uploads/5ea811fe24e32709cd7205c1bcb797e2/image.png)

**In simplified mode:** The planning informations are associated to the element:

![image](uploads/21e3c8af3bf8bdb12cf81832c9ff7590/image.png)

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

![image](uploads/67bbd6084ad6907254079a0c5b815910/image.png)

###### Back

![image](uploads/bfb6dbe6d1cfd61698b50ef3af18f64a/image.png)

Return to the PCMM Planning Home page.

###### Information

![image](uploads/70b56a1aaec64e809cf97453beb36241/image.png)

Opens the contextual help for the PCMM Planning view:

![image](uploads/47a9208d969e21ccf9ead40b6bf9a466/image.png)


### Analyst Decisions

Analyst Decision is a minitool to record analyst decisions and their characteristics (consequence, techniques maturity, description...).

![image](uploads/eb0df3fbaacf108268e9e38a092a0617/image.png)

There is no limit in the number of levels for the groups, decisions, sub-decisions...

#### Analyst Decisions Buttons and Actions

##### Add Decision Group

![image](uploads/877d468b3de947110452d886630c9391/image.png)

This button opens a new dialog to add a decision group. The configured fields will be available into the dialog:

![image](uploads/db851a6494278569c8a0feac8167e834/image.png)

##### Back

![image](uploads/bfb6dbe6d1cfd61698b50ef3af18f64a/image.png)

Return to Home page (see [Home View](#home-view)).

##### Information

![image](uploads/70b56a1aaec64e809cf97453beb36241/image.png)

It opens the contextual help for the Decision view:

![image](uploads/c9b276644a942d82b75650ef20b1eb76/image.png)

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

![image](uploads/a4bfb76af439b6cb0bd0a98c2eedc2a3/image.png)

##### Add Analyst Decision

The user can add a decision under an existing decision group or a decision by clicking on the `Add` button:

![image](uploads/084236040b8256bffb5a3f4d514e531f/image.png)

A new dialog will popup and ask the user to input the necessary informations:

![image](uploads/f3a48c678deaeeaaaa10aef764618b58/image.png)

##### Open Analyst Decision

The `References` field stores a link to a document associated to the decision. Clicking on the `Open` button will open this document (url, document inside the workspace):

![image](uploads/3b280454b34f0ea216e52eec7a3e09ad/image.png)

##### View Analyst Decision

It is possible to view the decision informations by clicking on the `View` button:

![image](uploads/96e5d0973ba256fd2a55ba2a0b44961f/image.png)

A new dialog will popup with the decision informations in read-only mode:

![image](uploads/96b58c937faf5e04bfd7e8d468ca3b7a/image.png)

##### Edit Analyst Decision

To edit the decision informations click the `Edit` button:

![image](uploads/764b25403f4b7303d763034926b52a4e/image.png)

A new dialog will popup in edition mode:

![image](uploads/49a402a9a83112824cf01b9b7f9dba4b/image.png)

Click `Update` to save changes.

##### Delete Analyst Decision

To delete a decision click the `Delete` button:

![image](uploads/e9f467393a3e1a739ad0c9324b1d7fed/image.png)

A confirm box will popup:

![image](uploads/b691440c3367d222c5463b162da27ddf/image.png)

All the decisions and sub-decisions associated will be deleted.

## Phenomena, PIRT
[Go back to Contents](#contents)

PIRT feature allows to describe quantities of interest and to associate phenomena and criteria with a created quantity of interest. The PIRT view is a stack of tabs starting with the QoI Home View. After that, multiple PIRT tables can be added for each quantity of interest opened.

PIRT content persists across sessions in `<filename>.cf` file.

### QoI Home View

This view lists all the quantities of interest associated to the current credibility process. It is possible to add, edit/open, copy, tag or delete a QoI (Quantity of Interest).

![image](uploads/388e46dea2a04027fd32dab8c4150db6/image.png)

#### Model Header Table

![image](uploads/e06f34137fc2341fa96866aae8968dbc/image.png)

The Model Header table contains model information:
- `Application`: the application of this model.
- `Contact`: the person(s) to contact for further information about the model.

The Model Header table can be hidden by clicking on the topbar containing *Model Description*.

#### Table QoI columns:

![image](uploads/cee3b1218f6623bec84808617f292a38/image.png)

The table has four columns:
- `<Tagged?>`: is QoI tagged? The first column displays a tag icon if the QoI is tagged, otherwise nothing. A tagged QoI is always displayed under its parent untagged QoI. Its line background color is brown.
- `Name`: the QoI name
- `Description`: the summary of the description of the QoI
- `Creation Date`: QoI creation date
- `Action columns`: 

#### Table QoI actions:
- **Double-click**: opens the selected QoI in a new tab in edition. A tagged QoI is only visible and can not be edited.

#### Table QoI action buttons:

![image](uploads/ac8bf657810dae1649c1cc7956a603e1/image.png)

- `Open`: opens the selected QoI in a new tab in edition. A tagged QoI is only visible and can not be edited.
- `Copy`: makes a copy of the QoI and of all its associated phenomenon groups, phenomena and criteria.
- `Tag`: creates a tag for this QoI. A tag is a copy that can not be edited. It is used to kept the state of a QoI. It helps the user to see the progress and take decision. Each associated phenomenon groups, phenomena and criteria will be copied and tagged (see [Tag QoI](#tag-qoi)).
- `Delete`: deletes the QoI. A deletion confirmation dialog is opened on click:

![image](uploads/39323837ca5fd7705b8fa45a10730400/image.png)

If the user confirms deletion, the selected QoI is deleted with all the associated phenomenon groups, phenomena and criteria.

#### QoI Buttons

##### Add Quantity of Interest

![image](uploads/ac26373dd6d8efeb2bfe83f3e1f7a669/image.png)

This button creates a new quantity of interest with a new PIRT table. It throws a new QoI creation wizard.

- `Name`: The user can enter a **name** for the new QoI. The name can not be null.
- `Description`: The QoI can be described with a RichText editor with tools to layout the description as a document.
- The **creation date** will be automatically stored on the new QoI.

![image](uploads/bde47e9eb43fe25f9028a8d861403b9d/image.png)

##### Back

![image](uploads/bfb6dbe6d1cfd61698b50ef3af18f64a/image.png)

`Back` button opens the cf-plugin `Home View`.

##### Guidance

![image](uploads/2dc9d990372c821da1b88776a1f832bb/image.png)

This button opens the `Credibility Framework Guidance` view with the PIRT Guidance in it. See [PIRT Guidance](#pirt-guidance).

##### Information

![image](uploads/70b56a1aaec64e809cf97453beb36241/image.png)

It opens the contextual help for Quantities of Interest:

![image](uploads/5a13287a12e2704660cc151d72ba6809/image.png)

### Phenomena View

The Phenomena view displays the information of a QoI. This view is splitted in two tables, quantity of interest header containing the QoI information and phenomena table to assess criteria credibility.

![image](uploads/4fb6c8d93ce967d10ba7eb60f04e0106/image.png)

#### Phenomena Buttons and Actions

##### Add Phenomenon Group

![image](uploads/2c996842fcfd31c1c8a9b6436d8bbd5a/image.png)

Opens a new dialog to create a new phenomenon group:

![image](uploads/1d85a227afe169d5bdaa5022096e7d1b/image.png)

Required field for phenomenon group is:
- `Description`: this is the group label or name

##### Tag QoI

![image](uploads/53e3aa875fca5484512dcb9edb154f81/image.png)

Creates a tag for this QoI. A tag is a copy that can not be edited. It is used to kept the state of a QoI. It helps the user to see the progress and take decision. Each associated phenomenon groups, phenomena and criteria will be copied and tagged.

The user is prompted to confirm the tag action:

![image](uploads/fb0d330852164fbe583f7a93503cc9b1/image.png)

And is encouraged to enter a `Description` for this tag:

![image](uploads/9971fc9ad609a046ed0ffd1217d3a745/image.png)

##### Reset

![image](uploads/2487945bc3c3c4f0d7911d0368edeb8a/image.png)

Resets the current Quantity of Interest:
- Delete all variable QoI Headers
- Delete all Phenomenon groups
- Delete all Phenomena
- Delete all Criteria

The user has to confirm the reset action:

![image](uploads/1ac2eca37d395f663689489fd0adbb22/image.png)

##### Back

![image](uploads/bfb6dbe6d1cfd61698b50ef3af18f64a/image.png)

`Back` button opens the cf-plugin `Home View`.

##### Close

![image](uploads/8209bee319c7d4031fba82985b2a115e/image.png)

This button closes the current Quantity of Interest tab.

##### Guidance

![image](uploads/2dc9d990372c821da1b88776a1f832bb/image.png)

This button opens the `Credibility Framework Guidance` view with the PIRT Guidance in it. See [PIRT Guidance](#pirt-guidance).

##### Information

![image](uploads/70b56a1aaec64e809cf97453beb36241/image.png)

It opens the contextual help for PIRT table:

![image](uploads/2ac97b7483b96a66ae649b8ca16f4215/image.png)

#### QoI Header Table

The QoI Header table contains QoI information. The information is separated in two types, the fixed fields and the variable ones.

![image](uploads/a817a653a2cfabbfc87ef6f308da5414/image.png)

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

![image](uploads/82ee13f97c1990e15bf6e2e663004f16/image.png)

A new dialog will popup with the RichText tools:

![image](uploads/56238cdbdf6ad804b0a3e17261351b7d/image.png)

##### View a RichText field
This type of field can be viewed by clicking on the view icon:

![image](uploads/2e4efb919eb9112601a86b071c130f0f/image.png)

A new dialog will popup with the full content:

![image](uploads/b6a115d56098cea61db8fd195a98d009/image.png)

#### Phenomena Table

This table presents the different phenomenon groups with their associated phenomena. The phenomena has several criteria. Information is separated in two types, the fixed columns and variable.

![image](uploads/b825741010b95b5e63ab1183c69c2533/image.png)

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

![image](uploads/5bd19e43077fb84b84bd53276c4687c3/image.png)

The criteria columns of *Levels* type are colored. Their color changes depending of the *Importance* column. All columns of *Level* type has a numerical value. The numerical value is used to be compared with the *Importance* column.

The difference is splitted in three level differencing colors. This colors are configured in the project configuration file.

`Importance Numerical Value* - *Level Criterion Numerical Value`:
- `>= 0`
- `= -1`
- `<= -2`

See [Generate Configuration for Credibility Project](https://gitlab.com/iwf/cf/wikis/Generate-Configuration-for-Credibility-Project) for more details.

#### Phenomena Table action buttons:

![image](uploads/4191dcdc23fb91ea91fc242fd198ff3f/image.png)

##### Add Phenomenon

![image](uploads/a259c381ae32feeb0f51756ad89232d0/image.png)

Opens a dialog to add a new phenomenon:

![image](uploads/a6de89100ce54294dd80417b881ebe2e/image.png)

Fixed fields for phenomenon are:
- `Phenomenon group`: the phenomenon group to associate with.
- `Description`: this is the phenomenon label or name.
- `Importance`: the importance level of the QoI to compare with adequacy columns.

Fields under `Adequacy` are the configurable criteria columns to assess the QoI.

Text or RichText criteria fields will be added at the bottom.

##### View Phenomenon/Group

![image](uploads/d1f1cf8a009d15b93efd852192abda4c/image.png)

Opens a new dialog to display the group or phenomenon ranking information:

![image](uploads/632bcd1f48fa798d14b26fb2292809dd/image.png)

##### Edit Phenomenon/Group

![image](uploads/e694c995f6fbcf09c0a6799550eedc25/image.png)

Opens a new dialog to edit the group or phenomenon:

![image](uploads/95ecef71bf7e7bb9fbc76f93939a4e04/image.png)

##### Delete Phenomenon/Group

![image](uploads/31219361896732a120b2118aac8c1b80/image.png)

Deletes the group or phenomenon. A deletion confirmation dialog is opened on click:

![image](uploads/fbbee10b4f7691ebf9e708046f3861ad/image.png)

If the user confirms deletion, the selected group or phenomenon is deleted with all the associated phenomena and criteria.


## Credibility, PCMM
[Go back to Contents](#contents)

PCMM is a tool to help analysts to classify evidence, and assess this evidence. This will be used to start discussion and detect lack in the process, not to assess the analysts.

PCMM also gives tools to summarize the assessments and display the state and progress of the model with an aggregated score for the different tested elements. This aggregation can be shown as a table or as a radar chart.

**PIRT is a prerequisite to PCMM**, please firstly fill in QoI and PIRT tables:

![image](uploads/ede02b2e9e2d782c9691132331ad58b1/image.png)

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

![image](uploads/e6ae71a0f2ac99275aec0a156706dd8c/image.png)

If not defined, the user is prompted to select his role when he tries to access to the evidence or the asses views:

![image](uploads/667bf11977afb86ae3b163664fc8d2f6/image.png)

If the user cancels, the following message box is opened:

![image](uploads/1332340dc300f95372c117971f798dab/image.png)

### PCMM Home View

The PCMM Home view displays the progress of the PCMM and the PCMM wheel with the different elements. 

The PCMM wheel text is adaptive to the view, by default the full PCMM element name is displayed:

![image](uploads/830131e5eb5598f177f5bd85926837fa/image.png)

But if the available space for the wheel is too small, the PCMM element acronym is displayed:

![image](uploads/57e589e5c66739afaf8280b609f271b7/image.png)

#### PCMM Progress

This section displays the overall progress of the PCMM process including the PCMM Planning, Assess and Evidence phases.

![image](uploads/b0ddd2cfdbf11d441576c7a8af2c91bb/image.png)

The progress is indexed by PCMM element. The progress is divided in two major parts, the evidence and the assessment.

**In default mode:** The progress of each PCMM element is divided by the number of PCMM subelements of this PCMM element.

Once an evidence is added or an assessment is made on a PCMM subelement, the progress is marked as done.

For example, a PCMM Element has 5 subelements. The progress bar will be divided in 10 parts (5 for the evidence and 5 for the assessment).

**In simplified mode:** The progress is divided by two. Once an evidence is added and an assessment is made on an element, the progress is marked as done.

#### PCMM Tags

It is possible to tag the PCMM process to a specific state. A `Tags` part is available on the PCMM Home view, below the `Progress` one:

![image](uploads/c599b5059923175783ef6f0ae9aea3f4/image.png)

##### New Tag

To create a new tag, click on the `Tag` button:

![image](uploads/59b8dd92dbcf4c7f9877688df30694ad/image.png)

A new dialog will popup to enter the tag information:

![image](uploads/7d25d42dcb0ec933b0fbcb72654a3770/image.png)

The plugin will inform the user of the success of the tag operation:

![image](uploads/70dbf00e9ac2328190986f27a15c57d6/image.png)

##### Select a Tag

By default, the tag `Latest version (working)` is selected. It is the current working state of the PCMM process.

The user can select an existing tag in the `Tag` list:

![image](uploads/aae6a0e53a9e01c2f42dd9ffd591d330/image.png)

When a tag is selected, the associated evidence and assessments are loaded. All the PCMM views are available. But **the user can not alter** (create/update/delete) the evidence or assessments once tagged.

##### Manage Tags

To manage the existing tags, click on the `Manage Tag` button:

![image](uploads/ec5a9f881ed531ccaa3484c144e8ed73/image.png)

A new dialog will popup with a table containing all the existing tags. It is possible to sort the table by each column:

![image](uploads/942f7c5de68e18ccbfcf32461fdad8d0/image.png)

It is also possible to delete a tag by clicking the `Delete` button:

![image](uploads/3dd87f07696af6a5fabcb7c38c61e122/image.png)

or pressing **DEL** key on the keyboard:

![image](uploads/444ab8ca283a7e1b9871c9d74543b57e/image.png)

The plugin will confirm the deletion success:

![image](uploads/2b858056cb766d180e0cc59345adf059/image.png)

#### PCMM Wheel

The PCMM wheel shows the different PCMM elements in one chart. 

To start adding evidence and/or add assessment:
- **Click on a PCMM Element**, a contextual menu with two options will popup:
  - `Evidence`: manage evidence for this PCMM element. It opens the evidence view (see [PCMM Evidence View](#pcmm-evidence-view)).
  - `Assess`: manage assessments for this PCMM element. It opens the assessment view (see [PCMM Assess View](#pcmm-assess-view)).

![image](uploads/87d9b9699b90dc376eeecb173c2ee1f5/image.png)

**Important**: This sections requires a role to be defined for the user before it an be used. If there is no role defined, the user is prompted to select a role (see [Change PCMM Role](#change-pcmm-role)).

**Important**: The PCMM Element PMMF (Physics and Material Model Fidelity) needs to define PIRT quantities of interest first:

![image](uploads/ffcd316ca13e0dda43c6667a043a9d3c/image.png)

#### PCMM Home Buttons

##### Back

![image](uploads/bfb6dbe6d1cfd61698b50ef3af18f64a/image.png)

Return to home page (see [Home section](#home)).

##### Guidance

![image](uploads/2dc9d990372c821da1b88776a1f832bb/image.png)

This button opens the `Credibility Framework Guidance` view with the PCMM Guidance in it. See [PCMM Guidance](#pcmm-guidance).

##### Information

![image](uploads/70b56a1aaec64e809cf97453beb36241/image.png)

It opens the contextual help for PCMM Home view:

![image](uploads/26336b7d77b6494cc8e95101d27e516e/image.png)

##### Aggregate Individual Assessments

![image](uploads/47069ebbfcce6f5d867af0097106bc85/image.png)

Open the aggregate view (see [PCMM Aggregate View](#pcmm-aggregate-view)).

##### PCMM Radar Plot/Quality Stamp

![image](uploads/9c0cf33ab6e460c06bcb0c993c400fce/image.png)

Open the PCMM stamp view (see [PCMM Radar Plot/Quality Stamp View](#pcmm-radar-plotquality-stamp-view)).

### PCMM Evidence View

The evidence view helps analysts to manage evidence. All evidence are available, but only the ones associated to the selected PCMM element can be managed. 

The user and role which created the evidence is referenced in the `User` and `Role` columns. It is useful to analyze the credibility of the evidence.

**In default mode:** The evidence is associated to the subelements:

![image](uploads/e4d77187391aab414e02189beca6b9f7/image.png)

**In simplified mode:** The evidence is associated to the elements:

![image](uploads/444ab41c0a199e2e9eb251f6bc78a377/image.png)

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

![image](uploads/ed6796f4ceb3e31228b3efc1f7733af4/image.png)

A tooltip is available to have further information about the warning or error:

![image](uploads/9570b731bfc7627eca515b567bbec029/image.png)

This may occur if the evidence has the following problems:
- **Errors**:
  - The evidence file `doesn't exist anymore`
  - The evidence file is `already associated with the same PCMM element/subelement`.
- **Warnings**:
  - The evidence file is `already associated with another PCMM element/subelement`.
  - The evidence `file changed`. It is possible for the evidence creator to delete this warning message by clicking on the `Edit` button, check the **Remove "file changed" notification**, and click **Update**.
  
![image](uploads/7897ed9539a78d7c2e35d6b5ea7119a0/image.png)

#### PCMM Evidence Table Actions

![image](uploads/59b91374549e08f77ec9e751cfbd5163/image.png)

##### Add Evidence

To add an evidence:

- **Click** on the `Add` button:

![image](uploads/28722777a8f2db8f47f2f24e110adafa/image.png)

**Select/Browse** the `file` or `URL` and enter a `Description`:

![image](uploads/f9a2b15653cffb85eeba27cffa3fb858/image.png)

- **Drag and drop** a document from the Eclipse explorator to the destination subelement or element. If the element dragged is not valid or if the source is not valid, the interface will show the forbidden icon on the dropped item.

![image](uploads/2f372077c9ea4b00fb52b49572e0d222/image.png)

**Evidence Type**: An evidence can be a file in the Eclipse project or a URL. To select the type, click on the radio button in top of the add evidence dialog:

![image](uploads/6f0f68285c658fdb9a3e45d6c8cc95ac/image.png)

![image](uploads/366bacac873cda5ec0c29300ad2cf40a/image.png)

##### Open Evidence

It is possible to open one or more evidence by:
- **Double-clicking** on the evidence row.
- **Selecting** the evidence to open and typing **Enter key** on the keyboard.
- **Selecting** the evidence to open and clicking the `Open` button:

![image](uploads/1879b0a284eb8c733a2079b2445ce3fb/image.png)

By default, the evidence is opened by the Eclipse product. It is possible to change this behavior into the Eclipse  preferences (see [Eclipse File Associations](#eclipse-file-associations))

##### Edit Evidence

Only the user which created the Evidence can edit it.

To edit the Evidence description, click the `Edit` button:

![image](uploads/e19b49d30d38dc1182bb7e09b41bac16/image.png)

A new dialog will popup with the **current errors and warnings** and the `Description`:

![image](uploads/a59f180c1d4a4bf351aee410e8eaa5eb/image.png)

Click `Ok` to save changes.

##### Delete Evidence

Only the user which created the Evidence can delete it.

To delete it, do the following:
- `Click` the `Delete` button:

![image](uploads/37e82ecab215ce39b2d46970aff3a058/image.png)

- **Select** the evidence to delete and type **"Del" key** on the keyboard.

A confirm box will popup:

![image](uploads/af6797900d88dab6b7185ff095c71a8d/image.png)

#### PCMM Evidence Buttons and Actions

##### Back

![image](uploads/bfb6dbe6d1cfd61698b50ef3af18f64a/image.png)

Return to PCMM Home page (see [PCMM Home View](#pcmm-home-view)).

##### Guidance

![image](uploads/2dc9d990372c821da1b88776a1f832bb/image.png)

This button opens the `Credibility Framework Guidance` view with the PCMM Guidance in it. See [PCMM Guidance](#pcmm-guidance).

##### Information

![image](uploads/70b56a1aaec64e809cf97453beb36241/image.png)

It opens the contextual help for PCMM Evidence view:

![image](uploads/dfc066048203a3f6e8c97baa59a33c62/image.png)

### PCMM Assess View

The Assess view is used to evaluate each element and subelement depending of the evidence associated. The analyst is invited to take a role to assess.

An analyst can only assess once a subelement/element per role selected. The analyst has to change his role to assess several times.

__In default mode:__ The assessment are done on the subelement:

![image](uploads/40b847d411707c595ea8bab23ccd6b9a/image.png)

__In simplified mode:__ The assessment are done on the element:

![image](uploads/48a9c3d1cd16db6fb00e9a3066f3df6b/image.png)

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

![image](uploads/854208905b70d51513a72f05f63fe3fe/image.png)

##### Open/Examine Evidence

![image](uploads/7b433ff96e03011c1a4f8f95ef74e40f/image.png)

To examine evidence click the `Open` button.

If there is **only one evidence**, the **evidence will be opened** inside or outside of Eclipse according to the preferences. For further information see section [Open Evidence](#open-evidence).

Otherwise, a **list of evidence dialog** will be opened containing all the added evidence for this PCMM element/subelement:

![image](uploads/11dedef9355d6d47aa1940d7fa59deb9/image.png)

The user has different actions:
- `Open`: open the evidence
- `View`: view the evidence description

##### Assess/Edit

There is different ways to assess:
- **Double-click** on the PCMM element/subelement row.
- Click `Add` or `Edit` button:
  - **Add** assessment: by default if there is no assessment for this role and user:
![image](uploads/1863c565e6d5c0f2073d74e87ba8bf62/image.png)

  - **Edit** assessment: present if there is already an assessment for this role and user:
![image](uploads/4d8dce4e3fb847c90a6fbd8aeab38173/image.png)

![image](uploads/d0f8461b1ec77bddbfa808ec681905a0/image.png)

The `Level achieved` and `Comment` fields are mandatory.

**Important Rule:** An analyst can only assess once a PCMM subelement/element per role selected. The analyst has to change his role to assess several times.

**Important Rule:** It is possible to assess every subelement/element, but it is only possible to give a the lowest level if there is no evidence associated to subelement/element. A warning message is displayed if the user tries to assess with a higher level than permitted:

![image](uploads/53aae41575bf5cbfa842e20c63f59f22/image.png)

##### Delete

![image](uploads/7bad796a7e28021df0a707873f0046ce/image.png)

The `Delete` button removes the assessment for this user and role. 

A confirmation dialog is opened:

![image](uploads/cbd37ded71c4a54ed43dad23071843eb/image.png)

#### PCMM Assess Buttons and Actions

##### Back

![image](uploads/bfb6dbe6d1cfd61698b50ef3af18f64a/image.png)

Return to PCMM Home page (see [PCMM Home View](#pcmm-home-view)).

##### Guidance

![image](uploads/2dc9d990372c821da1b88776a1f832bb/image.png)

This button opens the `Credibility Framework Guidance` view with the PCMM Guidance in it. See [PCMM Guidance](#pcmm-guidance).

##### Information

![image](uploads/70b56a1aaec64e809cf97453beb36241/image.png)

It opens the contextual help for PCMM Assess view:

![image](uploads/64890e04b6e0aca375e627db1d20d881/image.png)

### PCMM Aggregate View

The aggregate view aggregates all the assessment by subelement and element. It gives a score level to the element based on the level configured. 

**Tip**: There is as many levels as there are subelements/elements. A numerical code is stored on the level to have a correspondance between the subelement/element level and the aggregation level. This gives a numerical score for the aggregation. This numerical score is then converted to a level score.

__In default mode:__ The aggregation is made for the subelements and the elements.

![image](uploads/2d0b746f26acedc586c8211d5542a256/image.png)

__In simplified mode:__ The aggregation is only made for the elements.

![image](uploads/90fe6325df51295742632e079b2fba74/image.png)

**Warning:** If the PCMM overall progress is not complete, the plugin shows a warning to the user:

![image](uploads/927a9ddc6806e385c7229da5c1fa5787/image.png)

#### PCMM Aggregate Table Columns:

- `ID`: the first column is for the PCMM element/subelement code
- `Element/Sub-element`: the PCMM element/subelement name
- `Level Achieved`: the aggregate level
- `Evidence`: the number of evidence
- `Open`: open button to examine all the evidence
- `Comments`: An abstract of the assessment comments
- `Details`: details button to open the list of assessment dialog

#### PCMM Aggregate Table Filter

![image](uploads/8c1bbaa55002fadaca2cb0f3f2a21211/image.png)

The aggregate list can be filtered by:
- `Role`: the role of the assessment creator

The aggregate table data will change depending of the filter selection.

#### PCMM Aggregate Table Actions

##### Open/Examine Evidence

![image](uploads/b50334c1836210cbcf41c03011c43ece/image.png)

To examine evidence click the `Open` button.

If there is **only one evidence**, the **evidence will be opened** inside or outside of Eclipse according to the preferences. For further information see section [Open Evidence](#open-evidence).

Otherwise, a **list of evidence dialog** will be opened containing all the added evidence for this PCMM element/subelement:

![image](uploads/202ee0b4877909f78e2bb880e78d971d/image.png)

The user has different actions:
- `Open`: open the evidence
- `View`: view the evidence description

##### Details

![image](uploads/b0b2c908c4a8b41a9626c8745140dbf6/image.png)

The user can inspect all the assessments done with:
- **Double-click** the subelement/element.
- **Click** the `Details` button.

A new dialog is opened containing all the assessments per user and role:

![image](uploads/8db25a7b61cb7221b33b9f4651199dff/image.png)

**Tip**: By default all assessments are expanded. But the list of assessments can be very long. It is possible to **collapse** an assessment by **clicking the title** of the assessment description:

![image](uploads/f90b27f0acb570ca36e60d46ae58aad1/image.png)

#### PCMM Aggregate Buttons and Actions

##### Back

![image](uploads/bfb6dbe6d1cfd61698b50ef3af18f64a/image.png)

Return to PCMM Home page (see [PCMM Home View](#pcmm-home-view)).

##### Guidance

![image](uploads/2dc9d990372c821da1b88776a1f832bb/image.png)

This button opens the `Credibility Framework Guidance` view with the PCMM Guidance in it. See [PCMM Guidance](#pcmm-guidance).

##### Information

![image](uploads/70b56a1aaec64e809cf97453beb36241/image.png)

It opens the contextual help for PCMM Aggregate view:

![image](uploads/a01f884e23033294b98527e08425b50d/image.png)

### PCMM Radar Plot/Quality Stamp View

The PCMM Radar Plot/Quality Stamp View is the **aggregation and the average** of all the assessments over team members. 

In a **low rigor** ModSim project, the aggregation will be computed from the assessments done on each PCMM elements. 

In a **high rigor** project, the aggregation is firstly computed at the PCMM subelement level, then computed from the result of the subelement aggregation to the PCMM element.

![image](uploads/e11a0645dbe34ebec0426f369dc973c2/image.png)

**Warning:** If the progress is not complete, the plugin shows a warning:

![image](uploads/d7cba45bf52ae47917d7188603e9529b/image.png)

#### PCMM Stamp Filter

![image](uploads/b6d9a72ebaeb8f90aedcfaeab673e4e6/image.png)

The aggregate list can be filtered by:
- `Role`: the role of the assessment creator

The PCMM stamp data will change depending of the filter selection.

#### PCMM Stamp Buttons and Actions

##### Back

![image](uploads/bfb6dbe6d1cfd61698b50ef3af18f64a/image.png)

Return to PCMM Home page (see [PCMM Home View](#pcmm-home-view)).

##### Guidance

![image](uploads/2dc9d990372c821da1b88776a1f832bb/image.png)

This button opens the `Credibility Framework Guidance` view with the PCMM Guidance in it. See [PCMM Guidance](#pcmm-guidance).

##### Information

![image](uploads/70b56a1aaec64e809cf97453beb36241/image.png)

It opens the contextual help for PCMM Stamp view:

![image](uploads/6e64e18a76295dbcd843cd0f2f19a759/image.png)



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

![image](uploads/760c740296d7fe70d0e4963e251f7177/image.png)

The Report home view appears with its different sections:

![image](uploads/8e1b4ccd932303920e7a9c43727e2e70/image.png)

**Important**: the Credibility Report feature is using ARG (Automati Report Generator) library to create the reports. Please see [How to install ARG](How-to-install-CF-plugin#arg) page.

#### ARG Setup

This section describes the ARG setup including the executable path, the python pre-script path and python command.

![image](uploads/8ad0cbf6999ac1eb5a713f101e80dd30/image.png)

- `Python executable path`: this is the python command name on the system. By default `python` is filled. This field can not be empty.
- `ARG executable path`: the location of the ARG executable. This is mandatory and setted empty by default.
- `ARG setenv script path`: this field locates the environment script to execute before generating the report. Setted empty by default, this script is optional.
- `Use local configuration from Preferences`: by default, the configuration is saved into the `<filename.cf>` file. But it is possible to overwrite the file configuration by the local one with this option. The configuration has to be setted directly into the plugin Preferences.

##### Open Preferences Button

![image](uploads/6643fe5e2dca5f73bc6187268a9673a3/image.png)

This button opens the local Preferences. See [Preferences](#preferences) to have furthermore informations about the local configuration.

#### ARG Parameters

This section is used to configure ARG and the report generation:

![image](uploads/2dc70da0e8d2e6db7dc2d7cbc97f6336/image.png)

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

![image](uploads/58a161803a4d17cb701d00a01042ee8f/image.png)

#### PIRT Parameters

The PIRT Parameters section lets select the PIRT data to include into the report. Each Quantity of Interest can be added/removed with its associated tag (into the combobox).

![image](uploads/97d17fbe3d5674adebc000503b3c8e43/image.png)

#### PCMM Parameters

The PCMM Parameters section permits to include the PCMM data into the report. The PCMM tag to include is setted by default to the working version.

The PCMM features can be selected:
- PCMM Planning defintion
- Evidence with their associated documents
- Assessments

![image](uploads/b53b86ba5edfabdc3fc21c23c122455e/image.png)

#### Custom Report Ending

Custom Report Ending is an optional feature to allow to append some content to the generated report.

This content has to be added as an ARG core structure file `.yml` containing at least one `chapters` tag. Please see [ARG documentation](https://automaticreportgenerator.gitlab.io/arg/) for furthermore informations.

![image](uploads/d8b14fa4d368a44cfe793bb4562fb3f1/image.png)

The content will be added to the end of the report:

![image](uploads/1d426d720562620350949e76928a49e2/image.png)

#### ARG Console

The ARG console is used to display the ARG execution logs:

![image](uploads/879b27302e3b6c1b15877aebf208dda8/image.png)

#### Document Inlining

CF uses ARG to inline evidence and references documents.

- **Images** are automatically inlined as images into the final report:

![image](uploads/c3dbadea6821607a13a9be291ab8ce1c/image.png)

A caption can be added directly in the user interface. If the caption is left empty, the file name will be used:

![GifMaker_20220106170329052](uploads/e1b8edff810f106471f1ce0c69c44966/GifMaker_20220106170329052.gif)

- **Word** documents is still in a bêta mode. This option can be activated for PCMM evidence in the [Preferences - Developer Options](#developer-options) and on the Report view [ARG Parameters - Inline Word document](#arg-parameters) section.

#### RichText rendering (bêta)

Some of the CF features have RichText editors available:

![image](uploads/ae6348b746900d526e4c8de908bfb145/image.png)

ARG is able to render the RichText editors content (based on html) in the final report:

![image](uploads/8b8675de4a3703cb1ce9ab5a4b5af65d/image.png)


## CF Project Configuration
[Go back to Contents](#contents)

![image](uploads/f85e3b7443fca4d14db2ee45a1dd6c91/image.png)

In the top right corner of each page, the `Configuration` button allows to access CF configuration options:

![image](uploads/883bac70a002bfb76e756d326611126b/image.png)

The configuration has two tabs:
- Import Configuration
- Export Configuration

![image](uploads/36d3e69203e8b9c32f5aee8390d325e8/image.png)

### Import Configuration

This tab permits to change the current configuration of each CF feature. It is used to enable/disable CF features too.

![image](uploads/2864e8c4f86e5e3df419f931bfcd7ed4/image.png)

Each CF feature can be imported. It contains two buttons:

#### Browse

![image](uploads/0e7bf0a8771ef2e5d8e7971dbfaaf986/image.png)

The browse button lets select the `.yml` configuration file for the desired feature.

#### Import

![image](uploads/5906d02869c7a1547a937f228eec9357/image.png)

The `Import` button opens a new dialog. This dialog compares the current configuration with the new one. The user can select the changes to apply. To apply the changes, click on `Confirm Import`:

![image](uploads/741aab88bbde8c4719167751f623397b/image.png)

**Important**: if the user decides to delete an existing field, all the data associated will be deleted.

**Tip**: Once confirmed, the changes will be applied to the current CF project. The changes are made locally but not saved until the user saves the CF editor. To revert a change in the configuration, close the current CF editor without saving. 

### Export Configuration

This tab is used to export the current configuration of each CF feature.

![image](uploads/e27faa08b055505892f8e918b7dfeb7a/image.png)

Each CF feature can be exported. It contains two buttons:

#### Browse

![image](uploads/02682e9bcd2855ec2bff36baeb22f928/image.png)

The browse button lets select the output `.yml` configuration file for the desired feature.

#### Export

![image](uploads/0b51bfbaef156931e35415822de1191b/image.png)

The `Export` button create a new `.yml` file with the configuration of the selected feature. This file can be used to create a new CF file. A confirmation message pops up:

![image](uploads/322f0e7d5e566823c8fb6c17c6e72ae1/image.png)

**Tip**: use this option to copy a CF project file without data



## Credibility Framework Guidance view
[Go back to Contents](#contents)

A guidance view called `Credibility Framework Guidance` is available within the CF plugin. 

The guidance view is a separated Eclipse view and can be moved into the Eclipse plugin or outside:

![image](uploads/f4f57a46cd24756ed5093a010b231600/image.png)

![image](uploads/79357efd956164a3505896c669c50f14/image.png)

The guidance view also changes according to the credibility process opened and the current view (PIRT, PCMM,...) chosen by the user. The view is opened once for all the credibility process and if closed, disappears for all processes.

The currently implemented guidelines are:
- [PIRT Guidance](#pirt-guidance)
- [PCMM Guidance](#pcmm-guidance)

### Open the Credibility Framework Guidance view:
It can be opened by:
- clicking the `Guidance` button on each view:

![image](uploads/2dc9d990372c821da1b88776a1f832bb/image.png)

- within **Eclipse menu**:
  - click `Window > Show View > Others...`:

  ![image](uploads/6cea9d1f19e4203286fbc1e48d5eb4f0/image.png)

  - then select `Credibility Framework Guidance` under `Credibility Framework`, and click `Open`:
  
  ![image](uploads/3e7980aa611aff0dec54f8ef66b307ce/image.png)

### PIRT Guidance

The PIRT Guidance is separated in two sections:
- `PIRT Guidance Levels`: displays the PIRT Guidance for each adequacy column and level. This comes from the PIRT configuration schema file.
- `PIRT Level Difference Colors`: lists the difference colors to help the user to fill in the PIRT table.

![image](uploads/8c59d8f9d62dc40a02bf35c3d6a52966/image.png)

### PCMM Guidance

The guidance level view displays the description of the levels of each subelement/element to help the user to assess:

By default, the PCMM level view is shown in columns:

![image](uploads/d1c0d49dd83fff84180736c71913ad3e/image.png)

But if the available size is less than 500 pixels, the PCMM level view is shown in row:

![image](uploads/af27976a2d0a60c4bbbe896b588c2875/image.png)

To increase the readability of this table, a tooltip is available with the full content of the row:

![image](uploads/1fe014276684db996fd27c51b5baee32/image.png)



## Concurrency Support

### Enable Developer Option

To enable concurrency support into CF plugin, enable the option into Window > Preferences > Credibility Framework > Developer Options:

![image](uploads/d132eb1d0bd55236a72fdb3b86b50d28/image.png)

### Create a new web project

You will be able to create an existing project with the "New Credibility Process" wizard:

- Right click the project and select New > Other...:

![image](uploads/5c77a790d78cd71bbe6bb6c60885c1ac/image.png)

- Select Credibility Process:

![image](uploads/a6a55ed86ca118dde21989d53e01d327/image.png)

- Enter the credibility process name:

![image](uploads/44c4eddfc1567911f1eae19a9955b0cb/image.png)

- Select the project type (Web):

![image](uploads/54b395c7e61c76055def04b5082dacb1/image.png)

- Enter the server URL and click "Test Connection":

![image](uploads/08c18a02d110d044440fdfa2c3760fb6/image.png)

- Select "New Web Project":

![image](uploads/2baa1fd79cc1cac2d34eae1850c8b07f/image.png)

- Enter the project information and click "Finish":

![image](uploads/80610e49c255bab64ffd7d5cd4fec403/image.png)

- The project will be created and will be prompted to enter your credentials, just click "Connect":

![image](uploads/d9836ad4138b2bd2230fed3d7839563d/image.png)

The web project is open:

![image](uploads/d33e010ab9e538815e9277ac8fdfcb0e/image.png)


### Connect to an existing web project

You will be able to connect to an existing project with the "New Credibility Process" wizard:

- Right click the project and select New > Other...:

![image](uploads/2d0e7504f5862a115790d5e7c2358da5/image.png)

- Select Credibility Process:

![image](uploads/250ccc4ee94ddd2d24902536a1892934/image.png)

- Enter the credibility process name:

![image](uploads/a1c50cfbcabbf0ee9618194035caaa18/image.png)

- Select the project type (Web):

![image](uploads/d8791f817cbb6143af854c8a94277312/image.png)

- Enter the server URL and click "Test Connection":

![image](uploads/bcb1e55cc656fccc60c03a49c069ff51/image.png)

- Select "Existing Web Project":

![image](uploads/d642c43a4709c8e3f2dc3e978f5cb558/image.png)

- Select the project and click "Finish":

![image](uploads/9db061286b39a6ff4136b00d471147da/image.png)

- The project will open and you will be prompted to enter your credentials, just click "Connect":

![image](uploads/68aec19a6ae2dcb5c5a0d437baa18c4f/image.png)

The web project is open:

![image](uploads/a8db9b403ea6e6e5eb3297a37c277e19/image.png)



## Eclipse File Associations
[Go back to Contents](#contents)

By default, files are opened by the Eclipse product. It is possible to change this behavior into the Eclipse preferences:

In the toolbar, Go to `Window > Preferences`:

![image](uploads/8a0a1d688b8875fcf3742ad8e0d3f42a/image.png)

Then in the left menu, go to `General > Editors > File Associations`:

![image](uploads/92302878949365c010384cf5cc9dfa0a/image.png)

In this screen you can add unknown file extensions and associate them to an editor (internal or external).
It is also possible to associate the editor by Content-Type. For that, Go to `General > Content Types`:

![image](uploads/013cff9ca18890dd527b069bea9306d3/image.png)



## Eclipse Web Browser
[Go back to Contents](#contents)

Eclipse has its own preferences to open a web page or a link. 

![image](uploads/d4f0ce58bf807111a9b7b46e4223add3/image.png)

To change the Eclipse behavior then follow the below steps:
- Go to `Windows` -> `Preferences` if on Windows OS (`Eclipse` -> `Preferences` on Mac OS X)
- Go to `General` -> `Web Browser`

Here you would see the Default system web Browser being selected. Click on New and Select your browser (Safari, Firefox, Chrome or IE) from Applications.

- Click `OK`



[Go back to the top of the page](#content-body)