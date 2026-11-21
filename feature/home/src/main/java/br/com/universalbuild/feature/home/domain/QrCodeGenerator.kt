package br.com.universalbuild.feature.home.domain

/** Binary raster independent of Android types, so encoding can be unit-tested. */
data class QrMatrix(val size: Int, val pixels: BooleanArray) {
    init {
        require(size > 0 && pixels.size == size * size)
    }
}

interface QrCodeEncoder {
    fun encode(payload: String, size: Int): QrMatrix
}

class GenerateQrCodeUseCase(private val encoder: QrCodeEncoder) {
    operator fun invoke(value: String): QrMatrix {
        val text = value.trim()
        require(text.isNotEmpty()) { "empty" }
        require(text.toByteArray(Charsets.UTF_8).size <= MAX_INPUT_BYTES) { "too_long" }
        return encoder.encode(text, DEFAULT_SIZE)
    }

    companion object {
        const val MAX_INPUT_BYTES = 1024
        const val DEFAULT_SIZE = 512
    }
}
