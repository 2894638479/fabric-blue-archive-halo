package io.github.u2894638479.config

import io.github.u2894638479.kotlinmcui.context.DslTopContext
import io.github.u2894638479.kotlinmcui.context.onClose
import io.github.u2894638479.kotlinmcui.context.scaled
import io.github.u2894638479.kotlinmcui.dslBackend
import io.github.u2894638479.kotlinmcui.functions.DslFunction
import io.github.u2894638479.kotlinmcui.functions.autoAnimate
import io.github.u2894638479.kotlinmcui.functions.ctxBackend
import io.github.u2894638479.kotlinmcui.functions.dataStore
import io.github.u2894638479.kotlinmcui.functions.decorator.animateHeight
import io.github.u2894638479.kotlinmcui.functions.decorator.background
import io.github.u2894638479.kotlinmcui.functions.decorator.clickable
import io.github.u2894638479.kotlinmcui.functions.decorator.highlightBox
import io.github.u2894638479.kotlinmcui.functions.decorator.onHovered
import io.github.u2894638479.kotlinmcui.functions.decorator.renderScissor
import io.github.u2894638479.kotlinmcui.functions.forEachWithId
import io.github.u2894638479.kotlinmcui.functions.remember
import io.github.u2894638479.kotlinmcui.functions.ui.Button
import io.github.u2894638479.kotlinmcui.functions.ui.Column
import io.github.u2894638479.kotlinmcui.functions.ui.Row
import io.github.u2894638479.kotlinmcui.functions.ui.ScrollableColumn
import io.github.u2894638479.kotlinmcui.functions.ui.Spacer
import io.github.u2894638479.kotlinmcui.functions.ui.TextAutoFold
import io.github.u2894638479.kotlinmcui.functions.ui.TextFlatten
import io.github.u2894638479.kotlinmcui.functions.ui.defaultBackground
import io.github.u2894638479.kotlinmcui.functions.withId
import io.github.u2894638479.kotlinmcui.math.Color
import io.github.u2894638479.kotlinmcui.math.align.Aligner
import io.github.u2894638479.kotlinmcui.math.px
import io.github.u2894638479.kotlinmcui.modifier.Modifier
import io.github.u2894638479.kotlinmcui.modifier.height
import io.github.u2894638479.kotlinmcui.modifier.padding
import io.github.u2894638479.kotlinmcui.modifier.weight
import io.github.u2894638479.kotlinmcui.prop.getValue
import io.github.u2894638479.kotlinmcui.prop.setValue
import net.minecraft.client.MinecraftClient

private fun pages(hasBonus: Boolean) = mapOf<String, DslFunction>(
    "beacon rings" to {
        var unfold by remember(-1)
        Config.instance.run {
            levels.forEach { (level,list) ->
                withId(level) {
                    Column(Modifier.padding(5.scaled)) {
                        TextFlatten(Modifier.padding(10.scaled)) {
                            "rings for level $level".emit(size = 18.scaled)
                        }
                        if(unfold == level) list.editor(
                            Modifier, ringRadiusRange(level),
                            ringNum(level),level
                        )
                        Spacer(Modifier.weight(Double.MAX_VALUE), Unit)
                    }.clickable {
                        unfold = if(unfold != level) level else -1
                    }.renderScissor().highlightBox().animateHeight()
                }
            }
        }
        TextAutoFold(Modifier.padding(10.scaled)) {
            "activate beacons to unlock settings for more levels.".emit()
        }
    },
    "player rings" to {

    },
    "special settings" to {
        Config.instance.special.editor(hasBonus = hasBonus)
    },
    "preview" to {
        Row {
            Spacer {}
            Button(Modifier.height(20.scaled).padding(5.scaled)) {
                TextFlatten { "stop/run game time".emit() }
            }.clickable { dataStore.pauseGame = !dataStore.pauseGame }
        }
    }
)

context(ctx: DslTopContext)
fun ConfigPage(hasBonus: Boolean) {
    val hudHidden by remember {
        MinecraftClient.getInstance().options.hudHidden
    }
    hudHidden
    MinecraftClient.getInstance().options.hudHidden = true
    onClose {
        Config.save()
        MinecraftClient.getInstance().options.hudHidden = hudHidden
        defaultOnClose()
    }
    val pages by remember { pages(hasBonus) }
    val previewPage = pages.entries.last()
    var selected by pages.entries.first().remember
    Row {
        ScrollableColumn(Modifier.weight(0.4)) {
            TextFlatten(Modifier.padding(5.scaled)) { "config page".emit() }
            pages.entries.forEachWithId {
                var hovered by remember(false)
                val size by autoAnimate(if(selected == it) 1.0 else if(hovered) 0.8 else 0.0)
                val padding by autoAnimate(if(hovered) 5.scaled else 0.px)
                Row(alignerHorizontal = Aligner.weightedExtend) {
                    Button(Modifier.height(30.scaled).padding(3.scaled).padding(v = padding)) {
                        TextFlatten { it.key.emit() }
                    }.clickable(selected != it && !(it == previewPage && !ctxBackend.isInWorld)) {
                        selected = it }.onHovered { hovered = it }
                    Spacer(Modifier.weight(0.5 - 0.5*size)) {}
                }
            }
        }
        ScrollableColumn { selected.value() }
    }.run {
        if(!dslBackend.isInWorld) defaultBackground()
        else if(selected != previewPage) background(Color(0,0,0,150))
        else this
    }
}