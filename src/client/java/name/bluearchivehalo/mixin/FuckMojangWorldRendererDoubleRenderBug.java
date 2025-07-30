package name.bluearchivehalo.mixin;

import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value = WorldRenderer.class,priority = 1)
public class FuckMojangWorldRendererDoubleRenderBug {
    @Redirect(method = "renderBlockEntities",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/chunk/ChunkBuilder$ChunkData;getBlockEntities()Ljava/util/List;"))
    List<BlockEntity> fuckDoubleRender(ChunkBuilder.ChunkData instance){
        List<BlockEntity> list = instance.getBlockEntities();
        if(!list.isEmpty()){
            list.removeIf((entity)-> entity instanceof BeaconBlockEntity);
        }
        return list;
    }
}
