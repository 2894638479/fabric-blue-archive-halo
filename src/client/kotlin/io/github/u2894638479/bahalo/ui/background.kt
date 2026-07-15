package io.github.u2894638479.bahalo.ui

import io.github.u2894638479.kotlinmcui.container.DslChild
import io.github.u2894638479.kotlinmcui.context.DslContext
import io.github.u2894638479.kotlinmcui.dsl.dataStore
import io.github.u2894638479.kotlinmcui.dsl.decorator.background
import io.github.u2894638479.kotlinmcui.dsl.ui.Spacer
import io.github.u2894638479.kotlinmcui.dsl.ui.defaultBackground
import io.github.u2894638479.kotlinmcui.math.Color

context(ctx: DslContext)
fun DslChild.myBackground() = if(dataStore.backend.isInWorld) background(Color(0, 0, 0, 150)) else defaultBackground()

context(ctx: DslContext)
fun MyBackground() = Spacer {}.myBackground()