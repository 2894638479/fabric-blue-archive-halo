package name.bluearchivehalo

import name.bluearchivehalo.screen.MainScreen
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.ModContainer
import net.neoforged.fml.ModList
import net.neoforged.fml.common.Mod
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.neoforge.client.gui.IConfigScreenFactory


@Mod("blue_archive_halo")
class NeoForgeEntry {
    init {
        if(FMLEnvironment.getDist() == Dist.CLIENT){
            BlueArchiveHaloClient.onInitializeClient()
            ModList.get().getModContainerById("blue_archive_halo").ifPresent { container: ModContainer ->
                container.registerExtensionPoint<IConfigScreenFactory>(IConfigScreenFactory::class.java) {
                    IConfigScreenFactory { _, modListScreen -> MainScreen(modListScreen) }
                }
            }
        }
    }
}