# Quak Data

In Quak, database operations can be done using the `quak.framework.data` module. This module supports querying using abstractions over SQL or by directly writing queries.

## Database Configuration

To configure database access in your Quak application, you can set the following properties in your application's `application.properties` file:

- `hibernate.dialect`: The hibernate SQL dialect to use.
- `hibernate.connection.driver_class`: The database driver class to use.
- `hibernate.connection.url`: The connection URL to use.
- `hibernate.connection.username`: The database username.
- `hibernate.connection.password`: The database password.
- `hibernate.hbm2ddl.auto`: The hibernate.hbm2ddl.auto setting. Affects how tables are managed when starting and stopping the application.

## Database Proxy

At the core of Quak's database access is a class called `io.john.amiscaray.quak.data.DatabaseProxy`. This class contains methods for querying, testing if an entity exists by ID, and CRUD operations. `quak.framework.data` instantiates this class and adds it to the application context. In doing so, it initializes it using the above-mentioned properties from the application properties. The sections below will talk about the less intuitive details of this class (querying, updating, and deleting). The rest can be figured out by looking through the java docs for the class.

## Query API

The `DatabaseProxy` class offers a unique and semantically intuitive API for querying your database. Below is an example of what a simple query looks like:

```java
import io.john.amiscaray.quak.core.di.provider.annotation.Instantiate;
import io.john.amiscaray.quak.core.di.provider.annotation.ManagedType;
import io.john.amiscaray.quak.data.query.DatabaseProxy;
import io.john.amiscaray.quak.data.query.DatabaseQuery;

import static io.john.amiscaray.quak.data.query.QueryCriteria.*;

@ManagedType
public class Test {
    
    private final DatabaseProxy dbProxy;
    
    @Instantiate
    public Test(DatabaseProxy dbProxy) {
        this.dbProxy = dbProxy;
    }

    public List<Employee> fetchEmployeesNamedJohn() {
        return dbProxy.queryAllWhere(Employee.class, valueOfField("name", is("John")));
    }
    
}
```
In the example above, `DatabaseProxy#queryAllWhere` method accepts the type of the database entity being querying along with an instance of `io.john.amiscaray.quak.data.query.QueryCriteria`. The `QueryCriteria` interface is used to define criteria for the query (basically the equivalent of conditions in the _WHERE_ clause in an SQL _SELECT_ statement). The interface contains various static utility methods to help you create implementations of this interface semantically. In the example above, the expression `valueOfField("name", is("John"))` returns a `io.john.amiscaray.quak.data.query.QueryCriteria`. This query criteria checks that the value of the _name_ column is _"John"_. If you look at the second argument we passed to this function (the expression `is("John")`), that expression returns an implementation of `io.john.amiscaray.quak.data.query.QueryCriteriaProvider`. This interface has a single method which returns a `QueryCriteria` given the name of a field. Essentially, the `valueOf` method accepts the name of the field and a `QueryCriteriaProvider` to pass that field name to create our `QueryCriteria`. `QueryCriteria` provides various other static methods for `QueryCriteriaProvider`s you can pass to this method. Below, you'll find more example queries you can make with this API:

```java
import io.john.amiscaray.quak.core.di.provider.annotation.Instantiate;
import io.john.amiscaray.quak.core.di.provider.annotation.ManagedType;
import io.john.amiscaray.quak.data.query.DatabaseProxy;
import io.john.amiscaray.quak.data.query.DatabaseQuery;

import static io.john.amiscaray.quak.data.query.QueryCriteria.*;

@ManagedType
public class Test {
    
    private final DatabaseProxy dbProxy;
    
    @Instantiate
    public Test(DatabaseProxy dbProxy) {
        this.dbProxy = dbProxy;
    }

    public List<Employee> fetchEmployeesNamedJohn() {
        return dbProxy.queryAllWhere(Employee.class, valueOfField("name", is("John")));
    }
    
    public List<Employee> fetchEmployeesWithIDBetween2And10() {
        return dbProxy.queryAllWhere(Employee.class, valueOfField("id", isBetween(2, 10))); // inBetween is inclusive of the min and max
    }
    
    public List<Employee> fetchEmployeesWithIDGreaterThanOrEqualTo2AndLessThanOrEqualTo4() {
        return dbProxy.queryAllWhere(Employee.class, matchesAllOf(
                        isGreaterThanOrEqualTo(2),
                        isLessThanOrEqualTo(4)));
    }

    public List<Employee> fetchEmployeesWithNameContainingN() {
        return dbProxy.queryAllWhere(Employee.class, valueOfField("name", contains("n")));
    }
    
}
```

## Deletion API

Similar to the Query API, `DatabaseProxy` has a `deleteAll` method to delete database entries using a `DatabaseQuery` object to determine what rows to delete. See the `DatabaseProxy` class for more details.

## Update API

Quak's `DatabaseProxy` class also has an API for updating rows in your database. Similar to the query API, you can pass a `DatabaseQuery` object to this API to specify which rows this update applies to. Then, you can pass the new value to set the field to:

```java
import io.john.amiscaray.quak.core.di.provider.annotation.Instantiate;
import io.john.amiscaray.quak.core.di.provider.annotation.ManagedType;
import io.john.amiscaray.quak.data.query.DatabaseProxy;
import io.john.amiscaray.quak.data.query.DatabaseQuery;

import static io.john.amiscaray.quak.data.update.UpdateExpression.*;
import static io.john.amiscaray.quak.data.query.QueryCriteria.*;

@ManagedType
public class Test {

    private final DatabaseProxy dbProxy;

    @Instantiate
    public Test(DatabaseProxy dbProxy) {
        this.dbProxy = dbProxy;
    }
    
    public void updateEmployeesWithDepartmentTechToDepartmentTechnology() {
        dbProxy.updateAllWhereAndSetTo(Employee.class,
                "department", 
                valueOfField("department", is("Tech")),
                "Technology");
    }

}
```

Aside from that, Quak also allows you to pass expressions for more complex operations on the current value of the field. This is done using implementations of the `io.john.amiscaray.quak.data.update.UpdateExpression` interface. Like the `QueryCriteria` interface, this interface contains static utility methods to get implementations of this interface based on arguments you pass to it. For example, it has `add`, `subtract`, `multiply`, and `divide` methods which each accept a numeric value. These methods return an update expression used to perform each of these operations on the current value of the field using the passed operand. For example, we can add another method to our example above to multiply employee's salaries by 1.5 and add 5000:

```java
import io.john.amiscaray.quak.core.di.provider.annotation.Instantiate;
import io.john.amiscaray.quak.core.di.provider.annotation.ManagedType;
import io.john.amiscaray.quak.data.query.DatabaseProxy;
import io.john.amiscaray.quak.data.query.DatabaseQuery;

import static io.john.amiscaray.quak.data.update.UpdateExpression.*;
import static io.john.amiscaray.quak.data.query.QueryCriteria.*;

@ManagedType
public class Test {

    private final DatabaseProxy dbProxy;

    @Instantiate
    public Test(DatabaseProxy dbProxy) {
        this.dbProxy = dbProxy;
    }
    
    public void updateEmployeesWithDepartmentTechToDepartmentTechnology() {
        dbProxy.updateAllWhereAndSetTo(Employee.class,
                "department",
                valueOfField("department", is("Tech")),
                "Technology");
    }
    
    public void updateEmployeesSalaryMultiplyBy50PercentAndAdd5000() {
        dbProxy.updateAll(Employee.class, // No DatabaseQuery passed here means that this applies to all rows. Optionally, you can add the query criteria after the entity type.
                FieldUpdate.<Number>builder("salary")
                        .apply(multiply(1.5))
                        .apply(add(5000))
                        .build()
        );
    }

}
```
Check the `DatabaseProxy` class and the `UpdateExpression` interface for more details.

## Native Queries

In case you want to write SQL queries directly, you can do this using `DatabaseProxy`'s support for _hibernate native queries_. In the example below, we create a native query with parameters interpolated into the query:

```java
import io.john.amiscaray.quak.core.di.provider.annotation.Instantiate;
import io.john.amiscaray.quak.core.di.provider.annotation.ManagedType;
import io.john.amiscaray.quak.data.query.DatabaseProxy;
import io.john.amiscaray.quak.data.query.DatabaseQuery;

import static io.john.amiscaray.quak.data.update.UpdateExpression.*;
import static io.john.amiscaray.quak.data.query.QueryCriteria.*;

@ManagedType
public class Test {

    private final DatabaseProxy dbProxy;

    @Instantiate
    public Test(DatabaseProxy dbProxy) {
        this.dbProxy = dbProxy;
    }

    public void updateDepartmentToTech() {
        dbProxy.createMutationQueryThen("UPDATE Employee e SET e.department = :newDepartment",
                query -> query.setParameter("newDepartment", "Tech") // This lambda accepts the query object created from the SQL string above.
                        .executeUpdate());
    }
}
```

Similarly, `DatabaseProxy` has a method for native queries used for selection:

```java
import io.john.amiscaray.quak.core.di.provider.annotation.Instantiate;
import io.john.amiscaray.quak.core.di.provider.annotation.ManagedType;
import io.john.amiscaray.quak.data.query.DatabaseProxy;
import io.john.amiscaray.quak.data.query.DatabaseQuery;

import static io.john.amiscaray.quak.data.update.UpdateExpression.*;
import static io.john.amiscaray.quak.data.query.QueryCriteria.*;

@ManagedType
public class Test {

    private final DatabaseProxy dbProxy;

    @Instantiate
    public Test(DatabaseProxy dbProxy) {
        this.dbProxy = dbProxy;
    }

    public void updateDepartmentToTech() {
        dbProxy.createMutationQueryThen("UPDATE Employee e SET e.department = :newDepartment",
                query -> query.setParameter("newDepartment", "Tech") // This lambda accepts the query object created from the SQL string above.
                        .executeUpdate());
    }
    
    public void getEmployeesInTheTechDepartment() {
        dbProxy.createSelectionQueryThen("FROM Employee WHERE department = 'Tech'", Employee.class, query -> {
            var myResults = query.getResultList();
            // Do what you need to do with the results...
        });
    }
}
```