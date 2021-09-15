This page references common errors and how to solve them.

[Go back to Wiki home page](home)

## Contents

[[_TOC_]]

## Maven: Project is not compiling with `Maven 3.6.1`
[Go back to Contents](#contents)

CF plugin is using `Maven Tycho` to automatically manage dependencies and build the plugin.
The last **Maven version compatible** with `Maven Tycho` is `<= 3.5.4` or `>= 3.6.3`. Please **do not use Maven version** `3.6.1` to compile cf plugin and Eclipse RCP projects.

## Missing Constraint: Require-Bundle error on org.eclipse.epp.logging.aeri
[Go back to Contents](#contents)

When launching the project both with `Run` or `Debug`, a **Missing Constraint: Require-Bundle** error may appear on *org.eclipse.epp.logging.aeri* plug-in as below: 

![MissingAeri](uploads/7aeacba9956f677aa2e85b5d7fd53ab1/MissingAeri.PNG)


In that case:
- Select the main project in `Project Explorer` view
- Click on the arrow next to the `Debug` button
- Go to `Debug Configurations...`
- Click on `Plug-ins` tab
- In `Launch with:` drop-down list, select `plug-ins selected below only`
- In **type filter text** field, type *aeri*
- Uncheck all filtered plug-ins as below: 

![DebugConfigurations](uploads/26f2c716d82f1e6367653773201d57c3/DebugConfigurations.PNG)

[Go back to the top of the page](#content-body)
