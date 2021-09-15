This page references the tools and installation setup for a new developer to contribute cf project.

[Go back to Wiki home page](home)

## Contents

[[_TOC_]]

## Required Softwares
[Go back to Contents](#contents)

### Pre-requisite
New developers must install following softwares to contribute on cf plugin:
- [JDK 8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Eclipse RCP 2020-06 R](https://www.eclipse.org/downloads/packages/release/2020-06/r/eclipse-ide-rcp-and-rap-developers-includes-incubating-components)
- A git client (in command line or other client...)
- A HSQLDB database manager (such as Dbeaver Eclipse plugin (see [Dbeaver](#dbeaver) or [DB Visualizer](https://www.dbvis.com/) (external tool))

### Configure Eclipse

#### Set JDK8

- Go to `Window > Preferences`
- Then `Java > Installed JREs`
- Check if you are using JDK 8

![image](uploads/0423f621d8431d81ea592d915b172cf9/image.png)

- If not, click `Add`, select `Standard VM` and `Next`:

![image](uploads/06f46defeae6d1c4295229ad524e1300/image.png)

- Click Finish
- Select jdk 8 and click `Apply and Close`:

![image](uploads/b040b90abd411aabfd15bb19c75e8b0f/image.png)

- Remove jre 8 to avoid confusion

#### Activate warnings on non-externalized strings: 

- Go to `Window > Preferences`
- Then `Java > Compiler > Errors/Warnings`
- Select `Warning` for `Non-externalized string (missing/unused $NON-NLS$ tag)`:

![image](uploads/c12c6c4a185adcbfa703c6e864f8b3c7/image.png)

The compiler will display a warning next to a non-externalized string, like below:

![Annotation_2020-04-23_112304](uploads/374de958ca6261b4eacff26dc76f93a4/Annotation_2020-04-23_112304.png)

To override the warning, add the following comment next to the line **//$NON-NLS-1$**:

![Annotation_2020-04-23_112319](uploads/d4f14d3f4d751ce35552b7c0ec7eeed1/Annotation_2020-04-23_112319.png)

### Nebula plugins

- Install Nebula plugins in Eclipse:
   - Go to `Help > Install New Software`
   - In `Work with` section enter Nebula repository uri: "https://download.eclipse.org/nebula/releases/latest/"

   - In `Nebula Release Individual Widget` category, select:
     - `Nebula CDateTime CSS Feature`
     - `Nebula CDateTime Widget`
     - `Nebula Opal Breadcrumb Widget`
     - `Nebula Opal Commons Widget`
     - `Nebula Progress Circle Widget`
     - `Nebula Rich Text Feature`

![image](uploads/54f947fac34eca13fed0f3127ad24ae6/image.png)

  - Click `Next`, then `Finish`. Your eclipse will restart and opcoach preferences will be installed.

### Maven

Maven is used to verify, test and build the plugin, feature and update-site. It is necessary to install it. Last supported version is `Maven 3.6.3` (do not use Maven 3.6.1 which has conflicts with Tycho, the OSGI Maven tool). 

See [Maven install tutorial](https://maven.apache.org/install.html).

### SpotBugs

SpotBugs will be executed by the CI of CF project and will reject all bugs and bad practices found. To avoid the CI pipeline to fail, the Eclipse SpotBugs plugin will help the developer to make it works.

- Install SpotBugs to detect bugs and bad practices:
  - Go to `Help > Eclipse Marketplace...`:
  - Search for `Spotbugs` and install `SpotBugs Eclipse plugin`:

![image](uploads/4723a845940aa58d96f0dd5b18afc543/image.png)

- Configure SpotBugs:
  - Go to `Window > Preferences`
  - Search for SpotBugs under Java

![image](uploads/4971f73a10cd3feb0d387a24a2856924/image.png)

  - Set the following preferences:
    - analysis effort: Default
    - Report Configuration tab:
      - Minimum rank to report: 20
      - Reported (visible) bug categories: select all
      - Minimum confidence to report: Medium
      - Mark bugs with ... rank as: set all to Warning
    - Plugins and Misc. Settings:
      - Check `Run SpotBugs analysis as extra job (independent from build job)`
  - Click on `Apply and Close`

### Sonarlint

CF project uses Sonar engine to check the code quality in the CI. To avoid bad pratices, this plugin will help to commit code compliant with Sonar rules.

- Install Sonarlint to activate code quality analysis:
  - Go to `Help > Eclipse Marketplace...`:
  - Search for `Sonarlint` and install `SonarLint`:

![sonarlint_plugin](uploads/cc84f61b32bac7af20ef02ea05b7e72b/sonarlint_plugin.png)

- Launch Sonar analysis:
By default, Sonar will be executed on each opened file. A blue underline will appear if the code needs to be refactored. The complete view will appear under the `Problems` view:

![image](uploads/08315651359fb3a0c403c214abccf582/image.png)

You can launch a full scan on an element by right-clicking on it, then `SonarLint > Analyze`. The SonarLint will be opened with all the issues detected:

![image](uploads/9f32d3f3183af855f177451ed0537426/image.png)

### Dbeaver

To manage HSQLDB database, a good way is to use Dbeaver Eclipse plugin.

It is present in the `Eclipse Markerplace` (Menu Help > Eclipse Marketplace...):

![image](uploads/a52316f77e53696bbbe6f09ceda9f3d6/image.png)

Once installed, you can create a `New Database Connection` in the menu `Database > New Database Connection`:

![image](uploads/a133cc626acbff369657ea142065dc59/image.png)

Select `HSQLDB Embedded` in the database type and `Next`:

![image](uploads/327a28546187784b76ed970d974cd7fa/image.png)

Enter the database information and click `Finish`:
- `Path`: enter the path of the `<filename>.cf` file temporary folder followed by **\data\credibility** (data is the database container, credibility is the database name)
- `Username`: SA

![image](uploads/e7c21c638a4e18755c9c553be76782d6/image.png)

A `Database Browser` view should appear containing the database schema:

![image](uploads/f73bc47ad9ac245dbdddaccc6581b678/image.png)

If it doesn't you can find it in the Eclipse view, menu `Window > Show View > Other...`, search for `Database > Database Browser`:

![image](uploads/ea1d6e7b5bbbeb01f3bd540e44f74a83/image.png)

## Tests
[Go back to Contents](#contents)

- Add JUnit 5 to buildpath;
   - Select project `gov.sandia.cf.plugin.tests` buildpath, right click on project `gov.sandia.cf.plugin.tests` and click on `Build Path`, `Configure Build Path`

![2019-08-13_junit5_buildpath1](uploads/da15cb88232b8704ea0c1f8610c8416a/2019-08-13_junit5_buildpath1.PNG)

   - Click on `Add Library`

![2019-08-13_junit5_buildpath2](uploads/9079c44196b50d626e21401f6dcaba89/2019-08-13_junit5_buildpath2.PNG)

   - Select `JUnit` then `Next`

![2019-08-13_junit5_buildpath3](uploads/cc05ef83c61e668f28ccf8595ea410c0/2019-08-13_junit5_buildpath3.PNG)

   - Select `JUnit 5` then `Finish`

![2019-08-13_junit5_buildpath4](uploads/51a89ab02b39e23ed32b87a1eaf79f28/2019-08-13_junit5_buildpath4.PNG)

   - Verify that JUnit 5 has been added to buildpath and click `Apply and Close`

![2019-08-13_junit5_buildpath5](uploads/c29da46555515b55b43969e736267858/2019-08-13_junit5_buildpath5.PNG)

- Install mockito plugin in Eclipse:
   - Go to `Help > Install New Software`
   - In `Work with` section enter Orbit's repository uri: "http://download.eclipse.org/tools/orbit/downloads/drops/R20181128170323/repository"
   - In **type filter text** field enter "mockito"
   - Select all `Orbit Bundles By Scope: Testing` section and click `Next`, then `Finish`. Your eclipse will restart and logback logger will be installed.

![2019-08-13_mockito_libs](uploads/4aab5da546a95c9060210c7b38be346d/2019-08-13_mockito_libs.PNG)

## Sources
[Go back to Contents](#contents)

Project sources can be downloaded with git pull command on following branches:
- `master`: contains releases versions
- branch `4-prototype-sqlite-based-pirt-data-persistence`: contains plugin development version

## Importing plugin project into Eclipse
[Go back to Contents](#contents)

1. In Eclipse workspace, click on `File > Import`:

![Annotation_2020-02-26_141752](uploads/fd685879a37630ecf66596600d089b50/Annotation_2020-02-26_141752.png)

2. In the Import wizard, select `Maven > Existing Maven Projects`:

![Annotation_2020-02-26_135843](uploads/f18fc1d287e56e0f3c1cd14ed1bc428d/Annotation_2020-02-26_135843.png)

3. Click on `Browse` in the next view and select the CF source code in `<git-cf-directory>/src/gov.sandia.cf`:

![Annotation_2020-02-26_141630](uploads/a6016fd6572e6cc2e44728ccfe8046a4/Annotation_2020-02-26_141630.png)

![Annotation_2020-02-26_135913](uploads/07579d18161ea6d720f723d92b953dec/Annotation_2020-02-26_135913.png)

4. Select `all the Maven projects` and click on `Finish`:

![Annotation_2020-02-26_141528](uploads/40169839f86d4df38b470b84707ebf0b/Annotation_2020-02-26_141528.png)

5. Some maven dependencies will be downloaded (like Tycho). Your Eclipse will restart. After that, your project is imported.


## Maven
[Go back to Contents](#contents)

CF project use `Maven Tycho` to compile sources. `Maven Tycho` is a combination of Eclipse RCP dependency definition and maven. Dependencies are searched in normal Eclipse RCP files (plugin.xml, feature.xml, site.xml...). But it is possible to **compile the project in command line** with maven. See [Maven Tycho plugin tutorial](https://www.vogella.com/tutorials/EclipseTycho/article.html)

### Install Maven

- Install maven (last supported version is Maven 3.6.3, do not use Maven 3.6.1) See [Maven install tutorial](https://maven.apache.org/install.html).
- Add `{maven-install-dir}/bin` into the environment variables
- Start or Restart the **command line** tool (Terminal, cmd,...)

### Test with Maven

- Open a **command line** tool (Terminal, cmd,...)
- Go to the CF git repo on your local machine and then **go to** `src\gov.sandia.cf`
- Type `mvn clean install` to build the project and run unit tests

### Detect bugs with Maven and Spotbugs

- Open a **command line** tool (Terminal, cmd,...)
- Go to the CF git repo on your local machine and then **go to** `src\gov.sandia.cf`
- Type `mvn clean verify` to build the project and run unit tests

### Build without tests with Maven

- Open a **command line** tool (Terminal, cmd,...)
- Go to the CF git repo on your local machine and then **go to** `src\gov.sandia.cf`
- Type `mvn clean install -DskipTests` to build the project and run unit tests

### Build with Maven

- Open a command line tool (Terminal, cmd,...)
- Go to the CF git repo on your local machine and then go to `src\gov.sandia.cf`
- Type `mvn clean install` to build the project and run unit tests

[Go back to the top of the page](#content-body)