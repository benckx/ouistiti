package be.encelade.ouistiti

import com.jme3.input.InputManager
import com.jme3.math.Vector2f

class MouseManager(private val inputManager: InputManager) {

    private var previousCursorPosition = Vector2f(inputManager.cursorPosition)
    private var cursorMovement = Vector2f(0f, 0f)

    fun simpleUpdate() {
        updateCursorSpeed(inputManager.cursorPosition)
    }

    private fun updateCursorSpeed(currentPosition: Vector2f) {
        val deltaX = currentPosition.x - previousCursorPosition.x
        val deltaY = currentPosition.y - previousCursorPosition.y
        cursorMovement = Vector2f(deltaX, deltaY)
        previousCursorPosition = Vector2f(currentPosition)
    }

    fun isCursorMoving(): Boolean {
        return cursorMovement.x != 0f || cursorMovement.y != 0f
    }

    fun cursorMovement(): Vector2f {
        return Vector2f(cursorMovement)
    }

}
