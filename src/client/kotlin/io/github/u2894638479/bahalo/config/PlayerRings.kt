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

@Serializable
@JvmInline
value class PlayerRings(
    val map: MutableMap<String, MutableList<RingInfo>> = mutableMapOf()
): Map<String, MutableList<RingInfo>> by map {

    fun ringNum(bonus: Boolean) = if(bonus) 3 else 2

    fun ringRadiusRange() = 0.1..1.0

    fun ringHeightRange() = 0.3..1.0

    fun ringWidthRange() = 0.01..0.2

    fun check(bonus: Boolean) = apply {
        values.forEach { list ->
            val max = ringNum(bonus)
            if(list.size > max) list.subList(max,list.size).clear()
        }
    }

    fun defaultRings(bonus: Boolean) = MutableList(ringNum(bonus)) { index ->
        val range = ringRadiusRange()
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
        var unfold by remember<String?>(null)
        forEach { (name,list) ->
            withId(name) {
                Column(Modifier.padding(5.scaled)) {
                    Row {
                        TextAutoFold(Modifier.padding(10.scaled)) {
                            "rings for player $name".emit(size = 18.scaled)
                        }
                        Button(Modifier.height(20.scaled).weight(0.0)) {
                            TextFlatten(Modifier.padding(2.scaled)) { "remove".emit() }
                        }.clickable { map.remove(name) }
                    }
                    if(unfold == name) Config.instance.run {
                        list.editor(Modifier,ringNum(hasBonus),{
                            RingInfo().apply {
                                height = 0.5
                                radius = 0.8
                                width = 0.05
                            } },false
                        ) {
                            it.editor(
                                Modifier.padding(5.scaled),
                                ringRadiusRange(),
                                ringHeightRange(),
                                ringWidthRange(),
                                true,2
                            )
                        }
                    }
                    Spacer(Modifier.weight(Double.MAX_VALUE), Unit)
                }.clickable {
                    unfold = if(unfold != name) name else null
                }.renderScissor().highlightBox().animateHeight()
            }
        }
    }
}