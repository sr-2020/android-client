package org.shadowrunrussia2020.android.model.qr

import org.junit.Test
import org.shadowrunrussia2020.android.common.models.QrType
import org.shadowrunrussia2020.android.common.models.SimpleQrData
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EncodeUtilTest {
    @Test
    fun decode_ThrowsFormatExceptionIfTooShort() {
        assertFailsWith(FormatException::class) {
            decode("EQ1ybkhZksI")
        }
    }

    val validContent = "92c2EQ1ybkhZHello"

    @Test
    fun decodable() {
        decodeNoTimeCheck(validContent)
    }


    @Test
    fun decode_ChangingAnySymbolFailsSignatureCheck() {
        for (i in validContent.indices) {
            assertFailsWith(ValidationException::class, message = i.toString()) {
                val invalidString = validContent.slice(IntRange(0, i - 1)) + "B" + validContent.slice(
                    IntRange(
                        i + 1,
                        validContent.length - 1
                    )
                )
                decode(invalidString)
            }
        }
    }

    @Test
    fun decode_InvalidSymbolInSignatureLeadToValidationError() {
        assertFailsWith(ValidationException::class) {
            decode("X2c2EQ1ybkhZHello") // Symbol X is not hex symbol
        }
    }

    @Test
    fun decode_InvalidSymbolsInHeaderLeadToException() {
        for (i in 4 until 4 + 8) {
            assertFailsWith(FormatException::class) {
                val invalidString = validContent.slice(IntRange(0, i - 1)) + "?" + validContent.slice(
                    IntRange(
                        i + 1,
                        validContent.length - 1
                    )
                )
                decode(invalidString)
            }
        }
    }

    @Test
    fun decode_TimeUntilInPastLeadsToException() {
        assertFailsWith(ExpiredException::class) {
            decode("d810Aw1ybkhZHello")
        }
    }

    @Test
    fun decode_IsCorrect() {
        assertEquals(
            SimpleQrData(
                QrType.PAYMENT_REQUEST_WITH_PRICE,
                13,
                1497919090,
                "Hello"
            ),
            decodeNoTimeCheck("d810Aw1ybkhZHello")
        )
    }

    @Test
    fun decode_IsCorrect2() {
        assertEquals(
            SimpleQrData(
                QrType.CLINICALLY_DEAD_BODY,
                0,
                1700000000,
                "123,1267,abc"
            ),
            decodeNoTimeCheck("5472BwAA8VNl123,1267,abc")
        )
    }

    @Test
    fun decode_IsCorrect3() {
        assertEquals(
            "178",
            decodeNoTimeCheck("9447AQDQvrZe178").payload
        )
    }

    @Test
    fun decode_IsCorrect4() {
        assertEquals(
            "178",
            decodeNoTimeCheck("c112AQCIyrZe178").payload
        )
    }

    @Test
    fun encode_IsCorrect() {
        assertEquals(
            "d810Aw1ybkhZHello",
            encode(
                SimpleQrData(
                    QrType.PAYMENT_REQUEST_WITH_PRICE,
                    13,
                    1497919090,
                    "Hello"
                )
            )
        )
    }

    @Test
    fun encode_IsCorrect2() {
        assertEquals(
            "5472BwAA8VNl123,1267,abc",
            encode(
                SimpleQrData(
                    QrType.CLINICALLY_DEAD_BODY,
                    0,
                    1700000000,
                    "123,1267,abc"
                )
            )
        )
    }

    @Test
    fun encode_WorksWithCyrillicCharacters() {
        val data = SimpleQrData(
            QrType.REWRITABLE,
            13,
            1893456000,
            "Рыба"
        )
        assertEquals(
            decode(
                encode(data)
            ), data)
    }
}
