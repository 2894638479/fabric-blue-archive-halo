package io.github.u2894638479.bahalo.config

import io.github.u2894638479.bahalo.ui.ColorEdit
import io.github.u2894638479.bahalo.ui.SliderConfig
import io.github.u2894638479.bahalo.ui.simpleTooltip
import io.github.u2894638479.kotlinmcui.context.DslContext
import io.github.u2894638479.kotlinmcui.context.scaled
import io.github.u2894638479.kotlinmcui.functions.translate
import io.github.u2894638479.kotlinmcui.functions.ui.ColorRect
import io.github.u2894638479.kotlinmcui.functions.ui.Column
import io.github.u2894638479.kotlinmcui.functions.ui.EditableText
import io.github.u2894638479.kotlinmcui.functions.ui.Row
import io.github.u2894638479.kotlinmcui.functions.ui.TextFlatten
import io.github.u2894638479.kotlinmcui.functions.ui.editBoxBackground
import io.github.u2894638479.kotlinmcui.identity.refId
import io.github.u2894638479.kotlinmcui.math.Color
import io.github.u2894638479.kotlinmcui.modifier.Modifier
import io.github.u2894638479.kotlinmcui.modifier.height
import io.github.u2894638479.kotlinmcui.modifier.padding
import io.github.u2894638479.kotlinmcui.modifier.size
import io.github.u2894638479.kotlinmcui.modifier.weight
import io.github.u2894638479.kotlinmcui.prop.StableRWProperty
import io.github.u2894638479.kotlinmcui.prop.property
import io.github.u2894638479.kotlinmcui.prop.remap
import io.github.u2894638479.kotlinmcui.scope.DslChild
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface ColorSampler {
    val textKey: String
    @Serializable
    data class Segment(val height: Int,val color: Color)
    fun sample(segments: List<Segment>): Color

    context(ctx: DslContext)
    fun editor(modifier: Modifier): DslChild

    @Serializable
    @SerialName("sample")
    class Sample: ColorSampler {
        override val textKey get() = "bahalo.sampler.sample"
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
        override fun editor(modifier: Modifier) = Column(modifier,id = this){
            Row(Modifier.padding(2.scaled)) {
                TextFlatten(Modifier.weight(0.0)) { "${translate("bahalo.conf.height")}:".emit() }
                EditableText(Modifier, ::height.property.remap({ it.toString() }, { it.toIntOrNull() ?: height })) {}
            }.editBoxBackground(padding = 3.scaled)
            SliderConfig(0..50,::height)
        }.simpleTooltip(translate(textKey), translate("$textKey.desc"))
    }

    @Serializable
    @SerialName("fixed")
    class Fixed: ColorSampler {
        override val textKey get() = "bahalo.sampler.fixed"
        var color = Color.WHITE
        override fun sample(segments: List<Segment>) = color

        context(ctx: DslContext)
        override fun editor(modifier: Modifier) = ColorEdit(modifier,::color.property,refId)
    }
}