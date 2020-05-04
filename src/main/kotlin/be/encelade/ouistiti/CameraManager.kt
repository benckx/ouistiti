package be.encelade.ouistiti

import be.encelade.ouistiti.CameraActionListener.Companion.ISO_VIEW_KEY
import be.encelade.ouistiti.CameraActionListener.Companion.MOUSE_RIGHT_CLICK
import be.encelade.ouistiti.CameraActionListener.Companion.ROTATE
import be.encelade.ouistiti.CameraActionListener.Companion.SIDE_VIEW_KEY
import be.encelade.ouistiti.CameraActionListener.Companion.SWITCH_VIEW
import be.encelade.ouistiti.CameraActionListener.Companion.TOP_VIEW_KEY
import be.encelade.ouistiti.CameraAnalogListener.Companion.WHEEL_DOWN
import be.encelade.ouistiti.CameraAnalogListener.Companion.WHEEL_UP
import be.encelade.ouistiti.ViewMode.*
import com.jme3.app.SimpleApplication
import com.jme3.input.InputManager
import com.jme3.input.KeyInput.*
import com.jme3.input.MouseInput.AXIS_WHEEL
import com.jme3.input.MouseInput.BUTTON_RIGHT
import com.jme3.input.controls.KeyTrigger
import com.jme3.input.controls.MouseAxisTrigger
import com.jme3.input.controls.MouseButtonTrigger
import com.jme3.math.FastMath.*
import com.jme3.math.Vector3f
import com.jme3.renderer.Camera
import com.jme3.scene.CameraNode
import com.jme3.scene.Node

open class CameraManager(val rootNode: Node, val camera: Camera, val inputManager: InputManager, var viewMode: ViewMode = ISO_VIEW) {

    constructor(app: SimpleApplication, viewMode: ViewMode = ISO_VIEW) : this(app.rootNode, app.camera, app.inputManager, viewMode)

    var isRightClickPressed = false

    val actionListener = CameraActionListener(this)
    val analogListener = CameraAnalogListener(this)

    private var mouseManager = MouseManager(inputManager)
    private var cameraNode: CameraNode = resetCameraNode(viewMode)

    private var nbrRotations: Int = 0
    private var clockWise = true

    fun loadDefaultKeyMappings() {
        inputManager.addMapping(MOUSE_RIGHT_CLICK, MouseButtonTrigger(BUTTON_RIGHT))

        inputManager.addMapping(ROTATE, KeyTrigger(KEY_R), KeyTrigger(KEY_O))
        inputManager.addMapping(SWITCH_VIEW, KeyTrigger(KEY_V))
        inputManager.addMapping(TOP_VIEW_KEY, KeyTrigger(KEY_T))
        inputManager.addMapping(SIDE_VIEW_KEY, KeyTrigger(KEY_S))
        inputManager.addMapping(ISO_VIEW_KEY, KeyTrigger(KEY_I))

        inputManager.addMapping(WHEEL_UP, MouseAxisTrigger(AXIS_WHEEL, false))
        inputManager.addMapping(WHEEL_DOWN, MouseAxisTrigger(AXIS_WHEEL, true))

        inputManager.addListener(actionListener, MOUSE_RIGHT_CLICK, ROTATE, SWITCH_VIEW, TOP_VIEW_KEY, SIDE_VIEW_KEY, ISO_VIEW_KEY)
        inputManager.addListener(analogListener, WHEEL_UP, WHEEL_DOWN)
    }

    fun simpleUpdate(tpf: Float) {
        mouseManager.simpleUpdate()
        if (isRightClickPressed && mouseManager.isCursorMoving()) {
            rightClickMovement(tpf)
        }
    }

    fun rotate() {
        if (clockWise) {
            for (i in 1..3) incrementNbrRotations()
        } else {
            incrementNbrRotations()
        }

        if (viewMode == TOP_VIEW) {
            val zAngle = if (clockWise) -HALF_PI else HALF_PI
            val locationBefore = Vector3f(cameraNode.camera.location)

            cameraNode.move(-locationBefore.x, -locationBefore.y, 0f)
            cameraNode.rotate(0f, 0f, zAngle)
            cameraNode.move(locationBefore.x, locationBefore.y, 0f)
        } else {
            resetCameraNode(viewMode)
        }
    }

    fun switchViewMode(newMode: ViewMode = viewMode.next()) {
        resetCameraNode(newMode)
        this.viewMode = newMode
    }

    fun cameraZoom(value: Float, tpf: Float) {
        val currentZ = cameraNode.camera.location.z
        val deltaZ = cameraZoomSpeed(tpf, value)
        val targetZ = currentZ + deltaZ

        if ((value < 0 && targetZ > MIN_Z) || (value > 0 && targetZ < MAX_Z)) {
            val cameraMovement = when (viewMode) {
                TOP_VIEW -> Vector3f(0f, 0f, deltaZ)
                SIDE_VIEW -> Vector3f(0f, -deltaZ / 2, deltaZ / 2)
                ISO_VIEW -> Vector3f(-deltaZ / 2, -deltaZ / 2, deltaZ)
            }

            cameraNode.move(rotateForCurrentAngle(cameraMovement))
        }
    }

    /**
     * Can be overridden to customize the speed
     */
    open fun cameraMovementSpeed(tpf: Float): Float {
        // speed is proportional by Z axis (i.e. by distance from the floor), so we move faster as we are zoomed out
        return CAMERA_BASE_SPEED * cameraNode.camera.location.z
    }

    /**
     * Can be overridden to customize the speed
     */
    open fun cameraZoomSpeed(tpf: Float, value: Float): Float {
        val currentZ = cameraNode.camera.location.z
        return ZOOM_BASE_SPEED * value * currentZ
    }

    private fun incrementNbrRotations() {
        if (nbrRotations == 3) {
            nbrRotations = 0
        } else {
            nbrRotations++
        }
    }

    private fun resetCameraNode(viewMode: ViewMode): CameraNode {
        rootNode.detachChildNamed(CAMERA_NODE)
        cameraNode = CameraNode(CAMERA_NODE, camera)
        rootNode.attachChild(cameraNode)

        val rotation = baseRotationFor(viewMode)
        cameraNode.rotate(rotation.x, rotation.y, rotation.z)

        cameraNode.camera.location = Vector3f(0f, 0f, 0f)
        cameraNode.move(baseLocationFor(viewMode))

        return cameraNode
    }

    private fun rightClickMovement(tpf: Float) {
        var cameraMovement = Vector3f(mouseManager.deltaX, mouseManager.deltaY, 0f)

        if (viewMode == ISO_VIEW) {
            val rotated = Vector3f(cameraMovement.y, -cameraMovement.x, 0f)
            cameraMovement = cameraMovement.add(rotated)
        }

        // we multiply by -1 at the end because we want to move in the opposite direction of the mouse
        val movementSpeed = cameraMovementSpeed(tpf) * -1
        cameraNode.move(rotateForCurrentAngle(cameraMovement.mult(movementSpeed)))
    }

    private fun baseLocationFor(viewMode: ViewMode): Vector3f {
        val result = when (viewMode) {
            TOP_VIEW -> Vector3f(0f, 0f, 20f)
            SIDE_VIEW -> Vector3f(0f, -18f, 20f)
            ISO_VIEW -> Vector3f(-13f, -13f, 18f)
        }

        return rotateForCurrentAngle(result)
    }

    private fun baseRotationFor(viewMode: ViewMode): Vector3f {
        val topView = Vector3f(PI, 0f, PI)

        val baseRotation = when (viewMode) {
            TOP_VIEW -> topView
            SIDE_VIEW -> topView.add(Vector3f(-QUARTER_PI, 0f, 0f))
            ISO_VIEW -> topView.add(Vector3f(-QUARTER_PI, 0f, -QUARTER_PI))
        }

        return baseRotation.add(Vector3f(0f, 0f, -(nbrRotations * HALF_PI)))
    }

    private fun rotateForCurrentAngle(input: Vector3f): Vector3f {
        var result = Vector3f(input)
        for (i in 1..nbrRotations) {
            result = Vector3f(result.y, -result.x, result.z)
        }

        return result
    }

    private companion object {

        const val CAMERA_NODE = "CAMERA_NODE"

        const val CAMERA_BASE_SPEED = 0.0005f
        const val ZOOM_BASE_SPEED = 0.04f

        // TODO: also make those configurable
        const val MIN_Z = 2
        const val MAX_Z = 40

    }
}
