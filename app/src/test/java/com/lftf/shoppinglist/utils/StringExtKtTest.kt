package com.lftf.shoppinglist.utils

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class StringExtKtTest {

    @Test
    fun `given String price int parse to float price`() {
        val input = "R$ 1,00"
        assertThat(input.parsePrice(), `is`(equalTo(1f)))
    }

    @Test
    fun `given String price cents parse to float price`() {
        val input = "R$ 0,33"
        assertThat(input.parsePrice(), `is`(equalTo(0.33f)))
    }

    @Test
    fun `given String price more than one thousand parse to float price`() {
        val input = "R$ 4.354,00"
        assertThat(input.parsePrice(), `is`(equalTo(4_354f)))
    }

    @Test
    fun `given String with no numbers return 0f`() {
        val input = "abc"
        val result = input.parsePrice()
        assertThat(result, `is`(equalTo(0f)))
    }

    @Test
    fun `given String with no spaces parse float price`() {
        val input = "R$1,00"
        val result = input.parsePrice()
        assertThat(result, `is`(equalTo(1f)))
    }

    @Test
    fun `given String with dot parse float price`() {
        val input = "R$ 1.000.00"
        val result = input.parsePrice()
        assertThat(result, `is`(equalTo(1_000f)))
    }

}