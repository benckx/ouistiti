package be.encelade.ouistiti

import com.jme3.scene.CameraNode

class DefaultCameraSpeedCalculator : CameraSpeedCalculator {

    override fun cameraMovementSpeed(tpf: Float, cameraNode: CameraNode): Float {
        // speed is proportional by Z axis (i.e. by distance from the floor),
        // so we move faster as we are more zoomed out
        return CAMERA_BASE_SPEED * cameraNode.camera.location.z
    }

    /**
     * @param value from [com.jme3.input.controls.AnalogListener] call
     */
    override fun cameraZoomSpeed(tpf: Float, value: Float, cameraNode: CameraNode): Float {
        val currentZ = cameraNode.camera.location.z
        return ZOOM_BASE_SPEED * value * currentZ
    }

    companion object {

        const val CAMERA_BASE_SPEED = 0.0005f
        const val ZOOM_BASE_SPEED = 0.04f

    }

}
