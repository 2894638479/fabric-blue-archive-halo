package io.github.u2894638479.config

import io.github.u2894638479.kotlinmcui.context.DslContext
import io.github.u2894638479.kotlinmcui.context.scaled
import io.github.u2894638479.kotlinmcui.functions.decorator.clickable
import io.github.u2894638479.kotlinmcui.functions.ui.Button
import io.github.u2894638479.kotlinmcui.functions.ui.Column
import io.github.u2894638479.kotlinmcui.functions.ui.Row
import io.github.u2894638479.kotlinmcui.functions.ui.SliderHorizontal
import io.github.u2894638479.kotlinmcui.functions.ui.TextFlatten
import io.github.u2894638479.kotlinmcui.math.Color
import io.github.u2894638479.kotlinmcui.modifier.Modifier
import io.github.u2894638479.kotlinmcui.modifier.height
import io.github.u2894638479.kotlinmcui.modifier.padding
import io.github.u2894638479.kotlinmcui.prop.StableRWProperty
import io.github.u2894638479.kotlinmcui.prop.property
import io.github.u2894638479.kotlinmcui.prop.value
import kotlinx.serialization.Serializable

@Serializable
class Special {
    var depthWrite = true
    var clientCache = true
    var bonus = false
    var extraFarPlane = 0.0

    context(ctx: DslContext)
    fun editor(modifier: Modifier = Modifier.Companion, hasBonus: Boolean) = Column(modifier, id = this) {
        context(ctx: DslContext)
        fun editBool(prop: StableRWProperty<Boolean>, text: String) =
            Button(Modifier.padding(5.scaled), id = text) {
                TextFlatten {
                    "$text : ".emit()
                    prop.value.toString().emit(if (prop.value) Color.GREEN else Color.RED)
                }
            }.clickable { prop.value = !prop.value }

        Row(Modifier.height(30.scaled)) {
            editBool(::depthWrite.property, "Depth Write")
            editBool(::clientCache.property, "Client Cache")
        }
        Row(Modifier.height(30.scaled)) {
            if (hasBonus) editBool(::bonus.property, "Bonus")
            SliderHorizontal(Modifier.padding(5.scaled), 0.0..3000.0, ::extraFarPlane.property) {
                TextFlatten { "Extra far plane: ${String.format("%.2f", extraFarPlane)}".emit() }
            }
        }
        Button(Modifier.height(20.scaled).padding(5.scaled)) {
            TextFlatten { "reload config file".emit() }
        }.clickable { Config.load() }
    }
}