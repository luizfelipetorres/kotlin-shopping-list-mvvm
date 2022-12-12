package com.lftf.shoppinglist.utils

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class PriceTest {

    @Test
    fun `given String price int parse to float price`() {
        assertThat("R$ 1,00".parsePrice(), `is`(equalTo(1f)))
    }

    @Test
    fun `given String price cents parse to float price`() {
        assertThat("R$ 0,33".parsePrice(), `is`(equalTo(0.33f)))
    }

    @Test
    fun `given String price more than one thousand parse to float price`() {
        assertThat("R$ 4.354,00".parsePrice(), `is`(equalTo(4_354f)))
    }
}