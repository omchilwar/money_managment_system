# Money Manager Project --- Learning & Revision Notes

> **Purpose:** This document records what we are building, why we are
> building it, and what each important part means. Use it to revise
> instead of trying to memorise every line of JDBC code.
>
> **Project status:** Java--MySQL connection works. The `Category` model
> and `CategoryDAO` have been created and tested successfully.

------------------------------------------------------------------------

## 1. What are we building?

We are building a **Money Manager CLI application** using Java and
MySQL.

The application is intended to help manage:

-   Expense categories
-   Daily expenses
-   Monthly allowance
-   People we lend money to or borrow money from
-   Loans and debts
-   Partial repayments

We are starting with plain Java, JDBC, and Maven so that we understand
how a Java application communicates with a relational database. Later,
these fundamentals will help us understand what Spring Boot and its
data-access tools are doing behind the scenes.

### Our learning rule

Do not memorise JDBC boilerplate line by line. Understand:

1.  What a class is responsible for.
2.  What each JDBC object represents.
3.  What SQL operation is being performed.
4.  How data moves from MySQL into Java objects.
5.  Why resources must be closed.

------------------------------------------------------------------------

## 2. Tools and technologies

  -----------------------------------------------------------------------
  Technology                          What it does in this project
  ----------------------------------- -----------------------------------
  Java 17                             Programming language and runtime

  VS Code                             Editor used to write and run Java

  Maven                               Manages project configuration and
                                      dependencies

  MySQL                               Stores our application's data

  JDBC                                Lets Java communicate with a
                                      relational database

  MySQL Connector/J                   JDBC driver that allows Java to
                                      communicate with MySQL
  -----------------------------------------------------------------------

### What is Maven?

Maven is a build and dependency-management tool. Instead of manually
downloading and adding every library, we declare dependencies in
`pom.xml`. Maven downloads and makes them available to the project.

### Why did we add MySQL Connector/J?

JDBC defines Java's database API, but Java needs a MySQL-specific driver
to communicate with a MySQL server. MySQL Connector/J is that driver.

The dependency in `pom.xml` is:

``` xml
<dependencies>
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <version>8.2.0</version>
    </dependency>
</dependencies>
```

The `<dependencies>` element belongs inside the main `<project>`
element. It can be added manually if it is not already present.

After saving `pom.xml`, VS Code/Maven must reload the project so Maven
can download the dependency. We checked the Java/Maven dependency list
and saw `mysql-connector-j-8.2.0.jar`.

------------------------------------------------------------------------

## 3. Database setup

We created a MySQL database named:

``` sql
CREATE DATABASE IF NOT EXISTS finance_db;
USE finance_db;
```

**Important:** The database name is `finance_db`, not `moneymanager`.
Our JDBC URL must use `finance_db`.

### Tables in our database

We created six tables.

#### 3.1 `categories`

Stores the names of expense categories.

  Column   Meaning
  -------- ------------------------------------------------
  `id`     Unique category ID; auto-increment primary key
  `name`   Category name; required

We inserted these default categories:

-   Food
-   Travel
-   Supplies
-   Entertainment

#### 3.2 `expenses`

Stores expense records.

  Column           Meaning
  ---------------- --------------------------------------
  `id`             Unique expense ID
  `amount`         Amount spent
  `category_id`    Category associated with the expense
  `expense_date`   Date of the expense
  `description`    Optional description

`category_id` is a foreign key referencing `categories(id)`. This links
an expense to a valid category.

#### 3.3 `allowance`

Stores money received as a monthly allowance.

  Column            Meaning
  ----------------- ---------------------------------
  `id`              Unique allowance record ID
  `amount`          Amount received
  `month_year`      Month in `YYYY-MM` format
  `received_date`   Date the allowance was received

#### 3.4 `contacts`

Stores people connected to lending or borrowing.

  Column        Meaning
  ------------- -------------------------------
  `id`          Unique contact ID
  `name`        Person's name
  `is_active`   Whether the contact is active

The `is_active` field supports a soft-delete approach: a contact can be
marked inactive instead of immediately deleting the record.

#### 3.5 `ledger_entries`

Stores the main lending and borrowing records.

  Column         Meaning
  -------------- -------------------------------------
  `id`           Unique ledger entry ID
  `contact_id`   Person connected to the transaction
  `type`         `LENT` or `BORROWED`
  `amount`       Original amount
  `entry_date`   Date of the entry
  `status`       `PENDING` or `SETTLED`

`contact_id` is a foreign key referencing `contacts(id)`.

#### 3.6 `ledger_payments`

Stores repayments, including partial repayments.

  Column           Meaning
  ---------------- ---------------------------
  `id`             Unique payment ID
  `entry_id`       Ledger entry being repaid
  `amount`         Amount paid
  `payment_date`   Date of payment

`entry_id` is a foreign key referencing `ledger_entries(id)`.

### Why do we use primary keys and foreign keys?

-   **Primary key:** Uniquely identifies a row in a table.
-   **Foreign key:** Connects a row in one table to a related row in
    another table.

For example, `expenses.category_id` tells us which category belongs to
an expense.

------------------------------------------------------------------------

## 4. Connecting Java to MySQL

We created a Java class called `DBconnection` in the package
`com.moneymanager`.

Its job is to provide a connection to the database.

The JDBC URL should follow this pattern:

``` text
jdbc:mysql://localhost:3306/finance_db
```

Meaning:

-   `jdbc:mysql` --- use JDBC with the MySQL driver.
-   `localhost` --- the MySQL server is running on this computer.
-   `3306` --- the usual MySQL port.
-   `finance_db` --- the database we want to use.

The connection code uses:

``` java
Connection conn = DriverManager.getConnection(
    "jdbc:mysql://localhost:3306/finance_db",
    "root",
    "YOUR_PASSWORD"
);
```

Replace `YOUR_PASSWORD` with the password configured for your MySQL
account. Do not commit real passwords to a public repository. Later, we
should move credentials into environment variables or a configuration
file excluded from version control.

### Important JDBC terms

  -----------------------------------------------------------------------
  Term                                Simple meaning
  ----------------------------------- -----------------------------------
  `Connection`                        An open communication channel
                                      between Java and the database

  `DriverManager`                     Helps obtain a database connection
                                      using an available JDBC driver

  `getConnection()`                   Requests and returns a database
                                      connection
  -----------------------------------------------------------------------

We tested the connection and got:

``` text
Database Connected Successfully!
```

That confirmed that Java could connect to the MySQL database with the
current URL and credentials.

------------------------------------------------------------------------

## 5. Organising Java code with packages

We are using packages to separate classes by responsibility.

Our project structure is:

``` text
src/
└── main/
    └── java/
        └── com/
            └── moneymanager/
                ├── DBconnection.java
                ├── Main.java
                ├── model/
                │   └── Category.java
                └── dao/
                    └── CategoryDAO.java
```

### Why are `model` and `dao` inside `com.moneymanager`?

Java packages organise related classes and give them package names.

-   `com.moneymanager` --- main application-level classes.
-   `com.moneymanager.model` --- classes representing application data.
-   `com.moneymanager.dao` --- classes responsible for database
    operations.

The directory structure should match the package declaration.

For example, at the top of `Category.java`:

``` java
package com.moneymanager.model;
```

At the top of `CategoryDAO.java`:

``` java
package com.moneymanager.dao;
```

To use `Category` inside `CategoryDAO`, import it:

``` java
import com.moneymanager.model.Category;
```

------------------------------------------------------------------------

## 6. What is a Model class?

A **model class** represents a type of data in our application.

For example, one row in the `categories` table can be represented by one
`Category` object.

Our `Category.java`:

``` java
package com.moneymanager.model;

public class Category {

    private int id;
    private String name;

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
```

### Understand the parts

-   `private int id` --- stores the category's ID.
-   `private String name` --- stores the category's name.
-   The constructor assigns values when a `Category` object is created.
-   `getId()` returns the ID.
-   `getName()` returns the name.

Example:

``` java
Category category = new Category(1, "Food");
```

This creates a Java object representing the category with ID `1` and
name `Food`.

**Remember:** A model object represents data. It does not need to
execute SQL or connect to MySQL.

------------------------------------------------------------------------

## 7. What is DAO?

DAO stands for **Data Access Object**.

A DAO class contains code responsible for accessing and changing data in
the database.

For example:

-   `CategoryDAO` works with category data.
-   `ExpenseDAO` will work with expense data.
-   `AllowanceDAO` will work with allowance data.

Why use DAO?

Without DAO, SQL queries and database code can become mixed into the
application's menu and user-interaction code. DAO separates database
work from the rest of the application.

This makes the project easier to read, test, maintain, and extend.

### Our first DAO: `CategoryDAO`

Our first goal was to fetch all rows from the `categories` table and
convert them into Java `Category` objects.

The SQL query is:

``` sql
SELECT * FROM categories;
```

`SELECT` reads data. `*` means all columns. `FROM categories` tells
MySQL which table to read.

------------------------------------------------------------------------

## 8. JDBC concepts we are learning

These are the most important concepts to understand. Focus on what each
one does, rather than memorising every line.

### 8.1 Connection

A `Connection` represents an active connection between Java and the
database.

``` java
Connection conn = DBconnection.getConnection();
```

Think of it as the communication channel.

### 8.2 Statement and PreparedStatement

A statement is used to send SQL to the database.

`PreparedStatement` is especially useful because it supports
parameterised SQL:

``` java
String sql = "SELECT * FROM categories WHERE id = ?";
PreparedStatement pstmt = conn.prepareStatement(sql);
pstmt.setInt(1, 1);
```

The `?` is a parameter placeholder. `setInt(1, 1)` sets the first
placeholder to the value `1`.

Why prefer `PreparedStatement`?

-   It supports parameters.
-   It helps prevent SQL injection when values are supplied as
    parameters.
-   It avoids manually joining user input into SQL strings.
-   It can be reused with different parameter values.

### 8.3 ResultSet

A `ResultSet` represents the rows returned by a query such as `SELECT`.

``` java
ResultSet rs = pstmt.executeQuery();
```

To read each row:

``` java
while (rs.next()) {
    int id = rs.getInt("id");
    String name = rs.getString("name");
}
```

`rs.next()` moves the cursor to the next row. It returns `true` when a
row is available and `false` when there are no more rows.

Useful methods include:

-   `getInt("id")` --- reads an integer column.
-   `getString("name")` --- reads a text column.
-   `getBigDecimal("amount")` --- reads a decimal value; this is
    suitable for money values.

### 8.4 executeQuery() and executeUpdate()

  -----------------------------------------------------------------------
  Method                  Typical use             Return value
  ----------------------- ----------------------- -----------------------
  `executeQuery()`        `SELECT`                `ResultSet`

  `executeUpdate()`       `INSERT`, `UPDATE`,     Number of affected rows
                          `DELETE`                
  -----------------------------------------------------------------------

Example:

``` java
ResultSet rs = pstmt.executeQuery();
```

For an insert:

``` java
int rows = pstmt.executeUpdate();
```

If one row was inserted, `rows` will normally be `1`.

### 8.5 Try-with-resources

JDBC resources should be closed when finished.

Try-with-resources closes resources automatically:

``` java
try (
    Connection conn = DBconnection.getConnection();
    PreparedStatement pstmt = conn.prepareStatement(sql);
    ResultSet rs = pstmt.executeQuery()
) {
    // Read the result here.
}
```

When execution leaves the try block, the resources are closed
automatically, including when an exception occurs.

This helps prevent resource leaks.

**Important:** The connection must be non-null before calling
`conn.prepareStatement(...)`. Our current connection helper returns
`null` if it cannot connect, so the DAO should handle a failed
connection safely. We will improve this in a later lesson.

------------------------------------------------------------------------

## 9. Our first DAO code, explained

This is the `CategoryDAO.java` code we created and tested:

``` java
package com.moneymanager.dao;

import com.moneymanager.DBconnection;
import com.moneymanager.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public List<Category> getAllCategories() {

        List<Category> categories = new ArrayList<>();

        String sql = "SELECT * FROM categories";

        try (
            Connection conn = DBconnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()
        ) {

            while (rs.next()) {

                int id = rs.getInt("id");
                String name = rs.getString("name");

                Category category = new Category(id, name);

                categories.add(category);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }
}
```

### Walkthrough

**1. Package declaration**

``` java
package com.moneymanager.dao;
```

Says that this class belongs to the DAO package.

**2. Imports**

``` java
import com.moneymanager.DBconnection;
import com.moneymanager.model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
```

These make the classes we need available in this file.

**3. Method declaration**

``` java
public List<Category> getAllCategories()
```

This method returns a list of `Category` objects.

**4. Create an empty list**

``` java
List<Category> categories = new ArrayList<>();
```

This list will hold the categories read from MySQL.

**5. Write the SQL**

``` java
String sql = "SELECT * FROM categories";
```

This query asks MySQL for every category row.

**6. Obtain the connection and prepare the query**

``` java
Connection conn = DBconnection.getConnection();
PreparedStatement pstmt = conn.prepareStatement(sql);
```

The first line obtains the database connection. The second prepares the
SQL query.

**7. Execute the query**

``` java
ResultSet rs = pstmt.executeQuery();
```

This runs the `SELECT` and gives us the returned rows.

**8. Loop through the rows**

``` java
while (rs.next()) {
```

Each successful call moves to the next row.

**9. Read column values**

``` java
int id = rs.getInt("id");
String name = rs.getString("name");
```

These lines read the current row's ID and name.

**10. Convert the row into a Java object**

``` java
Category category = new Category(id, name);
```

This creates a model object for the current database row.

**11. Add it to the list**

``` java
categories.add(category);
```

The object is added to our list.

**12. Return the list**

``` java
return categories;
```

The method returns all the category objects it collected.

------------------------------------------------------------------------

## 10. Testing the DAO from Main.java

We tested our DAO by calling it from `Main.java`:

``` java
package com.moneymanager;

import com.moneymanager.dao.CategoryDAO;
import com.moneymanager.model.Category;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        CategoryDAO dao = new CategoryDAO();

        List<Category> categories = dao.getAllCategories();

        for (Category c : categories) {
            System.out.println(c.getId() + " - " + c.getName());
        }
    }
}
```

### What happens here?

1.  `new CategoryDAO()` creates a DAO object.
2.  `dao.getAllCategories()` asks the DAO to retrieve all categories.
3.  The returned list is stored in `categories`.
4.  The `for` loop prints each category's ID and name.

### Successful output

``` text
1 - Food
2 - Travel
3 - Supplies
4 - Entertainment
```

**Milestone achieved:** We successfully retrieved data from MySQL,
converted the rows into Java model objects, and displayed them in the
console.

------------------------------------------------------------------------

## 11. The complete data flow

Keep this flow in mind:

``` text
Main.java
    |
    | Calls getAllCategories()
    v
CategoryDAO.java
    |
    | Gets a Connection
    v
DBconnection.java
    |
    | Connects through JDBC driver
    v
MySQL: finance_db
    |
    | Executes SELECT query
    v
ResultSet
    |
    | Reads one row at a time
    v
Category objects
    |
    | Added to a List<Category>
    v
Main.java displays the categories
```

The key idea is that **MySQL rows are converted into Java objects** so
the rest of our Java application can work with them.

------------------------------------------------------------------------

## 12. What comes next?

We will build the remaining parts one at a time.

  -----------------------------------------------------------------------
  Order                   Class or task           Purpose
  ----------------------- ----------------------- -----------------------
  Completed               `DBconnection`          Connect Java to MySQL

  Completed               `Category`              Represent one category

  Completed               `CategoryDAO`           Retrieve all categories

  Next                    `Expense`               Represent an expense

  Next                    `ExpenseDAO`            Add, read, update and
                                                  delete expenses

  Later                   `Allowance` and         Manage monthly
                          `AllowanceDAO`          allowance

  Later                   `Contact` and           Manage contacts
                          `ContactDAO`            

  Later                   `LedgerEntry` and       Manage loans and debts
                          `LedgerDAO`             

  Later                   `LedgerPayment` and     Record partial
                          `LedgerPaymentDAO`      repayments

  Later                   Main menu               Connect the features
                                                  into the CLI
                                                  application
  -----------------------------------------------------------------------

We will also revisit error handling, input validation, transaction
management, and secure configuration as the project grows.

------------------------------------------------------------------------

## 13. Revision questions

Try answering these without looking at the explanations above.

1.  What is the purpose of Maven?
2.  Why did we add MySQL Connector/J?
3.  What is JDBC?
4.  What does `Connection` represent?
5.  What is the purpose of a model class?
6.  What does DAO stand for?
7.  Why did we create separate `model` and `dao` packages?
8.  What does `SELECT * FROM categories` do?
9.  What does `PreparedStatement` do?
10. What does `executeQuery()` return?
11. What is the purpose of `rs.next()`?
12. How do we read the `name` column from a ResultSet?
13. Why do we create a `Category` object for each returned row?
14. Why do we use `categories.add(category)`?
15. What does try-with-resources do?
16. What is the difference between `executeQuery()` and
    `executeUpdate()`?

### Quick answers for revision

1.  Maven manages dependencies and the build.
2.  It provides the MySQL-specific JDBC driver.
3.  JDBC is Java's API for communicating with databases.
4.  An active database communication channel.
5.  It represents application data.
6.  Data Access Object.
7.  To separate responsibilities and organise code.
8.  Reads every column and row from `categories`.
9.  Prepares SQL and supports parameters.
10. A `ResultSet`.
11. Moves the cursor to the next row.
12. `rs.getString("name")`.
13. To represent a database row as Java data.
14. To store the object in our list.
15. Automatically closes resources.
16. `executeQuery()` is normally for SELECT; `executeUpdate()` is
    normally for INSERT, UPDATE and DELETE.

------------------------------------------------------------------------

## 14. How to use these notes so you don't forget

Use this simple revision routine:

### After each coding session

-   Read the new section once.
-   Explain the concept aloud in your own words.
-   Run the code yourself.
-   Write down one thing you found confusing.

### The next day

-   Try to explain the previous lesson without opening this document.
-   Rebuild a small part of the code from memory.
-   Then check your notes and correct what you missed.

### After three days and one week

-   Answer the revision questions again.
-   Try to write the SQL and JDBC flow without copying.
-   Revisit only the concepts you could not explain.

**Remember:** Forgetting syntax is normal. Knowing how to reason about
the code and where to look things up is much more important.

------------------------------------------------------------------------

## 15. Current project checklist

-   [x] Create Maven Java project.
-   [x] Configure Java 17.
-   [x] Add MySQL Connector/J dependency.
-   [x] Reload Maven dependencies.
-   [x] Create `finance_db` database and six tables.
-   [x] Connect Java to MySQL.
-   [x] Create the `model` package.
-   [x] Create the `dao` package.
-   [x] Create `Category` model.
-   [x] Create `CategoryDAO`.
-   [x] Retrieve and print all categories successfully.
-   [ ] Build `Expense` model.
-   [ ] Build `ExpenseDAO`.
-   [ ] Build remaining models and DAOs.
-   [ ] Complete the CLI application.

------------------------------------------------------------------------

**Last completed milestone:** `CategoryDAO.getAllCategories()`
successfully retrieves the four default categories from `finance_db`.

**Next lesson:** Build the `Expense` model and `ExpenseDAO`, learning
how to use `PreparedStatement` parameters and `executeUpdate()` to
insert an expense.



# Expense Model and ExpenseDAO – Project Notes

## 1. Introduction

In our Money Manager project, we created an Expense Model and ExpenseDAO to manage expenses using Java and MySQL.

We implemented three operations:

1. Add an expense (INSERT)
2. Display all expenses (SELECT)
3. Delete an expense (DELETE)

---

## 2. Expense Model

**File location:**
`src/main/java/com/moneymanager/model/Expense.java`

### Purpose

The Expense Model represents a single expense in our Java application.

It stores the information of an expense in the form of an object.

### Database Table: expenses

| Column | Data Type | Description |
|---|---|---|
| id | INT | Unique expense ID |
| amount | DECIMAL(10,2) | Expense amount |
| category_id | INT | Category ID |
| expense_date | DATE | Date of expense |
| description | VARCHAR(255) | Expense description |

### Expense.java Code

```java
package com.moneymanager.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Expense {

    private int id;
    private BigDecimal amount;
    private int categoryId;
    private LocalDate expenseDate;
    private String description;

    public Expense(int id, BigDecimal amount, int categoryId,
                   LocalDate expenseDate, String description) {

        this.id = id;
        this.amount = amount;
        this.categoryId = categoryId;
        this.expenseDate = expenseDate;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public String getDescription() {
        return description;
    }
}
```

### Important Concepts

**1. BigDecimal**

- Used to store money values accurately.
- Avoids common floating-point precision problems.
- Preferred over `double` for financial calculations.

Example:

```java
BigDecimal amount = new BigDecimal("250.00");
```

**2. LocalDate**

- Used to represent dates.
- Does not store time.

Example:

```java
LocalDate date = LocalDate.now();
```

**3. Constructor**

A constructor initializes an Expense object with its values.

**4. Getters**

Getter methods retrieve private variables from the Expense object.

---

## 3. ExpenseDAO

**File location:**

`src/main/java/com/moneymanager/dao/ExpenseDAO.java`

### What is DAO?

DAO stands for Data Access Object.

It is responsible for communicating with the database and performing database operations.

Our ExpenseDAO contains three methods:

- `addExpense()`
- `getAllExpenses()`
- `deleteExpense()`

---

## 4. Adding an Expense – INSERT

### Method

```java
public boolean addExpense(Expense expense)
```

### Purpose

Inserts a new expense into the MySQL database.

### Code

```java
public boolean addExpense(Expense expense) {

    String sql = "INSERT INTO expenses " +
                 "(amount, category_id, expense_date, description) " +
                 "VALUES (?, ?, ?, ?)";

    try (
        Connection conn = DBconnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)
    ) {

        pstmt.setBigDecimal(1, expense.getAmount());
        pstmt.setInt(2, expense.getCategoryId());
        pstmt.setDate(3, java.sql.Date.valueOf(expense.getExpenseDate()));
        pstmt.setString(4, expense.getDescription());

        int rows = pstmt.executeUpdate();

        return rows > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
```

### Important Concepts

**1. PreparedStatement**

PreparedStatement is used to execute parameterized SQL queries.

It uses `?` as placeholders.

Example:

```sql
INSERT INTO expenses
(amount, category_id, expense_date, description)
VALUES (?, ?, ?, ?);
```

**2. Setting placeholder values**

| Method | Placeholder | Value |
|---|---|---|
| setBigDecimal() | 1 | Amount |
| setInt() | 2 | Category ID |
| setDate() | 3 | Expense date |
| setString() | 4 | Description |

Remember: Placeholder numbering starts from 1, not 0.

**3. executeUpdate()**

Used for:

- INSERT
- UPDATE
- DELETE

It returns the number of affected rows.

Example:

- 1 = One row inserted.
- 0 = No rows affected.

**4. Return value**

```java
return rows > 0;
```

Returns `true` if the insertion succeeds and `false` otherwise.

---

## 5. Displaying All Expenses – SELECT

### Method

```java
public List<Expense> getAllExpenses()
```

### Purpose

Retrieves all expenses from the MySQL database and converts each database row into an Expense object.

### Code

```java
public List<Expense> getAllExpenses() {

    List<Expense> expenses = new ArrayList<>();

    String sql = "SELECT * FROM expenses";

    try (
        Connection conn = DBconnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery()
    ) {

        while (rs.next()) {

            int id = rs.getInt("id");

            BigDecimal amount =
                rs.getBigDecimal("amount");

            int categoryId =
                rs.getInt("category_id");

            LocalDate expenseDate =
                rs.getDate("expense_date").toLocalDate();

            String description =
                rs.getString("description");

            Expense expense = new Expense(
                id, amount, categoryId, expenseDate, description
            );

            expenses.add(expense);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return expenses;
}
```

### Important Concepts

**1. List<Expense>**

Stores multiple Expense objects in a list.

**2. ArrayList**

```java
List<Expense> expenses = new ArrayList<>();
```

Creates an empty list to store expenses.

**3. ResultSet**

ResultSet stores the rows returned by a SELECT query.

**4. rs.next()**

```java
while (rs.next())
```

Moves the cursor to the next row.

It returns `true` if another row exists and `false` when no more rows are available.

**5. Reading database values**

| Method | Purpose |
|---|---|
| getInt() | Reads an integer |
| getBigDecimal() | Reads a decimal value |
| getString() | Reads text |
| getDate() | Reads a SQL date |

**6. Converting SQL Date to LocalDate**

```java
rs.getDate("expense_date").toLocalDate();
```

Converts a SQL Date into Java's LocalDate.

**7. Creating an Expense object**

```java
Expense expense = new Expense(
    id, amount, categoryId, expenseDate, description
);
```

Converts the database row into a Java object.

**8. Adding to ArrayList**

```java
expenses.add(expense);
```

Adds the Expense object to the list.

---

## 6. Deleting an Expense – DELETE

### Method

```java
public boolean deleteExpense(int id)
```

### Purpose

Deletes an expense using its unique ID.

### Code

```java
public boolean deleteExpense(int id) {

    String sql = "DELETE FROM expenses WHERE id = ?";

    try (
        Connection conn = DBconnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)
    ) {

        pstmt.setInt(1, id);

        int rows = pstmt.executeUpdate();

        return rows > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
```

### Important Concepts

**1. DELETE query**

```sql
DELETE FROM expenses WHERE id = ?;
```

Deletes only the record whose ID matches the given ID.

**2. Setting ID**

```java
pstmt.setInt(1, id);
```

Replaces the question mark with the ID supplied by the user.

**3. Checking affected rows**

```java
int rows = pstmt.executeUpdate();
```

- 1 = Expense deleted successfully.
- 0 = Expense with that ID was not found.

---

## 7. Understanding AUTO_INCREMENT

Our expenses table has:

```sql
id INT AUTO_INCREMENT PRIMARY KEY
```

### What does AUTO_INCREMENT mean?

MySQL automatically generates a unique ID whenever a new expense is inserted.

Example:

| ID | Amount | Description |
|---|---:|---|
| 1 | 250 | Lunch |
| 2 | 500 | Bus ticket |
| 3 | 100 | Notebook |

When we insert another expense, MySQL generates the next available AUTO_INCREMENT value.

### Why do we still need the ID?

Although MySQL generates the ID automatically, we need it to identify a particular record.

For example:

```java
deleteExpense(2);
```

Deletes the expense with ID 2.

### Important Difference

| Operation | Does user provide ID? | Reason |
|---|---|---|
| Insert | No | MySQL generates ID |
| Display | No | MySQL returns ID |
| Delete | Yes | Identifies the expense |
| Update | Yes | Identifies the expense |

Remember:

**AUTO_INCREMENT generates the ID, but the ID is still necessary to identify individual records.**

---

## 8. Important JDBC Concepts Revision

| Concept | Meaning |
|---|---|
| Connection | Connects Java to MySQL |
| DriverManager | Helps establish database connections |
| PreparedStatement | Executes parameterized SQL queries |
| ResultSet | Holds SELECT query results |
| executeQuery() | Executes SELECT |
| executeUpdate() | Executes INSERT, UPDATE, DELETE |
| rs.next() | Moves to the next result row |
| ArrayList | Stores multiple objects |
| try-with-resources | Automatically closes database resources |
| SQLException | Handles SQL-related errors |

### Remember This

- `executeQuery()` → SELECT
- `executeUpdate()` → INSERT, UPDATE, DELETE

---

## 9. Common Mistake: Duplicate Expenses

During testing, we noticed that every time Main.java ran, another expense was inserted.

### Why did this happen?

Because the following method was called every time:

```java
expenseDAO.addExpense(expense);
```

Every program execution inserted another database record.

### Solution

After testing insertion, temporarily comment out the insertion code when testing SELECT.

```java
// expenseDAO.addExpense(expense);
```

This prevents accidental duplicate records during retrieval testing.

Remember: MySQL data remains stored even after the Java program stops.

---

## 10. Main.java Testing

We used Scanner to accept input directly from the terminal instead of manually creating objects with fixed values.

This allows users to:

- Enter expense details.
- Add expenses.
- View all expenses.
- Enter an expense ID to delete it.

Scanner belongs to Java's `java.util` package.

Example:

```java
import java.util.Scanner;

Scanner sc = new Scanner(System.in);

System.out.print("Enter amount: ");
BigDecimal amount = sc.nextBigDecimal();
```

---

## 11. Project Progress

### Completed

- [x] Maven project setup
- [x] MySQL connection
- [x] Category Model
- [x] CategoryDAO
- [x] Expense Model
- [x] ExpenseDAO – addExpense()
- [x] ExpenseDAO – getAllExpenses()
- [x] ExpenseDAO – deleteExpense()
- [x] Scanner-based terminal input

### Next Topics

- [ ] ExpenseDAO – updateExpense()
- [ ] Display category names instead of category IDs
- [ ] Allowance Model and DAO
- [ ] Contacts Model and DAO
- [ ] Ledger Entries Model and DAO
- [ ] Ledger Payments Model and DAO
- [ ] Main menu integration

---

## 12. Quick Revision Questions

**Q1. What is DAO?**

DAO stands for Data Access Object. It performs database operations.

**Q2. Why do we use BigDecimal for money?**

To perform accurate decimal calculations.

**Q3. What is PreparedStatement?**

It executes parameterized SQL queries using placeholders.

**Q4. What does executeQuery() do?**

Executes SELECT queries and returns a ResultSet.

**Q5. What does executeUpdate() do?**

Executes INSERT, UPDATE, and DELETE queries and returns the number of affected rows.

**Q6. What does rs.next() do?**

Moves the ResultSet cursor to the next row.

**Q7. Why do we use ArrayList?**

To store multiple Expense objects.

**Q8. Why do we need an ID if it is AUTO_INCREMENT?**

Because we need the ID to identify a particular expense when deleting or updating it.

**Q9. Why are duplicates appearing when running Main.java repeatedly?**

Because the INSERT method executes every time the program runs.

**Q10. What is the difference between Model and DAO?**

- Model represents the data.
- DAO performs database operations.

---

## Final Revision Summary

Our Expense Model stores expense information as Java objects.

Our ExpenseDAO communicates with MySQL and performs three operations:

1. `addExpense()` – inserts an expense.
2. `getAllExpenses()` – retrieves all expenses.
3. `deleteExpense()` – deletes an expense using its ID.

**Most important concept:**

Model = Data representation

DAO = Database operations

JDBC = Communication between Java and MySQL