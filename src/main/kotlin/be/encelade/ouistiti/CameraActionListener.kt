package be.encelade.ouistiti

import be.encelade.ouistiti.CameraManager.Companion.ISOMETRIC_VIEW_KEY
import be.encelade.ouistiti.CameraManager.Companion.MOVEMENT_KEY_PRESSED
import be.encelade.ouistiti.CameraManager.Companion.ROTATE_CAMERA_AXIS_KEY_PRESSED
import be.encelade.ouistiti.CameraManager.Companion.ROTATE_CLOCKWISE_KEY
import be.encelade.ouistiti.CameraManager.Companion.ROTATE_COUNTER_CLOCKWISE_KEY
import be.encelade.ouistiti.CameraManager.Companion.ROTATE_WORLD_AXIS_KEY_PRESSED
import be.encelade.ouistiti.CameraManager.Companion.SWITCH_VIEW_KEY
import be.encelade.ouistiti.CameraManager.Companion.TOP_VIEW_KEY
import be.encelade.ouistiti.ViewMode.ISOMETRIC_VIEW
import be.encelade.ouistiti.ViewMode.TOP_VIEW
import com.jme3.input.controls.ActionListener

class CameraActionListener(private val cameraManager: CameraManager) : ActionListener {

    override fun onAction(name: String?, isPressed: Boolean, tpf: Float) {
        when (name) {
            MOVEMENT_KEY_PRESSED -> cameraManager.isMovementClickPressed = isPressed
            ROTATE_WORLD_AXIS_KEY_PRESSED -> cameraManager.isRotationMovementPressed = isPressed
            ROTATE_CAMERA_AXIS_KEY_PRESSED -> cameraManager.isCameraRotationMovementPressed = isPressed
            ROTATE_CLOCKWISE_KEY -> cameraManager.isRotationClockwisePressed = isPressed
            ROTATE_COUNTER_CLOCKWISE_KEY -> cameraManager.isRotationCounterClockwisePressed = isPressed
        }

        if (isPressed) {
            when (name) {
                SWITCH_VIEW_KEY -> cameraManager.switchViewMode()
                TOP_VIEW_KEY -> cameraManager.switchViewMode(TOP_VIEW)
                ISOMETRIC_VIEW_KEY -> cameraManager.switchViewMode(ISOMETRIC_VIEW)
            }
        }
    }

}
