<a href="https://paypal.me/benckx/2">
<img src="https://img.shields.io/badge/Donate-PayPal-green.svg"/>
</a>

# About

A basic camera system for a management game for jMonkeyEngine.

*Ouistiti* is French for marmoset. 

![https://www.youtube.com/watch?v=s6HhQtTJuFI](images/demo.png)

* https://www.youtube.com/watch?v=Tm6QexsctfQ
* https://www.youtube.com/watch?v=qzs6Z_jM6mE

# Features

* Move camera by holding right click
* Zoom in and out with the mouse wheel
* Switch view with **V** (or use **T** for Top View, **S** for Side View, **I** for Isometric View)
* Rotate view with **R** 

# Usage

```Java
    import be.encelade.ouistiti.CameraManager;
    import com.jme3.app.SimpleApplication;
    
    // ...

    public static class MyJavaApp extends SimpleApplication {

        CameraManager cameraManager;

        @Override
        public void simpleInitApp() {
            cameraManager = new CameraManager(this, ISO_VIEW);
            cameraManager.addDefaultKeyMappings();
        }

        @Override
        public void simpleUpdate(float tpf) {
            cameraManager.simpleUpdate(tpf);
        }
}
```

# Examples

## In Kotlin
https://github.com/benckx/ouistiti/blob/master/src/test/kotlin/Demo.kt

## In Java
Sample project with Gradle configuration (Java 8):<br/>
https://github.com/benckx/ouistiti-java-sample

# Import with Gradle

    repositories {
        maven { url "https://jitpack.io" }
    }
    
    dependencies {
        compile "com.github.benckx:ouistiti:1.2"
    }

# Change log
## Version 1.2
* Use `chimp-utils` project
* Update Kotlin from 1.4.10 to 1.4.20
* Add more customizations settings (split `CameraSpeedCalculator`, split the different default key mappings, etc.)

# Related Project
Collection Kotlin of APIs and Helper:
* https://github.com/benckx/chimp-utils

If I manage to extract re-usable management game features from my game, I would move them to the ouistiti project, while more generic / engine-related components would be added here in the chimp-utils project.
