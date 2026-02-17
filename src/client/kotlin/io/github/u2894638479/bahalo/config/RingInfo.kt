package io.github.u2894638479.bahalo.config

import io.github.u2894638479.kotlinmcui.context.DslContext
import io.github.u2894638479.kotlinmcui.context.scaled
import io.github.u2894638479.kotlinmcui.functions.decorator.animateHeight
import io.github.u2894638479.kotlinmcui.functions.decorator.clickable
import io.github.u2894638479.kotlinmcui.functions.translate
import io.github.u2894638479.kotlinmcui.functions.ui.Button
import io.github.u2894638479.kotlinmcui.functions.ui.Column
import io.github.u2894638479.kotlinmcui.functions.ui.Row
import io.github.u2894638479.kotlinmcui.functions.ui.SliderHorizontal
import io.github.u2894638479.kotlinmcui.functions.ui.Spacer
import io.github.u2894638479.kotlinmcui.functions.ui.TextFlatten
import io.github.u2894638479.kotlinmcui.math.Measure
import io.github.u2894638479.kotlinmcui.modifier.Modifier
import io.github.u2894638479.kotlinmcui.modifier.height
import io.github.u2894638479.kotlinmcui.modifier.padding
import io.github.u2894638479.kotlinmcui.modifier.weight
import io.github.u2894638479.kotlinmcui.modifier.width
import io.github.u2894638479.kotlinmcui.prop.property
import io.github.u2894638479.kotlinmcui.scope.DslChild
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
class RingInfo {
    var radius = 100.0
    var cycle = 300L + Random.nextInt(0,100)
    var width = 2.0
    var style: RingStyle = RingStyle.Radar()
    var height = 0.0
    var sampler: ColorSampler = ColorSampler.Fixed()

    var speed get() = if(cycle == 0L) 0.0 else 400.0/cycle
        set(value) { cycle = if(value == 0.0) 0L else (400.0/value).toLong() }

    val subRings = mutableListOf<SubRingInfo>()

    fun subRingRadiusRange() = radius/100..radius/4
    fun subRingWidthRange() = 0.0..width
    fun subRingHeightRange() = -radius/8..radius/8

    context(ctx: DslContext)
    fun editor(
        modifier: Modifier = Modifier.Companion,
        radiusRange: ClosedFloatingPointRange<Double>,
        heightRange: ClosedFloatingPointRange<Double>,
        widthRange: ClosedFloatingPointRange<Double>,
        fixSampler: Boolean,
        maxSubRingNum: Int
    ): DslChild = Column(modifier, id = this) {
        Row(Modifier.height(Measure.AUTO_MIN)) {
            SliderHorizontal(Modifier.height(20.scaled).padding(1.scaled),
                radiusRange, ::radius.property) {
                TextFlatten { "radius:${String.format("%.2f", radius)}".emit() }
            }
            SliderHorizontal(Modifier.height(20.scaled).padding(1.scaled), widthRange, ::width.property) {
                TextFlatten { "width:${String.format("%.2f", width)}".emit() }
            }
        }
        Row(Modifier.height(Measure.AUTO_MIN)) {
            SliderHorizontal(Modifier.height(20.scaled).padding(1.scaled),-5.0..5.0, ::speed.property) {
                TextFlatten { "speed:${String.format("%.2f", speed)}".emit() }
            }
            SliderHorizontal(Modifier.height(20.scaled).padding(1.scaled),heightRange, ::height.property) {
                TextFlatten { "height:${String.format("%.2f", height)}".emit() }
            }
        }
        Button {
            Column {
                TextFlatten(Modifier.padding(5.scaled)) { "style: ${translate(style.textKey)}".emit() }
                style.editor(Modifier.padding(5.scaled))
            }.animateHeight()
        }.clickable { style = style.next }

        Button {
            Column(Modifier.padding(5.scaled)) {
                Row(Modifier.width(Measure.AUTO_MIN)) {
                    TextFlatten { "sampler:".emit() }
                    sampler.description(Modifier)
                }
                sampler.editor(Modifier)
            }
        }.clickable(!fixSampler) {
            sampler = when(sampler) {
                is ColorSampler.Sample -> ColorSampler.Fixed()
                is ColorSampler.Fixed -> ColorSampler.Sample()
            }
        }


        if(maxSubRingNum > 0 || subRings.isNotEmpty()) {
            TextFlatten(Modifier.padding(5.scaled)) { "subRings".emit() }
            subRings.editor(Modifier,maxSubRingNum,{ SubRingInfo().apply {
                ringInfo.radius = subRingRadiusRange().run { start + endInclusive } / 2
                ringInfo.height = 0.0
                ringInfo.width = subRingWidthRange().endInclusive
            } }) {
                it.speedEditor(Modifier.height(20.scaled).padding(h = 5.scaled))
                it.ringInfo.editor(Modifier.padding(5.scaled),subRingRadiusRange(),
                    subRingHeightRange(),subRingWidthRange(),fixSampler,maxSubRingNum - 1)
            }
        }
        Spacer(Modifier.weight(Double.MAX_VALUE)) {}
    }
}