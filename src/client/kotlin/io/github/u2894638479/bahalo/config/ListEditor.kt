package io.github.u2894638479.bahalo.config

import io.github.u2894638479.kotlinmcui.context.DslContext
import io.github.u2894638479.kotlinmcui.context.scaled
import io.github.u2894638479.kotlinmcui.functions.decorator.animateHeight
import io.github.u2894638479.kotlinmcui.functions.decorator.background
import io.github.u2894638479.kotlinmcui.functions.decorator.clickable
import io.github.u2894638479.kotlinmcui.functions.decorator.highlightBox
import io.github.u2894638479.kotlinmcui.functions.decorator.renderScissor
import io.github.u2894638479.kotlinmcui.functions.forEachWithId
import io.github.u2894638479.kotlinmcui.functions.remember
import io.github.u2894638479.kotlinmcui.functions.ui.Button
import io.github.u2894638479.kotlinmcui.functions.ui.Column
import io.github.u2894638479.kotlinmcui.functions.ui.Row
import io.github.u2894638479.kotlinmcui.functions.ui.TextFlatten
import io.github.u2894638479.kotlinmcui.math.Color
import io.github.u2894638479.kotlinmcui.math.Measure
import io.github.u2894638479.kotlinmcui.modifier.Modifier
import io.github.u2894638479.kotlinmcui.modifier.height
import io.github.u2894638479.kotlinmcui.modifier.padding
import io.github.u2894638479.kotlinmcui.prop.getValue
import io.github.u2894638479.kotlinmcui.prop.setValue

context(ctx: DslContext)
fun <T> MutableList<T>.editor(
    modifier: Modifier = Modifier,
    maxSize: Int,
    create:() -> T,
    id: Any? = null,
    unfolded: context(DslContext) (T)-> Unit
) = Column(modifier,id = id ?: unfolded::class) {
    val visible by remember { this.toMutableList() }

    if(!visible.containsAll(this)) {
        visible.clear()
        visible.addAll(this)
    }
    var unfold by remember<T?>(null)

    visible.forEachWithId {
        Column(Modifier.padding(1.scaled)) {
            if(it !in this) return@Column
            Row(Modifier.height(Measure.AUTO_MIN)) {
                TextFlatten { "ring ${indexOf(it)}".emit() }
                Button(Modifier.height(20.scaled).padding(5.scaled)) {
                    TextFlatten { "remove".emit() }
                }.clickable { remove(it) }
            }
            if(unfold === it) unfolded(it)
        }.animateHeight().renderScissor().clickable {
            unfold = if(unfold === it) null else it
        }.highlightBox().background(Color(0, 200, 200, 30))
    }

    Button(Modifier.height(20.scaled).padding(5.scaled)) {
        TextFlatten { "add".emit() }
    }.clickable(size+1 <= maxSize) {
        if(size+1 <= maxSize) {
            val element = create()
            add(element)
            visible += element
        }
    }
}