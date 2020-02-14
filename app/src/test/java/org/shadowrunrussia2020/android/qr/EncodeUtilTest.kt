package org.shadowrunrussia2020.android.qr

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EncodeUtilTest {
    @Test
    fun decode_ThrowsFormatExceptionIfTooShort() {
        assertFailsWith(FormatException::class) {
            decode("6263Aw2A2Nt")
        }
    }

    @Test
    fun decode_ChangingAnySymbolFailsSignatureCheck() {
        val validString = "92c2EQ1ybkhZHello"
        for (i in 0 until validString.length) {
            assertFailsWith(ValidationException::class, message = i.toString()) {
                val invalidString = validString.slice(IntRange(0, i - 1)) + "B" + validString.slice(
                    IntRange(
                        i + 1,
                        validString.length - 1
                    )
                )
                decode(invalidString)
            }
        }
    }

    @Test
    fun decode_InvalidSymbolInSignatureLeadToValidationError() {
        assertFailsWith(ValidationException::class) {
            decode("X263Aw2A2NtwHello") // Symbol X is not hex symbol
        }
    }

    @Test
    fun decode_InvalidSymbolsInHeaderLeadToException() {
        val validString = "6263Aw2A2NtwHello"
        for (i in 4 until 4 + 8) {
            assertFailsWith(FormatException::class) {
                val invalidString = validString.slice(IntRange(0, i - 1)) + "?" + validString.slice(
                    IntRange(
                        i + 1,
                        validString.length - 1
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
            Data(Type.CRAFTED_ITEM, 13, 1893456000, "Hello"),
            decode("6263Aw2A2NtwHello"))
    }

    @Test
    fun decode_IsCorrect2() {
        assertEquals(
            Data(Type.PAYMENT_REQUEST_WITH_PRICE, 0, 1700000000, "123,1267,abc"),
            decode("ca7fBwAA8VNl123,1267,abc"))
    }

    @Test
    fun encode_IsCorrect() {
        assertEquals(
            "6263Aw2A2NtwHello",
            encode(Data(Type.CRAFTED_ITEM, 13, 1893456000, "Hello")))
    }

    @Test
    fun encode_WorksWithCyrillicCharacters() {
        val data = Data(Type.CRAFTED_ITEM, 13, 1893456000, "Рыба")
        assertEquals(decode(encode(data)), data)
    }
}
