## Introduction

The Drop Dis-File system is a software-defined solution based on the DNA of a real distributed system. The system can provide global access to the same data, which is extremely difficult to achieve in any other way, particularly when numerous sites are processing and consuming the same data. The Drop Dis-File system is designed to provide a distributed, fault-tolerant, and highly available solution for data storage and retrieval.

Users can access this system using a Web Server where all the existing files owned by the user will be listed. A user can add new files, delete existing files and even update existing files. 


## How to use the system

- We should first build the server repository which is available at:
https://github.com/praguna14/DropDisFile-Servers
- Clone this repository and then on the terminal navigate to the folder
which contains the pom.xml file.
- Build the maven for the server using the command:
```
mvn clean install
```
- Once this is built, a JAR file will be created in the target folder of
this repository. 

- After building the server JAR, we will have to build the CMS JAR which
is available at: https://github.com/praguna14/DropDisFile-CentralService
- Clone this repository and change the path of the server JAR in this
repository at the following file and line:
    - src/main/java/com/CS6650/CentralManagementService/utility/ServerCreation.java line 12
     (https://github.com/praguna14/DropDisFile-CentralService/blob/main/src/main/java/com/CS6650/CentralManagementService/utility/ServerCreation.java#L12)
- After making the above change, on the terminal navigate to the folder which
contains the pom.xml file.
- Build the maven for CMS using the command:
```
mvn clean install
```

- After this, we must run the CMS JAR to start our system. 
- To run the CMS JAR, we will have to navigate to the folder which contains
the CMS JAR file using the terminal and then run the following command:
```
java -jar DropDisFile-CentralService-1.0-SNAPSHOT.jar
```
- On running the CMS JAR, by default three servers will be created. (This takes a while to run because the server creation takes time (around 30 seconds))
- After the CMS is fully up and running, we must clone the FrontEnd repository
which is available at: https://github.com/praguna14/DropDisFile-FrontEnd
- After cloning this repository, navigate to the folder containing the package.json file
on the terminal.
- Then install the dependent npm libraries using the command:
```
npm install
```
- Once all the dependent libraries have been installed, run the UI using the command:
```
npm start
```

- This will open up a browser which will be redirected to the login page. The two users which are hardcoded to be allowed are
    - Username: JohnDoe
    - Password: 123456

    - Username: JaneDoe
    - Password: 123456

- After logging in, the user can upload a new file using the two text boxes and the upload button.
- The user will be able to see a list of files which are available to him/her.
- The user can download the file by clicking on the download button.
- The user can delete the file by clicking on the delete button.
- The user can even switch between the servers which are visible in the dropdown available to the user.
