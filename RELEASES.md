# Releases

# Version 0.3.0 (2016-11-09)

## Features
 - Combined components: This version introduces the ability to define components by extending existing ones.
   [[ #3 ]](https://github.com/jayware/entity-essentials/issues/3)
 - Entity Groups continued - Trees: The concept of the assembly module and its grouping-mechanism has been developed further to model trees of entities.
   [[ #12 ]](https://github.com/jayware/entity-essentials/issues/12)

## Enhancements
 - The ComponentManager does now offer an operation to obtain an instance of a component without the need to add the component to an entity.
   [[ #25 ]](https://github.com/jayware/entity-essentials/issues/25)
 - An Anspect does now offer an operation to get the internal set of component-types.
   [[ #26 ]](https://github.com/jayware/entity-essentials/issues/26)
 - The EntityManager does now offer an operation to delete all entities at once.
   [[ #32 ]](https://github.com/jayware/entity-essentials/issues/32)
 - The Aspect class has been rewritten and does now offer possibilities to specify an aspect of an entity more precisely.   
   [[ #35 ]](https://github.com/jayware/entity-essentials/issues/35)
 - Components do now implement the equals and hashcode operations.
   [[ #38 ]](https://github.com/jayware/entity-essentials/issues/38)

## Fixes
 - A NegativeArraySizeException has been fixed which occurred when an EventDispatcher was generated for an EventHandler with long or double parameters.
   [[ #14 ]](https://github.com/jayware/entity-essentials/issues/14)
 - A NullPointerException has been fixed which occurred when state-change of a Query was signaled.
   [[ #21 ]](https://github.com/jayware/entity-essentials/issues/21)
 - Framework-components are now instantiated eagerly to avoid problems when entities/components get created in an event-driven fashion.
   [[ #29 ]](https://github.com/jayware/entity-essentials/issues/29)
 - A bug has been fixed where two EntityRefs of two different contexts are equal if they reference an entity with the same UUID.
   [[ #30 ]](https://github.com/jayware/entity-essentials/issues/30)
 - A bug has been fixed which prevents the deletion of entities, because events which are related to the deletion use deprecated EntityPath instead of UUID.
   [[ #31 ]](https://github.com/jayware/entity-essentials/issues/31)
 - A bug has been fixed which potentially leads to a deadlock, because queries where scheduled on other worker threads. 
   [[ #37 ]](https://github.com/jayware/entity-essentials/issues/37)

## Misc
 - A couple of JavaDoc warnings have been fixed.
   [[ #16 ]](https://github.com/jayware/entity-essentials/issues/16)
 - The description of events has been revised.
   [[ #34 ]](https://github.com/jayware/entity-essentials/issues/34)

## Dependencies

| artifact                       | old version | new version |
| :----------------------------- | ----------- | ----------- |
| com.google.guava:guava         | 19          | 20          |
| org.slf4j:slf4j-api            | 1.7.18      | 1.7.21      |
| ch.qos.logback:logback-classic | 1.1.5       | 1.1.7       |
| org.testng:testng              | 6.9.10      | 6.9.13.6    |
| org.assertj:assertj-core       | 3.3.0       | 3.5.1       |
| org.jmockit:jmockit            | -           | 1.29        |

# Version 0.2.0 (2016-02-29)

## Features
 - Entity Groups: This Version introduces a new concept of managing group of entities.
   [[ #9 ]](https://github.com/jayware/entity-essentials/issues/9)
 - Asynchronous Queries: Initial support for query-operations to retrieve data asynchronously.
   [[ #11 ]](https://github.com/jayware/entity-essentials/issues/11)

## Enhancements
 - The Context interface does now offer a generic method to obtain managers from other modules.
   [[ #1 ]](https://github.com/jayware/entity-essentials/issues/1)
 - The ComponentStorage does now use the ServiceLoader infrastructure to obtain a ComponentFactory instance.
   [[ #2 ]](https://github.com/jayware/entity-essentials/issues/2)

## Fixes
 - A VerifyError has been fixed which occurred when an EventDispatcher was generated for an EventHandler with primitive parameters.
   [[ #13 ]](https://github.com/jayware/entity-essentials/issues/13)
 - A defect in the ComponentFactory has been fixed which led to invalid components when the getter and setter of a property are not of the same type.
   [[ #8 ]](https://github.com/jayware/entity-essentials/issues/8)

## Dependencies

| artifact                       | old version | new version |
| :----------------------------- | ----------- | ----------- |
| org.slf4j:slf4j-api            | 1.7.12      | 1.7.18      |
| com.google.guava:guava         | 18          | 19          |
| ch.qos.logback:logback-classic | 1.1.3       | 1.1.5       |
| org.testng:testng              | 6.9.4       | 6.9.10      |
| org.assertj:assertj-core       | 3.0.0       | 3.3.0       |

# Version 0.1.0 (2016-01-02)
Initial release.