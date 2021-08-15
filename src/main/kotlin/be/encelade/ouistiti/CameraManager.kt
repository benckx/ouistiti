package be.encelade.ouistiti

import be.encelade.chimp.utils.VectorOperatorUtils.plus
import be.encelade.chimp.utils.VectorOperatorUtils.times
import be.encelade.ouistiti.CameraAnalogListener.Companion.WHEEL_DOWN
import be.encelade.ouistiti.CameraAnalogListener.Companion.WHEEL_UP
import be.encelade.ouistiti.ViewMode.*
import com.jme3.app.SimpleApplication
import com.jme3.input.FlyByCamera
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

class CameraManager(private val rootNode: Node,
                    private val camera: Camera,
                    flyByCam: FlyByCamera,
                    private val inputManager: InputManager,
                    private var viewMode: ViewMode = ISO_VIEW,
                    private val cameraSpeedCalculator: CameraSpeedCalculator) {

    constructor(app: SimpleApplication,
                viewMode: ViewMode = ISO_VIEW,
                cameraSpeedCalculator: CameraSpeedCalculator = DefaultCameraSpeedCalculator()) :
            this(app.rootNode, app.camera, app.flyByCamera, app.inputManager, viewMode, cameraSpeedCalculator)

    internal var isMovementClickPressed = false
    internal var isRotationMovementPressed = false
    internal var isCameraRotationMovementPressed = false

    internal var isRotationClockwisePressed = false
    internal var isRotationCounterClockwisePressed = false

    private val actionListener = CameraActionListener(this)
    private val analogListener = CameraAnalogListener(this)

    private var mouseManager = MouseManager(inputManager)
    private var cameraNode: CameraNode = resetCameraNode(viewMode)

    private var cameraAngleZ: Float = 0f

    init {
        inputManager.isCursorVisible = true
        flyByCam.isEnabled = false

        inputManager.addListener(actionListener, MOVEMENT_KEY_PRESSED_ACTION, ROTATE_WORLD_AXIS_KEY_PRESSED_ACTION,
                ROTATE_CAMERA_AXIS_KEY_PRESSED_ACTION, ROTATE_CLOCKWISE_KEY_PRESSED_ACTION, ROTATE_COUNTER_CLOCKWISE_KEY_PRESSED_ACTION,
                SWITCH_VIEW, TOP_VIEW_KEY, SIDE_VIEW_KEY, ISO_VIEW_KEY)

        inputManager.addListener(analogListener, WHEEL_UP, WHEEL_DOWN)
    }

    fun addDefaultKeyMappings() {
        addDefaultRightClickInputMappings()
        addDefaultRotationInputMappings()
        addDefaultSwitchViewInputMappings()
        addDefaultMouseWheelInputMappings()
    }

    fun addDefaultRightClickInputMappings() {
        inputManager.addMapping(MOVEMENT_KEY_PRESSED_ACTION, MouseButtonTrigger(BUTTON_RIGHT))
    }

    fun addDefaultRotationInputMappings() {
        inputManager.addMapping(ROTATE_WORLD_AXIS_KEY_PRESSED_ACTION, KeyTrigger(KEY_LCONTROL))
        inputManager.addMapping(ROTATE_CAMERA_AXIS_KEY_PRESSED_ACTION, KeyTrigger(KEY_LSHIFT))
        inputManager.addMapping(ROTATE_COUNTER_CLOCKWISE_KEY_PRESSED_ACTION, KeyTrigger(KEY_B))
        inputManager.addMapping(ROTATE_CLOCKWISE_KEY_PRESSED_ACTION, KeyTrigger(KEY_N))
    }

    fun addDefaultSwitchViewInputMappings() {
        inputManager.addMapping(SWITCH_VIEW, KeyTrigger(KEY_V))
        inputManager.addMapping(TOP_VIEW_KEY, KeyTrigger(KEY_T))
        inputManager.addMapping(SIDE_VIEW_KEY, KeyTrigger(KEY_S))
        inputManager.addMapping(ISO_VIEW_KEY, KeyTrigger(KEY_I))
    }

    fun addDefaultMouseWheelInputMappings() {
        inputManager.addMapping(WHEEL_UP, MouseAxisTrigger(AXIS_WHEEL, false))
        inputManager.addMapping(WHEEL_DOWN, MouseAxisTrigger(AXIS_WHEEL, true))
    }

    fun simpleUpdate(tpf: Float) {
        mouseManager.simpleUpdate()
        if (mouseManager.isCursorMoving()) {
            if (isMovementClickPressed) {
                if (isRotationMovementPressed || isCameraRotationMovementPressed) {
                    val delta = if (viewMode == TOP_VIEW) -mouseManager.deltaY else mouseManager.deltaX
                    if (isRotationMovementPressed) {
                        rotateOnWorldAxis(tpf * delta)
                    } else {
                        rotateOnCameraAxis(tpf * delta)
                    }
                } else {
                    moveCamera(tpf)
                }
            }
        } else if (isRotationClockwisePressed && !isRotationCounterClockwisePressed) {
            rotateOnWorldAxis(cameraSpeedCalculator.cameraRotationSpeed(cameraNode) * tpf)
        } else if (!isRotationClockwisePressed && isRotationCounterClockwisePressed) {
            rotateOnWorldAxis(-cameraSpeedCalculator.cameraRotationSpeed(cameraNode) * tpf)
        }
    }

    /**
     * Rotate [CameraNode] and move it along a circle, giving the impression
     * that the world is rotating from the camera perspective.
     */
    private fun rotateOnWorldAxis(angle: Float) {
        rotateCameraOnAxisZ(angle)

        if (viewMode != TOP_VIEW) {
            val radius = calculateRotationRadius()
            val x = sin(angle)
            val y = 1 - cos(angle)
            val delta = Vector3f(x, y, 0f) * radius
            cameraNode.move(rotateForCurrentAngle(delta))
        }

        cameraAngleZ -= angle
    }

    private fun rotateOnCameraAxis(angle: Float) {
        rotateCameraOnAxisZ(angle)
        cameraAngleZ -= angle
    }

    /**
     * Rotate [CameraNode] on its Z axis, without changing the value of "cameraAngleZ"
     */
    private fun rotateCameraOnAxisZ(angle: Float) {
        val baseRotation = initRotation[viewMode]!!
        val revertBaseRotation = baseRotation * -1f

        cameraNode.rotate(revertBaseRotation.x, revertBaseRotation.y, 0f)
        cameraNode.rotate(0f, 0f, angle)
        cameraNode.rotate(baseRotation.x, baseRotation.y, 0f)
    }

    /**
     * Distance between the camera (x, y) position and point that intersects with ground, if we trace a ray from the camera.
     * This radius is proportional to Z (rotation radius is larger if we're far from the ground)
     */
    private fun calculateRotationRadius(): Float {
        return cameraNode.localTranslation.z * cos(QUARTER_PI)
    }

    fun switchViewMode(newMode: ViewMode = viewMode.next()) {
        resetCameraNode(newMode)
        this.viewMode = newMode
    }

    fun cameraZoom(value: Float, tpf: Float) {
        val currentZ = cameraNode.camera.location.z
        val deltaZ = cameraSpeedCalculator.cameraZoomSpeed(tpf, value, cameraNode)
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

    private fun resetCameraNode(viewMode: ViewMode): CameraNode {
        cameraAngleZ = 0f

        rootNode.detachChildNamed(CAMERA_NODE)
        cameraNode = CameraNode(CAMERA_NODE, camera)
        rootNode.attachChild(cameraNode)

        val initRotation = initRotation[viewMode]!!
        cameraNode.rotate(initRotation.x, initRotation.y, initRotation.z)

        cameraNode.camera.location = Vector3f(0f, 0f, 0f)
        cameraNode.move(initLocation[viewMode]!!)

        return cameraNode
    }

    private fun moveCamera(tpf: Float) {
        var cameraMovement = Vector3f(mouseManager.deltaX, mouseManager.deltaY, 0f)

        if (viewMode == ISO_VIEW) {
            // add 1 rotation
            cameraMovement += Vector3f(cameraMovement.y, -cameraMovement.x, 0f)
        }

        // we multiply by -1 at the end because we want to move in the opposite direction of the mouse
        val movementSpeed = cameraSpeedCalculator.cameraMovementSpeed(tpf, cameraNode) * -1
        cameraNode.move(rotateForCurrentAngle(cameraMovement * movementSpeed))
    }

    private fun rotateForCurrentAngle(input: Vector3f): Vector3f {
        // found on https://en.wikipedia.org/wiki/Rotation_of_axes
        val x = input.x * cos(cameraAngleZ) + input.y * sin(cameraAngleZ)
        val y = -(input.x * sin(cameraAngleZ)) + input.y * cos(cameraAngleZ)
        return Vector3f(x, y, input.z)
    }

    companion object {

        const val CAMERA_NODE = "CAMERA_NODE"

        const val MOVEMENT_KEY_PRESSED_ACTION = "MOUSE_RIGHT_CLICK"
        const val ROTATE_WORLD_AXIS_KEY_PRESSED_ACTION = "ROTATE_WORLD"
        const val ROTATE_CAMERA_AXIS_KEY_PRESSED_ACTION = "ROTATE_CAMERA"

        const val ROTATE_CLOCKWISE_KEY_PRESSED_ACTION = "ROTATE_CLOCKWISE"
        const val ROTATE_COUNTER_CLOCKWISE_KEY_PRESSED_ACTION = "ROTATE_COUNTER_CLOCKWISE"

        const val SWITCH_VIEW = "SWITCH_VIEW"
        const val TOP_VIEW_KEY = "TOP_VIEW"
        const val SIDE_VIEW_KEY = "SIDE_VIEW"
        const val ISO_VIEW_KEY = "ISO_VIEW"

        // TODO: also make those configurable
        const val MIN_Z = 2
        const val MAX_Z = 40

        private val initLocation = mapOf(
                TOP_VIEW to Vector3f(0f, 0f, 20f),
                SIDE_VIEW to Vector3f(0f, -18f, 20f),
                ISO_VIEW to Vector3f(-13f, -13f, 18f)
        )

        private val topViewInitRotation = Vector3f(PI, 0f, PI)

        private val initRotation = mapOf(
                TOP_VIEW to topViewInitRotation + Vector3f(0f, 0f, 0f),
                SIDE_VIEW to topViewInitRotation + Vector3f(-QUARTER_PI, 0f, 0f),
                ISO_VIEW to topViewInitRotation + Vector3f(-QUARTER_PI, 0f, -QUARTER_PI)
        )

    }

}
