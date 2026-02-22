package io.github.u2894638479.bahalo.ui

import io.github.u2894638479.kotlinmcui.component.DslComponent
import io.github.u2894638479.kotlinmcui.context.DslContext
import io.github.u2894638479.kotlinmcui.context.scaled
import io.github.u2894638479.kotlinmcui.functions.decorator.animateHeight
import io.github.u2894638479.kotlinmcui.functions.decorator.background
import io.github.u2894638479.kotlinmcui.functions.decorator.clickable
import io.github.u2894638479.kotlinmcui.functions.decorator.highlightBox
import io.github.u2894638479.kotlinmcui.functions.decorator.renderScissor
import io.github.u2894638479.kotlinmcui.functions.forEachWithId
import io.github.u2894638479.kotlinmcui.functions.remember
import io.github.u2894638479.kotlinmcui.functions.translate
import io.github.u2894638479.kotlinmcui.functions.ui.Box
import io.github.u2894638479.kotlinmcui.functions.ui.Button
import io.github.u2894638479.kotlinmcui.functions.ui.Column
import io.github.u2894638479.kotlinmcui.functions.ui.Row
import io.github.u2894638479.kotlinmcui.functions.ui.Spacer
import io.github.u2894638479.kotlinmcui.functions.ui.TextFlatten
import io.github.u2894638479.kotlinmcui.math.Color
import io.github.u2894638479.kotlinmcui.math.Measure
import io.github.u2894638479.kotlinmcui.math.px
import io.github.u2894638479.kotlinmcui.math.rect.height
import io.github.u2894638479.kotlinmcui.modifier.Modifier
import io.github.u2894638479.kotlinmcui.modifier.height
import io.github.u2894638479.kotlinmcui.modifier.padding
import io.github.u2894638479.kotlinmcui.modifier.weight
import io.github.u2894638479.kotlinmcui.modifier.width
import io.github.u2894638479.kotlinmcui.prop.getValue
import io.github.u2894638479.kotlinmcui.prop.setValue

context(ctx: DslContext)
fun <T> MutableCollection<T>.editor(
    modifier: Modifier = Modifier,
    text: (T) -> String,
    maxSize: Int,
    create:() -> T,
    color: Color,
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
        Box {
            if(it !in this) return@Box
            Column(Modifier.padding(1.scaled)) {
                Row(Modifier.height(Measure.AUTO_MIN)) {
                    TextFlatten { text(it).emit() }
                    Button(Modifier.height(20.scaled).width(Measure.AUTO_MIN).padding(5.scaled)) {
                        TextFlatten(Modifier.padding(h = 5.scaled)) { translate("bahalo.ui.remove").emit() }
                    }.clickable { remove(it) }
                }
                if(unfold === it) unfolded(it)
            }.renderScissor().clickable {
                unfold = if(unfold === it) null else it
            }.highlightBox().background(color)
        }.animateHeight().change {  delegate ->
            object : DslComponent by delegate {
                context(instance: DslComponent)
                override fun layoutVertical() {
                    delegate.layoutVertical()
                    if(delegate.rect.height == 0.px) visible.remove(it)
                }
            }
        }
    }

    Box {
        if(size+1 <= maxSize) Button(Modifier.height(20.scaled).padding(5.scaled)) {
            TextFlatten { translate("bahalo.ui.add").emit() }
        }.clickable {
            if(size+1 <= maxSize) {
                val element = create()
                add(element)
                visible += element
            }
        }
    }.animateHeight()

    Spacer(Modifier.weight(Double.MAX_VALUE), Unit)
}