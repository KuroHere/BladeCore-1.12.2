package com.bladecore.client.utils

import com.bladecore.client.event.listener.runTrying
import com.bladecore.client.utils.math.MathUtils.lerp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.FloatControl

object SoundUtils {
    private val scope = CoroutineScope(Dispatchers.Default)

    fun playSound(volume: Double = 1.0, url: () -> String) {
        scope.launch { runTrying { play(url(), volume.toFloat()) } }
    }

    private fun play(url: String, volume: Float) {
        val clip = AudioSystem.getClip()
        val audioSrc = this::class.java.getResourceAsStream("/assets/bladecore/sounds/$url") ?: return
        val bufferedIn = BufferedInputStream(audioSrc)
        val inputStream = AudioSystem.getAudioInputStream(bufferedIn)
        clip.open(inputStream)
        val gainControl = clip.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
        gainControl.value = lerp(-30f, 0f, volume)
        clip.start()
    }
}