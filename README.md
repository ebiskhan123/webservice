 # Webservice
Repo for CSYE6225 course assignments and projects.
## Prerequisites 
This project would require **Java 8** and **Maven** to be set up.
To set up maven use the tutorial in the link : https://www.baeldung.com/install-maven-on-windows-linux-mac

## Steps to run the application.
Use the command line/terminal to navigate to the project folder.

- To build use 
```
mvn --batch-mode --update-snapshots install 
```
- To run tests
```
mvn --batch-mode --update-snapshots test
```
- To start the application
```
cd target
java -jar assignment-1.0-SNAPSHOT.jar
```

The url to check the health would be http://localhost:8080/api/healthz


