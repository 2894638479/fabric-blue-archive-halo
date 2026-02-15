package io.github.u2894638479.config

import io.github.u2894638479.kotlinmcui.context.DslTopContext
import io.github.u2894638479.kotlinmcui.context.onClose
import io.github.u2894638479.kotlinmcui.context.scaled
import io.github.u2894638479.kotlinmcui.dslBackend
import io.github.u2894638479.kotlinmcui.functions.*
import io.github.u2894638479.kotlinmcui.functions.decorator.background
import io.github.u2894638479.kotlinmcui.functions.decorator.clickable
import io.github.u2894638479.kotlinmcui.functions.decorator.onHovered
import io.github.u2894638479.kotlinmcui.functions.ui.*
import io.github.u2894638479.kotlinmcui.math.Color
import io.github.u2894638479.kotlinmcui.math.Scroller
import io.github.u2894638479.kotlinmcui.math.align.Aligner
import io.github.u2894638479.kotlinmcui.math.px
import io.github.u2894638479.kotlinmcui.modifier.Modifier
import io.github.u2894638479.kotlinmcui.modifier.height
import io.github.u2894638479.kotlinmcui.modifier.padding
import io.github.u2894638479.kotlinmcui.modifier.weight
import io.github.u2894638479.kotlinmcui.modifier.width
import io.github.u2894638479.kotlinmcui.prop.getValue
import io.github.u2894638479.kotlinmcui.prop.setValue
import io.github.u2894638479.kotlinmcui.text.DslCharStyle
import net.minecraft.client.MinecraftClient

private fun pages(hasBonus: Boolean) = mapOf<String, DslFunction>(
    "beacon rings" to {
        Config.instance.levels.editor(Modifier, hasBonus)
        TextAutoFold(Modifier.padding(10.scaled)) {
            "activate beacons to unlock settings for more levels.".emit()
        }
    },
    "player rings" to {
        Config.instance.players.editor(Modifier,hasBonus)
    },
    "special settings" to {
        Config.instance.special.editor(Modifier, hasBonus)
    },
    "preview" to {
        Row {
            Spacer {}
            Button(Modifier.height(20.scaled).padding(5.scaled)) {
                TextFlatten {
                    "game time: ".emit()
                    if(dataStore.pauseGame) "stopped".emit(Color.RED, style = DslCharStyle().italic)
                    else "running".emit(Color.GREEN,style = DslCharStyle().italic)
                }
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
        val scroller by Scroller.empty.remember.property
        ScrollableColumn(Modifier,scroller,id = selected) { selected.value() }
        ScrollBarVertical(Modifier.width(10.scaled),scroller,id = selected)
    }.run {
        if(!dslBackend.isInWorld) defaultBackground()
        else if(selected != previewPage) background(Color(0,0,0,150))
        else this
    }
}