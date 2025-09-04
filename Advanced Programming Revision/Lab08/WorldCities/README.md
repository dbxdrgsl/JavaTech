Lab 8
[valid 2024-2025]
JDBC - World Cities
Write an application that allows to connect to a relational database by using JDBC, submit SQL statements and display the results.

The main specifications of the application are:

Compulsory (1p)

Create a relational database using any RDBMS (Oracle, Postgres, MySql, Java DB, etc.).
Write an SQL script that will create the following tables:
countries: id, name, code, continent
continents: id, name
Update pom.xml, in order to add the database driver to the project libraries.
Create a singleton class in order to manage a connection to the database.
Create DAO classes that offer methods for creating countries and continents, and finding them by their ids and names;
Implement a simple test using your classes.
Homework (2p)

Use a connection pool in order to manage database connections, such as C3PO, HikariCP or Apache Commons DBCP.
Create the necessary table in order to store cities in your database. A city may contain: id, country, name, capital(true/false), latitude, longitude
Create an object-oriented model of the data managed by the application.
Create a tool to import data from a real dataset: World capitals gps or other.
Display the distances between various cities in the world.
Bonus (2p)

Two cities are connected (sister, twins) if they have a form of legal or social agreement between them, for the purpose of promoting cultural and commercial ties.
Create and insert into your database a large number of fake cities (≥10_000) and random sister relationships among them (the sisterhood probability should be low).
Implement an algorithm in order to determine all maximal sets of cities with the property that their corresponding subgraphs are 2-connected.
Create a 2D map (using Swing / JavaFX or generate an image / SVG) representing the cities at their corresponding locations and their connections. Use different colors for drawing the 2-connected subgraphs.
Resources

Slides
JDBC
Oracle Database JDBC Developer's Guide and Reference
JDBC Tutorial - The ULTIMATE Guide
Objectives

Understand terms and concepts related to relational databases: DBMS, SQL, table, query, stored procedure, cursor, etc.
Connect to a relational database by using a JDBC driver
Submit queries and get results from the database
Specify JDBC driver information externally
Perform CRUD operations by using the JDBC API