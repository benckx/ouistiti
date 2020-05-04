<a href="https://paypal.me/benckx/2">
<img src="https://img.shields.io/badge/Donate-PayPal-green.svg"/>
</a>

# About

A basic camera system to for a management game.

![https://www.youtube.com/watch?v=s6HhQtTJuFI](images/demo.png)

# Features

* Move camera by holding right click
* Zoom in and out with the mouse wheel
* Switch view with **V** (or use **T** for Top View, **S** for Side View, **I** for Isometric View)
* Rotate view with **R** 

# Examples

In Java:<br/> 
https://github.com/benckx/ouistiti/blob/master/src/test/java/TestCameraManagerJava.java

In Kotlin:<br/>
https://github.com/benckx/ouistiti/blob/master/src/test/kotlin/TestCameraManager.kt

# Usage

```Java
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
            cameraManager.simpleUpdate();
        }
}
```

# Import with Gradle

    repositories {
        maven { url "https://jitpack.io" }
    }
    
    dependencies {
        implementation "com.github.benckx:ouistiti:1.0"
    }
