# SugarLib
A multi-loader Minecraft library mod supporting both Fabric and NeoForge.

## Features
- Configs with UI, saving and server-client syncing built-in.

## Using in your mod
Because SugarLib is multi-loader, you need to depend on both the common code, and the loader-specific code.

### Dependency setup

#### Maven Repository
```groovy
repositories {
    maven {
        url = uri("https://repo.alignedcookie88.com/maven-public")
    }
}
```

#### Architectury
In `common/build.gradle`:
```groovy
dependencies {
    modImplementation "com.alignedcookie88.sugarlib:common:${sugarlib_version}"
}
```
In `fabric/build.gradle`:
```groovy
dependencies {
    modImplementation "com.alignedcookie88.sugarlib:fabric:${sugarlib_version}"
}
```
In `neoforge/build.gradle`:
```groovy
dependencies {
    modImplementation "com.alignedcookie88.sugarlib:neoforge:${sugarlib_version}"
}
```

#### Fabric
```groovy
dependencies {
    modImplementation "com.alignedcookie88.sugarlib:common:${sugarlib_version}"
    modImplementation "com.alignedcookie88.sugarlib:fabric:${sugarlib_version}"
}
```

#### NeoForge
```groovy
dependencies {
    implementation "com.alignedcookie88.sugarlib:common:${sugarlib_version}"
    implementation "com.alignedcookie88.sugarlib:neoforge:${sugarlib_version}"
}

runs {
    configureEach {
        dependencies {
            runtime "com.alignedcookie88.sugarlib:common:${sugarlib_version}"
            runtime "com.alignedcookie88.sugarlib:neoforge:${sugarlib_version}"
        }
    }
}
```

### Code
SugarLib uses ModInfo objects to do some work automatically for you. You should create one and register it with SugarLib.

In your initialisation method:
```java
// Fabric
ModInfo info = FabricModInfo.fromId("mod-id");
// NeoForge
ModInfo info = NeoForgeModInfo.fromModObject(this);

// Both
SugarLib.registerMod(info);
```
In architectury environments, you should pass your ModInfo object to your common initialisation method.

For more info, see the [wiki](https://github.com/AlignedCookie88/SugarLib/wiki).