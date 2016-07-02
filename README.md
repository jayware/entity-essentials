<p align="center">
    <img src="http://jayware.github.io/entity-essentials/assets/images/e2-logo.svg" height="250" alt="Entity Essentials">
</p>
<p>
    <h1>Entity Essentials - E<sup>2</sup></h1>
<table>
    <thead>
        <tr>
            <th align="center" colspan="3">master</th>
            <th align="center" colspan="3">development</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td align="center">
                <a href="https://travis-ci.org/jayware/entity-essentials">
                    <img src="https://img.shields.io/travis/jayware/entity-essentials/master.svg?style=flat-square" alt="Build Status">
                </a>
            </td>
            <td align="center">
                <a href="https://coveralls.io/github/jayware/entity-essentials?branch=master">
                    <img src="https://img.shields.io/coveralls/jayware/entity-essentials/master.svg?style=flat-square" alt="Coverage Status" />
                </a>
            </td>
            <td align="center">
                <a href="https://www.versioneye.com/user/projects/56b464390a0ff50035ba7e9f?child=summary">
                    <img src="https://www.versioneye.com/user/projects/56b464390a0ff50035ba7e9f/badge.svg?style=flat" alt="Dependency Status" />
                </a>
            </td>
            <td align="center">
                <a href="https://travis-ci.org/jayware/entity-essentials">
                    <img src="https://img.shields.io/travis/jayware/entity-essentials/development.svg?style=flat-square" alt="Build Status">
                </a>
            </td>
            <td align="center">
                <a href="https://coveralls.io/github/jayware/entity-essentials?branch=development">
                    <img src="https://img.shields.io/coveralls/jayware/entity-essentials/development.svg?style=flat-square" alt="Coverage Status" />
                </a>
            </td>
            <td align="center">
                <a href="https://www.versioneye.com/user/projects/56b465e30a0ff5002c85efe0?child=summary">
                    <img src="https://www.versioneye.com/user/projects/56b465e30a0ff5002c85efe0/badge.svg?style=flat" alt="Dependency Status" />
                </a>
            </td>
        </tr>
    </tbody>
</table>
</p>

## Description
Entity Essentials is a Component-based Entity System written in Java. This framework provides operations and structures to work with entities and components.
Apart from that, it provides a couple of useful extras. The current implementation isn't a typical <a href="https://en.wikipedia.org/wiki/Entity_component_system">Entity Component System (ECS)</a>
known from game development as a part of a game engine. Instead, this framework is designed as foundation for graphical modeling editors.

## Quickstart
```java
public class Quickstart {
  
    public static void main (String[] args) {
        
        /* Create a context and obtain required managers */
        Context context = ContextProvider.getInstance().createContext();
        EntityManager entityManager = context.getService(EntityManager.class);
        ComponentManager componentManager = context.getService(ComponentManager.class);
        
        /* Initially prepare custom components */
        componentManager.prepareComponent(context, ExampleComponent.class);
        
        /* Create an entity */
        entityManager.createEntity(context, EntityPath.path("/example"));
        
        /* Lookup an entity */
        EntityRef ref = entityManager.findEntity(context, EntityPath.path("/example"));
        
        /* Add a component to the entity */
        componentManager.addComponent(ref, ExampleComponent.class);
        
        /* Lookup a component */
        ExampleComponent cmp = componentManager.getComponent(ref, ExampleComponent.class);
        
        /* Change the component's properties */
        cmp.setText("Fubar!");
        cmp.setTextSize(14);
        
        /* Commit changes */
        cmp.pushTo(ref);
        
        /* Shutdown everything */
        context.dispose();
    }
    
    /* Define a custom component by a java interface */
    public interface ExampleComponent extends Component {
        /* Define properties by declaration of getters and setters  */
        String getText();
        
        void setText(String text);
        
        int getTextSize();
        
        void setTextSize(int size);
    }
}
```
More examples can be found [here](examples/README.md).

## Usage

### Maven coordinates
| Group ID              | Artifact ID                                                                                                      | Version |
| :-------------------: | :--------------------------------------------------------------------------------------------------------------: | :-----: |
| org.jayware           | <a href="https://jcenter.bintray.com/org/jayware/entity-essentials-api/">entity-essentials-api</a>     | 0.3.0   |
| org.jayware           | <a href="https://jcenter.bintray.com/org/jayware/entity-essentials-impl/">entity-essentials-impl</a>   | 0.3.0   |

### Gradle
###### Repository
```groovy
repositories {
   jcenter()
}
```
###### Dependencies
```groovy
dependencies {
    compile group: 'org.jayware', name: 'entity-essentials-api', version: '0.3.0'
    runtime group: 'org.jayware', name: 'entity-essentials-impl', version: '0.3.0'
}
```

### Maven
###### Repository
```xml
<repository>
    <id>central</id>
    <name>bintray</name>
    <url>http://jcenter.bintray.com</url>
</repository>
```
###### Dependencies
```xml
<dependency>
    <groupId>org.jayware</groupId>
    <artifactId>entity-essentials-api</artifactId>
    <version>0.3.0</version>
</dependency>
<dependency>
    <groupId>org.jayware</groupId>
    <artifactId>entity-essentials-impl</artifactId>
    <version>0.3.0</version>
    <scope>runtime</scope>
</dependency>
```

### Download
| <a href="https://jcenter.bintray.com/org/jayware/entity-essentials-api/">entity-essentials-api</a> | <a href="https://jcenter.bintray.com/org/jayware/entity-essentials-impl">entity-essentials-impl</a> |
| :------------------------------------------------------------------------------------------------: | :-------------------------------------------------------------------------------------------------: |

## Contributions
All contributions are welcome: ideas, patches, documentation, bug reports, complaints.
