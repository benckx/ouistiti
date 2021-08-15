package be.encelade.ouistiti

import com.jme3.scene.CameraNode

interface CameraSpeedCalculator {

    fun cameraMovementSpeed(cameraNode: CameraNode): Float

    fun cameraZoomSpeed(value: Float, cameraNode: CameraNode): Float

    fun cameraRotationSpeed(cameraNode: CameraNode): Float

}
