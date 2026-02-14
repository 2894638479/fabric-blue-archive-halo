package name.bluearchivehalo.config

import io.github.u2894638479.kotlinmcui.context.DslContext
import io.github.u2894638479.kotlinmcui.context.scaled
import io.github.u2894638479.kotlinmcui.functions.decorator.animateHeight
import io.github.u2894638479.kotlinmcui.functions.decorator.animateWidth
import io.github.u2894638479.kotlinmcui.functions.decorator.clickable
import io.github.u2894638479.kotlinmcui.functions.translate
import io.github.u2894638479.kotlinmcui.functions.ui.Button
import io.github.u2894638479.kotlinmcui.functions.ui.Column
import io.github.u2894638479.kotlinmcui.functions.ui.Row
import io.github.u2894638479.kotlinmcui.functions.ui.SliderHorizontal
import io.github.u2894638479.kotlinmcui.functions.ui.TextFlatten
import io.github.u2894638479.kotlinmcui.math.Measure
import io.github.u2894638479.kotlinmcui.modifier.Modifier
import io.github.u2894638479.kotlinmcui.modifier.height
import io.github.u2894638479.kotlinmcui.modifier.padding
import io.github.u2894638479.kotlinmcui.prop.property
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
class RingInfo {
    var radius = 100.0
    var cycle = 300 + Random.nextInt(0,100)
    var width = 2.0
    var style: RingStyle = RingStyle.Pulse()

    var speed get() = if(cycle == 0) 0.0 else 400.0/cycle
        set(value) { cycle = if(value == 0.0) 0 else (400.0/value).toInt() }

    context(ctx: DslContext)
    fun editor(
        modifier: Modifier = Modifier.Companion,
        radiusRange: ClosedFloatingPointRange<Double>
    ) = Column(modifier, id = this) {
        Row(Modifier.height(Measure.AUTO_MIN)) {
            SliderHorizontal(Modifier.height(20.scaled).padding(1.scaled),
                radiusRange, ::radius.property) {
                TextFlatten { "radius:${String.format("%.2f", radius)}".emit() }
            }
            SliderHorizontal(Modifier.height(20.scaled).padding(1.scaled), 1.0..5.0, ::width.property) {
                TextFlatten { "width:${String.format("%.2f", width)}".emit() }
            }
        }
        Row(Modifier.height(Measure.AUTO_MIN)) {
            SliderHorizontal(Modifier.height(20.scaled).padding(1.scaled),-5.0..5.0, ::speed.property) {
                TextFlatten { "speed:${String.format("%.2f", speed)}".emit() }
            }
        }
        Button(Modifier.padding(1.scaled)) {
            Column {
                TextFlatten(Modifier.padding(5.scaled)) { "style: ${translate(style.textKey)}".emit() }
                style.editor(Modifier.padding(5.scaled))
            }.animateHeight()
        }.clickable { style = style.next }
    }
}