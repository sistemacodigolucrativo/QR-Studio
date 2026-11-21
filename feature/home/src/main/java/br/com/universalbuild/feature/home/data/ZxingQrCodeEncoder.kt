package br.com.universalbuild.feature.home.data

import br.com.universalbuild.feature.home.domain.QrCodeEncoder
import br.com.universalbuild.feature.home.domain.QrMatrix
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

class ZxingQrCodeEncoder : QrCodeEncoder {
    override fun encode(payload: String, size: Int): QrMatrix {
        val hints = mapOf(
            EncodeHintType.CHARACTER_SET to "UTF-8",
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
            EncodeHintType.MARGIN to 2
        )
        val matrix = MultiFormatWriter().encode(payload, BarcodeFormat.QR_CODE, size, size, hints)
        val pixels = BooleanArray(size * size) { index ->
            matrix[index % size, index / size]
        }
        return QrMatrix(size, pixels)
    }
}
