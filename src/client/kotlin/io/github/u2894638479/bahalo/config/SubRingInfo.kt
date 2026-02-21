package io.github.u2894638479.bahalo.config

import io.github.u2894638479.bahalo.ui.SliderConfig
import io.github.u2894638479.kotlinmcui.context.DslContext
import io.github.u2894638479.kotlinmcui.functions.ui.Box
import io.github.u2894638479.kotlinmcui.identity.refId
import io.github.u2894638479.kotlinmcui.modifier.Modifier
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
class SubRingInfo {
    var cycle = 600L + Random.nextInt(0,100)

    var revolutionSpeed get() = if(cycle == 0L) 0.0 else 400.0/cycle
        set(value) { cycle = if(value == 0.0) 0L else (400.0/value).toLong() }

    val ringInfo = RingInfo()
}