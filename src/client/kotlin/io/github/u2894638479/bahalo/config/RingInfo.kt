package io.github.u2894638479.bahalo.config

import io.github.u2894638479.bahalo.ui.BoolConfig
import io.github.u2894638479.bahalo.ui.MyBackground
import io.github.u2894638479.bahalo.ui.SliderConfig
import io.github.u2894638479.bahalo.ui.editor
import io.github.u2894638479.bahalo.ui.simpleTooltip
import io.github.u2894638479.kotlinmcui.context.DslContext
import io.github.u2894638479.kotlinmcui.context.scaled
import io.github.u2894638479.kotlinmcui.functions.dataStore
import io.github.u2894638479.kotlinmcui.functions.decorator.animateHeight
import io.github.u2894638479.kotlinmcui.functions.decorator.clickable
import io.github.u2894638479.kotlinmcui.functions.decorator.renderScissor
import io.github.u2894638479.kotlinmcui.functions.showScreen
import io.github.u2894638479.kotlinmcui.functions.translate
import io.github.u2894638479.kotlinmcui.functions.ui.*
import io.github.u2894638479.kotlinmcui.math.Color
import io.github.u2894638479.kotlinmcui.math.Measure
import io.github.u2894638479.kotlinmcui.modifier.*
import io.github.u2894638479.kotlinmcui.scope.DslChild
import io.github.u2894638479.kotlinmcui.utils.Simple
import kotlinx.serialization.Serializable
import kotlin.collections.all
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
        operator fun contains(ring: RingInfo):Boolean = ring.height in heightRange
                && ring.radius in radiusRange
                && ring.width in widthRange
                && ring.subRings.size <= maxSubRingNum
                && (ring.sampler is ColorSampler.Fixed || !fixSampler)
                && ring.subRings.all { it.ringInfo in ring.subRingConstraint(this) }
    }

    fun subRingConstraint(constraint: Constraint) = object: Constraint by constraint {
        override val radiusRange get() = radius/100..radius/4
        override val widthRange get() = 0.0..width
        override val heightRange get() = -radius/8..radius/8
        override val maxSubRingNum get() = constraint.maxSubRingNum - 1
    }
    fun coerceIn(constraint: Constraint) {
        radius = radius.coerceIn(constraint.radiusRange)
        width = width.coerceIn(constraint.widthRange)
        height = height.coerceIn(constraint.heightRange)
        if(constraint.fixSampler && sampler !is ColorSampler.Fixed) sampler = ColorSampler.Fixed()
        while(subRings.size > constraint.maxSubRingNum) subRings.removeLast()
        subRings.forEach { it.ringInfo.coerceIn(subRingConstraint(constraint)) }
    }
    context(ctx: DslContext)
    fun changer(modifier: Modifier = Modifier, constraint: Constraint, onChange: (RingInfo) -> Unit) = Row(modifier,id = this) {
        Simple.Button("code") {
            showScreen {
                ConfigTextConvertPage(this) {
                    it ?: return@ConfigTextConvertPage this
                    if(it in constraint) {
                        onChange(it)
                        it
                    } else {
                        dataStore.onClose()
                        showScreen {
                            Column(Modifier.size(Measure.AUTO_MIN, Measure.AUTO_MIN)) {
                                TextFlatten { "pasted content not in constraint".emit() }
                                Spacer(Modifier.height(40.scaled)) {}
                                Row {
                                    Simple.Button("auto adapt") {
                                        dataStore.onClose()
                                        onChange(it.apply { coerceIn(constraint) })
                                    }
                                    Simple.Button("cancel") {}
                                }
                            }
                            MyBackground()
                        }
                        this
                    }
                }
                MyBackground()
            }
        }
    }

    context(ctx: DslContext)
    fun editor(
        modifier: Modifier = Modifier,
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
                TextFlatten(Modifier.padding(5.scaled)) { "${translate("bahalo.style")}: ${translate(style.textKey)}".emit() }
                style.editor(Modifier.padding(5.scaled))
            }.animateHeight().renderScissor()
        }.clickable { style = style.next }

        Button(Modifier.padding(2.scaled)) {
            Column(Modifier.padding(5.scaled)) {
                Column(Modifier.height(Measure.AUTO_MIN)) {
                    Row(Modifier.width(Measure.AUTO_MIN)) {
                        TextFlatten { "${translate("bahalo.sampler")}:${translate(sampler.textKey)}".emit() }
                    }
                    sampler.editor(Modifier)
                }
            }.animateHeight().renderScissor()
        }.clickable(!constraint.fixSampler) {
            sampler = when(sampler) {
                is ColorSampler.Sample -> ColorSampler.Fixed()
                is ColorSampler.Fixed -> ColorSampler.Sample()
            }
        }

        if(constraint.maxSubRingNum > 0 || subRings.isNotEmpty()) {
            TextFlatten(Modifier.padding(5.scaled)) { translate("bahalo.ui.subrings").emit() }
            subRings.editor(Modifier, { translate("bahalo.ui.subring",subRings.indexOf(it)) },
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