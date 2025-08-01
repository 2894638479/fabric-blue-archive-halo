package name.bluearchivehalo

import name.bluearchivehalo.BeaconHaloRenderer.Companion.ArgbFloat.Companion.white
import name.bluearchivehalo.BlueArchiveHaloClient.texture
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BeaconBlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.*
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Matrix4f
import net.minecraft.util.math.Quaternion
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3f
import net.minecraft.util.math.random.LocalRandom
import kotlin.math.*

class BeaconHaloRenderer(ctx: BlockEntityRendererFactory.Context?) : BeaconBlockEntityRenderer(ctx) {
    override fun render(
        entity: BeaconBlockEntity, tickDelta: Float, matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int
    ) {
        val segments = entity.beamSegments.ifEmpty { return }
        val world = entity.world ?: return
        val rand = LocalRandom(seed(entity))
        fun ring(r:Float,cycleTicks:Int,color:ArgbFloat,height:Float,thickness:Float){
            val rotation = run {
                if(cycleTicks == 0) 0.0
                else {
                    val abs = cycleTicks.absoluteValue
                    val mod = world.time % cycleTicks + tickDelta
                    val rad = mod / abs * 2 * PI
                    if(cycleTicks > 0) rad
                    else 2*PI - rad
                }
            }.toFloat()
            val angleCount = run {
                val cameraPos = MinecraftClient.getInstance().gameRenderer.camera.pos
                val entityPos = entity.pos.run { Vec3d(x+0.5,y+0.5,z+0.5) }
                val distance = entityPos.add(0.0, height.toDouble(), 0.0).distanceTo(cameraPos)
                if(distance <= (height + r)) r.toInt()
                else max(10,((height + r)*r / distance).toInt())
            }
            matrices.stack {
                matrices.translate(0.5, rand.nextDouble() + height, 0.5)
                matrices.multiply(Quaternion(Vec3f.POSITIVE_Y,rotation,false))
                renderHorizontalCircleRing(matrices, vertexConsumers, r,thickness,angleCount,cycleTicks < 0){color}
            }
        }
        val levelShrink = entity.levelShrink
        fun color(index:Int,mixWhite:Float = 0.3f):ArgbFloat{
            var sum = 0
            segments.forEach {
                sum += it.height
                if(sum > (index + levelShrink)) return ArgbFloat(it.color).mix(white,mixWhite)
            }
            return ArgbFloat(segments.last().color).mix(white,mixWhite)
        }
        when(entity.level - levelShrink){
            0 -> {}
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
        super.render(entity, tickDelta, matrices, vertexConsumers, light, overlay)
    }

    override fun getRenderDistance() =  Int.MAX_VALUE
    override fun isInRenderDistance(beaconBlockEntity: BeaconBlockEntity?, vec3d: Vec3d?): Boolean {
        return beaconBlockEntity?.isRemoved == false
    }




    companion object {
        inline fun MatrixStack.stack(block:()->Unit){ push();block();pop() }
        val BeaconBlockEntity.levelShrink : Int get() {
            var shrink = 0
            val world = world ?: return 0
            val pos = pos ?: return 0
            val level = level
            while(shrink < level){
                val block = world.getBlockState(pos.add(0,shrink + 1,0))
                val shrinkLevel = block.isOf(Blocks.GLASS) || block.isOf(Blocks.GLASS_PANE)
                if(shrinkLevel) shrink++ else break
            }
            return shrink
        }
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
            class Scope(val consumer: VertexConsumer,val modelMatrix: Matrix4f){
                fun AngleInfo.vertex(radius:Float,y:Float = 0f) = vertex(consumer,modelMatrix,radius,y)
                fun AngleInfo.vertex2(radius:Float,y:Float = 0f) = repeat(2) { vertex(consumer, modelMatrix, radius, y) }
            }
            fun vertex(consumer: VertexConsumer,modelMatrix: Matrix4f,radius:Float,y:Float){
                consumer.vertex(modelMatrix,radius*cos,y,radius*sin).color(color).next()
            }
        }
        fun renderHorizontalCircleRing(
            matrices: MatrixStack, consumerProvider: VertexConsumerProvider,
            radius: Float, thickness: Float, segmentCount:Int, reverseRotation:Boolean,
            colorBy0to1: (Double) -> ArgbFloat
        ) {
            val consumer = consumerProvider.getBuffer(RenderLayer.getBeaconBeam(texture,true))
            val modelMatrix = matrices.peek().positionMatrix
            val radiusInner = radius - thickness/2
            val radiusOuter = radius + thickness/2
            val viewHeight = thickness * 2 / 3


            val angles = (0..segmentCount).map {
                2 * PI * it / segmentCount
            }.map {
                val cos = cos(it).toFloat()
                val sin = sin(it).toFloat()
                val rawAlpha = if(reverseRotation) 1 + (it - 2*PI)*2/PI else 1 - it*2/PI
                val alpha = max(0.33f,rawAlpha.toFloat())
                val color = colorBy0to1(it / (2*PI)).alpha(alpha).toInt()
                AngleInfo(cos,sin,color)
            }
            AngleInfo.Scope(consumer,modelMatrix).run {
                angles.firstOrNull()?.vertex2(radiusInner)
                angles.forEach {
                    it.vertex(radiusInner)
                    it.vertex(radiusOuter)
                }
                angles.forEach {
                    it.vertex(radiusOuter)
                    it.vertex(radius, viewHeight)
                }
                angles.forEach {
                    it.vertex(radius, viewHeight)
                    it.vertex(radiusInner)
                }
                angles.lastOrNull()?.vertex2(radiusInner)
            }
        }
    }
}