package io.github.u2894638479.bahalo.config

import io.github.u2894638479.bahalo.config.ConfigStringProcessor.base64
import io.github.u2894638479.bahalo.config.ConfigStringProcessor.fromBase64
import io.github.u2894638479.kotlinmcui.context.DslContext
import io.github.u2894638479.kotlinmcui.context.DslExecuteContext
import io.github.u2894638479.kotlinmcui.context.scaled
import io.github.u2894638479.kotlinmcui.dsl.dataStore
import io.github.u2894638479.kotlinmcui.dsl.local
import io.github.u2894638479.kotlinmcui.dsl.ui.Column
import io.github.u2894638479.kotlinmcui.dsl.ui.Row
import io.github.u2894638479.kotlinmcui.dsl.ui.ScrollableColumn
import io.github.u2894638479.kotlinmcui.dsl.ui.Spacer
import io.github.u2894638479.kotlinmcui.dsl.ui.TextAutoFold
import io.github.u2894638479.kotlinmcui.dsl.ui.TextFlatten
import io.github.u2894638479.kotlinmcui.modifier.Modifier
import io.github.u2894638479.kotlinmcui.modifier.padding
import io.github.u2894638479.kotlinmcui.modifier.size
import io.github.u2894638479.kotlinmcui.prop.getValue
import io.github.u2894638479.kotlinmcui.prop.setValue
import io.github.u2894638479.kotlinmcui.utils.Simple

context(ctx: DslContext)
inline fun <reified T> ConfigTextConvertPage(obj:T, crossinline onPaste:context(DslExecuteContext)(T?) -> T) = Column(Modifier.padding(20.scaled)) {
    var string by local { obj.base64() }
    ScrollableColumn {
        TextFlatten { "object".emit() }
        TextAutoFold { string.emit() }
    }
    Row {
        Simple.Button("copy⬇") {
            dataStore.backend.clipBoard = string
        }
        Simple.Button("paste⬆") {
            val clipboard = dataStore.backend.clipBoard
            string = onPaste(clipboard.fromBase64()).base64()
        }
    }
    Spacer(Modifier.size(40.scaled,40.scaled)) {}
    ScrollableColumn {
        TextFlatten { "clipboard".emit() }
        TextAutoFold { dataStore.backend.clipBoard.emit() }
    }
}