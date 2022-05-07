package be.encelade.ouistiti

import com.jme3.input.controls.AnalogListener

class CameraAnalogListener(private val cameraManager: CameraManager) : AnalogListener {

    override fun onAnalog(name: String?, value: Float, tpf: Float) {
        when (name) {
            ZOOM_IN -> cameraManager.cameraZoom(-value)
            ZOOM_OUT -> cameraManager.cameraZoom(value)
            else -> println("Unknown $name")
        }
    }

    companion object {

        const val ZOOM_IN = "ZOOM_IN"
        const val ZOOM_OUT = "ZOOM_OUT"

    }

}
