package be.encelade.ouistiti

import be.encelade.ouistiti.CameraManager.Companion.ISOMETRIC_VIEW_ACTION
import be.encelade.ouistiti.CameraManager.Companion.MOUSE_MOVEMENT_ACTION
import be.encelade.ouistiti.CameraManager.Companion.MOVE_DOWN
import be.encelade.ouistiti.CameraManager.Companion.MOVE_LEFT
import be.encelade.ouistiti.CameraManager.Companion.MOVE_RIGHT
import be.encelade.ouistiti.CameraManager.Companion.MOVE_UP
import be.encelade.ouistiti.CameraManager.Companion.ROTATE_CAMERA_AXIS_WITH_MOUSE
import be.encelade.ouistiti.CameraManager.Companion.ROTATE_CLOCKWISE_ACTION
import be.encelade.ouistiti.CameraManager.Companion.ROTATE_COUNTER_CLOCKWISE_ACTION
import be.encelade.ouistiti.CameraManager.Companion.ROTATE_WORLD_AXIS_WITH_MOUSE
import be.encelade.ouistiti.CameraManager.Companion.SWITCH_VIEW_ACTION
import be.encelade.ouistiti.CameraManager.Companion.TOP_VIEW_ACTION
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
            ROTATE_CLOCKWISE_ACTION -> cameraManager.isRotationClockwisePressed = isPressed
            ROTATE_COUNTER_CLOCKWISE_ACTION -> cameraManager.isRotationCounterClockwisePressed = isPressed
        }

        if (isPressed) {
            when (name) {
                SWITCH_VIEW_ACTION -> cameraManager.switchViewMode()
                TOP_VIEW_ACTION -> cameraManager.switchViewMode(TOP_VIEW)
                ISOMETRIC_VIEW_ACTION -> cameraManager.switchViewMode(ISOMETRIC_VIEW)
            }
        }
    }

}
