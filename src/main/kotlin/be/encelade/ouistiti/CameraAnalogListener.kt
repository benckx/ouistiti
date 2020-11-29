package be.encelade.ouistiti

import com.jme3.input.controls.AnalogListener

class CameraAnalogListener(private val cameraManager: CameraManager) : AnalogListener {

    override fun onAnalog(name: String?, value: Float, tpf: Float) {
        when (name) {
            WHEEL_UP -> cameraManager.cameraZoom(-value, tpf)
            WHEEL_DOWN -> cameraManager.cameraZoom(value, tpf)
            else -> println("Unknown $name")
        }
    }

    companion object {

        const val WHEEL_UP = "WHEEL_UP"
        const val WHEEL_DOWN = "WHEEL_DOWN"

    }
}
