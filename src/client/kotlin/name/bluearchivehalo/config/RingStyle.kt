package name.bluearchivehalo.config

import io.github.u2894638479.kotlinmcui.context.DslContext
import io.github.u2894638479.kotlinmcui.context.scaled
import io.github.u2894638479.kotlinmcui.functions.ui.Column
import io.github.u2894638479.kotlinmcui.functions.ui.Row
import io.github.u2894638479.kotlinmcui.functions.ui.SliderHorizontal
import io.github.u2894638479.kotlinmcui.functions.ui.TextFlatten
import io.github.u2894638479.kotlinmcui.math.Color
import io.github.u2894638479.kotlinmcui.math.Measure
import io.github.u2894638479.kotlinmcui.modifier.Modifier
import io.github.u2894638479.kotlinmcui.modifier.height
import io.github.u2894638479.kotlinmcui.modifier.padding
import io.github.u2894638479.kotlinmcui.prop.property
import io.github.u2894638479.kotlinmcui.scope.DslChild
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.max

@Serializable
sealed interface RingStyle {
    val textKey: String
    val descriptionKey: String

    val next get() = when(this) {
        is Radar -> Pulse()
        is Pulse -> Radar()
    }

    context(ctx: DslContext)
    fun editor(modifier: Modifier = Modifier.Companion): DslChild

    // direction: 0 to 1
    fun color(direction: Double, color: Color): Color

    @Serializable
    @SerialName("radar")
    class Radar: RingStyle {
        override val textKey get() = "config.radar"
        override val descriptionKey get() = "config.radar_description"

        var minAlpha = 0.3
        var maxAlpha = 1.0
        var length = 0.25
        context(ctx: DslContext)
        override fun editor(modifier: Modifier) = Column(modifier, id = this) {
            Row(Modifier.height(Measure.AUTO_MIN)) {
                SliderHorizontal(
                    Modifier.height(20.scaled).padding(2.scaled),
                    0.0..maxAlpha, ::minAlpha.property
                ) {
                    TextFlatten { "minAlpha:${String.format("%.2f", minAlpha)}".emit() }
                }
                SliderHorizontal(
                    Modifier.height(20.scaled).padding(2.scaled),
                    minAlpha..1.0, ::maxAlpha.property
                ) {
                    TextFlatten { "maxAlpha:${String.format("%.2f", maxAlpha)}".emit() }
                }
            }
            Row(Modifier.height(Measure.AUTO_MIN)) {
                SliderHorizontal(
                    Modifier.height(20.scaled).padding(2.scaled),
                    0.0..1.0, ::length.property
                ) {
                    TextFlatten { "length:${String.format("%.2f", length)}".emit() }
                }
            }
        }

        override fun color(direction: Double, color: Color): Color {
            val alphaRange = maxAlpha - minAlpha
            val radarAlpha = alphaRange * (1 - direction / length)
            val alpha = max(minAlpha, radarAlpha)
            return color.change(a = color.aDouble * alpha)
        }

    }

    @Serializable
    @SerialName("pulse")
    class Pulse: RingStyle {
        override val textKey get() = "config.pulse"
        override val descriptionKey get() = "config.pulse_description"

        var minAlpha = 0.1
        var maxAlpha = 0.8
        var count = 8
        context(ctx: DslContext)
        override fun editor(modifier: Modifier) = Column(modifier) {
            Row(Modifier.height(Measure.AUTO_MIN)) {
                SliderHorizontal(
                    Modifier.height(20.scaled).padding(2.scaled),
                    0.0..maxAlpha, ::minAlpha.property
                ) {
                    TextFlatten { "minAlpha${String.format("%.2f", minAlpha)}".emit() }
                }
                SliderHorizontal(
                    Modifier.height(20.scaled).padding(2.scaled),
                    minAlpha..1.0, ::maxAlpha.property
                ) {
                    TextFlatten { "maxAlpha${String.format("%.2f", maxAlpha)}".emit() }
                }
            }
            Row(Modifier.height(Measure.AUTO_MIN)) {
                SliderHorizontal(
                    Modifier.height(20.scaled).padding(2.scaled),
                    2..33, ::count.property
                ) {
                    TextFlatten { "count:$count".emit() }
                }
            }
        }

        override fun color(direction: Double, color: Color): Color {
            val highlight = ((direction * count * 2).toInt() % 2) != 0
            return color.change(a = color.aDouble * if(highlight) maxAlpha else minAlpha)
        }

    }
}