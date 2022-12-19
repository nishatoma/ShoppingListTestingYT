package com.androiddevs.shoppinglisttestingyt.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.androiddevs.shoppinglisttestingyt.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// We are inside an Android env, we have
// to explicitly mention we are using JUNIT here.
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class ShoppingDaoTest {

    // Specify JUNIt we want to run all our tests
    // on same thread
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()


    // Create ref to room database
    private lateinit var database: ShoppingItemDatabase
    private lateinit var dao: ShoppingDao

    @Before
    fun setup() {
        // Hold in memory (RAM). Just for testing
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ShoppingItemDatabase::class.java
        ).allowMainThreadQueries() // Allow to access ROOM DB from mainthread also
            .build()
        dao = database.shoppingDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertShoppingItem() = runBlockingTest {
        val item = ShoppingItem("name", 1, 1f, "url", id = 1)
        dao.insertShoppingItem(item)
        // Read entries database
        // Returns live data, we dont want to run this async tho in our test case..
        // So, luckily, Google helps us here
        val list = dao.observeAllShoppingItems().getOrAwaitValue()

        assertThat(list).contains(item)
    }

    @Test
    fun deleteShoppingItem() = runBlockingTest {
        val item = ShoppingItem("name", 1, 1f, "url", id = 1)
        dao.insertShoppingItem(item)
        dao.deleteShoppingItem(item)
        // Read entries database
        // Returns live data, we dont want to run this async tho in our test case..
        // So, luckily, Google helps us here
        val list = dao.observeAllShoppingItems().getOrAwaitValue()
        assertThat(list).doesNotContain(item)
        assertThat(list).isEmpty()
    }

    @Test
    fun observeTotalPriceSum() = runBlockingTest {
        val item = ShoppingItem("name", 5, 100f, "url", id = 1)
        val item2 = ShoppingItem("name", 5, 2f, "url", id = 2)
        val item3 = ShoppingItem("name", 5, 3f, "url", id = 3)
        // insert all 3
        dao.insertShoppingItem(item)
        dao.insertShoppingItem(item2)
        dao.insertShoppingItem(item3)
        // Get Sum
        assertThat(dao.observeTotalPrice()).isEqualTo(525f)
    }
}