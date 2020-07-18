package be.encelade.ouistiti

import be.encelade.ouistiti.ViewMode.*
import com.jme3.input.controls.ActionListener

class CameraActionListener(val cameraManager: CameraManager) : ActionListener {

    override fun onAction(name: String?, isPressed: Boolean, tpf: Float) {
        if (name == MOUSE_RIGHT_CLICK) {
            cameraManager.isRightClickPressed = isPressed
        }

        if (isPressed) {
            when (name) {
                SWITCH_VIEW -> cameraManager.switchViewMode()
                TOP_VIEW_KEY -> cameraManager.switchViewMode(TOP_VIEW)
                SIDE_VIEW_KEY -> cameraManager.switchViewMode(SIDE_VIEW)
                ISO_VIEW_KEY -> cameraManager.switchViewMode(ISO_VIEW)
                ROTATE -> cameraManager.rotate()
            }
        }
    }

    companion object {

        const val MOUSE_RIGHT_CLICK = "MOUSE_RIGHT_CLICK"

        const val ROTATE = "ROTATE"

        const val SWITCH_VIEW = "SWITCH_VIEW"
        const val TOP_VIEW_KEY = "TOP_VIEW"
        const val SIDE_VIEW_KEY = "SIDE_VIEW"
        const val ISO_VIEW_KEY = "ISO_VIEW"

    }
}
