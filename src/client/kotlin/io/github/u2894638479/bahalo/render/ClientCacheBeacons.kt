package io.github.u2894638479.bahalo.render

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.nbt.NbtCompound
import net.minecraft.world.World

class ClientCacheBeacons(entityType: EntityType<ClientCacheBeacons>, world: World): Entity(entityType,world) {
    override fun initDataTracker() {}
    override fun readCustomDataFromNbt(nbt: NbtCompound?) {}
    override fun writeCustomDataToNbt(nbt: NbtCompound?) {}
    companion object {
        val id = "client_beacon_cache_renderer"
    }

    override fun isInvisible() = true
    override fun shouldRender(cameraX: Double, cameraY: Double, cameraZ: Double) = true
    override fun shouldRender(distance: Double) = true
}