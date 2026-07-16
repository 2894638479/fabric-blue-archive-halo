package io.github.u2894638479.bahalo.config

import io.github.u2894638479.bahalo.ui.myBackground
import io.github.u2894638479.kotlinmcui.context.DslContext
import io.github.u2894638479.kotlinmcui.context.scaled
import io.github.u2894638479.kotlinmcui.dsl.*
import io.github.u2894638479.kotlinmcui.dsl.decorator.clickable
import io.github.u2894638479.kotlinmcui.dsl.decorator.onHovered
import io.github.u2894638479.kotlinmcui.dsl.ui.*
import io.github.u2894638479.kotlinmcui.math.Color
import io.github.u2894638479.kotlinmcui.math.Scroller
import io.github.u2894638479.kotlinmcui.math.align.Aligner
import io.github.u2894638479.kotlinmcui.modifier.*
import io.github.u2894638479.kotlinmcui.prop.getValue
import io.github.u2894638479.kotlinmcui.prop.setValue
import io.github.u2894638479.kotlinmcui.text.DslCharStyle
import net.minecraft.client.Minecraft

private fun pages(hasBonus: Boolean) = mapOf<String, DslFunction>(
    "beaconRings" to {
        Config.instance.levels.editor(Modifier, hasBonus)
        TextAutoFold(Modifier.padding(10.scaled)) {
            translate("bahalo.ui.actbeacon").emit()
        }
    },
    "playerRings" to {
        Config.instance.players.editor(Modifier,hasBonus)
        TextAutoFold(Modifier.padding(10.scaled)) {
            translate("bahalo.ui.actplayer").emit()
        }
    },
    "special" to {
        Config.instance.special.editor(Modifier, hasBonus)
    },
    "preview" to {
        Row {
            Spacer {}
            Button(Modifier.height(20.scaled).padding(5.scaled)) {
                TextFlatten {
                    "${translate("bahalo.ui.gameTime")}: ".emit()
                    if(dataStore.pauseGame) translate("bahalo.ui.paused").emit(Color.RED, style = DslCharStyle().italic)
                    else translate("bahalo.ui.running").emit(Color.GREEN,style = DslCharStyle().italic)
                }
            }.clickable { dataStore.pauseGame = !dataStore.pauseGame }
        }
    }
)

context(ctx: DslContext)
fun ConfigPage(hasBonus: Boolean) {
    val hudHidden by local {
        Minecraft.getInstance().gui.hud.isHidden.also {
            Minecraft.getInstance().gui.hud.isHidden = true
        }
    }
    local.dispose {
        Config.save()
        Minecraft.getInstance().gui.hud.isHidden = hudHidden
    }
    val pages by local { pages(hasBonus).mapKeys { translate("bahalo.page.${it.key}") } }
    val previewPage = pages.entries.last()
    var selected by local { pages.entries.first() }
    Row {
        ScrollableColumn(Modifier.weight(0.4)) {
            TextFlatten(Modifier.padding(5.scaled)) { translate("bahalo.configPage").emit() }
            pages.entries.forEachWithId {
                var hovered by local { false }
                val size by local.autoAnimate { if(selected == it) 1.0 else if(hovered) 0.8 else 0.0 }
                val padding by local.autoAnimate { if(hovered) 5.0 else 0.0 }
                Row(alignerHorizontal = Aligner.weightedExtend) {
                    Button(Modifier.height(30.scaled).padding(3.scaled).padding(v = padding.scaled)) {
                        TextFlatten { it.key.emit() }
                    }.clickable(selected != it && !(it == previewPage && !backend.isInWorld)) {
                        selected = it }.onHovered { hovered = it }
                    Spacer(Modifier.weight(0.5 - 0.5*size)) {}
                }
            }
        }
        val scroller = local { Scroller.empty }
        ScrollableColumn(Modifier,scroller,id = selected) { selected.value() }
        ScrollBarVertical(Modifier.width(10.scaled),scroller,id = selected)
    }.run {
        if(selected == previewPage) this else myBackground()
    }
}