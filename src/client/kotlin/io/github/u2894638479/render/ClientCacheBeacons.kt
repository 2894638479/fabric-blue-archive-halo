package io.github.u2894638479.render

import io.github.u2894638479.BlueArchiveHaloClient
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.world.World

class ClientCacheBeacons(entityType: EntityType<ClientCacheBeacons>, world: World): Entity(entityType,world) {
    override fun initDataTracker() {}
    override fun readCustomDataFromNbt(nbt: NbtCompound?) {}
    override fun writeCustomDataToNbt(nbt: NbtCompound?) {}
    companion object {
        val id = "client_beacon_cache_renderer"
        fun register() = Registry.register(
            Registries.ENTITY_TYPE,
            "${BlueArchiveHaloClient.id}:$id",
            EntityType.Builder.create(::ClientCacheBeacons, SpawnGroup.MISC).build(id)
        )
    }

    override fun shouldRender(cameraX: Double, cameraY: Double, cameraZ: Double) = true
    override fun shouldRender(distance: Double) = true
}