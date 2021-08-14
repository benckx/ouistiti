package be.encelade.ouistiti

import com.jme3.scene.CameraNode

interface CameraSpeedCalculator {

    fun cameraMovementSpeed(tpf: Float, cameraNode: CameraNode): Float

    fun cameraZoomSpeed(tpf: Float, value: Float, cameraNode: CameraNode): Float

    fun cameraRotationSpeed(cameraNode: CameraNode): Float

}
