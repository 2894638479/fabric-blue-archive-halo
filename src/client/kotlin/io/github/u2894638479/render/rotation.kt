package io.github.u2894638479.render

import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.sign

fun rotation(ticks: Long,tickDelta: Double,cycleTicks: Long): Double {
    if(cycleTicks == 0L) return 0.0
    val abs = cycleTicks.absoluteValue
    val mod = ticks % cycleTicks + tickDelta
    val rad = mod / abs * 2 * PI
    return rad * cycleTicks.sign
}