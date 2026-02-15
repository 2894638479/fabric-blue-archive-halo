package io.github.u2894638479.config

import io.github.u2894638479.kotlinmcui.context.DslContext
import io.github.u2894638479.kotlinmcui.context.scaled
import io.github.u2894638479.kotlinmcui.functions.ui.ColorRect
import io.github.u2894638479.kotlinmcui.functions.ui.EditableText
import io.github.u2894638479.kotlinmcui.functions.ui.Row
import io.github.u2894638479.kotlinmcui.functions.ui.TextFlatten
import io.github.u2894638479.kotlinmcui.functions.ui.editBoxBackground
import io.github.u2894638479.kotlinmcui.math.Color
import io.github.u2894638479.kotlinmcui.math.Measure
import io.github.u2894638479.kotlinmcui.modifier.Modifier
import io.github.u2894638479.kotlinmcui.modifier.height
import io.github.u2894638479.kotlinmcui.modifier.padding
import io.github.u2894638479.kotlinmcui.modifier.size
import io.github.u2894638479.kotlinmcui.modifier.weight
import io.github.u2894638479.kotlinmcui.modifier.width
import io.github.u2894638479.kotlinmcui.prop.StableRWProperty
import io.github.u2894638479.kotlinmcui.prop.property
import io.github.u2894638479.kotlinmcui.prop.remap
import io.github.u2894638479.kotlinmcui.scope.DslChild
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface ColorSampler {
    class Segment(val height: Int,val color: Color)
    fun sample(segments: List<Segment>): Color

    context(ctx: DslContext)
    fun editor(modifier: Modifier): DslChild

    context(ctx: DslContext)
    fun description(modifier: Modifier): DslChild

    @Serializable
    @SerialName("sample")
    class Sample: ColorSampler {
        var height = 1
        override fun sample(segments: List<Segment>): Color {
            if(segments.isEmpty()) return Color.WHITE
            var sum = 0
            segments.forEach {
                sum += it.height
                if(sum > height) return it.color
            }
            return segments.last().color
        }

        context(ctx: DslContext)
        override fun editor(modifier: Modifier) = Row(modifier,id = this) {
            TextFlatten(Modifier.weight(0.0)) { "height:".emit() }
            EditableText(Modifier,::height.property.remap({it.toString()},{it.toIntOrNull() ?: height})) {}
        }.editBoxBackground()

        context(ctx: DslContext)
        override fun description(modifier: Modifier) = TextFlatten(modifier,id = this) {
            "sample".emit()
        }
    }

    @Serializable
    @SerialName("fixed")
    class Fixed: ColorSampler {
        var color = Color.WHITE
        override fun sample(segments: List<Segment>) = color

        context(ctx: DslContext)
        override fun editor(modifier: Modifier) = Row(modifier,id = this) {
            ColorRect(Modifier.size(20.scaled,20.scaled),color) {}
            Row(Modifier.height(20.scaled).padding(5.scaled)) {
                TextFlatten { "r:".emit(Color.RED) }
                EditableText(Modifier,object :StableRWProperty<String> {
                    override fun getValue() = color.r.toString()
                    override fun setValue(value: String) { value.toIntOrNull()?.let { color = color.change(r = it) } }
                }) {}
            }.editBoxBackground()
            Row(Modifier.height(20.scaled).padding(5.scaled)) {
                TextFlatten { "g:".emit(Color.GREEN) }
                EditableText(Modifier,object :StableRWProperty<String> {
                    override fun getValue() = color.g.toString()
                    override fun setValue(value: String) { value.toIntOrNull()?.let { color = color.change(g = it) } }
                }) {}
            }.editBoxBackground()
            Row(Modifier.height(20.scaled).padding(5.scaled)) {
                TextFlatten { "b:".emit(Color.BLUE) }
                EditableText(Modifier,object :StableRWProperty<String> {
                    override fun getValue() = color.b.toString()
                    override fun setValue(value: String) { value.toIntOrNull()?.let { color = color.change(b = it) } }
                }) {}
            }.editBoxBackground()
            Row(Modifier.height(20.scaled).padding(5.scaled)) {
                TextFlatten { "a:".emit() }
                EditableText(Modifier,object :StableRWProperty<String> {
                    override fun getValue() = color.a.toString()
                    override fun setValue(value: String) { value.toIntOrNull()?.let { color = color.change(a = it) } }
                }) {}
            }.editBoxBackground()
        }

        context(ctx: DslContext)
        override fun description(modifier: Modifier) = TextFlatten(modifier,id = this) {
            "fixed".emit()
        }
    }
}