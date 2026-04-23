package io.github.u2894638479.bahalo.config

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import java.util.Base64
import java.util.zip.Deflater
import java.util.zip.Inflater

object ConfigStringProcessor {
    fun ByteArray.compressToB64(): String =
        Deflater(Deflater.BEST_COMPRESSION, true).run {
            setInput(this@compressToB64)
            finish()
            val buf = ByteArray(this@compressToB64.size + 32)
            val len = deflate(buf)
            end()
            buf.copyOfRange(0, len)
        }.toB64()
    private fun ByteArray.toB64() = Base64.getUrlEncoder().withoutPadding().encodeToString(this)
    inline fun <reified T> T.base64() = Json.encodeToString(this).toByteArray().compressToB64()

    fun decompress(base64String: String): String {
        val compressedData = Base64.getUrlDecoder().decode(base64String)
        val inflater = Inflater(true)
        inflater.setInput(compressedData)
        val bos = ByteArrayOutputStream(compressedData.size)
        val buf = ByteArray(1024)
        try {
            while (!inflater.finished()) {
                val count = inflater.inflate(buf)
                if (count == 0 && inflater.needsInput()) break
                bos.write(buf, 0, count)
            }
        } finally {
            inflater.end()
        }
        return bos.toString(Charsets.UTF_8.name())
    }
    inline fun <reified T> String.fromBase64() = try {
        val str = decompress(this)
        Json.decodeFromString<T>(str)
    } catch (e: Exception) { null }
}