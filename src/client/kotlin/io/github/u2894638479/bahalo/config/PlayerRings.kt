package io.github.u2894638479.bahalo.config

import io.github.u2894638479.bahalo.ui.editor
import io.github.u2894638479.kotlinmcui.context.DslContext
import io.github.u2894638479.kotlinmcui.context.scaled
import io.github.u2894638479.kotlinmcui.functions.decorator.animateHeight
import io.github.u2894638479.kotlinmcui.functions.decorator.clickable
import io.github.u2894638479.kotlinmcui.functions.decorator.highlightBox
import io.github.u2894638479.kotlinmcui.functions.decorator.renderScissor
import io.github.u2894638479.kotlinmcui.functions.remember
import io.github.u2894638479.kotlinmcui.functions.translate
import io.github.u2894638479.kotlinmcui.functions.ui.Button
import io.github.u2894638479.kotlinmcui.functions.ui.Column
import io.github.u2894638479.kotlinmcui.functions.ui.Row
import io.github.u2894638479.kotlinmcui.functions.ui.Spacer
import io.github.u2894638479.kotlinmcui.functions.ui.TextAutoFold
import io.github.u2894638479.kotlinmcui.functions.ui.TextFlatten
import io.github.u2894638479.kotlinmcui.functions.withId
import io.github.u2894638479.kotlinmcui.identity.refId
import io.github.u2894638479.kotlinmcui.math.Color
import io.github.u2894638479.kotlinmcui.modifier.Modifier
import io.github.u2894638479.kotlinmcui.modifier.height
import io.github.u2894638479.kotlinmcui.modifier.padding
import io.github.u2894638479.kotlinmcui.modifier.weight
import io.github.u2894638479.kotlinmcui.prop.getValue
import io.github.u2894638479.kotlinmcui.prop.setValue
import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class PlayerRings(
    val map: MutableMap<String, MutableList<RingInfo>> = mutableMapOf()
): Map<String, MutableList<RingInfo>> by map {

    fun ringNum(bonus: Boolean) = if(bonus) 3 else 2

    fun ringConstraint() = object : RingInfo.Constraint {
        override val radiusRange get() = 0.1..1.0
        override val heightRange get() = 0.3..1.0
        override val widthRange get() = 0.01..0.2
        override val fixSampler get() = true
        override val maxSubRingNum get() = 2
    }

    fun check(bonus: Boolean) = apply {
        values.forEach { list ->
            val max = ringNum(bonus)
            if(list.size > max) list.subList(max,list.size).clear()
        }
    }

    fun defaultRings(bonus: Boolean) = MutableList(ringNum(bonus)) { index ->
        val range = ringConstraint().radiusRange
        val radius = range.start + index * (range.endInclusive - range.start) / ringNum(bonus)
        RingInfo().also {
            it.radius = radius
            it.height = 0.68 + 0.02*index
            it.width = 0.05
        }
    }

    override fun get(key: String) = map[key] ?: defaultRings(false).also{
        map[key] = it
        Config.save()
    }

    context(ctx: DslContext)
    fun editor(modifier: Modifier,hasBonus: Boolean) = Column(modifier,id = map.refId) {
        map.keys.editor(Modifier,{ translate("bahalo.ui.ringsForPlayer",it) },0,{error("")},Color.TRANSPARENT_WHITE) { name ->
            val list = map[name] ?: return@editor
            val color = Color(200,100,200,60)
            list.editor(Modifier, { translate("bahalo.ui.playerRing",list.indexOf(it)) },
                ringNum(hasBonus),{ defaultRings(hasBonus).last() },color
            ) { it.editor(Modifier.padding(5.scaled), ringConstraint(),color.changeHSV(h = color.hFloat + 1/6f)) }
        }
    }
}