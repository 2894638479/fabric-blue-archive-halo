package name.bluearchivehalo.render

import io.github.u2894638479.kotlinmcui.math.Color
import name.bluearchivehalo.BlueArchiveHaloClient
import name.bluearchivehalo.config.Config
import name.bluearchivehalo.config.RingStyle
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BeaconBlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.RotationAxis
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.random.LocalRandom
import org.joml.Matrix4f
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

class BeaconHaloRenderer(ctx: BlockEntityRendererFactory.Context?) : BeaconBlockEntityRenderer(ctx) {
    override fun render(
        entity: BeaconBlockEntity, tickDelta: Float, matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int
    ) {
        val segments = entity.beamSegments.ifEmpty { return }
        val world = entity.world ?: return
        val rand = LocalRandom(seed(entity))
        fun ring(r: Double, cycleTicks:Int, color: Color, height: Double, thickness: Double, style: RingStyle){
            val rotation = run {
                if(cycleTicks == 0) 0.0
                else {
                    val abs = cycleTicks.absoluteValue
                    val mod = world.time % cycleTicks + tickDelta
                    val rad = mod / abs * 2 * PI
                    if(cycleTicks > 0) rad
                    else 2* PI - rad
                }
            }.toFloat()
            val angleCount = run {
                val cameraPos = MinecraftClient.getInstance().gameRenderer.camera.pos
                val entityPos = entity.pos.toCenterPos()
                val distance = entityPos.add(0.0, height,0.0).distanceTo(cameraPos)
                if(distance <= (height + r)) r.toInt()
                else max(10, ((height + r) * r / distance).toInt())
            }
            val colorBy0to1:(Double)-> Color = { style.color(it,color) }
            matrices.stack {
                matrices.translate(0.5, rand.nextDouble() + height, 0.5)
                matrices.multiply(RotationAxis.POSITIVE_Y.rotation(rotation))
                renderHorizontalCircleRing(matrices, vertexConsumers, r,thickness,angleCount,colorBy0to1)
            }
        }
        val levelShrink = entity.levelShrink
        fun color(index:Int): Color {
            var sum = 0
            fun FloatArray.toColor() = Color(get(0), get(1), get(2))
            segments.forEach {
                sum += it.height
                if(sum > (index + levelShrink)) return it.color.toColor()
            }
            return segments.last().color.toColor()
        }
        val renderLevel = entity.level - levelShrink
        if (renderLevel > 0){
            Config.instance.rings(renderLevel).forEachIndexed { i, it ->
                ring(it.radius,it.cycle,color(i),100.0,it.width,it.style)
            }
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

        class AngleInfo(
            val cos:Float,val sin:Float,val color: Color
        ){
            class Scope(val consumer: VertexConsumer, val modelMatrix: Matrix4f){
                fun AngleInfo.vertex(radius:Float,y:Float = 0f) = vertex(consumer,modelMatrix,radius,y)
                fun AngleInfo.vertex2(radius:Float,y:Float = 0f) = repeat(2) { vertex(consumer, modelMatrix, radius, y) }
            }
            fun vertex(consumer: VertexConsumer, modelMatrix: Matrix4f, radius:Float, y:Float){
                consumer.vertex(modelMatrix,radius*cos,y,radius*sin).color(color.argbInt).next()
            }
        }
        fun renderHorizontalCircleRing(
            matrices: MatrixStack, consumerProvider: VertexConsumerProvider,
            radius: Double, thickness: Double, segmentCount:Int,
            colorBy0to1: (Double) -> Color
        ) {
            val radius = radius.toFloat()
            val thickness = thickness.toFloat()
            val consumer = consumerProvider.getBuffer(RenderLayer.getBeaconBeam(BlueArchiveHaloClient.Companion.texture,true))
            val modelMatrix = matrices.peek().positionMatrix
            val radiusInner = radius - thickness/2
            val radiusOuter = radius + thickness/2
            val viewHeight = thickness * 2 / 3


            val angles = (0..segmentCount).map {
                2 * PI * it / segmentCount
            }.map {
                val cos = cos(it).toFloat()
                val sin = sin(it).toFloat()
                val color = colorBy0to1(it / (2* PI))
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