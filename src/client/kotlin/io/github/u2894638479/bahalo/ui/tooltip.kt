package io.github.u2894638479.bahalo.ui

import io.github.u2894638479.kotlinmcui.context.DslContext
import io.github.u2894638479.kotlinmcui.context.scaled
import io.github.u2894638479.kotlinmcui.functions.ctxBackend
import io.github.u2894638479.kotlinmcui.functions.decorator.tooltip
import io.github.u2894638479.kotlinmcui.functions.translate
import io.github.u2894638479.kotlinmcui.functions.ui.ColorRect
import io.github.u2894638479.kotlinmcui.functions.ui.Column
import io.github.u2894638479.kotlinmcui.functions.ui.TextAutoFold
import io.github.u2894638479.kotlinmcui.functions.ui.TextFlatten
import io.github.u2894638479.kotlinmcui.functions.ui.tooltipBackground
import io.github.u2894638479.kotlinmcui.math.Color
import io.github.u2894638479.kotlinmcui.math.Measure
import io.github.u2894638479.kotlinmcui.modifier.Modifier
import io.github.u2894638479.kotlinmcui.modifier.height
import io.github.u2894638479.kotlinmcui.modifier.minWidth
import io.github.u2894638479.kotlinmcui.modifier.padding
import io.github.u2894638479.kotlinmcui.modifier.width
import io.github.u2894638479.kotlinmcui.scope.DslChild


context(ctx: DslContext)
fun DslChild.confTooltip(name: String) = ctxBackend.translate("bahalo.conf.$name.desc")
    ?.let { simpleTooltip(translate("bahalo.conf.$name"),it) } ?: this

context(ctx: DslContext)
fun DslChild.simpleTooltip(title: String,content: String) = tooltip {
    Column(Modifier.width(Measure.AUTO_MIN).padding(5.scaled)) {
        TextFlatten { title.emit(color = Color(50,50,180),size = 18.scaled) }
        ColorRect(Modifier.height(1.scaled).minWidth(150.scaled).padding(3.scaled),Color(70,70,240)) {}
        TextAutoFold { content.emit() }
    }.tooltipBackground()
}