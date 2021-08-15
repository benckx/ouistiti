package be.encelade.ouistiti

import com.jme3.scene.CameraNode

open class DefaultCameraSpeedCalculator : CameraSpeedCalculator {

    override fun cursorMovementSpeed(cameraNode: CameraNode): Float {
        // speed is proportional by Z axis (i.e. by distance from the floor),
        // so we move faster as we are more zoomed out
        return CAMERA_BASE_SPEED * cameraNode.camera.location.z
    }

    override fun keysMovementSpeed(cameraNode: CameraNode): Float {
        // speed is proportional by Z axis (i.e. by distance from the floor),
        // so we move faster as we are more zoomed out
        return CAMERA_KEY_SPEED * cameraNode.camera.location.z
    }

    override fun zoomSpeed(value: Float, cameraNode: CameraNode): Float {
        return ZOOM_BASE_SPEED * value
    }

    override fun cameraRotationSpeed(cameraNode: CameraNode): Float {
        return ROTATION_BASE_SPEED
    }

    companion object {

        const val CAMERA_BASE_SPEED = 0.0005f
        const val CAMERA_KEY_SPEED = 2f
        const val ZOOM_BASE_SPEED = 2f
        const val ROTATION_BASE_SPEED = 4f

    }

}
