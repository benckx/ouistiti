package be.encelade.ouistiti

import be.encelade.chimp.utils.VectorOperatorUtils.plus
import be.encelade.chimp.utils.VectorOperatorUtils.times
import be.encelade.ouistiti.CameraActionListener.Companion.ISO_VIEW_KEY
import be.encelade.ouistiti.CameraActionListener.Companion.LEFT_CONTROL
import be.encelade.ouistiti.CameraActionListener.Companion.MOUSE_RIGHT_CLICK
import be.encelade.ouistiti.CameraActionListener.Companion.ROTATE
import be.encelade.ouistiti.CameraActionListener.Companion.SIDE_VIEW_KEY
import be.encelade.ouistiti.CameraActionListener.Companion.SWITCH_VIEW
import be.encelade.ouistiti.CameraActionListener.Companion.TOP_VIEW_KEY
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

    internal var isRightClickPressed = false
    internal var isLeftControlPressed = false
    internal var isLeftAltPressed = false

    private val actionListener = CameraActionListener(this)
    private val analogListener = CameraAnalogListener(this)

    private var mouseManager = MouseManager(inputManager)
    private var cameraNode: CameraNode = resetCameraNode(viewMode)

    private var cameraAngleZ: Float = 0f
    private var clockWise = true

    init {
        inputManager.isCursorVisible = true
        flyByCam.isEnabled = false

        inputManager.addListener(actionListener, MOUSE_RIGHT_CLICK, LEFT_CONTROL, ROTATE, SWITCH_VIEW, TOP_VIEW_KEY, SIDE_VIEW_KEY, ISO_VIEW_KEY)
        inputManager.addListener(analogListener, WHEEL_UP, WHEEL_DOWN)
    }

    fun addDefaultKeyMappings() {
        addDefaultRightClickInputMappings()
        addControlRotateInputMappings()
        addDefaultRotateInputMappings()
        addDefaultSwitchViewInputMappings()
        addDefaultMouseWheelInputMappings()
    }

    fun addDefaultRightClickInputMappings() {
        inputManager.addMapping(MOUSE_RIGHT_CLICK, MouseButtonTrigger(BUTTON_RIGHT))
    }

    fun addControlRotateInputMappings() {
        inputManager.addMapping(LEFT_CONTROL, KeyTrigger(KEY_LCONTROL))
    }

    fun addDefaultRotateInputMappings() {
        inputManager.addMapping(ROTATE, KeyTrigger(KEY_R), KeyTrigger(KEY_O))
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
            if (isRightClickPressed) {
                if (isLeftControlPressed) {
                    val delta = if (viewMode == TOP_VIEW) -mouseManager.deltaY else mouseManager.deltaX
                    rotateCameraAxis(tpf * delta)
                } else {
                    rightClickMovement(tpf)
                }
            }
        }
    }

    private fun rotateCameraAxis(angle: Float) {
        val baseRotation = baseRotation[viewMode]!! + Vector3f(0f, 0f, -cameraAngleZ)
        val revertBaseRotation = baseRotation * -1f

        cameraNode.rotate(revertBaseRotation.x, revertBaseRotation.y, revertBaseRotation.y)
        cameraNode.rotate(0f, 0f, angle)
        cameraNode.rotate(baseRotation.x, baseRotation.y, baseRotation.y)

        if (viewMode != TOP_VIEW) {
            val radius = calculateRotationRadius()
            val x = radius * sin(angle)
            val y = radius * (1 - cos(angle))
            val delta = Vector3f(x, y, 0f)
            println("$x, $y / ${RAD_TO_DEG * angle}")
            cameraNode.move(rotateForCurrentAngle(delta))
        }

        this.cameraAngleZ -= angle
    }

    private fun calculateRotationRadius(): Float {
        return cameraNode.localTranslation.z * cos(QUARTER_PI)
    }

    fun rotate() {
        repeat(if (clockWise) 3 else 1) {
            cameraAngleZ += HALF_PI
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

        val rotation = baseRotation[viewMode]!!
        cameraNode.rotate(rotation.x, rotation.y, rotation.z)

        cameraNode.camera.location = Vector3f(0f, 0f, 0f)
        cameraNode.move(baseLocation[viewMode]!!)

        return cameraNode
    }

    private fun rightClickMovement(tpf: Float) {
        var cameraMovement = Vector3f(mouseManager.deltaX, mouseManager.deltaY, 0f)

        if (viewMode == ISO_VIEW) {
            // add 1 rotation
            cameraMovement += Vector3f(cameraMovement.y, -cameraMovement.x, 0f)
        }

        // we multiply by -1 at the end because we want to move in the opposite direction of the mouse
        val movementSpeed = cameraSpeedCalculator.cameraMovementSpeed(tpf, cameraNode) * -1
        cameraNode.move(rotateForCurrentAngle(cameraMovement * movementSpeed))
    }


    /**
     * Found on https://en.wikipedia.org/wiki/Rotation_of_axes
     */
    private fun rotateForCurrentAngle(input: Vector3f): Vector3f {
        val x = input.x * cos(cameraAngleZ) + input.y * sin(cameraAngleZ)
        val y = -(input.x * sin(cameraAngleZ)) + input.y * cos(cameraAngleZ)
        return Vector3f(x, y, input.z)
    }

    private companion object {

        const val CAMERA_NODE = "CAMERA_NODE"

        // TODO: also make those configurable
        const val MIN_Z = 2
        const val MAX_Z = 40

        private val baseLocation = mapOf(
                TOP_VIEW to Vector3f(0f, 0f, 20f),
                SIDE_VIEW to Vector3f(0f, -18f, 20f),
                ISO_VIEW to Vector3f(-13f, -13f, 18f)
        )

        private val topViewRotation = Vector3f(PI, 0f, PI)

        private val baseRotation = mapOf(
                TOP_VIEW to topViewRotation + Vector3f(0f, 0f, 0f),
                SIDE_VIEW to topViewRotation + Vector3f(-QUARTER_PI, 0f, 0f),
                ISO_VIEW to topViewRotation + Vector3f(-QUARTER_PI, 0f, -QUARTER_PI)
        )

    }

}
