package be.encelade.ouistiti

import com.jme3.input.InputManager
import com.jme3.math.Vector2f

class MouseManager(val inputManager: InputManager) {

    private var previousCursorPosition: Vector2f? = null

    var deltaX = 0f
    var deltaY = 0f

    fun simpleUpdate() {
        updateCursorSpeed(inputManager.cursorPosition)
    }

    fun isCursorMoving() = deltaX != 0f || deltaY != 0f

    private fun updateCursorSpeed(currentPosition: Vector2f) {
        if (previousCursorPosition == null) {
            previousCursorPosition = currentPosition
        } else {
            deltaX = currentPosition.x - previousCursorPosition!!.x
            deltaY = currentPosition.y - previousCursorPosition!!.y
            previousCursorPosition = Vector2f(currentPosition)
        }
    }
}
