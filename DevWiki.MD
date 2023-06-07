# Dev Wiki
Welcome to the Dev Wiki, a collection of core concepts to speed through understanding the codebase.



### Getters & Setters
[Lombok](https://github.com/projectlombok/lombok "") is used to generate getters and setters with annotations. Use only the `@Getter` and `@Setter` annotations, never applied to a whole class at once.


### Naming
- All references to the game should be as
  `Melo Astronomer`, `MeloAstronomer`, `astronomer`, or `MA`

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
Systems are responsible for taking the components of an entity and processing game logic using them.
Systems can only be added a single engine

- `Single Entity System` Processes all entities of an _exact_ type.


## Networking
The server wraps incoming client connections for convenient storing of data. Client does not see this wrapped connection class.

### Packets
Any classes extending Packet.java under the same package are automatically registered with kryo using reflection.


### Notes
- Use [Clumsy](https://github.com/jagt/clumsy "") to simulate network lag


## Assets
All supported file formats are automatically loaded. The assets can be easily accessed using just the file name. The file name should include the extension e.g. texture.png


### Notes
- Asset files should follow snake case naming convention. Ex: `example_asset.png`
- All file names should be unique
