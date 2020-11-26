import be.encelade.chimp.material.MaterialDefinitions
import be.encelade.chimp.utils.ColorHelperUtils.ColorRGBA
import be.encelade.ouistiti.CameraManager
import com.jme3.app.SimpleApplication
import com.jme3.system.AppSettings

fun main() {
    val settings = AppSettings(true)
    settings.isFullscreen = false
    settings.setResolution(1280, 720)

    val simpleApp = DemoSimpleApp()
    simpleApp.setSettings(settings)
    simpleApp.isShowSettings = false
    simpleApp.start()
}

class DemoSimpleApp : SimpleApplication() {

    lateinit var cameraManager: CameraManager

    override fun simpleInitApp() {
        MaterialDefinitions.load(assetManager)

        cameraManager = CameraManager(this)
        cameraManager.loadDefaultKeyMappings()

        inputManager.isCursorVisible = true
        flyCam.isEnabled = false
        viewPort.backgroundColor = ColorRGBA("#1c3064")

        rootNode.attachChild(SceneNode())
    }

    override fun simpleUpdate(tpf: Float) {
        cameraManager.simpleUpdate(tpf)
    }

}
