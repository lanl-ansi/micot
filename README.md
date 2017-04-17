
These badges cover the tests that do not rely on proprietary software

[![Build Status](https://travis-ci.org/lanl-ansi/micot.svg?branch=master)](https://travis-ci.org/lanl-ansi/micot)
[![codecov](https://codecov.io/gh/lanl-ansi/micot/branch/master/graph/badge.svg)](https://codecov.io/gh/lanl-ansi/micot)

# Description
Multi Infrastructure Control and Optimization Toolkit (MICOT)

MICOT is a tool for optimizing and controlling infrastructure systems. In includes modules for optimizing the operations of an infrastructure structure (for example optimal dispatch), designing infrastructure systems, restoring infrastructures systems, resiliency, preparing for natural disasters, interdicting networks, state estimation, sensor placement, and simulation of infrastructure systems. It implements algorithms developed at LANL that have been published in the academic community.

# Installation

## Maven Installation

MICOT is distributed as Maven project. To install MICOT as Maven project, follow these steps

1. Download [Apache Maven](https://maven.apache.org/download.cgi) and unzip the package into a location of your choice.
2. Update your PATH variable to point at the bin directory where the "mvn" executable is located
3. Download and install [Java 1.7 JDK (or later)](http://www.oracle.com/technetwork/java/javase/downloads/index-jsp-138363.html).
4. [Update](https://docs.oracle.com/cd/E19182-01/820-7851/inst_cli_jdk_javahome_t/) JAVA_HOME to point at directory of your Java installation.
5. Install a git tool
6. Download the repository using the following command ```git clone https://github.com/lanl-ansi/micot.git```
7. Build and package the code using the command ```mvn -Dmaven.test.skip=true package``` from the top level directory of the git repository

### LANL Only
If you are behind the LANL firewall, you need to tell Maven where the proxyserver is.  In ${user.home}/.m2/settings.xml add the following block (create settings.xml if it does not exist)

```xml
<?xml version="1.0"?>
<settings xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://maven.apache.org/SETTINGS/1.0.0">
<proxies>
<proxy>
<id>lanlproxy</id>
<active>true</active>
<protocol>http</protocol>
<host>proxyout.lanl.gov</host>
<port>8080</port>
</proxy>
</proxies>
</settings>
```

The maven build does not compile code that depends on [cplex](https://www-01.ibm.com/software/commerce/optimization/cplex-optimizer/). To build micot with cplex follow the steps provided in the CPLEX installation section below.

 
## Eclipse Installation

MICOT is distributed as an Eclipse project. To install MICOT as an eclipse project, follow these steps

1. Download and install [Java 1.7 JDK (or later)](http://www.oracle.com/technetwork/java/javase/downloads/index-jsp-138363.html).
2. Download and install [Eclipse](https://eclipse.org/ide/).
3. Install a git tool
4. Download the repository using the following command ```git clone https://github.com/lanl-ansi/micot.git```
5. Download [Apache Maven](https://maven.apache.org/download.cgi) and unzip the package into a location of your choice.
6. Update your PATH variable to point at the bin directory where the "mvn" executable is located
7. [Update](https://docs.oracle.com/cd/E19182-01/820-7851/inst_cli_jdk_javahome_t/) JAVA_HOME to point at directory of your Java installation.
8. From the top directory of the Micot repository run ```mvn eclipse:eclipse```
9. Import the git project in Eclipse.  File -> Import -> Projects from Git -> Existing Local Repository
  * Sometimes you have Right click on the project in Eclipse and choose Maven -> Update Project

The eclipse project is configured to compile without cplex. To build micot with cplex follow the steps provided in the CPLEX installation section below.

## [Optional] DCOM Installation 

Some functionality of MICOT requires interactions with third-party software through the Windows DCOM protocols. To install the native libraries required to interact with DCOM, follow these instructions

### LANL
1. Install a git tool
2. Download the native libraries using the following command ```git clone https://github.lanlytics.com:rbent/micot-libraries.git```

### Outside LANL
1. Download the [Java Com bridge](https://sourceforge.net/projects/jacob-project/) version 1.18.
2. Add the directory where the file "jacob-1.18-x64.dll" is located to your PATH environment variable

## [Optional] CPLEX Installation

If you wish to configure MICOT to work with CPLEX to perform optimization of MILP problems, follow these steps.

1. Buy [cplex](https://en.wikipedia.org/wiki/CPLEX)
2. Remove 
  ```xml
  <plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-compiler-plugin</artifactId>
  <configuration>
  <excludes>
  <exclude>**/cplex/*.java</exclude>
  </excludes>
  </configuration>
  </plugin>
  ``` 
  from pom.xml

3. Remove "excluding="**/cplex/*.java" in the line 
  ```xml
  <classpathentry excluding="**/cplex/*.java" kind="src" output="target/classes" path="src/main/java">
  ``` 
  of .classpath

### LANL
1. Download the native libraries using the following command ```git clone https://github.lanlytics.com:rbent/micot-libraries.git```
2. Add
  ```xml
  <classpathentry kind="lib" path="/micot-libraries/jars/cplex1263.jar"/>
  ```
  to .classpath
  * This step might have to be repeated during git updates 

### Outside LANL
1. Add
  ```xml
  <classpathentry kind="lib" path="/foo/cplex.jar"/>
  ```
  to .classpath, where foo is the location of your cplex.jar file
  * This step might have to be repeated during git updates 

TODO: Figure out instructions on how to do this "right" using Maven and Cplex as a dependency in the pom.xml.

## [Optional] SCIP Installation

TODO

## [Optional] COIN-OR Installation

TODO

## [Optional] OpenDSS Installation

TODO

## [Optional] DEW Installation

TODO

# Usage

## Run Application Executable

TODO

## Run Resilient Design Executable

TODO

## Run Powerworld Executable

Under the Windows operating system, running and executing powerworld from MICOT is done by following these instructions

1. Purchase [Powerworld](https://www.powerworld.com/)
2. Install a git tool
3. Download the repository using the following command ```git clone https://github.com/lanl-ansi/micot.git```
4. Follow the DCOM installation instructions
  *To run from anywhere, add the directory where the file "jacob-1.18-x64.dll" is located to your PATH environment variable 
5. From within target directory run the following command ```java -jar micot-powerworld.jar -p <powerworld or raw file> -m (optional) <modification json file> ```  

TODO document schema of json file

