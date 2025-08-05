package name.bluearchivehalo

import name.bluearchivehalo.BeaconHaloRenderer.Companion.ArgbFloat.Companion.white
import name.bluearchivehalo.BlueArchiveHaloClient.texture
import name.bluearchivehalo.config.Config
import name.bluearchivehalo.config.LevelConfig
import name.bluearchivehalo.config.RingStyle
import name.bluearchivehalo.config.RingStyle.Companion.FLAT
import name.bluearchivehalo.config.RingStyle.Companion.PULSE
import name.bluearchivehalo.config.RingStyle.Companion.SPACING
import name.bluearchivehalo.config.RingStyle.Companion.STATIC
import net.minecraft.block.Blocks
import net.minecraft.block.entity.BeaconBlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Matrix4f
import net.minecraft.util.math.Quaternion
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3f
import net.minecraft.world.gen.random.SimpleRandom
import kotlin.math.*

class BeaconHaloRenderer(ctx: BlockEntityRendererFactory.Context?) : BeaconBlockEntityRenderer(ctx) {
    override fun render(
        entity: BeaconBlockEntity, tickDelta: Float, matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider, light: Int, overlay: Int
    ) {
        val segments = entity.beamSegments.ifEmpty { return }
        val world = entity.world ?: return
        val rand = SimpleRandom(seed(entity))
        fun ring(r:Float,cycleTicks:Int,color:ArgbFloat,height:Float,thickness:Float,style: RingStyle){
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
            val colorBy0to1:(Double)-> ArgbFloat = when(style){
                PULSE -> {
                    val baseAlpha = Config.instance.baseAlpha.get
                    val pulseTail = Config.instance.pulseTail.get
                    val b = (1-baseAlpha)/pulseTail
                    {
                        val pulseAlpha = if(cycleTicks<0) 1-b*(1-it) else 1-b*it
                        val alpha = max(Config.instance.baseAlpha.get,pulseAlpha.toFloat())
                        color.alpha(alpha)
                    }
                }
                SPACING -> {
                    val spaceCount = Config.instance.spacingCount.get
                    val alpha1 = Config.instance.spacingMidAlpha.get
                    val alpha2 = Config.instance.spacingAlpha.get
                    {
                        val bl = ((it*spaceCount*2).toInt() % 2) != 0
                        color.alpha(if(bl) alpha2 else alpha1)
                    }
                }
                FLAT -> { color.alpha(Config.instance.baseAlpha.get).run{{this}} }
                STATIC -> {{color}}
                else -> return
            }
            matrices.stack {
                matrices.translate(0.5, rand.nextDouble() + height, 0.5)
                matrices.multiply(Quaternion(Vec3f.POSITIVE_Y,rotation,false))
                renderHorizontalCircleRing(matrices, vertexConsumers, r,thickness,angleCount,colorBy0to1)
            }
        }
        val levelShrink = entity.levelShrink
        fun color(index:Int):ArgbFloat{
            var sum = 0
            val mixWhite = Config.instance.mixWhite.get
            segments.forEach {
                sum += it.height
                if(sum > (index + levelShrink)) return ArgbFloat(it.color).mix(white,mixWhite)
            }
            return ArgbFloat(segments.last().color).mix(white,mixWhite)
        }
        val renderLevel = entity.level - levelShrink
        if (renderLevel > 0){
            val conf = Config.instance.levels.get[renderLevel] ?: LevelConfig(renderLevel)
            conf.rings.get.forEach {
                val colorFrom = (renderLevel - it.ringIndex)*conf.colorSpacing.get
                ring(it.radius.get,it.rotateCycle.get,color(colorFrom),conf.height.get,it.width.get,it.style.get)
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
            radius: Float, thickness: Float, segmentCount:Int,
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
                val color = colorBy0to1(it / (2*PI)).toInt()
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