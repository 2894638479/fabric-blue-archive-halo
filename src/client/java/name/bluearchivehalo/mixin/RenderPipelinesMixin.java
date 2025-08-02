package name.bluearchivehalo.mixin;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(RenderPipelines.class)
public class RenderPipelinesMixin {
    @Shadow @Final private static Map<Identifier, RenderPipeline> PIPELINES;

    @Inject(method = "register",at = @At("HEAD"),cancellable = true)
    private static void changeBeaconBeamTranslucentPipeline(RenderPipeline pipeline, CallbackInfoReturnable<RenderPipeline> cir){
        if(pipeline.getLocation().equals(Identifier.ofVanilla("pipeline/beacon_beam_translucent"))){
            RenderPipeline.Snippet snippet = RenderPipeline
                    .builder(RenderPipelines.MATRICES_COLOR_SNIPPET)
                    .withVertexShader("core/rendertype_beacon_beam")
                    .withFragmentShader("core/rendertype_beacon_beam")
                    .withSampler("Sampler0")
                    .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLE_STRIP)
                    .buildSnippet();
            RenderPipeline pipeline1 = RenderPipeline
                    .builder(snippet)
                    .withLocation("pipeline/beacon_beam_translucent")
                    .withDepthWrite(true)
                    .withBlend(BlendFunction.TRANSLUCENT).build();
            PIPELINES.put(pipeline1.getLocation(),pipeline1);
            cir.setReturnValue(pipeline1);
        }
    }
}
