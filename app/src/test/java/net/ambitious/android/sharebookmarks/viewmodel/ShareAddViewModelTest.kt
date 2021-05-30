package net.ambitious.android.sharebookmarks.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import net.ambitious.android.sharebookmarks.data.local.item.Item
import net.ambitious.android.sharebookmarks.data.local.item.ItemDao
import net.ambitious.android.sharebookmarks.ui.shareadd.ShareAddViewModel
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito.mock

class ShareAddViewModelTest {
  @get:Rule
  val rule: TestRule = InstantTaskExecutorRule()

  private lateinit var viewModel: ShareAddViewModel

  @Before
  fun setUp() {
    val itemDaoMock: ItemDao =
      object : ItemDao by mock(ItemDao::class.java) {
        override suspend fun getFolderItems(selfId: Long) = listOf(
          Item(0, 0, 0, "test", null, null, 0, 0, 0)
        )

        override suspend fun insert(items: Item) = INSERTED_ID
      }
    viewModel = ShareAddViewModel(itemDaoMock)
  }

  @Test
  fun fetchFolders() {
    MatcherAssert.assertThat(viewModel.folders.size, `is`(2))
  }

  @Test
  fun insertItem() {
    val itemName = "itemName"
    val itemUrl = "itemUrl"

    val observer = TestObserver<Triple<Long, String, String>>()
    viewModel.postResult.observeForever(observer)
    viewModel.insertItem(itemName, itemUrl, 5)
    observer.await()

    val result = observer.get()!!

    MatcherAssert.assertThat(result.first, `is`(INSERTED_ID))
    MatcherAssert.assertThat(result.second, `is`(itemName))
    MatcherAssert.assertThat(result.third, `is`(itemUrl))
  }

  companion object {
    private const val INSERTED_ID = 10L
  }
}