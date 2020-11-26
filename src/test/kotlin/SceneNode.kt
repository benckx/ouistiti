import be.encelade.chimp.material.UnshadedMaterial
import be.encelade.chimp.utils.ColorHelperUtils.ColorRGBA
import com.jme3.math.ColorRGBA.Blue
import com.jme3.math.FastMath.HALF_PI
import com.jme3.scene.Geometry
import com.jme3.scene.Node
import com.jme3.scene.debug.Grid
import com.jme3.scene.shape.Box

class SceneNode : Node("MY_SCENE") {

    init {
        attachChild(makeFloor())
        attachChild(makeGrid())
    }

    private companion object {

        private const val sizeX = 10
        private const val sizeY = 8

        private fun makeFloor(): Geometry {
            val floorMat = UnshadedMaterial()
            floorMat.setColor(ColorRGBA(155, 164, 193))

            val floor = Geometry("FLOOR", Box(sizeX / 2f, sizeY / 2f, 0f))
            floor.material = floorMat

            return floor
        }

        private fun makeGrid(): Geometry {
            val gridMat = UnshadedMaterial()
            gridMat.setColor(Blue)

            val grid = Geometry("GRID", Grid(sizeX + 1, sizeY + 1, 1f))
            grid.material = gridMat
            grid.rotate(HALF_PI, 0f, HALF_PI)
            grid.move(-(sizeX / 2f), -(sizeY / 2f), 0.01f)

            return grid
        }

    }
}
