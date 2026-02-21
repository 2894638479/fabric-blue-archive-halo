package io.github.u2894638479.bahalo.config

import io.github.u2894638479.bahalo.ui.BoolConfig
import io.github.u2894638479.bahalo.ui.SliderConfig
import io.github.u2894638479.bahalo.ui.editor
import io.github.u2894638479.kotlinmcui.context.DslContext
import io.github.u2894638479.kotlinmcui.context.scaled
import io.github.u2894638479.kotlinmcui.functions.decorator.animateHeight
import io.github.u2894638479.kotlinmcui.functions.decorator.clickable
import io.github.u2894638479.kotlinmcui.functions.decorator.renderScissor
import io.github.u2894638479.kotlinmcui.functions.translate
import io.github.u2894638479.kotlinmcui.functions.ui.*
import io.github.u2894638479.kotlinmcui.math.Color
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

    interface Constraint {
        val radiusRange: ClosedFloatingPointRange<Double>
        val heightRange: ClosedFloatingPointRange<Double>
        val widthRange: ClosedFloatingPointRange<Double>
        val fixSampler: Boolean
        val maxSubRingNum: Int
    }

    fun subRingConstraint(constraint: Constraint) = object: Constraint by constraint {
        override val radiusRange get() = radius/100..radius/4
        override val widthRange get() = 0.0..width
        override val heightRange get() = -radius/8..radius/8
        override val maxSubRingNum get() = constraint.maxSubRingNum - 1
    }

    context(ctx: DslContext)
    fun editor(
        modifier: Modifier = Modifier.Companion,
        constraint: Constraint,
        color: Color
    ): DslChild = Column(modifier, id = this) {
        Row {
            SliderConfig(constraint.radiusRange, ::radius)
            SliderConfig(constraint.widthRange, ::width)
        }
        Row {
            SliderConfig(-5.0..5.0, ::speed)
            SliderConfig(constraint.heightRange, ::height)
        }
        Row {
            BoolConfig(::autoSide)
            if(!autoSide) SliderConfig(3..100, ::sides)
        }
        Button(Modifier.padding(2.scaled)) {
            Column {
                TextFlatten(Modifier.padding(5.scaled)) { "style: ${translate(style.textKey)}".emit() }
                style.editor(Modifier.padding(5.scaled))
            }.animateHeight().renderScissor()
        }.clickable { style = style.next }

        Button(Modifier.padding(2.scaled)) {
            Column(Modifier.padding(5.scaled)) {
                Row(Modifier.width(Measure.AUTO_MIN)) {
                    TextFlatten { "sampler:".emit() }
                    sampler.description(Modifier)
                }
                sampler.editor(Modifier)
            }.animateHeight().renderScissor()
        }.clickable(!constraint.fixSampler) {
            sampler = when(sampler) {
                is ColorSampler.Sample -> ColorSampler.Fixed()
                is ColorSampler.Fixed -> ColorSampler.Sample()
            }
        }

        if(constraint.maxSubRingNum > 0 || subRings.isNotEmpty()) {
            TextFlatten(Modifier.padding(5.scaled)) { "subRings".emit() }
            subRings.editor(Modifier, { "Sub Ring ${subRings.indexOf(it)}" },
                constraint.maxSubRingNum,{ SubRingInfo().apply {
                val constraint = subRingConstraint(constraint)
                ringInfo.radius = constraint.radiusRange.run { start + endInclusive } / 2
                ringInfo.height = 0.0
                ringInfo.width = constraint.widthRange.run { start + endInclusive } / 2
                ringInfo.sampler = ColorSampler.Sample()
            } },color) {
                SliderConfig(-5.0..5.0, it::revolutionSpeed)
                it.ringInfo.editor(Modifier.padding(5.scaled),subRingConstraint(constraint),color.changeHSV(h = color.hFloat + 1/6f))
            }
        }
        Spacer(Modifier.weight(Double.MAX_VALUE)) {}
    }
}