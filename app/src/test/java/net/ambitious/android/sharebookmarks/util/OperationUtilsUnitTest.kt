package net.ambitious.android.sharebookmarks.util

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.startsWith
import org.hamcrest.core.Is.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.junit.rules.TestRule

class OperationUtilsUnitTest {
  @get:Rule
  val rule: TestRule = InstantTaskExecutorRule()

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
          OperationUtils.getOgpImage("https://www.ambitious-i.net"),
          `is`("https://www.ambitious-i.net/img/main.jpg")
      )

      // URL 形式でない
      assertNull(OperationUtils.getOgpImage("test"))

      // OGP がない
      assertNull(OperationUtils.getOgpImage("https://bvlion-app.firebaseapp.com"))

      // URL スキーム
      OperationUtils.getOgpImage("slack://channel?team=T12345&id=Cabcde")?.let {
        assertThat(it, `is`(startsWith("https://")))
        assertThat(it, `is`(containsString("slack")))
      }
    }
  }
}
