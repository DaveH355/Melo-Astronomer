# Dev Wiki
Welcome to the Dev Wiki, a collection of core concepts to speed through understanding the codebase.




### Getters & Setters
[Lombok](https://github.com/projectlombok/lombok "") is used to generate getters and setters with annotations. Use only the `@Getter` and `@Setter` annotations, never applied to a whole class at once.


### Naming
- All references to the game should be as
  `Melo Astronomer`, `MeloAstronomer`, `meloastronomer`, or `MA`

### Logging
Here's a simple example of logging.
```java
public class Example {
   public Example() {
       Log.set(Log.LEVEL_DEBUG);

       Log.info("Category", "Info");
       Log.error("Error!", new Exception("An error occurred"));
       Log.debug("Category", "Debug");
   }
}
```

## Entity Component System

### Entities
Entities are purely for creating/configuring components



- `Components` are bags of data, but allowed to have basic functions operating on its own data

### Systems
Systems are responsible for taking the components of an entity and creating game logic using them. There are a couple ways to go about this.

- `Single Entity System` Processes all entities of an _exact_ type. If this entity is a subclass, it will automatically be exempt from the relevant Broad Entity System.

- `Broad Entity System` Processes all entities of a broad type, or all entities of a particular super and subclasses. Basically the same as Single Entity System but any subclasses are also covered

- `Base Entity System` Processes one or more components of all entities. Usually this is used to update the values of a single component. Defining relationships between multiple components is possible but heavy-handed as it applies to all entities.

## Networking
The server wraps incoming client connections for convenient storing of data.
Note, client does not see this wrapped connection class


### Notes
- Use [Clumsy](https://github.com/jagt/clumsy "") to simulate network lag


## Assets
Adding assets to the game has been made simple. All supported file formats in non-blacklisted folders are auto loaded. You can access them in-game through the AssetManager instance with the relative path of your file.

- Optionally you can enable path resolving in AssetManager and access any assets simply with the filename. However, this requires all files to have unique names
- Blacklisted folders are registered in the AssetFinder class

### Notes
- Asset files should follow snake case naming convention. Ex: `example_asset.png`
