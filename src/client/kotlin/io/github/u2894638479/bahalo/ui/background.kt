package io.github.u2894638479.bahalo.ui

import io.github.u2894638479.kotlinmcui.context.DslContext
import io.github.u2894638479.kotlinmcui.functions.dataStore
import io.github.u2894638479.kotlinmcui.functions.decorator.background
import io.github.u2894638479.kotlinmcui.functions.ui.Spacer
import io.github.u2894638479.kotlinmcui.functions.ui.defaultBackground
import io.github.u2894638479.kotlinmcui.math.Color
import io.github.u2894638479.kotlinmcui.scope.DslChild

context(ctx: DslContext)
fun DslChild.myBackground() = if(dataStore.backend.isInWorld) background(Color(0, 0, 0, 150)) else defaultBackground()

context(ctx: DslContext)
fun MyBackground() = Spacer {}.myBackground()