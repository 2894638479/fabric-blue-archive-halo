package name.bluearchivehalo.mixin;

import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameRenderer.class)
public interface GameRendererProgramGetter {
    @Accessor("positionColorProgram")
    public static ShaderProgram getPositionColorShaderProgram() {
        throw new IllegalArgumentException();
    }
}
