package io.github.u2894638479.bahalo.ui

import io.github.u2894638479.kotlinmcui.context.DslContext
import io.github.u2894638479.kotlinmcui.context.scaled
import io.github.u2894638479.kotlinmcui.functions.property
import io.github.u2894638479.kotlinmcui.functions.remember
import io.github.u2894638479.kotlinmcui.functions.ui.ColorRect
import io.github.u2894638479.kotlinmcui.functions.ui.Column
import io.github.u2894638479.kotlinmcui.functions.ui.LateBox
import io.github.u2894638479.kotlinmcui.functions.ui.Row
import io.github.u2894638479.kotlinmcui.functions.ui.ScrollBar
import io.github.u2894638479.kotlinmcui.functions.ui.ScrollableRow
import io.github.u2894638479.kotlinmcui.functions.ui.Slider
import io.github.u2894638479.kotlinmcui.functions.ui.TextAutoFold
import io.github.u2894638479.kotlinmcui.identity.refId
import io.github.u2894638479.kotlinmcui.math.Axis
import io.github.u2894638479.kotlinmcui.math.Color
import io.github.u2894638479.kotlinmcui.math.Scroller
import io.github.u2894638479.kotlinmcui.math.rect.width
import io.github.u2894638479.kotlinmcui.modifier.Modifier
import io.github.u2894638479.kotlinmcui.modifier.height
import io.github.u2894638479.kotlinmcui.modifier.minHeight
import io.github.u2894638479.kotlinmcui.modifier.minWidth
import io.github.u2894638479.kotlinmcui.modifier.padding
import io.github.u2894638479.kotlinmcui.modifier.width
import io.github.u2894638479.kotlinmcui.prop.StableRWProperty
import io.github.u2894638479.kotlinmcui.prop.getValue
import io.github.u2894638479.kotlinmcui.prop.remap
import io.github.u2894638479.kotlinmcui.prop.setValue
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

context(ctx: DslContext)
fun ColorEdit(
    modifier: Modifier = Modifier,
    property: StableRWProperty<Color>,
    id: Any
) = LateBox(modifier.minHeight(50.scaled),id = id) {
    var color by property
    context(ctx: DslContext)
    fun SF(text: String,get:()-> Double,set:(Double)-> Unit) = Slider(
        Modifier.width(20.scaled).padding(1.scaled),Axis.Vertical,
        object:StableRWProperty<Double> {
            override fun getValue() = 1 - get()
            override fun setValue(value: Double) = set(1 - value)
        },id = text
    ) { TextAutoFold { text.emit() } }

    context(ctx: DslContext)
    fun items() {
        ColorRect(Modifier.minWidth(20.scaled).padding(1.scaled),color) {}
        SF("r",{ color.rDouble },{color = color.change(r = it)})
        SF("g",{ color.gDouble },{color = color.change(g = it)})
        SF("b",{ color.bDouble },{color = color.change(b = it)})
        SF("a",{ color.aDouble },{color = color.change(a = it)})
        SF("h",{ color.hDouble },{color = color.changeHSV(h = it)})
        SF("s",{ color.sDouble },{color = color.changeHSV(s = it)})
        SF("v",{ color.vDouble },{color = color.changeHSV(v = it)})
    }

    when {
        width >= (22 * 8).scaled -> Row { items() }
        width >= (22 * 4).scaled -> Column {
            val scrollerProp by Scroller.empty.remember.property
            ScrollableRow(Modifier,scrollerProp) { items() }
            ScrollBar(Modifier.height(10.scaled),scrollerProp,Axis.Horizontal) {}
        }
    }
}