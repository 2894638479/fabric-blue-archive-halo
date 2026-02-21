package io.github.u2894638479.bahalo.config

import io.github.u2894638479.bahalo.ui.editor
import io.github.u2894638479.kotlinmcui.context.DslContext
import io.github.u2894638479.kotlinmcui.context.scaled
import io.github.u2894638479.kotlinmcui.functions.ui.Column
import io.github.u2894638479.kotlinmcui.identity.refId
import io.github.u2894638479.kotlinmcui.math.Color
import io.github.u2894638479.kotlinmcui.modifier.Modifier
import io.github.u2894638479.kotlinmcui.modifier.padding
import kotlinx.serialization.Serializable
import kotlin.math.max

@Serializable
@JvmInline
value class BeaconLevelRings(
    val map: MutableMap<Int, MutableList<RingInfo>> = mutableMapOf(),
) : Map<Int, MutableList<RingInfo>> by map {

    fun ringNum(level: Int,bonus: Boolean): Int {
        if(bonus) return max(0,level + 3)
        return max(0,level + 1)
    }

    fun ringConstraint(level: Int,bonus: Boolean) = object : RingInfo.Constraint {
        override val radiusRange: ClosedFloatingPointRange<Double>
            get() = 10.0..(ringNum(level,bonus) * 50 + 100.0)
        override val heightRange: ClosedFloatingPointRange<Double>
            get() = 0.0..radiusRange.endInclusive
        override val widthRange: ClosedFloatingPointRange<Double>
            get() = 0.5..5.0
        override val fixSampler: Boolean
            get() = false
        override val maxSubRingNum: Int
            get() = 2
    }

    fun check(bonus: Boolean) = apply {
        map.keys.removeIf { it <= 0 }
        entries.forEach { (level,list) ->
            val max = ringNum(level,bonus)
            if(list.size > max) list.subList(max,list.size).clear()
        }
    }

    fun defaultRings(level: Int,bonus: Boolean) = MutableList(ringNum(level,bonus)) { index ->
        val constraint = ringConstraint(level,bonus)
        val range = constraint.radiusRange
        val radius = range.start + index * (range.endInclusive - range.start) / ringNum(level,bonus)
        RingInfo().also {
            it.radius = radius
            it.height = constraint.heightRange.endInclusive*0.8
            it.sampler = ColorSampler.Sample().also { it.height = index + 1 }
        }
    }

    override fun get(key: Int) = map[key] ?: defaultRings(key,false).also{
        map[key] = it
        Config.save()
    }

    context(ctx: DslContext)
    fun editor(modifier: Modifier, bonus: Boolean) = Column(modifier,id = map.refId) {
        map.keys.editor(Modifier, { "Rings for level $it" },0,{error("")},Color.TRANSPARENT_WHITE) { level ->
            val list = map[level] ?: return@editor
            val color = Color(200,100,200,60)
            list.editor(Modifier, { "Beacon ring ${list.indexOf(it)}" },
                ringNum(level,bonus),{ defaultRings(level,bonus).last() },color) {
                it.editor(Modifier.padding(5.scaled), ringConstraint(level,bonus),color.changeHSV(h = color.hFloat + 1/6f))
            }
        }
    }
}