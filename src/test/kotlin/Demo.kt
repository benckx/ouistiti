import be.encelade.chimp.material.MaterialDefinitions
import be.encelade.chimp.utils.ColorHelperUtils.ColorRGBA
import be.encelade.ouistiti.CameraManager
import com.jme3.app.SimpleApplication
import com.jme3.system.AppSettings

fun main() {
    val settings = AppSettings(true)
    settings.title = "ouistiti demo"
    settings.isFullscreen = false
    settings.isVSync = false
    settings.samples = 16
    settings.setResolution(1920, 1080)

    val simpleApp = DemoSimpleApp()
    simpleApp.setSettings(settings)
    simpleApp.isShowSettings = false
    simpleApp.start()
}

class DemoSimpleApp : SimpleApplication() {

    private lateinit var cameraManager: CameraManager

    override fun simpleInitApp() {
        // init chimp-utils API for materials
        MaterialDefinitions.load(assetManager)

        // init CameraManager
        cameraManager = CameraManager(this)
        cameraManager.addDefaultKeyMappings()
//        cameraManager.addQwertyWASDInputMappings()
        cameraManager.addAzertyWASDInputMappings()

        // build scene
        viewPort.backgroundColor = ColorRGBA("#1c3064")
        rootNode.attachChild(SceneNode())
    }

    override fun simpleUpdate(tpf: Float) {
        cameraManager.simpleUpdate(tpf)
    }

}
