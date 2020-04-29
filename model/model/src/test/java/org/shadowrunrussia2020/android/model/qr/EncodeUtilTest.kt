package org.shadowrunrussia2020.android.model.qr

import org.junit.Test
import org.shadowrunrussia2020.android.common.models.Data
import org.shadowrunrussia2020.android.common.models.Type
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EncodeUtilTest {
    @Test
    fun decode_ThrowsFormatExceptionIfTooShort() {
        assertFailsWith(org.shadowrunrussia2020.android.model.qr.FormatException::class) {
            org.shadowrunrussia2020.android.model.qr.decode("6263Aw2A2Nt")
        }
    }

    @Test
    fun decode_ChangingAnySymbolFailsSignatureCheck() {
        val validString = "92c2EQ1ybkhZHello"
        for (i in 0 until validString.length) {
            assertFailsWith(org.shadowrunrussia2020.android.model.qr.ValidationException::class, message = i.toString()) {
                val invalidString = validString.slice(IntRange(0, i - 1)) + "B" + validString.slice(
                    IntRange(
                        i + 1,
                        validString.length - 1
                    )
                )
                org.shadowrunrussia2020.android.model.qr.decode(invalidString)
            }
        }
    }

    @Test
    fun decode_InvalidSymbolInSignatureLeadToValidationError() {
        assertFailsWith(org.shadowrunrussia2020.android.model.qr.ValidationException::class) {
            org.shadowrunrussia2020.android.model.qr.decode("X263Aw2A2NtwHello") // Symbol X is not hex symbol
        }
    }

    @Test
    fun decode_InvalidSymbolsInHeaderLeadToException() {
        val validString = "6263Aw2A2NtwHello"
        for (i in 4 until 4 + 8) {
            assertFailsWith(org.shadowrunrussia2020.android.model.qr.FormatException::class) {
                val invalidString = validString.slice(IntRange(0, i - 1)) + "?" + validString.slice(
                    IntRange(
                        i + 1,
                        validString.length - 1
                    )
                )
                org.shadowrunrussia2020.android.model.qr.decode(invalidString)
            }
        }
    }

    @Test
    fun decode_TimeUntilInPastLeadsToException() {
        assertFailsWith(org.shadowrunrussia2020.android.model.qr.ExpiredException::class) {
            org.shadowrunrussia2020.android.model.qr.decode("d810Aw1ybkhZHello")
        }
    }

    @Test
    fun decode_IsCorrect() {
        assertEquals(
            org.shadowrunrussia2020.android.model.qr.Data(
                org.shadowrunrussia2020.android.model.qr.Type.PAYMENT_REQUEST_WITH_PRICE,
                13,
                1893456000,
                "Hello"
            ),
            org.shadowrunrussia2020.android.model.qr.decode("6263Aw2A2NtwHello")
        )
    }

    @Test
    fun decode_IsCorrect2() {
        assertEquals(
            org.shadowrunrussia2020.android.model.qr.Data(
                org.shadowrunrussia2020.android.model.qr.Type.CLINICALLY_DEAD_BODY,
                0,
                1700000000,
                "123,1267,abc"
            ),
            org.shadowrunrussia2020.android.model.qr.decode("ca7fBwAA8VNl123,1267,abc")
        )
    }

    @Test
    fun encode_IsCorrect() {
        assertEquals(
            "6263Aw2A2NtwHello",
            org.shadowrunrussia2020.android.model.qr.encode(
                org.shadowrunrussia2020.android.model.qr.Data(
                    org.shadowrunrussia2020.android.model.qr.Type.PAYMENT_REQUEST_WITH_PRICE,
                    13,
                    1893456000,
                    "Hello"
                )
            )
        )
    }

    @Test
    fun encode_WorksWithCyrillicCharacters() {
        val data = org.shadowrunrussia2020.android.model.qr.Data(
            org.shadowrunrussia2020.android.model.qr.Type.REWRITABLE,
            13,
            1893456000,
            "Рыба"
        )
        assertEquals(
            org.shadowrunrussia2020.android.model.qr.decode(
                org.shadowrunrussia2020.android.model.qr.encode(data)
            ), data)
    }
}
