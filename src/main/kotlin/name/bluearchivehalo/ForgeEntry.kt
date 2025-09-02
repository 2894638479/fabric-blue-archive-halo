package name.bluearchivehalo

import name.bluearchivehalo.screen.MainScreen
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.ConfigScreenHandler
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod

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