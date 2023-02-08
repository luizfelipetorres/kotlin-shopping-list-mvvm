package com.lftf.shoppinglist.viewmodel

import androidx.annotation.VisibleForTesting
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.lftf.shoppinglist.model.ItemModel
import com.lftf.shoppinglist.model.MoneyModel
import com.lftf.shoppinglist.repository.local.ItemRepository
import com.lftf.shoppinglist.repository.local.MoneyRepository
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class MainViewModelTest {

    @get:Rule
    var rule = InstantTaskExecutorRule()

    @RelaxedMockK
    private lateinit var itemMock: ItemRepository

    @RelaxedMockK
    private lateinit var moneyMock: MoneyRepository

    @RelaxedMockK
    private lateinit var mainViewModel: MainViewModel
    private val listOfItens = listOf<ItemModel>(
        ItemModel().apply {
            id = 1
            title = "item 1"
            price = 0f
            quantity = 1
        },
        ItemModel().apply {
            id = 2
            title = "item 2"
            price = 5f
            quantity = 1
        },
        ItemModel().apply {
            id = 3
            title = "item 2"
            price = 10f
            quantity = 2
        },
    )

    private val newItem = ItemModel().apply {
        title = "Novo item"
    }

    private val listOfMoney = listOf<MoneyModel>(
        MoneyModel().apply { limit = 100f; method = "Forma 1" })

    @Before
    fun setup() {
        itemMock = mockk<ItemRepository>()
        moneyMock = mockk<MoneyRepository>()
        every { itemMock.listAll() } returns listOfItens
        every { itemMock.save(newItem) } returns true
        every { itemMock.update(listOfItens[0]) } returns true
        every { moneyMock.listAll() } returns listOfMoney

        mainViewModel = MainViewModel(itemMock, moneyMock).also {
            it.getAll()
        }

    }


    @Test
    fun `when viewModel fetches list of itens then it should call the repository`() {
        val list = mainViewModel.list.getOrAwaitValue()
        assertThat(list, `is`(equalTo(listOfItens)))
    }

    @Test
    fun `when viewModel save an item then it should call the repository and return true`() {
        val result = mainViewModel.save(newItem)
        assertThat(result, `is`(equalTo(1)))
    }

    @Test
    fun `when viewModel update an item then it should call the repository and return true`() {
        val result = mainViewModel.save(listOfItens[0])
        assertThat(result, `is`(equalTo(0)))
    }
}


@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)

    try {
        afterObserve.invoke()

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }

    } finally {
        this.removeObserver(observer)
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}