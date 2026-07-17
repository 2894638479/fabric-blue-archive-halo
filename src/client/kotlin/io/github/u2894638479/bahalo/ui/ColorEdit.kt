package io.github.u2894638479.bahalo.ui

import io.github.u2894638479.kotlinmcui.context.DslContext
import io.github.u2894638479.kotlinmcui.context.scaled
import io.github.u2894638479.kotlinmcui.dsl.decorator.clickable
import io.github.u2894638479.kotlinmcui.dsl.local
import io.github.u2894638479.kotlinmcui.dsl.ui.*
import io.github.u2894638479.kotlinmcui.math.Axis
import io.github.u2894638479.kotlinmcui.math.Color
import io.github.u2894638479.kotlinmcui.math.Scroller
import io.github.u2894638479.kotlinmcui.modifier.*
import io.github.u2894638479.kotlinmcui.prop.StableRW
import io.github.u2894638479.kotlinmcui.prop.getValue
import io.github.u2894638479.kotlinmcui.prop.setValue
import io.github.u2894638479.kotlinmcui.prop.value
import io.github.u2894638479.kotlinmcui.utils.Config

private enum class Mode {
    RGBA_TEXT,
    RGBA_SLIDER,
    RGBAHSV_SLIDER,
    RGBAHSV_SLIDER_MAX
}

context(ctx: DslContext)
fun ColorEdit(
    modifier: Modifier = Modifier,
    property: StableRW<Color>,
    id: Any
) = Column(modifier,id = id) {
    var color by property
    val mode = local { Mode.RGBAHSV_SLIDER }
    context(ctx: DslContext)
    fun SFH(text: String, value: String, get:()-> Double, set:(Double)-> Unit) = Slider(
        Modifier.height(20.scaled).padding(1.scaled),Axis.Horizontal,
        object: StableRW<Double> {
            override fun getValue() = 1 - get()
            override fun setValue(value: Double) = set(1 - value)
        },id = text
    ) { TextAutoFold { "$text: $value".emit() } }
    context(ctx: DslContext)
    fun SF(text: String,get:()-> Double,set:(Double)-> Unit) = Slider(
        Modifier.width(20.scaled).padding(1.scaled),Axis.Vertical,
        object: StableRW<Double> {
            override fun getValue() = 1 - get()
            override fun setValue(value: Double) = set(1 - value)
        },id = text
    ) { TextAutoFold { text.emit() } }

    context(ctx: DslContext)
    fun T(text: String,get:() -> UByte,set:(UByte) -> Unit) = Row(Modifier.padding(3.scaled)) {
        TextFlatten { "$text:".emit() }
        EditableText(Modifier.padding(2.scaled),object : StableRW<String> {
            override fun setValue(value: String) {
                val ub = value.toUByteOrNull() ?: return
                set(ub)
            }
            override fun getValue() = get().toString()
        },id = text)
    }.editBoxBackground().clickable {  }
    Row {
        Spacer(Modifier.weight(Double.MAX_VALUE)) {}
        Config.EnumButton(mode,"mode") {}
    }
    when(mode.value) {
        Mode.RGBAHSV_SLIDER -> {
            val scrollerProp = local { Scroller.empty }
            ScrollableRow(Modifier,scrollerProp) {
                ColorRect(Modifier.minWidth(20.scaled).minHeight(40.scaled).padding(1.scaled),color) {}
                SF("r",{ color.rDouble },{color = color.change(r = it)})
                SF("g",{ color.gDouble },{color = color.change(g = it)})
                SF("b",{ color.bDouble },{color = color.change(b = it)})
                SF("a",{ color.aDouble },{color = color.change(a = it)})
                SF("h",{ color.hDouble },{color = color.changeHSV(h = it)})
                SF("s",{ color.sDouble },{color = color.changeHSV(s = it)})
                SF("v",{ color.vDouble },{color = color.changeHSV(v = it)})
            }
            if(scrollerProp.value.isScrollable())
                ScrollBar(Modifier.height(10.scaled),scrollerProp,Axis.Horizontal) {}
        }
        Mode.RGBA_SLIDER -> {
            ColorRect(Modifier.minHeight(20.scaled).padding(1.scaled),color) {}
            SFH("r",color.r.toString(),{ color.rDouble },{color = color.change(r = it)})
            SFH("g",color.g.toString(),{ color.gDouble },{color = color.change(g = it)})
            SFH("b",color.b.toString(),{ color.bDouble },{color = color.change(b = it)})
            SFH("a",color.a.toString(),{ color.aDouble },{color = color.change(a = it)})
        }
        Mode.RGBA_TEXT -> {
            ColorRect(Modifier.height(20.scaled).padding(1.scaled),color) {}
            Row {
                T("r",{ color.r },{color = color.change(r = it)})
                T("g",{ color.g },{color = color.change(g = it)})
                T("b",{ color.b },{color = color.change(b = it)})
                T("a",{ color.a },{color = color.change(a = it)})
            }
        }
        Mode.RGBAHSV_SLIDER_MAX -> {
            ColorRect(Modifier.height(20.scaled).padding(1.scaled),color) {}
            SFH("r",color.r.toString(),{ color.rDouble },{color = color.change(r = it)})
            SFH("g",color.g.toString(),{ color.gDouble },{color = color.change(g = it)})
            SFH("b",color.b.toString(),{ color.bDouble },{color = color.change(b = it)})
            SFH("a",color.a.toString(),{ color.aDouble },{color = color.change(a = it)})
            SFH("h",String.format("%.3f",color.h),{ color.hDouble },{color = color.changeHSV(h = it)})
            SFH("s",String.format("%.3f",color.s),{ color.sDouble },{color = color.changeHSV(s = it)})
            SFH("v",String.format("%.3f",color.v),{ color.vDouble },{color = color.changeHSV(v = it)})
        }
    }
}