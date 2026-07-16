package io.github.u2894638479.bahalo.render

import io.github.u2894638479.bahalo.config.Config
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.entity.state.AvatarRenderState

context(rp: RenderParam)
fun renderPlayerRing(entity: AvatarRenderState) {
    if(!Config.instance.special.enablePlayerHalos) return
    stack {
        ms.translate(0.0,1.5,0.0)
        val e = Minecraft.getInstance().level?.getEntity(entity.id) ?: return
        Config.instance.players[e.name.string].forEach {
            renderRingInfo(it, listOf())
        }
    }
}