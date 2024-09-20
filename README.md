# Nil: Enhanced Null Check Syntax for Java

Nil allows you to check if an object is null in an if statement. Instead of writing:

```java
if (object != null) { ... }
```

you would write:

```java
if (object) { ... }
```

## Overview

Experiment & Proof-of-Concept. While functional on Java 21, Nil is _not_ recommended for any use. If you're looking for an alternative syntax, consider other JVM languages. It's not worth torturing the Java Compiler.

This project was inspired by [Fluent](https://github.com/rogerkeays/fluent), which provides a way to compile and test Nil.

## Related Resources

* [Fluent](https://github.com/rogerkeays/fluent): static extension methods for Java.
* [Lombok](https://github.com/projectlombok/lombok): the grand-daddy of `javac` hacks.
* [Manifold](https://manifold.systems): a `javac` plugin with many features.

## Disclaimer

* Nil is not supported or endorsed by the OpenJDK team.
* The reasonable man adapts himself to the world. The unreasonable one persists in trying to adapt the world to himself.
  Therefore all progress depends on the unreasonable man. --George Bernard Shaw

## License

This project is licensed under the [MIT LICENSE](LICENSE).