package be.encelade.ouistiti

import be.encelade.ouistiti.CameraManager.Companion.ISO_VIEW_KEY
import be.encelade.ouistiti.CameraManager.Companion.MOVEMENT_KEY_PRESSED_ACTION
import be.encelade.ouistiti.CameraManager.Companion.ROTATE_CAMERA_AXIS_KEY_PRESSED_ACTION
import be.encelade.ouistiti.CameraManager.Companion.ROTATE_CLOCKWISE_KEY_PRESSED_ACTION
import be.encelade.ouistiti.CameraManager.Companion.ROTATE_COUNTER_CLOCKWISE_KEY_PRESSED_ACTION
import be.encelade.ouistiti.CameraManager.Companion.ROTATE_WORLD_AXIS_KEY_PRESSED_ACTION
import be.encelade.ouistiti.CameraManager.Companion.SIDE_VIEW_KEY
import be.encelade.ouistiti.CameraManager.Companion.SWITCH_VIEW
import be.encelade.ouistiti.CameraManager.Companion.TOP_VIEW_KEY
import be.encelade.ouistiti.ViewMode.*
import com.jme3.input.controls.ActionListener

class CameraActionListener(private val cameraManager: CameraManager) : ActionListener {

    override fun onAction(name: String?, isPressed: Boolean, tpf: Float) {
        when (name) {
            MOVEMENT_KEY_PRESSED_ACTION -> cameraManager.isMovementClickPressed = isPressed
            ROTATE_WORLD_AXIS_KEY_PRESSED_ACTION -> cameraManager.isRotationMovementPressed = isPressed
            ROTATE_CAMERA_AXIS_KEY_PRESSED_ACTION -> cameraManager.isCameraRotationMovementPressed = isPressed
            ROTATE_CLOCKWISE_KEY_PRESSED_ACTION -> cameraManager.isRotationClockwisePressed = isPressed
            ROTATE_COUNTER_CLOCKWISE_KEY_PRESSED_ACTION -> cameraManager.isRotationCounterClockwisePressed = isPressed
        }

        if (isPressed) {
            when (name) {
                SWITCH_VIEW -> cameraManager.switchViewMode()
                TOP_VIEW_KEY -> cameraManager.switchViewMode(TOP_VIEW)
                SIDE_VIEW_KEY -> cameraManager.switchViewMode(SIDE_VIEW)
                ISO_VIEW_KEY -> cameraManager.switchViewMode(ISO_VIEW)
            }
        }
    }

}
