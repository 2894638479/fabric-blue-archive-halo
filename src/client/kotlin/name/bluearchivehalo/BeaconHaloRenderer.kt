package name.bluearchivehalo

import com.google.common.primitives.Floats.max
import name.bluearchivehalo.mixin.BeaconLevelGetter
import net.minecraft.block.entity.BeaconBlockEntity
import net.minecraft.client.render.*
import net.minecraft.client.render.VertexFormat.DrawMode
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
        val fullBright = LightmapTextureManager.pack(15, 15)
        val renderLayer = MyMultiPhase.myLayer()
        val vertexConsumer = vertexConsumers.getBuffer(renderLayer)
        val rand = LocalRandom(seed(entity))
        val cycleTicks = 400
        val rotation = ((world.time % cycleTicks + rand.nextInt(cycleTicks) + tickDelta) * 2 * PI / cycleTicks).toFloat()
        val split = splitSegments(segments.map { it.color.run {
            val color = ArgbFloat(1f,get(0),get(1),get(2)).mix(ArgbFloat.white,0.2f)
            Segment(color,it.height)
        } },20,1)
        val parse = parseSegments(split[0])
        matrices.stack {
            matrices.translate(0.5, 200.0, 0.5)
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation(rotation))
            renderHorizontalCircleRing(matrices, vertexConsumer, 200f, 1f, fullBright, OverlayTexture.DEFAULT_UV,parse)
        }
    }




    companion object {
        class Segment(val color:ArgbFloat,val height:Int){
            fun changeHeight(newHeight:Int) = Segment(color,newHeight)
        }
        fun splitSegments(segmentsList:List<Segment>,perHeight:Int,count:Int):List<List<Segment>>{
            val segments = segmentsList.toMutableList()
            segments.ifEmpty { return listOf() }
            //信标本身去除
            val first = segments[0]
            if(first.height <= 1) segments.removeAt(0)
            else segments[0] = first.changeHeight(first.height - 1)


            var totalH = 0
            val result = mutableListOf<List<Segment>>()
            val subList = mutableListOf<Segment>()
            var curIndex = 0
            while(curIndex in segments.indices){
                val cur = segments[curIndex]
                (totalH + cur.height).let {
                    if(it < perHeight){
                        curIndex++
                        totalH = it
                        subList += cur
                    } else if(it == perHeight) {
                        curIndex++
                        totalH = 0
                        subList += cur
                        result += subList.toList()
                        subList.clear()
                    } else if(it > perHeight) {
                        val consume = perHeight - totalH
                        segments[curIndex] = cur.changeHeight(cur.height - consume)
                        totalH = 0
                        subList += cur.changeHeight(consume)
                        result += subList.toList()
                        subList.clear()
                    }
                }
            }
            if(subList.isNotEmpty()){
                val remain = perHeight - subList.sumOf { it.height }
                if(remain > 0) subList[subList.lastIndex] = subList.last().run { Segment(color, height + remain) }
                result += subList
            }
            if(result.size >= count) return result.take(count)
            val segment = Segment(result.last().last().color,perHeight)
            val list = listOf(segment)
            return result + List(count - result.size){ list }
        }
        fun parseSegments(segments:List<Segment>):(Double)->ArgbFloat {
            val total = segments.sumOf { it.height }
            fun result(it:Double):ArgbFloat{
                if (segments.size == 1) return segments.first().color
                val place = it * total
                var prevNode = 0
                var prevColor = segments.last().color
                segments.forEach {
                    val nextNode = prevNode + it.height
                    if(place < nextNode) {
                        val offset = place - prevNode
                        if(offset < 0.5f) return prevColor.mix(it.color,0.5f + offset.toFloat())
                        if(nextNode - place > 0.5f) return it.color
                    }
                    prevNode = nextNode
                    prevColor = it.color
                }
                return segments.last().color.mix(segments.first().color,(place - total + 0.5f).toFloat())
            }
            return ::result
        }

        inline fun MatrixStack.stack(block:()->Unit){ push();block();pop() }
        val BeaconBlockEntity.level get() = (this as BeaconLevelGetter).level
        fun seed(entity: BeaconBlockEntity) = entity.level * 9439L + entity.pos.run { (x*31+y)*31+z }
        class ArgbFloat(val a:Float,val r:Float,val g:Float,val b:Float){
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
            light: Int, overlay: Int,colorBy0to1:(Double)->ArgbFloat
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
                val cycle = (radius*it / thickness).toFloat()
                val alpha = max(0.33f,1f - (it*2/PI).toFloat())
                AngleInfo(radiusInner*cos,radiusOuter*cos,radiusInner*sin,radiusOuter*sin,
                    0f,1f,cycle,cycle,colorBy0to1(it / (2*PI)).alpha(alpha).toInt())
            }.forEach {
                it.vertexInner(consumer,modelMatrix, light, overlay,-1f)
                it.vertexOuter(consumer, modelMatrix, light, overlay,-1f)
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
                fun myLayer():RenderLayer{
                    val par = Phases().apply {
                        program = BEACON_BEAM_PROGRAM
//                        texture = Texture(BEAM_TEXTURE, false,true)
                        writeMaskState = ALL_MASK
                        cull = DISABLE_CULLING
                        transparency = TRANSLUCENT_TRANSPARENCY
                    }
                    return MyMultiPhase("beacon_halo", VertexFormats.POSITION_COLOR_LIGHT,
                        DrawMode.TRIANGLE_STRIP, 2097152, false, false,par)
                }
            }
        }
    }
}