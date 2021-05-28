package net.ambitious.android.sharebookmarks.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import net.ambitious.android.sharebookmarks.data.remote.etc.EtcApi
import net.ambitious.android.sharebookmarks.data.remote.etc.EtcEntity
import net.ambitious.android.sharebookmarks.ui.others.DetailViewModel
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito.mock

class DetailViewModelTest {
  @get:Rule
  val rule: TestRule = InstantTaskExecutorRule()

  private lateinit var viewModel: DetailViewModel

  @Before
  fun setUp() {
    val etcApiMock: EtcApi =
      object : EtcApi by mock(EtcApi::class.java) {
        override suspend fun getTermsOfUse() = EtcEntity(TERMS_OF_USE)
        override suspend fun getPrivacyPolicy() = EtcEntity(PRIVACY_POLICY)
      }
    viewModel = DetailViewModel(etcApiMock)
  }

  @Test
  fun fetchTermsOfUse() {
    val observer = TestObserver<String>()
    viewModel.message.observeForever(observer)
    viewModel.getDetailMessages(true)
    observer.await()

    val message = observer.get()!!

    assertThat(message, `is`(TERMS_OF_USE))
  }

  @Test
  fun fetchPrivacyPolicy() {
    val observer = TestObserver<String>()
    viewModel.message.observeForever(observer)
    viewModel.getDetailMessages(false)
    observer.await()

    val message = observer.get()!!

    assertThat(message, `is`(PRIVACY_POLICY))
  }

  companion object {
    private const val TERMS_OF_USE = "TermsOfUse"
    private const val PRIVACY_POLICY = "PrivacyPolicy"
  }
}

