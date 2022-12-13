package com.lftf.shoppinglist.utils

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.text.NumberFormat
import java.util.*

@RunWith(JUnit4::class)
class FloatExtKtTest {

    private lateinit var locale: Locale

    @Before
    fun setup() {
        locale = Locale("pt", "BR")
    }


    @Test
    fun `given a float parse String price`() {
        var input = 1f
        val expected = { param: Float -> NumberFormat.getCurrencyInstance(locale).format(param) }
        assertThat(input.formatPrice(), `is`(equalTo(expected(1f))))

        input = 3.50f
        assertThat(input.formatPrice(), `is`(expected(input)))

        input = 1_498.70f
        assertThat(input.formatPrice(), `is`(expected(input)))
    }
}