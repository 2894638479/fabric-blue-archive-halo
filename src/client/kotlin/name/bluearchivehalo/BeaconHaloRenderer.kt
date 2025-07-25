package name.bluearchivehalo

import com.google.common.collect.ImmutableMap
import com.google.common.primitives.Floats.max
import name.bluearchivehalo.mixin.BeaconLevelGetter
import name.bluearchivehalo.mixin.GameRendererProgramGetter
import net.minecraft.block.entity.BeaconBlockEntity
import net.minecraft.client.render.*
import net.minecraft.client.render.VertexFormat.DrawMode
import net.minecraft.client.render.VertexFormats.COLOR_ELEMENT
import net.minecraft.client.render.VertexFormats.POSITION_ELEMENT
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.RotationAxis
import net.minecraft.util.math.random.LocalRandom
import org.joml.Matrix4f
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class BeaconHaloRenderer(ctx: BlockEntityRendererFactory.Context?) : BeaconBlockEntityRenderer(ctx) {
    override fun render(
        entity: BeaconBlockEntity, tickDelta: Float, matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int
    ) {
        super.render(entity, tickDelta, matrices, vertexConsumers, light, overlay)
        val segments = entity.beamSegments.ifEmpty { return }
        val world = entity.world ?: return
        val vertexConsumer = vertexConsumers.getBuffer(MyMultiPhase.myLayer)
        val rand = LocalRandom(seed(entity))
        val cycleTicks = 400
        val rotation = ((world.time % cycleTicks + rand.nextInt(cycleTicks) + tickDelta) * 2 * PI / cycleTicks).toFloat()
        val color = ArgbFloat(segments.last().color).mix(ArgbFloat.white,0.3f)
        matrices.stack {
            matrices.translate(0.5, 20.0, 0.5)
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation(rotation))
            renderHorizontalCircleRing(matrices, vertexConsumer, 5f, 1f){color}
        }
    }




    companion object {
        inline fun MatrixStack.stack(block:()->Unit){ push();block();pop() }
        val BeaconBlockEntity.level get() = (this as BeaconLevelGetter).level
        fun seed(entity: BeaconBlockEntity) = entity.level * 9439L + entity.pos.run { (x*31+y)*31+z }
        class ArgbFloat(val a:Float,val r:Float,val g:Float,val b:Float){
            constructor(arr:FloatArray):this(1f,arr[0],arr[1],arr[2])
            companion object {
                val white = ArgbFloat(1f,1f,1f,1f)
            }
            fun toInt():Int{
                val alpha = (a * 255).toInt() shl 24
                val red = (r * 255).toInt() shl 16
                val green = (g * 255).toInt() shl 8
                val blue = (b * 255).toInt()
                return alpha or red or green or blue
            }
            operator fun times(other:ArgbFloat) = ArgbFloat(a*other.a,r*other.r,g*other.g,b*other.b)
            fun alpha(alpha:Float) = ArgbFloat(alpha * a, r, g, b)
            fun mix(other:ArgbFloat,rate:Float):ArgbFloat{
                if(rate <= 0) return this
                if(rate >= 1) return other
                val thisRate = 1 - rate
                return ArgbFloat(a*thisRate+other.a*rate,r*thisRate+other.r*rate,g*thisRate+other.g*rate,b*thisRate+other.b*rate)
            }
        }
        class AngleInfo(
            val x1:Float,val x2:Float,val z1:Float,val z2:Float,val color:Int
        ){
            fun vertexInner(consumer: VertexConsumer, modelMatrix: Matrix4f){
                consumer.vertex(modelMatrix, x1, 0f, z1).color(color).next()
            }
            fun vertexOuter(consumer: VertexConsumer, modelMatrix: Matrix4f){
                consumer.vertex(modelMatrix, x2, 0f, z2).color(color).next()
            }
        }
        fun renderHorizontalCircleRing(
            matrices: MatrixStack, consumer: VertexConsumer,
            radius: Float, thickness: Float,
            colorBy0to1: (Double) -> ArgbFloat
        ) {
            val segments = (radius).toInt()
            val modelMatrix = matrices.peek().positionMatrix
            val radiusInner = radius - thickness/2
            val radiusOuter = radius + thickness/2


            (0..segments).map {
                2 * PI * it / segments
            }.map {
                val cos = cos(it).toFloat()
                val sin = sin(it).toFloat()
                val alpha = max(0.33f,1f - (it*2/PI).toFloat())
                AngleInfo(radiusInner*cos,radiusOuter*cos,radiusInner*sin,radiusOuter*sin,
                    colorBy0to1(it / (2*PI)).alpha(alpha).toInt())
            }.forEach {
                it.vertexInner(consumer, modelMatrix)
                it.vertexOuter(consumer, modelMatrix)
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
                val list get() = listOf(texture, program, transparency, depthTest, cull, lightmap,
                    overlay, layering, target, texturing, writeMaskState, colorLogic, lineWidth)
            }
            companion object {
                val myLayer:RenderLayer = run{
                    val par = Phases().apply {
                        program = ShaderProgram { GameRendererProgramGetter.getPositionColorShaderProgram() }
                        writeMaskState = ALL_MASK
                        cull = DISABLE_CULLING
                        transparency = TRANSLUCENT_TRANSPARENCY
                    }
                    val vertexFormat = VertexFormat(ImmutableMap.builder<String,VertexFormatElement>().put("Position", POSITION_ELEMENT).put("Color", COLOR_ELEMENT).build())
                    MyMultiPhase("beacon_halo", vertexFormat,
                        DrawMode.TRIANGLE_STRIP, 2097152, false, true,par)
                }
            }
        }
    }
}