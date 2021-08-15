package be.encelade.ouistiti

import be.encelade.chimp.utils.VectorOperatorUtils.plus
import be.encelade.chimp.utils.VectorOperatorUtils.times
import be.encelade.ouistiti.CameraAnalogListener.Companion.WHEEL_DOWN
import be.encelade.ouistiti.CameraAnalogListener.Companion.WHEEL_UP
import be.encelade.ouistiti.ViewMode.ISOMETRIC_VIEW
import be.encelade.ouistiti.ViewMode.TOP_VIEW
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
import com.jme3.math.Vector2f
import com.jme3.math.Vector3f
import com.jme3.renderer.Camera
import com.jme3.scene.CameraNode
import com.jme3.scene.Node

class CameraManager(private val rootNode: Node,
                    private val camera: Camera,
                    flyByCam: FlyByCamera,
                    private val inputManager: InputManager,
                    private var viewMode: ViewMode,
                    private val cameraSpeedCalculator: CameraSpeedCalculator) {

    constructor(app: SimpleApplication,
                viewMode: ViewMode = ISOMETRIC_VIEW,
                cameraSpeedCalculator: CameraSpeedCalculator = DefaultCameraSpeedCalculator()) :
            this(app.rootNode, app.camera, app.flyByCamera, app.inputManager, viewMode, cameraSpeedCalculator)

    /**
     * Is right-click pressed?
     */
    internal var isMovementClickPressed = false
    internal var isRotationMovementPressed = false
    internal var isCameraRotationMovementPressed = false

    internal var isRotationClockwisePressed = false
    internal var isRotationCounterClockwisePressed = false

    internal val directionKeyPressed = mutableMapOf<Direction, Boolean>()

    private val actionListener = CameraActionListener(this)
    private val analogListener = CameraAnalogListener(this)

    private var mouseManager = MouseManager(inputManager)
    private var cameraNode = initCameraNode()

    private var cameraAngleZ = 0f

    init {
        inputManager.clearMappings()
        inputManager.isCursorVisible = true
        flyByCam.isEnabled = false

        inputManager.addListener(actionListener, MOVE_LEFT_KEY, MOVE_RIGHT_KEY, MOVE_DOWN_KEY, MOVE_UP_KEY,
                MOVEMENT_KEY_PRESSED, ROTATE_WORLD_AXIS_KEY_PRESSED, ROTATE_CAMERA_AXIS_KEY_PRESSED,
                ROTATE_COUNTER_CLOCKWISE_KEY, ROTATE_CLOCKWISE_KEY,
                SWITCH_VIEW_KEY, TOP_VIEW_KEY, ISOMETRIC_VIEW_KEY)

        inputManager.addListener(analogListener, WHEEL_UP, WHEEL_DOWN)

        Direction.values().forEach { direction -> directionKeyPressed[direction] = false }
    }

    fun addDefaultKeyMappings() {
        addDefaultRightClickInputMappings()
        addWASDMovementInputMappings()
        addDefaultRotationInputMappings()
        addDefaultSwitchViewInputMappings()
        addDefaultMouseWheelInputMappings()
    }

    fun addDefaultRightClickInputMappings() {
        inputManager.addMapping(MOVEMENT_KEY_PRESSED, MouseButtonTrigger(BUTTON_RIGHT))
    }

    fun addWASDMovementInputMappings() {
        inputManager.addMapping(MOVE_LEFT_KEY, KeyTrigger(KEY_A))
        inputManager.addMapping(MOVE_RIGHT_KEY, KeyTrigger(KEY_D))
        inputManager.addMapping(MOVE_UP_KEY, KeyTrigger(KEY_W))
        inputManager.addMapping(MOVE_DOWN_KEY, KeyTrigger(KEY_S))
    }

    fun addArrowsMovementInputMappings() {
        inputManager.addMapping(MOVE_LEFT_KEY, KeyTrigger(KEY_LEFT))
        inputManager.addMapping(MOVE_RIGHT_KEY, KeyTrigger(KEY_RIGHT))
        inputManager.addMapping(MOVE_UP_KEY, KeyTrigger(KEY_UP))
        inputManager.addMapping(MOVE_DOWN_KEY, KeyTrigger(KEY_DOWN))
    }

    fun addDefaultRotationInputMappings() {
        inputManager.addMapping(ROTATE_WORLD_AXIS_KEY_PRESSED, KeyTrigger(KEY_LCONTROL))
        inputManager.addMapping(ROTATE_CAMERA_AXIS_KEY_PRESSED, KeyTrigger(KEY_LSHIFT))
        inputManager.addMapping(ROTATE_COUNTER_CLOCKWISE_KEY, KeyTrigger(KEY_B))
        inputManager.addMapping(ROTATE_CLOCKWISE_KEY, KeyTrigger(KEY_N))
    }

    fun addDefaultSwitchViewInputMappings() {
        inputManager.addMapping(SWITCH_VIEW_KEY, KeyTrigger(KEY_V))
        inputManager.addMapping(TOP_VIEW_KEY, KeyTrigger(KEY_T))
        inputManager.addMapping(ISOMETRIC_VIEW_KEY, KeyTrigger(KEY_I))
    }

    fun addDefaultMouseWheelInputMappings() {
        inputManager.addMapping(WHEEL_UP, MouseAxisTrigger(AXIS_WHEEL, false))
        inputManager.addMapping(WHEEL_DOWN, MouseAxisTrigger(AXIS_WHEEL, true))
    }

    fun switchViewMode(newMode: ViewMode = viewMode.next()) {
        this.viewMode = newMode
        initCameraNode()
    }

    fun simpleUpdate(tpf: Float) {
        mouseManager.simpleUpdate()
        if (mouseManager.isCursorMoving() && isMovementClickPressed) {
            if (isRotationMovementPressed || isCameraRotationMovementPressed) {
                val delta = if (viewMode == TOP_VIEW) -mouseManager.cursorMovement().y else mouseManager.cursorMovement().x
                if (isRotationMovementPressed) {
                    rotateOnWorldAxis(delta * tpf)
                } else {
                    rotateOnCameraAxis(delta * tpf)
                }
            } else {
                moveCameraBasedOnCursor()
            }
        } else if (isRotationClockwisePressed && !isRotationCounterClockwisePressed) {
            rotateOnWorldAxis(cameraSpeedCalculator.cameraRotationSpeed(cameraNode) * tpf)
        } else if (!isRotationClockwisePressed && isRotationCounterClockwisePressed) {
            rotateOnWorldAxis(-cameraSpeedCalculator.cameraRotationSpeed(cameraNode) * tpf)
        } else {
            directionKeyPressed
                    .filter { direction -> direction.value }
                    .keys
                    .forEach { direction ->
                        val directionalMovement = rotateForCurrentAngle(direction.vector * tpf)
                        val speed = cameraSpeedCalculator.keysMovementSpeed(cameraNode)
                        cameraNode.move(directionalMovement * speed)
                    }
        }
    }

    private fun moveCameraBasedOnCursor() {
        // we multiply by -1 as we want to move in the opposite direction of the mouse
        val movementSpeed = cameraSpeedCalculator.cursorMovementSpeed(cameraNode) * -1
        val cameraMovement = mouseManager.cursorMovement() * movementSpeed
        cameraNode.move(rotateForCurrentAngle(cameraMovement))
    }

    /**
     * Rotate [CameraNode] and move it along a circle, giving the impression
     * that the world is rotating from the camera perspective.
     */
    fun rotateOnWorldAxis(angle: Float) {
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

    /**
     * Rotate [CameraNode] on its Z axis, giving the impression
     * the camera is moving like a security camera.
     */
    fun rotateOnCameraAxis(angle: Float) {
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

    fun cameraZoom(value: Float) {
        val currentZ = cameraNode.camera.location.z
        val deltaZ = cameraSpeedCalculator.zoomSpeed(value, cameraNode)
        val targetZ = currentZ + deltaZ

        if ((value < 0 && targetZ > MIN_Z) || (value > 0 && targetZ < MAX_Z)) {
            val cameraMovement = when (viewMode) {
                TOP_VIEW -> Vector3f(0f, 0f, deltaZ)
                ISOMETRIC_VIEW -> Vector3f(0f, -deltaZ / 2, deltaZ / 2)
            }

            cameraNode.move(rotateForCurrentAngle(cameraMovement))
        }
    }

    private fun initCameraNode(): CameraNode {
        cameraAngleZ = 0f

        rootNode.detachChildNamed(CAMERA_NODE)
        cameraNode = CameraNode(CAMERA_NODE, camera)
        rootNode.attachChild(cameraNode)

        initRotation()
        initLocation()

        return cameraNode
    }

    private fun initRotation() {
        val initRotation = initRotation[viewMode]!!
        cameraNode.rotate(initRotation.x, initRotation.y, initRotation.z)

        if (viewMode == ISOMETRIC_VIEW) {
            rotateOnWorldAxis(-QUARTER_PI)
        }
    }

    private fun initLocation() {
        val initLocation = initLocation[viewMode]!!
        cameraNode.localTranslation = initLocation
    }

    private fun rotateForCurrentAngle(input: Vector2f): Vector3f {
        return rotateForCurrentAngle(Vector3f(input.x, input.y, 0f))
    }

    private fun rotateForCurrentAngle(input: Vector3f): Vector3f {
        // found on https://en.wikipedia.org/wiki/Rotation_of_axes
        val x = input.x * cos(cameraAngleZ) + input.y * sin(cameraAngleZ)
        val y = -(input.x * sin(cameraAngleZ)) + input.y * cos(cameraAngleZ)
        return Vector3f(x, y, input.z)
    }

    companion object {

        const val CAMERA_NODE = "CAMERA_NODE"

        const val MOVEMENT_KEY_PRESSED = "MOUSE_RIGHT_CLICK"
        const val ROTATE_WORLD_AXIS_KEY_PRESSED = "ROTATE_WORLD"
        const val ROTATE_CAMERA_AXIS_KEY_PRESSED = "ROTATE_CAMERA"

        const val MOVE_LEFT_KEY = "MOVE_LEFT"
        const val MOVE_RIGHT_KEY = "MOVE_RIGHT"
        const val MOVE_UP_KEY = "MOVE_UP"
        const val MOVE_DOWN_KEY = "MOVE_DOWN"

        const val ROTATE_CLOCKWISE_KEY = "ROTATE_CLOCKWISE"
        const val ROTATE_COUNTER_CLOCKWISE_KEY = "ROTATE_COUNTER_CLOCKWISE"

        const val SWITCH_VIEW_KEY = "SWITCH_VIEW"
        const val TOP_VIEW_KEY = "TOP_VIEW"
        const val ISOMETRIC_VIEW_KEY = "ISOMETRIC_VIEW"

        // TODO: also make those configurable
        const val MIN_Z = 2
        const val MAX_Z = 40

        private val topViewInitRotation = Vector3f(PI, 0f, PI)

        private val initRotation = mapOf(
                TOP_VIEW to topViewInitRotation + Vector3f(0f, 0f, 0f),
                ISOMETRIC_VIEW to topViewInitRotation + Vector3f(-QUARTER_PI, 0f, 0f)
        )

        private val initLocation = mapOf(
                TOP_VIEW to Vector3f(0f, 0f, 20f),
                ISOMETRIC_VIEW to Vector3f(-15f, -15f, 18f)
        )

    }

}
