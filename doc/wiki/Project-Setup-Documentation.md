This page references the tools and installation setup for a new developer to contribute cf project.

[Go back to Wiki home page](home)

## Contents

[[_TOC_]]

## Required
[Go back to Contents](#contents)

### Pre-requisite
New developers must install following softwares to contribute on cf plugin:
- [JDK 8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Eclipse RCP 2021-12 R](https://www.eclipse.org/downloads/packages/release/2021-12/r/eclipse-ide-rcp-and-rap-developers-includes-incubating-components)
- A git client (in command line or other client...)
- A HSQLDB database manager (such as Dbeaver Eclipse plugin (see [Dbeaver](#dbeaver) or [DB Visualizer](https://www.dbvis.com/) (external tool))

### Configure Eclipse

#### Set JDK11

- Go to `Window > Preferences`
- Then `Java > Compiler`
- Select Complier comliance level 11

![image](uploads/fea967dd619c410b4108d3fc1546cdca/image.png)

- Go to `Window > Preferences`
- Then `Java > Installed JREs`
- Check if you are using JDK 11

![image](uploads/2a8ace21353f3895714fb82e9f625bd6/image.png)

- If not, click `Add`, select `Standard VM` and `Next`:

![image](uploads/50f6ed007bb4f33defa9dd97158e586f/image.png)

- Click Finish
- Select jdk 11 and click `Apply and Close`
- Remove jre 11 to avoid confusion

#### Activate warnings on non-externalized strings: 

- Go to `Window > Preferences`
- Then `Java > Compiler > Errors/Warnings`
- Select `Warning` for `Non-externalized string (missing/unused $NON-NLS$ tag)`:

![image](uploads/94fdfb5063e13b2aa5591ce7df30a10b/image.png)

The compiler will display a warning next to a non-externalized string, like below:

![image](uploads/70c43f9271f5040d6ea24efe4b4f631c/image.png)

To override the warning, add the following comment next to the line **//$NON-NLS-1$**:

![image](uploads/b3cd7ab1506eb72db9e830db2c60f431/image.png)


#### Activate warnings on javadoc missings/malformations: 

- Go to `Window > Preferences`

- Then `Java > Compiler > Javadoc`

- Select and check boxes as the following:

![image](uploads/75c1f53a06594521c3187e7130ba83da/image.png)

- Apply and rebuild

### Server configuration

For the web project, the Server plugin has to installed into Eclipse.

To check its installation, go to `Window > Show view > Other...`, and search for `Server` view.

![image](uploads/a1652a1349ac7ca491580ee80446d75d/image.png)

If the Server view is not there, install it:

- Go to `Help > Eclipse Marketplace`, search for "Eclipse Web Developer Tools" and install it:

![image](uploads/8ca11c5941f664adde98ba419b74ce66/image.png)

- Select All, click `Confirm`, accept licence and click `Finish`. Eclipse will restart and the Server view will be available.

### Maven

Maven is used to verify, test and build the plugin, feature and update-site. It is necessary to install it. Last supported version is `Maven 3.6.3` (do not use Maven 3.6.1 which has conflicts with Tycho, the OSGI Maven tool). 

See [Maven install tutorial](https://maven.apache.org/install.html).

### Maven Tycho

Maven Tycho is an extension of Maven specific for building Eclipse OSGi bundles. CF projects are builded with Maven Tycho.

To install it:

- Go to `Window > Preferences`

- Select `Maven > Discovery` and click on `Open Catalog`

![image](uploads/8c9f763decb2d9e44c9e727974cd2a98/image.png)

- In the m2e catalog, search for "tycho", select `Tycho Configurator` and click `Finish`:

![image](uploads/d4e13ccfc3f9b72e526cdd5179a6fcde/image.png)

- Click `Next`:

![image](uploads/e28b1bba20537fe1f19df314330b2a0a/image.png)

- Accept licence, click `Finish` and `Restart now`. Tycho will be downloaded and installed.

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

![image](uploads/6d6c2fdd1f06644d4b1cc0783cd558e3/image.png)

  - Click `Next`, then `Finish`. Your eclipse will restart and opcoach preferences will be installed.

### Sonarlint

CF project uses Sonar engine to check the code quality in the CI. To avoid bad pratices, this plugin will help to commit code compliant with Sonar rules.

- Install Sonarlint to activate code quality analysis:
  - Go to `Help > Eclipse Marketplace...`:
  - Search for `Sonarlint` and install `SonarLint`:

![sonarlint_plugin](uploads/72a2b10b9abdcbdd548c753336ea9605/sonarlint_plugin.png)

- Launch Sonar analysis:
By default, Sonar will be executed on each opened file. A blue underline will appear if the code needs to be refactored. The complete view will appear under the `Problems` view:

![image](uploads/b00c91fd0670a5f653e8cfbb084e3900/image.png)

You can launch a full scan on an element by right-clicking on it, then `SonarLint > Analyze`. The SonarLint will be opened with all the issues detected:

![image](uploads/36eecb475c8726d5b7cdec6f9101e8d5/image.png)

### SpotBugs

SpotBugs will be executed by the CI (Continuous Integration) pipeline of the project and will reject all bugs and bad practices found according to Spotbugs. To avoid the CI pipeline to fail, the Eclipse SpotBugs plugin will help the developer to make it works.

- Install SpotBugs to detect bugs and bad practices:
  - Go to `Help > Eclipse Marketplace...`:
  - Search for `Spotbugs` and install `SpotBugs Eclipse plugin`:

![image](uploads/e6a46169de7d5e720ba8d94978869664/image.png)

- Configure SpotBugs:
  - Go to `Window > Preferences`
  - Search for SpotBugs under Java

![image](uploads/1bb1f01dd1af6469ee4902ba304b7cf4/image.png)

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

### Dbeaver

To manage HSQLDB database, a good way is to use Dbeaver Eclipse plugin.

It is present in the `Eclipse Markerplace` (Menu Help > Eclipse Marketplace...):

![image](uploads/b8ecd44119407943b916541fc451d97f/image.png)

Once installed, you can create a `New Database Connection` in the menu `Database > New Database Connection`:

![image](uploads/1c40be405a5d8357b06ff5d8f5e946bb/image.png)

Select `HSQLDB Embedded` in the database type and `Next`:

![image](uploads/cb05cf3e3a1a5eaa7bf89ce2267848f7/image.png)

Enter the database information and click `Finish`:
- `Path`: enter the path of the `<filename>.cf` file temporary folder followed by **\data\credibility** (data is the database container, credibility is the database name)
- `Username`: SA

![image](uploads/7557e38887bdc704cd891cf9ec75a73a/image.png)

A `Database Browser` view should appear containing the database schema:

![image](uploads/cf793ae7e8f50c364bfcd4e1a6a3d8c9/image.png)

If it doesn't you can find it in the Eclipse view, menu `Window > Show View > Other...`, search for `Database > Database Browser`:

![image](uploads/6c33f3341f4fd4201d3a275ec132dd4a/image.png)

### Lombok

Lombok is Java generator used to simplify source code. It can generate getter, setter, equals methods with one annotation. It automates logging variables and much more.

It is used on the web project. 

To install it follow this instructions:

- Download `lombok.jar` file from here: https://projectlombok.org/download

- Open terminal and change directory to the path where the downloaded file is located

- In the terminal, run this command: `java -jar lombok.jar`

- With the above command, an installer will open and would auto scan for the installation of Eclipse IDE. If you already know the IDE installation location, provide it by clicking the “Specify Location” button.

![image](uploads/d27273be7df7eeb2a70afaaedbbd4c2a/image.png)

- Once the wizard has located the IDE installation location, click the “Install button”. Close the Installer when done.

![image](uploads/45cccd2ebc8a6c0e2beda4f83551760d/image.png)

- Restart the Eclipse IDE if already running

That’s all. Now you can use Lombok’s annotations.

### Jautodoc

Jautodoc helps to generate Javadoc content on classes, methods, fields. It is also useful to add file headers (e.g. licence headers).

To install it:

- Go to `Help > Eclipse Marketplace`, search for "jautodoc" and click `Install`:

![image](uploads/9d685424668e0cdcfdf424078c721cd2/image.png)

- Accept licence, click `Install anyway` and `Restart now`


## Tests
[Go back to Contents](#contents)

- Add JUnit 5 to buildpath;
   - Select project `gov.sandia.cf.plugin.tests` buildpath, right click on project `gov.sandia.cf.plugin.tests` and click on `Build Path`, `Configure Build Path`

![image](uploads/6bae5edaa63567b818f313b6c6c8c42d/image.png)

   - Click on `Add Library`

![image](uploads/fe8672526678b6098b3cce707b8cf103/image.png)

   - Select `JUnit` then `Next`

![image](uploads/aae0a1686e1f15d8c19af8e51f64116a/image.png)

   - Select `JUnit 5` then `Finish`

![image](uploads/fddbd9b0fcf139c031258a4857547804/image.png)
   - Verify that JUnit 5 has been added to buildpath and click `Apply and Close`

![image](uploads/bd57eee7756269e038e85adfb29827c0/image.png)

- Install mockito plugin in Eclipse:
   - Go to `Help > Install New Software`
   - In `Work with` section enter Orbit's repository uri: "http://download.eclipse.org/tools/orbit/downloads/drops/R20181128170323/repository"
   - In **type filter text** field enter "mockito"
   - Select all `Orbit Bundles By Scope: Testing` section and click `Next`, then `Finish`. Your eclipse will restart and logback logger will be installed.

![image](uploads/d76019a839fb82352bc18661bc82db1d/image.png)

## Sources
[Go back to Contents](#contents)

Project sources can be downloaded with git pull command on following branches:
- `develop`: the default branch. Contains the current work, the snapshots and release candidates
- `master`: contains releases versions

Each sub-branch should start with the number of the associated issue into Gitlab (i.e. 62-swtbot-tests references issue #62).

## Importing plugin project into Eclipse
[Go back to Contents](#contents)

1. In Eclipse workspace, click on `File > Import`:

![image](uploads/32dab33762882f8351cf41d737253de7/image.png)

2. In the Import wizard, select `Maven > Existing Maven Projects`:

![image](uploads/5fdc8af0b6cd2ab400daad9b5815b66c/image.png)

3. Click on `Browse` in the next view and select the CF source code in `<git-cf-directory>/src/gov.sandia.cf`:

![image](uploads/92db0b3f69e38c3c8befbd8814f05c92/image.png)

![image](uploads/0e4a2a3fe0775466e7475440bda73434/image.png)

4. Select `all the Maven projects` and click on `Finish`:

![image](uploads/dedb17a93e9891742a1f9f3ee1d3f60f/image.png)

5. Some maven dependencies will be downloaded (like Tycho). Your Eclipse will restart. After that, your project is imported.


## Maven
[Go back to Contents](#contents)

CF project uses `Maven Tycho` to compile sources. `Maven Tycho` is a combination of Eclipse RCP dependency definition and maven. Dependencies are searched in normal Eclipse RCP files (plugin.xml, feature.xml, site.xml...). But it is possible to **compile the project in command line** with maven. See [Maven Tycho plugin tutorial](https://www.vogella.com/tutorials/EclipseTycho/article.html)

### Install Maven

- Install maven (last supported version is Maven 3.8.1, do not use Maven 3.6.1) See [Maven install tutorial](https://maven.apache.org/install.html).
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
- Type `mvn clean install -"Dmaven.test.skip"=true` to build the project and run unit tests

### Build with Maven

- Open a command line tool (Terminal, cmd,...)
- Go to the CF git repo on your local machine and then go to `src\gov.sandia.cf`
- Type `mvn clean install` to build the project and run unit tests


[Go back to the top of the page](#content-body)