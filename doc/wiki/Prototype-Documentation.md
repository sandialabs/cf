This page describes the technical documentation to install and run Credibility Framework Concurrency Support.

[Go back to Wiki home page](home)

## Contents

[[_TOC_]]

## Create database (and user if necessary)

- Execute the following SQL queries (change user and password by the desired ones):

```sql
CREATE DATABASE credibility_db;

CREATE USER 'cf_user'@'localhost' IDENTIFIED WITH mysql_native_password BY 'P@s$w0rd';

GRANT ALL PRIVILEGES ON credibility_db.* TO 'cf_user'@'localhost';
```

## Get springboot webapp archive folder

- Unzip webapp folder into the desired folder

## Configure springboot webapp

- Go into the `config` folder and open the `application.properties` file:

	- Change the database connection and credentials to match your configuration:

```yml
spring.datasource.url=jdbc:mysql://localhost:3306/credibility_db?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=cf_user
spring.datasource.password=P@s$w0rd
```

## Run springboot webapp

- Open a new terminal into the webapp folder and execute the following command line:

```shell
java -jar webapp-1.0.1.CSX.jar
```

- The webapp will run by default on port 8080 (http://localhost:8080/)

- To change default port append `--server.port={NEWPORT}` to the command:

```
java -jar webapp-1.0.1.CSX.jar --server.port={NEWPORT}
```

## Test webapp running

- Open the following page into a browser http://localhost:8080/

- You can replace localhost by your local ip address

**Tip:** If the database connection is not successful, the webapp will display an error message in the terminal and stop. Please configure application.properties file.

## Stop springboot webapp

- In the terminal hit `Ctrl` + `C`. This combo will stop the server.


## Package (dev)

- Package as SpringBoot .jar file:

```
mvn clean package spring-boot:repackage -Dpackaging=jar
```

- Package as java webapp .war file:

```
mvn clean package spring-boot:repackage -Dpackaging=war
```

## Concurrency Support in the plugin

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