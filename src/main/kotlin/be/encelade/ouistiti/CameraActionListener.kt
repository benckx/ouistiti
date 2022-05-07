package be.encelade.ouistiti

import be.encelade.ouistiti.Direction.*
import be.encelade.ouistiti.ViewMode.ISOMETRIC_VIEW
import be.encelade.ouistiti.ViewMode.TOP_VIEW
import com.jme3.input.controls.ActionListener

class CameraActionListener(private val cameraManager: CameraManager) : ActionListener {

    override fun onAction(name: String?, isPressed: Boolean, tpf: Float) {
        when (name) {
            MOUSE_MOVEMENT_ACTION -> cameraManager.isMovementClickPressed = isPressed
            MOVE_LEFT -> cameraManager.pressedDirectionKeysMap[LEFT] = isPressed
            MOVE_RIGHT -> cameraManager.pressedDirectionKeysMap[RIGHT] = isPressed
            MOVE_UP -> cameraManager.pressedDirectionKeysMap[UP] = isPressed
            MOVE_DOWN -> cameraManager.pressedDirectionKeysMap[DOWN] = isPressed
            ROTATE_WORLD_AXIS_WITH_MOUSE -> cameraManager.isRotationMovementPressed = isPressed
            ROTATE_CAMERA_AXIS_WITH_MOUSE -> cameraManager.isCameraRotationMovementPressed = isPressed
            ROTATE_CLOCKWISE -> cameraManager.isRotationClockwisePressed = isPressed
            ROTATE_COUNTER_CLOCKWISE -> cameraManager.isRotationCounterClockwisePressed = isPressed
        }

        if (isPressed) {
            when (name) {
                SWITCH_VIEW_ACTION -> cameraManager.switchViewMode()
                TOP_VIEW_ACTION -> cameraManager.switchViewMode(TOP_VIEW)
                ISOMETRIC_VIEW_ACTION -> cameraManager.switchViewMode(ISOMETRIC_VIEW)
            }
        }
    }

    companion object {

        const val MOUSE_MOVEMENT_ACTION = "MOUSE_MOVEMENT"
        const val ROTATE_WORLD_AXIS_WITH_MOUSE = "ROTATE_WORLD_WITH_MOUSE"
        const val ROTATE_CAMERA_AXIS_WITH_MOUSE = "ROTATE_CAMERA_WITH_MOUSE"

        const val MOVE_LEFT = "MOVE_LEFT"
        const val MOVE_RIGHT = "MOVE_RIGHT"
        const val MOVE_UP = "MOVE_UP"
        const val MOVE_DOWN = "MOVE_DOWN"

        const val ROTATE_COUNTER_CLOCKWISE = "ROTATE_COUNTER_CLOCKWISE"
        const val ROTATE_CLOCKWISE = "ROTATE_CLOCKWISE"

        const val SWITCH_VIEW_ACTION = "SWITCH_VIEW"
        const val TOP_VIEW_ACTION = "TOP_VIEW"
        const val ISOMETRIC_VIEW_ACTION = "ISOMETRIC_VIEW"

    }

}
