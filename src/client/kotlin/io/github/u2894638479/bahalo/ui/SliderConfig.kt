package io.github.u2894638479.bahalo.ui

import io.github.u2894638479.kotlinmcui.context.DslContext
import io.github.u2894638479.kotlinmcui.context.scaled
import io.github.u2894638479.kotlinmcui.functions.ui.Slider
import io.github.u2894638479.kotlinmcui.functions.ui.TextFlatten
import io.github.u2894638479.kotlinmcui.math.Axis
import io.github.u2894638479.kotlinmcui.modifier.Modifier
import io.github.u2894638479.kotlinmcui.modifier.minHeight
import io.github.u2894638479.kotlinmcui.modifier.padding
import io.github.u2894638479.kotlinmcui.prop.property
import kotlin.reflect.KMutableProperty0

context(ctx: DslContext)
fun SliderConfig(
    range: IntProgression,
    kProperty: KMutableProperty0<Int>
) = Slider(
    Modifier.minHeight(20.scaled).padding(2.scaled),
    Axis.Horizontal,
    range,
    kProperty.property,
    id = kProperty
) {
    TextFlatten {
        "${kProperty.name}: ${kProperty.get()}".emit()
    }
}

context(ctx: DslContext)
fun SliderConfig(
    range: ClosedFloatingPointRange<Double>,
    kProperty: KMutableProperty0<Double>
) = Slider(
    Modifier.minHeight(20.scaled).padding(2.scaled),
    Axis.Horizontal,
    range,
    kProperty.property,
    id = kProperty
) {
    TextFlatten {
        "${kProperty.name}: ${String.format("%.2f",kProperty.get())}".emit()
    }
}