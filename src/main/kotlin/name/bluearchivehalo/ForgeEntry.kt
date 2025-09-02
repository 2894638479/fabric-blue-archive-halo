package name.bluearchivehalo

import name.bluearchivehalo.screen.MainScreen
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.DistExecutor
import net.neoforged.fml.ModLoadingContext
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.client.ConfigScreenHandler

@Mod("blue_archive_halo")
class ForgeEntry {
    init {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT){
            Runnable {
                BlueArchiveHaloClient.onInitializeClient()
                ModLoadingContext.get().registerExtensionPoint<ConfigScreenHandler.ConfigScreenFactory?>(
                    ConfigScreenHandler.ConfigScreenFactory::class.java)
                { ConfigScreenHandler.ConfigScreenFactory { _: MinecraftClient?, parent: Screen? -> MainScreen(parent) } }
            }
        }
    }
}