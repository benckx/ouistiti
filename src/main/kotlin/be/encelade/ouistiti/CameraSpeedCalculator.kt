package be.encelade.ouistiti

import com.jme3.scene.CameraNode

interface CameraSpeedCalculator {

    fun cursorMovementSpeed(cameraNode: CameraNode): Float

    fun keysMovementSpeed(cameraNode: CameraNode): Float

    fun zoomSpeed(value: Float, cameraNode: CameraNode): Float

    fun cameraRotationSpeed(cameraNode: CameraNode): Float

}
