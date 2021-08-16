package be.encelade.ouistiti

import be.encelade.ouistiti.CameraManager.Companion.ISOMETRIC_VIEW_ACTION
import be.encelade.ouistiti.CameraManager.Companion.MOUSE_MOVEMENT_ACTION
import be.encelade.ouistiti.CameraManager.Companion.MOVE_DOWN_ACTION
import be.encelade.ouistiti.CameraManager.Companion.MOVE_LEFT_ACTION
import be.encelade.ouistiti.CameraManager.Companion.MOVE_RIGHT_ACTION
import be.encelade.ouistiti.CameraManager.Companion.MOVE_UP_ACTION
import be.encelade.ouistiti.CameraManager.Companion.ROTATE_CAMERA_AXIS_ACTION
import be.encelade.ouistiti.CameraManager.Companion.ROTATE_CLOCKWISE_ACTION
import be.encelade.ouistiti.CameraManager.Companion.ROTATE_COUNTER_CLOCKWISE_ACTION
import be.encelade.ouistiti.CameraManager.Companion.ROTATE_WORLD_AXIS_ACTION
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
            MOVE_LEFT_ACTION -> cameraManager.directionKeyPressed[LEFT] = isPressed
            MOVE_RIGHT_ACTION -> cameraManager.directionKeyPressed[RIGHT] = isPressed
            MOVE_UP_ACTION -> cameraManager.directionKeyPressed[UP] = isPressed
            MOVE_DOWN_ACTION -> cameraManager.directionKeyPressed[DOWN] = isPressed
            ROTATE_WORLD_AXIS_ACTION -> cameraManager.isRotationMovementPressed = isPressed
            ROTATE_CAMERA_AXIS_ACTION -> cameraManager.isCameraRotationMovementPressed = isPressed
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
