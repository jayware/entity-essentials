# Releases

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