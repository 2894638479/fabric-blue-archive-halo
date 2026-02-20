package io.github.u2894638479.bahalo.ui

import io.github.u2894638479.kotlinmcui.context.DslContext
import io.github.u2894638479.kotlinmcui.context.scaled
import io.github.u2894638479.kotlinmcui.functions.decorator.clickable
import io.github.u2894638479.kotlinmcui.functions.ui.Button
import io.github.u2894638479.kotlinmcui.functions.ui.TextFlatten
import io.github.u2894638479.kotlinmcui.math.Color
import io.github.u2894638479.kotlinmcui.modifier.Modifier
import io.github.u2894638479.kotlinmcui.modifier.minHeight
import io.github.u2894638479.kotlinmcui.modifier.padding
import kotlin.reflect.KMutableProperty0

context(ctx: DslContext)
fun BoolConfig(property: KMutableProperty0<Boolean>)
= Button(Modifier.minHeight(20.scaled).padding(2.scaled),id = property) {
    TextFlatten {
        "${property.name}: ".emit()
        property.get().toString().emit(if(property.get()) Color.GREEN else Color.RED)
    }
}.clickable { property.set(!property.get()) }