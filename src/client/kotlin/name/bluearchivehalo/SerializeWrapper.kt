package name.bluearchivehalo

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.ClassSerialDescriptorBuilder
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.serializer


abstract class SerializerWrapper<T : Any,D:SerializerWrapper.Descriptor<T>>(name:String, val desc:D):KSerializer<T> {

    final override val descriptor = buildClassSerialDescriptor(name){
        desc.items.forEach {
            it.run { buildElement() }
        }
    }
    abstract class Descriptor<T:Any>{
        abstract class Item <T:Any,E:Any>(val name: String){
            abstract fun ClassSerialDescriptorBuilder.buildElement()
            abstract fun CompositeDecoder.deserialize(descriptor:SerialDescriptor, index:Int):E
            abstract fun CompositeEncoder.serialize(descriptor: SerialDescriptor, index:Int, value:T)

            //this logic can only run on single thread
            internal var _value:E? = null
            val nonNull get() = _value ?: error("required field $name is null")
            val nullable get() = _value
            fun orElse(fallback:E) = _value ?: fallback
            internal fun CompositeDecoder.setValueByDeserialize(descriptor:SerialDescriptor, index:Int) {_value = deserialize(descriptor, index)}
        }
        inline infix fun <reified E:Any> String.from(crossinline serializeValue:T.()->E?) = create(this,serializeValue)
        inline fun <reified E:Any> create(name:String, crossinline serializeValue:T.()->E?) = object : Item<T, E>(name) {
            override fun ClassSerialDescriptorBuilder.buildElement() {
                element<E>(name)
            }
            override fun CompositeDecoder.deserialize(descriptor:SerialDescriptor, index:Int)
                    = decodeSerializableElement(descriptor,index,serializer<E>())
            override fun CompositeEncoder.serialize(descriptor: SerialDescriptor, index:Int, value:T){
                serializeValue(value)?.let {
                    encodeSerializableElement(descriptor,index, serializer<E>(),it)
                }
            }
        }.apply { items += this }
        val items = mutableListOf<Item<T,*>>()
        fun clearValue() = items.forEach { it._value = null }
    }

    final override fun deserialize(decoder: Decoder): T {
        var t:T? = null
        decoder.decodeStructure(descriptor){
            desc.clearValue()
            while(true) {
                val index = decodeElementIndex(descriptor)
                if (index == CompositeDecoder.DECODE_DONE) break
                val item = desc.items.getOrNull(index) ?: error("unexpected index:$index")
                item.run { setValueByDeserialize(descriptor, index) }
            }
            t = desc.generate()
            desc.clearValue()
        }
        return t ?: error("deserialize failed")
    }

    final override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeStructure(descriptor){
            desc.items.forEachIndexed { index,it ->
                it.run { serialize(descriptor,index,value) }
            }
        }
    }
    abstract fun D.generate():T
}