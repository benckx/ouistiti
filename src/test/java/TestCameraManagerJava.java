import be.encelade.ouistiti.CameraManager;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import static be.encelade.ouistiti.CameraManager.ViewMode.ISO_VIEW;
import static com.jme3.material.Materials.UNSHADED;

public class TestCameraManagerJava {

    public static void main(String[] args) {
        new MyJavaApp().start();
    }

    public static class MyJavaApp extends SimpleApplication {

        int sizeX = 10;
        int sizeY = 8;
        CameraManager cameraManager;

        @Override
        public void simpleInitApp() {
            cameraManager = new CameraManager(this, ISO_VIEW);
            cameraManager.loadDefaultKeyMappings();

            inputManager.setCursorVisible(true);
            flyCam.setEnabled(false);

            addFloor();
            viewPort.setBackgroundColor(new ColorRGBA(28 / 255f, 48 / 255f, 100 / 255f, 1f));
        }

        @Override
        public void simpleUpdate(float tpf) {
            cameraManager.simpleUpdate();
        }

        public void addFloor() {
            Material floorMat = new Material(assetManager, UNSHADED);
            floorMat.setColor("Color", new ColorRGBA(155 / 255f, 164 / 255f, 193 / 255f, 1f));

            Geometry floor = new Geometry("FLOOR", new Box(sizeX / 2f, sizeY / 2f, 0f));
            floor.setMaterial(floorMat);
            rootNode.attachChild(floor);
        }
    }

}
