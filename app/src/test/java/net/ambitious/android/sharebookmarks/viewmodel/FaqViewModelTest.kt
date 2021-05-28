package net.ambitious.android.sharebookmarks.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import net.ambitious.android.sharebookmarks.data.remote.etc.EtcApi
import net.ambitious.android.sharebookmarks.data.remote.etc.FaqEntity
import net.ambitious.android.sharebookmarks.ui.faq.FaqViewModel
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito.mock

class FaqViewModelTest {
  @get:Rule
  val rule: TestRule = InstantTaskExecutorRule()

  private lateinit var viewModel: FaqViewModel

  private val entity = FaqEntity(
    listOf(
      FaqEntity.FaqDetail(
        "question",
        "answer"
      )
    )
  )

  @Before
  fun setUp() {
    val etcApiMock: EtcApi =
      object : EtcApi by mock(EtcApi::class.java) {
        override suspend fun getFaq(lang: String) = entity
      }
    viewModel = FaqViewModel(etcApiMock)
  }

  @Test
  fun fetchFaq() {
    val observer = TestObserver<FaqEntity>()
    viewModel.faq.observeForever(observer)
    observer.await()

    val faqData = observer.get()!!

    assertThat(
      faqData.faq.size,
      `is`(1)
    )

    val faq = entity.faq[0]

    faqData.faq.forEach {
      assertThat(it.question, `is`(faq.question))
      assertThat(it.answer, `is`(faq.answer))
    }
  }
}

