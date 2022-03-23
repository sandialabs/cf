This page lists the Credibility Framework Requirements template files and explains how to generate configuration file needed to create a new Credibility project.

[Go back to wiki home page](home)

## Contents

[[_TOC_]]

## Description
Credibility Framework features need to be easily configured by a non-programmer. The choosen solution is to use an `Excel` file to describe the configuration, as it is commonly used.

`Yml` is a lightweight, easily understandable data description format which can be used as an entry point for software applications. It is currently used to configure credibility framework.

To convert `Excel` data to `Yml`, we use `VBA macro` embedded in the `Excel` file. After `Excel` file save, the `VBA macro` is executed and creates a new `Yml` file with the `Excel` file data.

The vba macro are into the following folder [doc/Requirements/vba](https://gitlab.com/iwf/cf/-/tree/develop/doc/Requirements/vba).

## Examples
[Go back to Contents](#contents)

The requirement files are present into [doc/Requirements](https://gitlab.com/iwf/cf/-/tree/develop/doc/Requirements) folder.

### PIRT

[PIRT_schema-V0.3.xlsm](uploads/85248b78f2f14b71ec9133875a908bb2/PIRT_schema-V0.3.xlsm)

### PCMM

- Elements and subelements with five levels of assessment and planning:

[PCMM_schema-With_Subelements_5_Levels-Assessment-v0.7.xlsm](uploads/14678c8240e9de6147ac92fce31fb597/PCMM_schema-With_Subelements_5_Levels-Assessment-v0.7.xlsm)

- Elements only with five levels of assessment and planning:

[PCMM_schema-No_Subelements_5_Levels-Assessment-v0.7.xlsm](uploads/ddc0cf049250b2b18544ad0c911e0349/PCMM_schema-No_Subelements_5_Levels-Assessment-v0.7.xlsm)

- Elements only with three levels of assessment and planning:

[PCMM_schema-No_Subelements-3_Levels-Assessment-v0.2.xlsm](uploads/9814521bdf9e1aed8fdb0c1995bf8c76/PCMM_schema-No_Subelements-3_Levels-Assessment-v0.2.xlsm)

### Analyst Decision

[ModSim_Decision-v0.1.xlsm](uploads/e364266c8b60e2a4ca4bc24d357eaadf/ModSim_Decision-v0.1.xlsm)

### Quantity of Interest Planning

[QoI_Planning-v0.2.xlsm](uploads/5685c047da159fdc325b77045b60e0eb/QoI_Planning-v0.2.xlsm)

### System Requirements

[Requirement_Parameter-v0.1.xlsm](uploads/e3408d4ff4d3c9c6ffe357c54147aab2/Requirement_Parameter-v0.1.xlsm)

### Uncertainty

[Uncertainty_Parameter-v0.2.xlsm](uploads/7cead57a5cf81e0bdb97a60067f2f5d6/Uncertainty_Parameter-v0.2.xlsm)

## Export Excel Requirement file to a Yml CF Configuration File
[Go back to Contents](#contents)

It is possible to export the convert the Excel spreadsheet to a yml file which will be ingested by cf.

This converter is based on Excel macro. The Excel macro are available in the following folder in the cf gitlab repository (\doc\Requirements\vba).

To manage Excel macro you need to enable the Developer tab on Excel (see [How to turn on Developer tab](#how-to-turn-on-developer-tab) or Microsoft support website topic [Show Developer tab](https://support.office.com/en-us/article/show-the-developer-tab-e1192344-5e56-4d45-931b-e5fd9bea2d45)). 

1. **Import** the vba macro to the spreadsheet:
- Go to `Developer` tab and click on `Visual Basic` button:

<div align="center">
![Annotation_2020-03-18_151352](uploads/ae03096b5703f366a96a454152c2d0ab/Annotation_2020-03-18_151352.png)
</div>

- Right-click on `Microsoft Excel Objects`:

<div align="center">
![Annotation_2020-03-18_151413](uploads/2b367d45df478dba6d9832e643c69ad0/Annotation_2020-03-18_151413.png)
</div>

- Click on `Import a file`:

<div align="center">
![Annotation_2020-03-18_151425](uploads/597ecbe7c1a04bc9c0ceb86c5255b7ed/Annotation_2020-03-18_151425.png)
</div>

- Select the `Excel macro file *.bas`:
  - For PIRT, select the file `PIRTConvertModule.bas`
  - For PCMM, select the file `PCMMConvertModule.bas`

<div align="center">
![Annotation_2020-03-18_160251](uploads/0307702e7bf166ee1cd41d5ff8199fd8/Annotation_2020-03-18_160251.png)
</div>

- You should have the macro into `Modules`:

<div align="center">
![Annotation_2020-03-18_151505](uploads/6a4a41274d17d94c611dab6d7e57252c/Annotation_2020-03-18_151505.png)
</div>

2. **Run** the macro:
- In the Developer tab, click on `Macros` button:

<div align="center">
![Annotation_2020-03-18_151518](uploads/74631dc6c6d15971ce595b2f732af454/Annotation_2020-03-18_151518.png)
</div>

- **Select** the macro and click on `Run`:
  - For PIRT, select the file `ExportPIRTToYaml` macro
  - For PCMM, select the file `ExportPCMMToYaml` macro

<div align="center">
![Annotation_2020-03-18_151530](uploads/a71faa4422e9f71c71c39aac99c07b1a/Annotation_2020-03-18_151530.png)
</div>
- You should have the generated yml file in the same path as your Excel Requirement file:

<div align="center">
![Annotation_2020-03-18_151548](uploads/97ddb9c8f2d7ef5f4e50d7f6a7ce6bd3/Annotation_2020-03-18_151548.png)
</div>

## How to turn on `Developer` tab:
[Go back to Contents](#contents)

- **Right click** anywhere on the **ribbon**, and then click `Customize the Ribbon`:

<div align="center">
![image](uploads/c2fd71544bc3b8413ac53d8828b3b6ba/image.png)
</div>

- Under Customize the Ribbon, on the right side of the dialog box, select `Main tabs` (if necessary):
- Check the `Developer` check box:

<div align="center">
![image](uploads/9be2dfed44ea4a87d8a8b1fd751c0dfc/image.png)
</div>

- Click `OK`
- You can find the Developer tab next to the View tab:

<div align="center">
![image](uploads/bcb487799345b12ed1fe1de7dd25e18b/image.png)
</div>

## PIRT Configuration
[Go back to Contents](#contents)

The PIRT Excel files contains 5 tabs:
- `Ranking Guidelines`
- `Header`
- `Adequacy`
- `Level`
- `Level Difference Coloring`

<div align="center">
![image](uploads/4eafa3e2c8cec9437ee057f51a2579a0/image.png)
</div>

The expected elements of configuration for PIRT feature are:
- `Header`: contains QoI header fields. 
  - `Fields`: The header contains a `Fields` tag which contains all the headers by name.
- `Adequacy` columns: contains PIRT table adequacy columns and their types. 
  - `Fields`: The adequacy tag contains a `Fields` tag which contains all the adequacy columns by name.
    - `<Adequacy column name>`
      - `Type`: Each column must define its type in the tag `Type`. Currently three types are implemented: *Levels* for adequacy assessment for phenomenon, *Text* for free input (like Comments field), and *RichText* for rich text content in HTML with editing tools and support.
- `Levels`: contains the adequacy columns values if column type is *Levels*. (e.g. H:High, M:Mean, L:Low, N/A:N/A, N:Not Adressed). 
  - `Fields`: The levels tag contains a `Fields` tag which contains all the levels by name.
    - `<Level name>`
	  - `NumericalValue`: Each level has a numerical value to compare rank with expected phenomenon importance. The difference between expected numerical value and current numerical value will have effects on column display.
	  - `Label`: The name to display in the plugin
- `Level Difference Coloring`: contains level difference colors between expected importance and current criterion level value. 
  - `Fields`: The levels difference tag contains a `Fields` tag which contains all the colors by name.
    - `<Level Difference Name>`
	  - `RGB`: Colors are implemented as rgb format (e.g. 0,255,0 for green). 
	  - `Description`: The description of the color difference to display in the guidance.
By default, three levels are implemented, *positive or zero*, *one level*, *two or more levels*. But it is possible to add a fixed color to a specific level by adding the `<Level name>` as `<Level Difference Name>` and setting the `RGB` and `Description` tags.
- `Ranking Guidelines`: gives the Adequacy columns purposes and their level description to allow the user to correctly fill in the PIRT table. This guidelines will be displayed in the Eclipse view `Credibility Framework Guidance`. 
  - `Guidelines`: the guidelines root tag.
    - `<Adequacy column name>`: the adequacy column name as described above.
	  - `Description`: the description of the adequacy column.
	  - `<Level name>`: the level name as described above. Aside the tag, the description of the level for this adequacy column can be added.

You can find an example in [PIRT Requirements](#pirt) section.

The cell A1 of each tab may contain a comment describing the tab content.

### Ranking Guidelines tab
This tab gives the PIRT guidelines. The user can add as much guidelines as needed. Each level guideline must be separated by at least one empty row. The VBA macro will inspect column B to search for a PIRT criteria name.

<div align="center">
![image](uploads/78bd60bc6fc24787ec0ffb5478ad0602/image.png)
</div>

<div align="center">
![image](uploads/d4534d245f93c91f275b5cd96543ec71/image.png)
</div>

### Header tab
This tab contains the PIRT view header table fields. The user can add as much field as he wants. The field names must be added in column B, starting at cell B3.

<div align="center">
![2019-07-22_pirt-conf-excel-header](uploads/ae4fa24cb9e1f460f98344b7b5b03f0c/2019-07-22_pirt-conf-excel-header.PNG)
</div>

<div align="center">
![image](uploads/130d64060bc2c6dc81d6c3f7d0c7c86c/image.png)
</div>

### Adequacy tab
This tab contains the criterion columns to add to the phenomenon description table. It has two fields: 
- `Fields` for the adequacy column name
- `Type` for the column Type

Currently, two types are supported, *Levels* for the criterion importance and *Text* for free text value. The user can add as much field as he wants. The field names must be added in column B, starting at cell B3 and type in column C starting at cell C3.

<div align="center">
![2019-07-22_pirt-conf-excel-adequacy](uploads/63dc90a51248cb6376e95aec1c3a9afb/2019-07-22_pirt-conf-excel-adequacy.PNG)
</div>

<div align="center">
![image](uploads/1a82ccd89fa4ceed652304fa979b1536/image.png)
</div>

Here are columns of type `Levels`:
<div align="center">
![image](uploads/603ea8bc92a38fc735e6774d3dc2327c/image.png)
</div>

### Levels tab
This tab is to define *Levels* type. This type is associated to an adequacy column to describe the importance of the adequacy column. It has three fields:
- `Fields` to store type name
- `NumericalValue` to store the level importance value as an integer (which will be used to determine cell color)
- `Label` to define the label to be displayed in the phenomenon table. 

The user can add as much Levels as he wants. The field names must be added in column B starting at cell B3, the NumericalValue must be added in column C, starting at cell C3 and Label must be added in column D starting at cell D3.

<div align="center">
![2019-07-22_pirt-conf-excel-levels](uploads/8c8a26ae8aed3b915d4efa1ede6ba08e/2019-07-22_pirt-conf-excel-levels.PNG)
</div>
<div align="center">
![image](uploads/27b835573893645cce6ac75a8af3a1ff/image.png)
</div>

### Level Difference Coloring tab
This tab describe the colors to display depending of the level difference. Colors are only displayed in **levels typed** columns. 

There is two types of level colors:
- **Colors applied by default** to each level and computed **depending of the Importance column level**:
  - Positive or zero
  - One level
  - Two or more levels
 The column `NumericalValue` of the `Levels` tab is used to compute the difference between column `Importance`, which is fixed, and columns of type `Levels` listed in `Adequacy` tab.  
  
- **Fixed colors**: A color can be defined for a specific independently of the other colors

The `Level Difference Coloring` tab contains the following fields:
- `Fields` for the level difference key to store
  - `Positive or zero`
  - `One level`
  - `Two or more levels` 
  
This values must not be modified because it is fixed and cf-plugin code links to the values contained in this field to determine the color difference. 

- `RGB` to define the difference color in rgb format. Rgb colors can be modified in column C.
- `Description`: the description of the level color displayed in the [PIRT Guidance](functional-specifications#pirt-guidance).

<div align="center">
![image](uploads/e829f88c36a026d3c49159f9de5a6aa2/image.png)
</div>

<div align="center">
![image](uploads/51e18a3334b8c6d8287ab3e2b9152c93/image.png)
</div>

## PCMM Configuration
[Go back to Contents](#contents)

The PCMM Excel files contains several tabs depending of the number of PCMM Elements. The required tabs are:
- `PCMM-Elements`
- `PCMM-Levels`
- `PCMM-Activites`
- `PCMM-Roles`
- `PCMM-Planning-Questions`
- `PCMM-Planning-Data Model`

The variables tabs are those used to describe the PCMM Elements content. Each tab starts with `PCMM-{element-abbrev}` where `{element-abbrev}` is the PCMM Element abbreviation. For a full PCMM process you may find the following tabs:
- `PCMM-CVER`
- `PCMM-PMMF`
- `PCMM-RGF`
- `PCMM-SVER`
- `PCMM-VAL`
- `PCMM-UQ`

The expected elements of configuration for PCMM feature are:
- `Phases`: contains a list of the PCMM project Phases and functionnalities in the following list: [Evidence, Assess, Aggregate, Stamp]
  - `Evidence`: it permits to manage evidence. The evidence is needed to assess at a Level > 0.
  - `Assess`: this is to assess the evidence and elements/subelements.
  - `Aggregate`: the aggregate view computes a level value for all the assessments of a PCMM element.
  - `Stamp`: the stamp view displays a Kiviat chart (or spider chart) for the aggregation.
- `Roles`: contains the list of roles used to assess the elements/subelements. (e.g. Customer, Analyst, Code Developer, Experimentalist)
- `Levels`: contains the assessment levels. Each level has a numerical code and a color.
Here are the attributes of one level:
  - `Code`: the numerical value of the level. It is used to be compared to other levels and to aggregate levels results. It must start with 0.
  - `Color`: the color to display on the assessment view
- `Elements`: contains the PCMM elements. 
The elements must have the following attributes:
  - `Name`: the name of the element
  - `Color`: the color to display on the PCMM wheel
  - `Abbreviation`: the PCMM element abbreviation
  - `Levels`: the list of levels of the element (if the PCMM Mode is simplified)
  - `Subelements`: the list of subelements (if the PCMM Mode is by default)

Each element can contains multiple subelements. The subelements must have the following attributes:
  - `Name`: the name of the subelement
  - `Code`: the code of the subelement or abbreviation
  - `Levels`: the list of levels for this subelement

Each subelement can contains multiple levels. The levels must have the following attributes:
  - `Name`: the name of the level
  - `Code`: the numerical value of the level. It is used to be compared to other levels and to aggregate levels results. It must start with 0.
  - `Descriptors`: the list of descriptors for this level or guidance

Each level can havemultiple descriptors. The levels must have the following attributes:
  - `<Descriptor name>`: the name of the descriptor
  - `<Descriptor Value>`: the value of the descriptor

You can find an example in [PCMM Requirements](#pcmm) section.

### `PCMM-Elements` tab

The PCMM feature offers two different modes (see [PCMM Mode](functional-specifications#pcmm-mode)):
- **High rigor** or **Default**: contains PCMM Elements and PCMM Subelements
- **Low rigor** or **Simplified**: only contains PCMM Elements

The mode is defined by the presence of PCMM Subelements in the `PCMM-Elements` tab:
- **High Rigor**:

Each PCMM Element must define its subelements under the element name. At least one row must be left blank to indicates to the macro a new PCMM Element. The PCMM Element abbreviation needs to be defined to find the PCMM Element tab corresponding.

<div align="center">
![image](uploads/d62cd1b1ad3557e3c4a6be3f57885ce9/image.png)
</div>

- **Low rigor**:

At least one row must be left blank to indicates to the macro a new PCMM Element. The PCMM Element abbreviation needs to be defined to find the PCMM Element tab corresponding.

<div align="center">
![image](uploads/45482bd5d07d11a91897d438837d6cf0/image.png)
</div>

### `PCMM-{element-abbrev}` tab

The `PCMM-{element-abbrev}` tab (where {element-abbrev} is the abbreviation of the PCMM Element e.g. PCMM-UQ) defines the subelements and levels description. The levels must be defined in the `PCMM-Levels` tab, otherwise it will not be taken into an account.

**Column definition:**
- `Column A`: indicates the element or subelement abbreviation
- `Column B`: describe the level name for an element or subelement
- `Column C and after`: Each column after and including column C defines a **level descriptor**. There can be as much level descriptor as wanted.

- **High Rigor**:

Each PCMM Subelement must define its levels under the subelement name. At least one row must be left blank to indicates to the macro a new PCMM Subelement. 

<div align="center">
![image](uploads/ac6a44a080be6571e8836781fc4579b8/image.png)
</div>

- **Low rigor**:

There is only the levels defined.

<div align="center">
![image](uploads/2fca0e746d504bd465c75a72b9241b5a/image.png)
</div>

### `PCMM-Levels` tab

The levels tab lists all the **available levels** to assess the evidence with their columns:
- `Column B`: is for the level name
- `Column C`: is for the level color (color is converted to rgb by the VBA macro)

<div align="center">
![image](uploads/af28c09774c1b57a53707de05f39d0f9/image.png)
</div>

### `PCMM-Activities` tab

This tab lists all the **available options** of the PCMM feature. VBA macro is inspecting the `column B`:
- `Planning`: the PCMM Planning management option (see [PCMM Planning View](functional-specifications#pcmm-planning-view))
- `Evidence`: the PCMM Evidence management option (see [PCMM Evidence View](functional-specifications#pcmm-evidence-view))
- `Assess`: offer the possibility to assess the PCMM evidence (see [PCMM Assess View](functional-specifications#pcmm-assess-view))
- `Aggregate`: the aggregation view of all the assessments (see [PCMM Aggregate View](functional-specifications#pcmm-aggregate-view))
- `Stamp`: the aggregation view of all the assessments as a radar chart (see [PCMM Radar Plot/Quality Stamp View](functional-specifications#pcmm-radar-plotquality-stamp-view))

<div align="center">
![image](uploads/d82f081615d9d893b6886cbcb396fdbf/image.png)
</div>

### `PCMM-Roles` tab

This tab lists all the **available roles** to add evidence and assess. VBA macro is inspecting `column B`. The roles are free to edit and will be attached to an user (see [Assessor and Role](functional-specifications#assessor-and-role)).

<div align="center">
![image](uploads/78497f09287fec6fbd37df997ff03288/image.png)
</div>

### `PCMM-Planning-Questions` tab

The planning question tab lists all the **planning questions** to fill and to associate to a PCMM element or subelement:
- `Column B`: indicates the element or subelement abbreviation
- `Column C to the end`: is for the planning questions. Each question is added to a new column. All the questions will be associated to the PCMM element or subelement which is on the same row

<div align="center">
![image](uploads/8db4d4806373428bef55638b0389597f/image.png)
</div>

### `PCMM-Planning-Data Model` tab

<div align="center">![image](uploads/bf5edbe93da6084647075d4db6800350/image.png)</div>

The levels tab lists all the **available levels** to assess the evidence with their columns:
- `Column A`: references the Planning fields to associate to each subelement (high rigor mode) or to each element (low rigor mode). It is possible to enter a non predefined type. This type has to be defined further in this spreadsheet (e.g. action Item Table)
<div align="center">![image](uploads/3b71dcc221edfdb3022e779e41e566b0/image.png)</div>

- `Column B`: is for the level name
- `Column C`: is for the level color (color is converted to rgb by the VBA macro)

Each row of the definition table is identified:
- `Row 1`: is for the variable name
- `Row 2`: is the variable required?
- `Row 3`: the variable type

![image](uploads/9b504ebaaf71dba7e33db5d3ea29d55e/image.png)

## Generic Parameters Configuration

A common table definition has been designed for the Analyst Decision, QoI Planning, System Requirement and Uncertainty configuration.

This features displays tables and trees, and need to define each column behavior.

### Field Description

The first column of the spreadsheet is for the type definition associated to variable. This types are:
- `level`: the level of availability of the field in the tree. `*` or empty means always available. `>=2` means the field can not be defined for the root and its first child. It will be available for the subchild and after. It is optional.
- `default`: the default value of this field. It is optional.
- `required`: is the field required? It is optional. Default value is `Optional`. 
Available values are: 
  - `false` or `Optional`: the field is optional
  - `true` or `Required`: the field is mandatory
  - `Desired`: the field is optional but a warning message will be shown to incitate to complete it
- `type`: the field type definition. It is **mandatory**. 
Available values are:
  - `Credibility_Element`: the PCMM element to associate
  - `Date`: the date
  - `Float`: a float
  - `Link`: a link to a file in the workspace or a url
  - `RichText`: a richtext component with tect highlighting, underlining, styles, colors... (value converted in HTML)
  - `Select`: a select box with values taken into the `values` type
  - `System_Requirement`: the System Requirement to associate
  - `Text`: a simple text value
- `values`: It is optional. Lists all the exhaustive possible values for this field. 
- `constraints`: It is optional. Defines the field validation rules as a list. It is possible to reference another field with brackets (e.g; - ">${Minimum Value}"). One constraint per row.

<div align="center">![image](uploads/667ce283ec7b2966f45d5719e1eca13c/image.png)</div>

### Complex values

It is possible to use [Expression language](https://docs.oracle.com/javaee/6/tutorial/doc/gjddd.html) to construct complex validation rules and constraints for the above fields. See the following link to discover the [managed operators](https://docs.oracle.com/javaee/6/tutorial/doc/bnaik.html).

e.g. "Characterization" field has a specific `required` value: "Model Feature == 'yes'". It says required  if the "Model Feature" field value equals 'yes'.

```
Uncertainty Parameters:
  Characterization:
    required: "Model Feature == 'yes'"
    type: "Select"
    values:
      - "epistemic"
      - "epistemic - model form"
      - "aleatory"
  Model Feature:
    required: "Required"
    type: "Select"
    values:
      - "yes"
      - "no"
      - "N/A"
```


## Analyst Decision Configuration
[Go back to Contents](#contents)

The Analyst Decision Excel file contains different tabs. The feature definition is into `Constants` tab.

You can find an example in [Analyst Decision](#analyst-decision) section.

This spreadsheet is based on the [Generic Parameters Configuration](#generic-parameters-configuration) above. It generates a `yml` structure under the root key `Decisions`.

## QoI Planning Configuration
[Go back to Contents](#contents)

The QoIPlanning Excel file contains different tabs. The feature definition is into `Data Model` tab.

You can find an example in [QoIPlanning](#quantity-of-interest-planning) section.

This spreadsheet is based on the [Generic Parameters Configuration](#generic-parameters-configuration) above. It generates a `yml` structure under the root key `QoIPlanning`.

## System Requirement Configuration
[Go back to Contents](#contents)

The System Requirement Excel file contains different tabs. The feature definition is into `Constants` tab.

You can find an example in [System Requirement](#system-requirements) section.

This spreadsheet is based on the [Generic Parameters Configuration](#generic-parameters-configuration) above. It generates a `yml` structure under the root key `Requirement Parameters`.

## Uncertainty Configuration
[Go back to Contents](#contents)

The Uncertainty Excel file contains different tabs:
- `Data Model`: contains the feature definition
- `Inventory`: lists the predefined data to import into the CF project

You can find an example in [Uncertainty Parameters](#uncertainty) section.

This spreadsheet is based on the [Generic Parameters Configuration](#generic-parameters-configuration) above. It generates a `yml` structure under the root key `Uncertainty Parameters`.



[Go back to the top of the page](#content-body)