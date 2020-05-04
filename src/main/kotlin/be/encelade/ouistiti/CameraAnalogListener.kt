package be.encelade.ouistiti

import com.jme3.input.controls.AnalogListener

class CameraAnalogListener(val cameraManager: CameraManager) : AnalogListener {

    override fun onAnalog(name: String?, value: Float, tpf: Float) {
        when (name) {
            WHEEL_UP -> cameraManager.cameraZoom(-value)
            WHEEL_DOWN -> cameraManager.cameraZoom(value)
            else -> println("Unknown $name")
        }
    }

    companion object {

        const val WHEEL_UP = "WHEEL_UP"
        const val WHEEL_DOWN = "WHEEL_DOWN"

    }
}
