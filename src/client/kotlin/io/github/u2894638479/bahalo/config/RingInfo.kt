package io.github.u2894638479.bahalo.config

import io.github.u2894638479.bahalo.ui.BoolConfig
import io.github.u2894638479.bahalo.ui.SliderConfig
import io.github.u2894638479.kotlinmcui.context.DslContext
import io.github.u2894638479.kotlinmcui.context.scaled
import io.github.u2894638479.kotlinmcui.functions.decorator.animateHeight
import io.github.u2894638479.kotlinmcui.functions.decorator.clickable
import io.github.u2894638479.kotlinmcui.functions.translate
import io.github.u2894638479.kotlinmcui.functions.ui.*
import io.github.u2894638479.kotlinmcui.math.Measure
import io.github.u2894638479.kotlinmcui.modifier.*
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
    var sides = 3
    var autoSide = true

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
        Row {
            SliderConfig(radiusRange, ::radius)
            SliderConfig(widthRange, ::width)
        }
        Row {
            SliderConfig(-5.0..5.0, ::speed)
            SliderConfig(heightRange, ::height)
        }
        Row(Modifier.height(Measure.AUTO_MIN)) {
            BoolConfig(::autoSide)
            if(!autoSide) SliderConfig(3..100, ::sides)
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
                ringInfo.sampler = ColorSampler.Sample()
            } }) {
                it.speedEditor(Modifier.height(20.scaled).padding(h = 5.scaled))
                it.ringInfo.editor(Modifier.padding(5.scaled),subRingRadiusRange(),
                    subRingHeightRange(),subRingWidthRange(),fixSampler,maxSubRingNum - 1)
            }
        }
        Spacer(Modifier.weight(Double.MAX_VALUE)) {}
    }
}