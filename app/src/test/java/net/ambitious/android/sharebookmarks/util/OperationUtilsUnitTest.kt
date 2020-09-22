package net.ambitious.android.sharebookmarks.util

import net.ambitious.android.sharebookmarks.util.Const
import net.ambitious.android.sharebookmarks.util.OperationUtils
import org.hamcrest.core.Is.`is`
import org.junit.Test

import org.junit.Assert.*

class OperationUtilsUnitTest {
  @Test
  fun createThumbnailUrlTest() {
    // OGP 取得
    assertThat(
        OperationUtils.createThumbnailUrl("https://www.ambitious-i.net"),
        `is`("https://www.ambitious-i.net/img/main.jpg")
    )

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
    assertThat(
        OperationUtils.createThumbnailUrl("slack://channel?team=T12345&id=Cabcde"),
        `is`("https://a.slack-edge.com/68794/marketing/img/homepage/hp-prospect/unfurl/slack-homepage-unfurl.ja-JP.jpg")
    )
  }
}
