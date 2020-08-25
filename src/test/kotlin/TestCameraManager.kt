import be.encelade.ouistiti.CameraManager
import com.jme3.app.SimpleApplication
import com.jme3.material.Material
import com.jme3.material.Materials.UNSHADED
import com.jme3.math.ColorRGBA
import com.jme3.math.FastMath.HALF_PI
import com.jme3.scene.Geometry
import com.jme3.scene.debug.Grid
import com.jme3.scene.shape.Box

fun main() {
    MyApp().start()
}

class MyApp : SimpleApplication() {

    private val sizeX = 10
    private val sizeY = 8

    lateinit var cameraManager: CameraManager

    override fun simpleInitApp() {
        cameraManager = CameraManager(this)
        cameraManager.loadDefaultKeyMappings()

        inputManager.isCursorVisible = true
        flyCam.isEnabled = false

        addFloor()
        addGrid()
        viewPort.backgroundColor = ColorRGBA(28 / 255f, 48 / 255f, 100 / 255f, 1f)
    }

    override fun simpleUpdate(tpf: Float) {
        cameraManager.simpleUpdate(tpf)
    }

    private fun addFloor() {
        val floorMat = Material(assetManager, UNSHADED)
        floorMat.setColor("Color", ColorRGBA(155 / 255f, 164 / 255f, 193 / 255f, 1f))

        val floor = Geometry("FLOOR", Box(sizeX / 2f, sizeY / 2f, 0f))
        floor.material = floorMat
        rootNode.attachChild(floor)
    }

    private fun addGrid() {
        val gridMat = Material(assetManager, UNSHADED)
        gridMat.setColor("Color", ColorRGBA.Blue)

        val grid = Geometry("GRID", Grid(sizeX + 1, sizeY + 1, 1f))
        grid.material = gridMat
        grid.rotate(HALF_PI, 0f, HALF_PI)
        grid.move(-(sizeX / 2f), -(sizeY / 2f), 0.01f)

        rootNode.attachChild(grid)
    }
}
