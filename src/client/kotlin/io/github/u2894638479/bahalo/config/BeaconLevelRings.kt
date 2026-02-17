package io.github.u2894638479.bahalo.config

import io.github.u2894638479.kotlinmcui.context.DslContext
import io.github.u2894638479.kotlinmcui.context.scaled
import io.github.u2894638479.kotlinmcui.functions.decorator.animateHeight
import io.github.u2894638479.kotlinmcui.functions.decorator.clickable
import io.github.u2894638479.kotlinmcui.functions.decorator.highlightBox
import io.github.u2894638479.kotlinmcui.functions.decorator.renderScissor
import io.github.u2894638479.kotlinmcui.functions.remember
import io.github.u2894638479.kotlinmcui.functions.ui.Button
import io.github.u2894638479.kotlinmcui.functions.ui.Column
import io.github.u2894638479.kotlinmcui.functions.ui.Row
import io.github.u2894638479.kotlinmcui.functions.ui.Spacer
import io.github.u2894638479.kotlinmcui.functions.ui.TextAutoFold
import io.github.u2894638479.kotlinmcui.functions.ui.TextFlatten
import io.github.u2894638479.kotlinmcui.functions.withId
import io.github.u2894638479.kotlinmcui.identity.refId
import io.github.u2894638479.kotlinmcui.modifier.Modifier
import io.github.u2894638479.kotlinmcui.modifier.height
import io.github.u2894638479.kotlinmcui.modifier.padding
import io.github.u2894638479.kotlinmcui.modifier.weight
import io.github.u2894638479.kotlinmcui.prop.getValue
import io.github.u2894638479.kotlinmcui.prop.setValue
import kotlinx.serialization.Serializable
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set
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

    fun ringRadiusRange(level: Int,bonus: Boolean) = 10.0..(ringNum(level,bonus) * 50 + 100.0)

    fun ringHeightRange(level: Int,bonus: Boolean) = 0.0..ringRadiusRange(level,bonus).endInclusive

    fun ringWidthRange(level: Int,bonus: Boolean) = 1.0..5.0

    fun check(bonus: Boolean) = apply {
        map.keys.removeIf { it <= 0 }
        entries.forEach { (level,list) ->
            val max = ringNum(level,bonus)
            if(list.size > max) list.subList(max,list.size).clear()
        }
    }

    fun defaultRings(level: Int,bonus: Boolean) = MutableList(ringNum(level,bonus)) { index ->
        val range = ringRadiusRange(level,bonus)
        val radius = range.start + index * (range.endInclusive - range.start) / ringNum(level,bonus)
        RingInfo().also {
            it.radius = radius
            it.height = ringHeightRange(level,bonus).endInclusive*0.8
            it.sampler = ColorSampler.Sample().also { it.height = index + 1 }
        }
    }

    override fun get(key: Int) = map[key] ?: defaultRings(key,false).also{
        map[key] = it
        Config.save()
    }

    context(ctx: DslContext)
    fun editor(modifier: Modifier, bonus: Boolean) = Column(modifier,id = map.refId) {
        var unfold by remember(-1)
        forEach { (level,list) ->
            withId(level) {
                Column(Modifier.padding(5.scaled)) {
                    Row {
                        TextAutoFold(Modifier.padding(10.scaled)) {
                            "rings for level $level".emit(size = 18.scaled)
                        }
                        Button(Modifier.height(20.scaled).weight(0.0)) {
                            TextFlatten(Modifier.padding(2.scaled)) { "remove".emit() }
                        }.clickable { map.remove(level) }
                    }
                    if(unfold == level) Config.instance.run {
                        list.editor(Modifier,ringNum(level,bonus),{ RingInfo() }) {
                            it.editor(
                                Modifier.padding(5.scaled),
                                ringRadiusRange(level,bonus),
                                ringHeightRange(level,bonus),
                                ringWidthRange(level,bonus),
                                false,2
                            )
                        }
                    }
                    Spacer(Modifier.weight(Double.MAX_VALUE), Unit)
                }.clickable {
                    unfold = if(unfold != level) level else -1
                }.renderScissor().highlightBox().animateHeight()
            }
        }
    }
}