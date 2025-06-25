package name.bluearchivehalo

import net.minecraft.block.entity.BeaconBlockEntity
import net.minecraft.client.render.*
import net.minecraft.client.render.VertexFormat.DrawMode
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.RotationAxis
import org.joml.Matrix4f
import kotlin.math.cos
import kotlin.math.sin

class BeaconHaloRenderer(ctx: BlockEntityRendererFactory.Context?) : BeaconBlockEntityRenderer(ctx) {
    override fun render(
        entity: BeaconBlockEntity, tickDelta: Float, matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int
    ) {
        val segments = entity.beamSegments.ifEmpty { return }
        val world = entity.world ?: return
        val fullBright = LightmapTextureManager.pack(15, 15)
        val renderLayer = MyMultiPhase.myLayer()
        val vertexConsumer = vertexConsumers.getBuffer(renderLayer)
        val rotation = (world.time + tickDelta) / 20f
        val color = segments[segments.size - 1].color.run {
            ArgbFloat(0.5f,get(0),get(1),get(2))
        }
        matrices.stack {
            matrices.translate(0.5, 20.0, 0.5)
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation(rotation))
            renderHorizontalCircleRing(matrices, vertexConsumer, 5f, 1f, color, fullBright, OverlayTexture.DEFAULT_UV)
        }
        super.render(entity, tickDelta, matrices, vertexConsumers, light, overlay)
    }




    companion object {
        inline fun MatrixStack.stack(block:()->Unit){ push();block();pop() }
        class ArgbFloat(
            val a:Float,val r:Float,val g:Float,val b:Float
        ){
            fun toInt():Int{
                val alpha = (a * 255).toInt() shl 24
                val red = (r * 255).toInt() shl 16
                val green = (g * 255).toInt() shl 8
                val blue = (b * 255).toInt()
                return alpha or red or green or blue
            }
            operator fun times(other:ArgbFloat) = ArgbFloat(a*other.a,r*other.r,g*other.g,b*other.b)
            fun alpha(alpha:Float) = ArgbFloat(alpha * a, r, g, b)
        }
        class AngleInfo(
            val x1:Float,val x2:Float,val z1:Float,val z2:Float,val u1:Float,val u2:Float,val v1:Float,val v2:Float,val color:Int
        ){
            fun vertexInner(consumer:VertexConsumer, modelMatrix:Matrix4f, light: Int, overlay: Int,normalY:Float){
                consumer.vertex(modelMatrix, x1, 0f, z1)
                    .color(color)
                    .texture(u1, v1)
                    .overlay(overlay)
                    .light(light)
                    .normal(0f,normalY,0f)
                    .next()
            }
            fun vertexOuter(consumer:VertexConsumer, modelMatrix:Matrix4f, light: Int, overlay: Int,normalY:Float){
                consumer.vertex(modelMatrix, x2, 0f, z2)
                    .color(color)
                    .texture(u2, v2)
                    .overlay(overlay)
                    .light(light)
                    .normal(0f,normalY,0f)
                    .next()
            }
        }
        fun renderHorizontalCircleRing(
            matrices: MatrixStack, consumer: VertexConsumer,
            radius: Float, thickness: Float,
            color: ArgbFloat, light: Int, overlay: Int
        ) {
            val segments = (3 * 2 * Math.PI * radius / thickness).toInt()
            val modelMatrix = matrices.peek().positionMatrix
            val radiusInner = radius - thickness/2
            val radiusOuter = radius + thickness/2


            (0..segments).map {
                2 * Math.PI * it / segments
            }.map {
                val cos = cos(it).toFloat()
                val sin = sin(it).toFloat()
                val cycle = (radius*it / thickness).toFloat()
                AngleInfo(radiusInner*cos,radiusOuter*cos,radiusInner*sin,radiusOuter*sin,
                    0f,1f,cycle,cycle,color.toInt())
            }.zipWithNext { a, b ->
                a.vertexInner(consumer,modelMatrix, light, overlay,-1f)
                a.vertexOuter(consumer, modelMatrix, light, overlay,-1f)
                b.vertexOuter(consumer,modelMatrix, light, overlay,-1f)
                b.vertexInner(consumer, modelMatrix, light, overlay,-1f)
            }
        }
        class MyMultiPhase private constructor(
            name: String,
            vertexFormat: VertexFormat,
            drawMode: DrawMode,
            expectedBufferSize: Int,
            hasCrumbling: Boolean,
            translucent: Boolean,
            phases: Phases
        ) : RenderLayer(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent,
                { phases.list.forEach { it.startDrawing() } },
                { phases.list.forEach { it.endDrawing() } }){
            private class Phases {
                var texture = NO_TEXTURE
                var program = NO_PROGRAM
                var transparency = NO_TRANSPARENCY
                var depthTest = LEQUAL_DEPTH_TEST
                var cull = ENABLE_CULLING
                var lightmap = DISABLE_LIGHTMAP
                var overlay = DISABLE_OVERLAY_COLOR
                var layering = NO_LAYERING
                var target = MAIN_TARGET
                var texturing = DEFAULT_TEXTURING
                var writeMaskState = ALL_MASK
                var lineWidth = FULL_LINE_WIDTH
                var colorLogic = NO_COLOR_LOGIC
                val list get() = listOf(
                    this.texture,
                    this.program,
                    this.transparency,
                    this.depthTest,
                    this.cull,
                    this.lightmap,
                    this.overlay,
                    this.layering,
                    this.target,
                    this.texturing,
                    this.writeMaskState,
                    this.colorLogic,
                    this.lineWidth
                )
            }
            companion object {
                fun myLayer():RenderLayer{
                    val par = Phases().apply {
                        program = BEACON_BEAM_PROGRAM
                        texture = Texture(BEAM_TEXTURE, false, false)
                        transparency = TRANSLUCENT_TRANSPARENCY
                        writeMaskState = ALL_MASK
                        cull = DISABLE_CULLING
                    }
                    return MyMultiPhase("beacon_halo", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
                        DrawMode.QUADS, 256, false, false,par)
                }
            }
        }
    }
}