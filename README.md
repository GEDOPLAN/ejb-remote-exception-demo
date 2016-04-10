# ejb-remote-exception-demo

Demonstrates, how serverside exceptions get transferred to a remote client

Serverside exceptions cause problems for remote clients, if they contain classes within the exception chain which are not known to the client, i. e. which are not part of the client's classpath. For that reason EJBException gets a special treatment by some servers (or clientside libraries). EJBException is widely used for wrapping other serverside exceptions, especially for wrapping checked exception from subsystems within an unchecked exception transferrable to the client without forcing the client to declare them within throws clauses.

This project contains a simple stateless ejb ThrowExceptionServiceBean, which can be used for throwing various exceptions on the server:

* throwMyException throws a MyException.
* throwNullPointerException throws a NullPointerException.
* throwSqlException throws a SQLException by violating the uniqueness of a database column when inserting two rows.
  The SQLException will be wrapped by various exceptions including a PersistenceException.
  
The expectation is, that checked exceptions - including MyException - are transfered unchanged to the remote client,
while unchecked exceptions are wrapped by an EJBException before transferring them to the client. The EJBException should be loadable by the client, meaning that every nonstandard exception (like an exception from a special database or persistence provider - H2 and Hibernate/EclipseLink/OpenJPA in this case) get masked or filtered out in some way when or before loading the exception into the client.

The project contins a JUnit test class for verifying the expected behaviour. Please choose the appropriate maven profile before running the test:

* WildFly 10 (Profile as_wildfly-10.0):
  testThrowSqlException shows, that an EJBException is received by the client as expected. But it wrappes a
  ClassNotFoundException superceding the originally causing exception and therefore leaving the calling 
  clientside code totally unaware of the real cause.
  All remaining tests succeed.
  
* GlassFish 4.1 (Profile as_glassfish-4.0):
  testThrowSqlException shows, that an EJBException is received by the client as expected. But it wrappes a
  MarshallException superceding the originally causing exception and therefore leaving the calling 
  clientside code totally unaware of the real cause. 
  All remaining tests succeed.
  
  