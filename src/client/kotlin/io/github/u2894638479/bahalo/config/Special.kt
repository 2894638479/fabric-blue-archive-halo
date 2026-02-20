package io.github.u2894638479.bahalo.config

import io.github.u2894638479.bahalo.ui.BoolConfig
import io.github.u2894638479.bahalo.ui.SliderConfig
import io.github.u2894638479.kotlinmcui.context.DslContext
import io.github.u2894638479.kotlinmcui.functions.ui.Column
import io.github.u2894638479.kotlinmcui.functions.ui.Row
import io.github.u2894638479.kotlinmcui.modifier.Modifier
import kotlinx.serialization.Serializable

@Serializable
class Special {
    var transparency = true
    var depthWrite = true
    var clientCache = true
    var combineBeacon = false
    var combineRadius = 1.0
    var bonus = false
    var extraFarPlane = 0.0
    var lodPrecision = 100.0
    var columnSides = 3

    context(ctx: DslContext)
    fun editor(modifier: Modifier = Modifier, hasBonus: Boolean) = Column(modifier, id = this) {
        Row {
            BoolConfig(::transparency)
            BoolConfig(::depthWrite)
        }

        Row {
            BoolConfig(::clientCache)
            if(clientCache) BoolConfig(::combineBeacon)
        }

        if(clientCache && combineBeacon) SliderConfig(1.0..10.0,::combineRadius)

        Row {
            if (hasBonus) BoolConfig(::bonus)
            SliderConfig(0.0..3000.0, ::extraFarPlane)
        }

        Row {
            SliderConfig(10.0..300.0, ::lodPrecision)
            SliderConfig(3..24, ::columnSides)
        }
    }
}