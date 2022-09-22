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

![image](uploads/1ff521e677f565d7eb708f2460e1aa3f/image.png)

### Create a new web project

You will be able to create an existing project with the "New Credibility Process" wizard:

- Right click the project and select New > Other...:

![image](uploads/c327d457f4f2703b31b05fba81d74f2b/image.png)

- Select Credibility Process:

![image](uploads/699de2ab9cbefa555d23d06377c7d8c7/image.png)

- Enter the credibility process name:

![image](uploads/1ce50568de127ae066235a48c0258b9d/image.png)

- Select the project type (Web):

![image](uploads/dfa81896ae71cfbb9abebf846c80fd09/image.png)

- Enter the server URL and click "Test Connection":

![image](uploads/34c3a5cc9636c271529b62295b369e74/image.png)

- Select "New Web Project":

![image](uploads/6e013331356970e6d1159d5676e9ea58/image.png)

- Enter the project information and click "Finish":

![image](uploads/281b6e8000d103d45f85389cd93c9960/image.png)

- The project will be created and will be prompted to enter your credentials, just click "Connect":

![image](uploads/d6d9f473acdf73ead52a48e90da96ed4/image.png)

The web project is open:

![image](uploads/b891ba944a4eee9a83e2ed7d1060a558/image.png)


### Connect to an existing web project

You will be able to connect to an existing project with the "New Credibility Process" wizard:

- Right click the project and select New > Other...:

![image](uploads/e781d4692904686bb9e84e439e9bd333/image.png)

- Select Credibility Process:

![image](uploads/bee4e12552d6ad2f1926232c8821297f/image.png)

- Enter the credibility process name:

![image](uploads/f0ad3e2bd5a40592e64cfafb5a5635a1/image.png)

- Select the project type (Web):

![image](uploads/336a64b7a202e62bee08cd3a3ad0c615/image.png)

- Enter the server URL and click "Test Connection":

![image](uploads/8a37d01b7ec60fcb5d6175ccf69f3d39/image.png)

- Select "Existing Web Project":

![image](uploads/87ac700b8c73337aa006dad8fa428e75/image.png)

- Select the project and click "Finish":

![image](uploads/5569bbdebfe3b2b048063d8e00ec0d56/image.png)

- The project will open and you will be prompted to enter your credentials, just click "Connect":

![image](uploads/49e93b082dd05329210c3ac4a4f5429b/image.png)

The web project is open:

![image](uploads/3a9921042692f958ab83b0b89c8f0f96/image.png)