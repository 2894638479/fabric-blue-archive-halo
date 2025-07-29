package name.bluearchivehalo.mixin;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Shader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameRenderer.class)
public interface GameRendererProgramGetter {
    @Accessor("positionColorShader")
    public static Shader getPositionColorShaderProgram() {
        throw new IllegalArgumentException();
    }
}
