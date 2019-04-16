package org.shadowrunrussia2020.android.qr

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UtilTest {
    @Test
    fun decode_ThrowsFormatExceptionIfTooShort() {
        assertFailsWith(FormatException::class) {
            decode("EQ1ybkhZksI")
        }
    }

    @Test
    fun decode_ChangingAnySymbolFailsSignatureCheck() {
        val validString = "92c2EQ1ybkhZHello"
        for (i in 0 until validString.length) {
            assertFailsWith(ValidationException::class) {
                val invalidString = validString.slice(IntRange(0, i - 1)) + "A" + validString.slice(
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
            decode("X2c2EQ1ybkhZHello") // Symbol X is not hex symbol
        }
    }

    @Test
    fun decode_InvalidSymbolsInHeaderLeadToException() {
        val validString = "92c2EQ1ybkhZHello"
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
    fun decode_IsCorrect() {
        assertEquals(
            Data(Type.CRAFTED_ITEM, 13, 1497919090, "Hello"),
            decode("d810Aw1ybkhZHello"))
    }

    @Test
    fun encode_IsCorrect() {
        assertEquals(
            "d810Aw1ybkhZHello",
            encode(Data(Type.CRAFTED_ITEM, 13, 1497919090, "Hello")))
    }
}
