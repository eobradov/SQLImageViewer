# SQL Image Viewer

JavaFX standalone application used to preview images inside SQL databases.

This application supports following databases:
* Microsoft SQL Server
* MySQL

## Getting Started

See deployment for notes on how to deploy the project on a live system.

### Prerequisites

What things you need to install the software and how to install them
* Java 1.8.0 (or higher) - https://java.com/en/download

```
Application was created using java version 1.8.0_144
```

### JDBC drivers

JAR contains JDBC drivers used to connect to SQL servers:
* mysql-connector-java-5.1.44-bin.jar - drivers for connecting to MySQL
* sqljdbc42.jar - drivers for connecting to Microsoft SQL Server

JDBC drivers have not been modified in any way.
You can use the drivers inside the JAR, or deploy your own drivers for JDBC.

## Deployment

* Download SQLImageViewer\SQLImageViewer.jar to your disk
* Execute application with *java -jar SQLImageViewer.jar*

## Authors

* **Edi Obradovic** - *Initial work*

## License

This project is licensed under the GNU GENERAL PUBLIC LICENSE Version 3 (https://www.gnu.org/licenses/gpl-3.0.en.html)

## Acknowledgments

* https://docs.microsoft.com/en-us/sql/connect/jdbc/overview-of-the-jdbc-driver - for Microsoft SQL Server JDBC driver
* https://dev.mysql.com/downloads/connector/ - for MySQL JDBC driver
* https://www.flaticon.com/ - for the awesome icons used in the application

