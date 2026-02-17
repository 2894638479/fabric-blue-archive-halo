package io.github.u2894638479.bahalo.config

import io.github.u2894638479.kotlinmcui.context.DslContext
import io.github.u2894638479.kotlinmcui.functions.ui.SliderHorizontal
import io.github.u2894638479.kotlinmcui.functions.ui.TextFlatten
import io.github.u2894638479.kotlinmcui.identity.refId
import io.github.u2894638479.kotlinmcui.modifier.Modifier
import io.github.u2894638479.kotlinmcui.prop.property
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
class SubRingInfo {
    var cycle = 600L + Random.nextInt(0,100)

    var speed get() = if(cycle == 0L) 0.0 else 400.0/cycle
        set(value) { cycle = if(value == 0.0) 0L else (400.0/value).toLong() }

    val ringInfo = RingInfo()

    context(ctx: DslContext)
    fun speedEditor(modifier:Modifier) = SliderHorizontal(modifier,-5.0..5.0,::speed.property,id = refId) {
        TextFlatten { "revolution speed:${String.format("%.2f",speed)}".emit() }
    }
}