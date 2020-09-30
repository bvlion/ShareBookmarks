package net.ambitious.android.sharebookmarks.util

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.runBlocking
import net.ambitious.android.sharebookmarks.data.remote.etc.EtcApi
import net.ambitious.android.sharebookmarks.data.remote.etc.OgpEntity
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.startsWith
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito

class OperationUtilsUnitTest {
  @get:Rule
  val rule: TestRule = InstantTaskExecutorRule()

  private lateinit var etcApi: EtcApi

  @Before
  fun setUp() {
    etcApi = object : EtcApi by Mockito.mock(EtcApi::class.java) {
      override suspend fun getOgpImageUrl(url: String) = OgpEntity("test")
    }
  }

  @Test
  fun createThumbnailUrlTest() {
    // favicon 取得 URL 生成
    assertThat(
        OperationUtils.createThumbnailUrl("https://bvlion-app.firebaseapp.com"),
        `is`("${Const.GOOGLE_FAVICON_URL}bvlion-app.firebaseapp.com")
    )

    // null
    assertNull(OperationUtils.createThumbnailUrl(null))

    // URL 形式でない
    assertNull(OperationUtils.createThumbnailUrl("test"))

    // URL スキーム
    OperationUtils.createThumbnailUrl("slack://channel?team=T12345&id=Cabcde")?.let {
      assertThat(it, `is`(startsWith("https://")))
      assertThat(it, `is`(containsString("slack.com")))
    }
  }

  @Test
  fun getOgpImageTest() {
    runBlocking {
      // OGP 取得
      assertThat(
          OperationUtils.getOgpImage("https://www.ambitious-i.net", etcApi),
          `is`("https://www.ambitious-i.net/img/main.jpg")
      )

      // URL 形式でない
      assertNull(OperationUtils.getOgpImage("test", etcApi))

      // OGP がない
      assertNull(OperationUtils.getOgpImage("https://bvlion-app.firebaseapp.com", etcApi))

      // URL スキーム
      OperationUtils.getOgpImage("slack://channel?team=T12345&id=Cabcde", etcApi)?.let {
        assertThat(it, `is`(startsWith("https://")))
        assertThat(it, `is`(containsString("slack")))
      }

      // 非 SSL（Android であれば etcApi が通信するが Unit Test では通らないためコメントアウト）
//      assertThat(
//          OperationUtils.getOgpImage("http://www.ambitious-i.net", etcApi),
//          `is`("test")
//      )
    }
  }
}
