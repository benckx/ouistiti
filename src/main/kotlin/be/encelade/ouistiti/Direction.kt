package be.encelade.ouistiti

import com.jme3.math.Vector2f

enum class Direction(val vector: Vector2f) {

    LEFT(Vector2f(-1f, 0f)),
    RIGHT(Vector2f(1f, 0f)),
    UP(Vector2f(0f, 1f)),
    DOWN(Vector2f(0f, -1f))

}
