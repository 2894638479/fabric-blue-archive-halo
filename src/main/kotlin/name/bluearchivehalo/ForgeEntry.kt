package name.bluearchivehalo

import name.bluearchivehalo.screen.MainScreen
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.ConfigGuiHandler
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod


@Mod("blue_archive_halo")
class ForgeEntry {
    init {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT){
            Runnable {
                BlueArchiveHaloClient.onInitializeClient()
                ModLoadingContext.get().registerExtensionPoint(
                    ConfigGuiHandler.ConfigGuiFactory::class.java
                ){ ConfigGuiHandler.ConfigGuiFactory{ MainScreen(it) }}
            }
        }
    }
}