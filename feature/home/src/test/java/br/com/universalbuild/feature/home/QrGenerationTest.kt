package br.com.universalbuild.feature.home

import br.com.universalbuild.feature.home.data.ZxingQrCodeEncoder
import br.com.universalbuild.feature.home.domain.GenerateQrCodeUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QrGenerationTest {
    private val generate = GenerateQrCodeUseCase(ZxingQrCodeEncoder())

    @Test
    fun generatesSquareMatrix() {
        val image = generate("https://example.com")
        assertEquals(512, image.size)
        assertEquals(512 * 512, image.pixels.size)
        assertTrue(image.pixels.any { it })
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsEmptyInput() {
        generate("   ")
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsOversizedInput() {
        generate("X".repeat(1025))
    }
}
