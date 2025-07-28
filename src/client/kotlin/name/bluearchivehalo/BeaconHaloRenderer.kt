package name.bluearchivehalo

import com.google.common.collect.ImmutableMap
import name.bluearchivehalo.mixin.BeaconLevelGetter
import name.bluearchivehalo.mixin.GameRendererProgramGetter
import net.minecraft.block.entity.BeaconBlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.*
import net.minecraft.client.render.VertexFormat.DrawMode
import net.minecraft.client.render.VertexFormats.COLOR_ELEMENT
import net.minecraft.client.render.VertexFormats.POSITION_ELEMENT
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.RotationAxis
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.random.LocalRandom
import org.joml.Matrix4f
import kotlin.math.*

class BeaconHaloRenderer(ctx: BlockEntityRendererFactory.Context?) : BeaconBlockEntityRenderer(ctx) {
    override fun render(
        entity: BeaconBlockEntity, tickDelta: Float, matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int
    ) {
        super.render(entity, tickDelta, matrices, vertexConsumers, light, overlay)
        val segments = entity.beamSegments.ifEmpty { return }
        val world = entity.world ?: return
        val rand = LocalRandom(seed(entity))
        fun ring(r:Float,cycleTicks:Int,color:ArgbFloat,height:Float,thickness:Float){
            val rotation = if(cycleTicks != 0) ((world.time % cycleTicks + rand.nextInt(abs(cycleTicks)) + tickDelta) * 2 * PI / cycleTicks).toFloat() else 0f
            val angleCount = run {
                val cameraPos = MinecraftClient.getInstance().gameRenderer.camera.pos
                val entityPos = entity.pos.toCenterPos()
                val distance = entityPos.add(0.0,height.toDouble(),0.0).distanceTo(cameraPos)
                if(distance <= (height + r)) r.toInt()
                else max(10,(r - (distance - height - r)/5).toInt())
            }
            matrices.stack {
                matrices.translate(0.5, rand.nextDouble() + height, 0.5)
                matrices.multiply(RotationAxis.POSITIVE_Y.rotation(rotation))
                renderHorizontalCircleRing(matrices, vertexConsumers, r,thickness,angleCount,cycleTicks < 0){color}
            }
        }
        fun color(index:Int):ArgbFloat{
            var sum = 0
            segments.forEach {
                sum += it.height
                if(sum > index) return ArgbFloat(it.color)
            }
            return ArgbFloat(segments.last().color)
        }
        when(entity.level){
            0 -> return
            1 -> {
                ring(150f,400,color(1),200f,2f)
            }
            2 -> {
                ring(130f,400,color(2),225f,2f)
                ring(200f,300,color(1),225f,2f)
            }
            3 -> {
                ring(130f,-400,color(3),225f,2f)
                ring(200f,250,color(2),225f,2f)
                ring(215f,300,color(1),225f,2f)
            }
            4 -> {
                ring(100f,400,color(5),250f,2f)
                ring(200f,300,color(4),250f,2f)
                ring(215f,-400,color(3),250f,2f)
                ring(300f,250,color(2),250f,2f)
                ring(315f,330,color(1),250f,2f)
            }
            else -> error("unsupported beacon level:${entity.level}")
        }
    }

    override fun isInRenderDistance(beaconBlockEntity: BeaconBlockEntity?, vec3d: Vec3d?): Boolean {
        return true
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
            val cos:Float,val sin:Float,val color:Int
        ){
            fun vertex(consumer: VertexConsumer,modelMatrix: Matrix4f,radius:Float,y:Float = 0f){
                consumer.vertex(modelMatrix,radius*cos,y,radius*sin).color(color).next()
            }
        }
        fun renderHorizontalCircleRing(
            matrices: MatrixStack, consumerProvider: VertexConsumerProvider,
            radius: Float, thickness: Float, segmentCount:Int, reverseRotation:Boolean,
            colorBy0to1: (Double) -> ArgbFloat
        ) {
            val consumer = consumerProvider.getBuffer(MyMultiPhase.myLayer)
            val modelMatrix = matrices.peek().positionMatrix
            val radiusInner = radius - thickness/2
            val radiusOuter = radius + thickness/2
            val viewHeight = thickness * 2 / 3


            val angles = (0..segmentCount).map {
                2 * PI * it / segmentCount
            }.map {
                val cos = cos(it).toFloat()
                val sin = sin(it).toFloat()
                val alpha = max(0.33f,1f - (it*2/PI).toFloat())
                val color = colorBy0to1(it / (2*PI)).alpha(alpha).toInt()
                if(reverseRotation) AngleInfo(sin,cos,color)
                else AngleInfo(cos,sin,color)
            }
            angles.forEach {
                it.vertex(consumer, modelMatrix, radiusInner)
                it.vertex(consumer, modelMatrix, radiusOuter)
            }
            angles.forEach {
                it.vertex(consumer, modelMatrix, radius, 0.1f)
                it.vertex(consumer, modelMatrix, radius, viewHeight)
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