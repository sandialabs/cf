The goal of this page is to explain Credibility Framework plugin and webapp installation.

[Go back to Wiki home page](home)

## Contents

[[_TOC_]]

## Obtain Credibility Framework
[Go back to Contents](#contents)

The builds of the Credibility Framework can be found into [Gitlab Package Registry](https://credibilityframework.gitlab.io/cf/packages/).

It contains all the available versions of the plugin.

Two zipped files will be available:
- `gov.sandia.cf.p2_repo.{VERSION}.zip` including the plugin installation folder
- `gov.sandia.cf.webapp.{VERSION}.zip` containing the webapp jar and the property files

![image](uploads/c55f835bd79ded7c3f03146e71715ab2/image.png)

To install a `specific version`, click on the desired one. Otherwise select the `latest` folder which contains the latest build from the repository (development or stable release).

Then copy the `p2 repository` url or download the zipped repository:

![image](uploads/de94e95eddb1e1a9a1139b6cdeac8b5a/image.png)


## Credibility Framework plugin

### Install plugin
[Go back to Contents](#contents)

1. Launch Eclipse

2. Go to `Help` menu, then click on `Install New Software`

![image](uploads/bc6fe3ae5f245953c18865bdccd7fac8/image.png)

3. In the opened window, enter the p2 repository url (e.g. https://credibilityframework.gitlab.io/cf/packages/latest/), select `Credibility Feature` and click `Next`

![image](uploads/578e6ff5ad58da946f56242f2ea5d65d/image.png)

4. Click `Next` again

![image](uploads/d933a569f25cbf45875e08aad0fd2723/image.png)

5. Check `I accept the terms of the license agreement` and click `Finish`

![image](uploads/224b8617e7ea6b09be3f0752d1e0d713/image.png)

6. Click `Install anyway` in Security Warning window

![image](uploads/8e6d579dbab3e4239fb29264ffb1f66e/image.png)

7. Click `Restart Now`. Eclipse will restart with your plugin installed

![image](uploads/8b4681bdaa4d67aecd7220a241e0ec62/image.png)


### Install plugin in a disconnected environment
[Go back to Contents](#contents)

1. Download the plugin repository from CF site [https://credibilityframework.gitlab.io/cf/packages/](https://credibilityframework.gitlab.io/cf/packages/).

Select the needed version and click on the zipped repository to download it:

![image](uploads/a1a31433e28a38616477c4a9393ef6fb/image.png)

2. Launch Eclipse

3. Go to `Help` menu, then click on `Install New Software`

![image](uploads/bc6fe3ae5f245953c18865bdccd7fac8/image.png)

4. In the opened window, click on `Add` button

![image](uploads/7f8761a9b2af6f76c990f0b2e70a7d37/image.png)

5. Click on `Archive` button and select the plugin zip

![image](uploads/03faee1309ba56056e5b7c7b1c0d418e/image.png)

![image](uploads/92fbd7eca6b31e0c3f7cb926d19ec673/image.png)

6. Click on `Add` button

![image](uploads/a54f37bb937a3f5dd07746eea1695b52/image.png)

7. Select `Credibility Feature` and click `Next`

![image](uploads/289af819ba4ad97fb557e6bfa957831a/image.png)

8. In a disconnected environment, disable the option to contact update sites:

- Solution 1, `disable contacting the update sites` during installation phase:

![image](uploads/47b3c5fac1c900c4af4de0c4f87faf62/image.png)

- Solution 2, `disable the other update sites`:

Go to `Window > Preferences`, then search for `Install/Update > Available Software Sites` and uncheck all the other update sites except the cf one:

![image](uploads/4034a9b47b3ba492876a6c9699500975/image.png)

![image](uploads/2fbdf36ba54613e86691999c2e22a08a/image.png)

https://stackoverflow.com/questions/11219215/eclipse-hangs-when-installing-new-software

9. Click `Next` again

![image](uploads/d933a569f25cbf45875e08aad0fd2723/image.png)

10. Check `I accept the terms of the license agreement` and click `Finish`

![image](uploads/224b8617e7ea6b09be3f0752d1e0d713/image.png)

11. Click `Install anyway` in Security Warning window

![image](uploads/8e6d579dbab3e4239fb29264ffb1f66e/image.png)

12. Click `Restart Now`. Eclipse will restart with your plugin installed

![image](uploads/8b4681bdaa4d67aecd7220a241e0ec62/image.png)

### Check plugin installation
[Go back to Contents](#contents)

1. Launch Eclipse

2. Go to `Help` menu, then click on `About Eclipse`

![image](uploads/c903415f34d9f5f6141cd39e2f96f581/image.png)

3. Click `Installation Details`

![image](uploads/94663808d65a6d485e130cc4808b8ccd/image.png)

4. Find `Credibility Feature` in current window, or type `Credibility Feature` in search bar

![image](uploads/075192000a9af3989c56770561f34434/image.png)

## Update plugin
[Go back to Contents](#contents)

1. Click on menu `Help > Check for updates`:

![image](uploads/518eef52551ab0b0698b33083d8f4f6d/image.png)

Eclipse will check the update sites:

![image](uploads/c073c043b72b8e252bb6748abe9929b4/image.png)

2. Check `Credibility Framework` and click `Next`:

![image](uploads/6e9e71c63126a75ef9adad5b40bd7b8e/image.png)

3. Click `Next`:

![image](uploads/5b5b65afd044166fbfaa053118686c9c/image.png)

4. Accept License terms and click `Finish`:

![image](uploads/69d082e76a0ae2f8de443018b83d78a4/image.png)

5. Click `Install anyway`:

![image](uploads/1cb3e45834a97c0b4ef6a97d0da23dd8/image.png)

6. Click `Restart Now`:

![image](uploads/6d8d577f320097afb1fcde7401153911/image.png)

Your plugin is updated.

## Uninstall plugin
[Go back to Contents](#contents)

1. Launch Eclipse

2. Go to `Help` menu, then click on `About Eclipse`

![image](uploads/ca92bece1dc1b4175f5b8b02042f765c/image.png)

3. Click `Installation Details`

![image](uploads/21e30a772b8021f50851b9c0ed5198aa/image.png)

4. Select `Credibility Feature` in current window and click `Uninstall`. You can search plugin by typing `Credibility Feature` in search bar

![image](uploads/d1a48fe48c81744da3abc24f2124ebe4/image.png)

5. Click on `Finish` to confirm uninstallation

![image](uploads/3334462d1bed87420d28a5b42ad4096e/image.png)

6. Click `Restart Now`. Eclipse will restart with your plugin uninstalled

![image](uploads/88e902611985fdab743958ce76b00b09/image.png)


### Dependencies

#### ARG

CF is using ARG (Automatic Report Generator) project to build and generate reports. See [Credibility Report](functional-specifications#credibility-report) feature description in the functional specifications.

See [ARG Wiki page](https://gitlab.com/AutomaticReportGenerator/arg/-/wikis/home) and/or [ARG documentation page](https://automaticreportgenerator.gitlab.io/arg/) for furthermore informations about how to install ARG.


## Credibility Framework Webapp

### Create database (and user if necessary)

- Execute the following SQL queries (change user and password by the desired ones):

```sql
CREATE DATABASE credibility_db;

CREATE USER 'cf_user'@'localhost' IDENTIFIED WITH mysql_native_password BY 'P@s$w0rd';

GRANT ALL PRIVILEGES ON credibility_db.* TO 'cf_user'@'localhost';
```

### Get springboot webapp archive folder

- Unzip webapp folder into the desired folder

### Configure springboot webapp

- Go into the `config` folder and open the `application.properties` file:

	- Change the database connection and credentials to match your configuration:

```yml
spring.datasource.url=jdbc:mysql://localhost:3306/credibility_db?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=cf_user
spring.datasource.password=P@s$w0rd
```

### Run springboot webapp

- Open a new terminal into the webapp folder and execute the following command line:

```shell
java -jar webapp-1.0.1.CSX.jar
```

- The webapp will run by default on port 8080 (http://localhost:8080/)

- To change default port append `--server.port={NEWPORT}` to the command:

```
java -jar webapp-1.0.1.CSX.jar --server.port={NEWPORT}
```

### Test webapp running

- Open the following page into a browser http://localhost:8080/

- You can replace localhost by your local ip address

**Tip:** If the database connection is not successful, the webapp will display an error message in the terminal and stop. Please configure application.properties file.

### Stop springboot webapp

- In the terminal hit `Ctrl` + `C`. This combo will stop the server.


### Package (dev)

- Package as SpringBoot .jar file:

```
mvn clean package spring-boot:repackage -Dpackaging=jar
```

- Package as java webapp .war file:

```
mvn clean package spring-boot:repackage -Dpackaging=war
```



[Go back to the top of the page](#content-body)