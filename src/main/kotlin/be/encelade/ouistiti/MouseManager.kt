package be.encelade.ouistiti

import com.jme3.input.InputManager
import com.jme3.math.Vector2f

class MouseManager(private val inputManager: InputManager) {

    private var previousCursorPosition: Vector2f? = null
    private var cursorMovement: Vector2f? = null

    var deltaX = 0f
    var deltaY = 0f

    fun simpleUpdate() {
        updateCursorSpeed(inputManager.cursorPosition)
    }

    fun isCursorMoving(): Boolean {
        return deltaX != 0f || deltaY != 0f
    }

    private fun updateCursorSpeed(currentPosition: Vector2f) {
        previousCursorPosition?.let { previous ->
            deltaX = currentPosition.x - previous.x
            deltaY = currentPosition.y - previous.y
            cursorMovement = Vector2f(deltaX, deltaY)
        }

        previousCursorPosition = Vector2f(currentPosition)
    }

}
