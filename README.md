<a href="https://paypal.me/benckx/2">
<img src="https://img.shields.io/badge/Donate-PayPal-green.svg"/>
</a>

# About

A basic camera system for a management game for jMonkeyEngine.

*Ouistiti* is French for marmoset. 

![https://www.youtube.com/watch?v=s6HhQtTJuFI](images/demo.png)

https://www.youtube.com/watch?v=ltBLNQPvsjU

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
            cameraManager.loadDefaultKeyMappings();

            inputManager.setCursorVisible(true);
            flyCam.setEnabled(false);
        }

        @Override
        public void simpleUpdate(float tpf) {
            cameraManager.simpleUpdate(tpf);
        }
}
```

Override these methods to customize the movement speed. Tpf is currently not included in the calculation. 

```Kotlin
    open fun cameraMovementSpeed(tpf: Float): Float
    open fun cameraZoomSpeed(tpf: Float, value: Float): Float
```

# Examples

## In Kotlin
https://github.com/benckx/ouistiti/blob/master/src/test/kotlin/TestCameraManager.kt

## In Java:
Sample project with Gradle configuration (Java 8):<br/>
https://github.com/benckx/ouistiti-java-sample

# Import with Gradle

    repositories {
        maven { url "https://jitpack.io" }
    }
    
    dependencies {
        compile "com.github.benckx:ouistiti:1.1"
    }
