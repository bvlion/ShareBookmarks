package net.ambitious.android.sharebookmarks.util

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is.`is`
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class StringConvertTest {
  @get:Rule
  val rule: TestRule = InstantTaskExecutorRule()

  @Test
  fun hiraganaToKatakanaTest() {
    MatcherAssert.assertThat(
      OperationUtils.hiraganaToKatakana("ぁあぃいぅうぇえぉおかがきぎくぐけげこごさざしじすずせぜそぞ"),
      `is`("ァアィイゥウェエォオカガキギクグケゲコゴサザシジスズセゼソゾ")
    )

    MatcherAssert.assertThat(
      OperationUtils.hiraganaToKatakana("ただちぢっつづてでとどなにぬねのはばぱひびぴふぶぷへべぺほぼぽ"),
      `is`("タダチヂッツヅテデトドナニヌネノハバパヒビピフブプヘベペホボポ")
    )

    MatcherAssert.assertThat(
      OperationUtils.hiraganaToKatakana("まみむめもゃやゅゆょよらりるれろゎわをん"),
      `is`("マミムメモャヤュユョヨラリルレロヮワヲン")
    )
  }

  @Test
  fun hiraganaToKatakanaHanTest() {
    MatcherAssert.assertThat(
      OperationUtils.kanaZenToHan("ぁあぃいぅうぇえぉおかがきぎくぐけげこごさざしじすずせぜそぞ"),
      `is`("ｧｱｨｲｩｳｪｴｫｵｶｶﾞｷｷﾞｸｸﾞｹｹﾞｺｺﾞｻｻﾞｼｼﾞｽｽﾞｾｾﾞｿｿﾞ")
    )

    MatcherAssert.assertThat(
      OperationUtils.kanaZenToHan("ただちぢっつづてでとどなにぬねのはばぱひびぴふぶぷへべぺほぼぽ"),
      `is`("ﾀﾀﾞﾁﾁﾞｯﾂﾂﾞﾃﾃﾞﾄﾄﾞﾅﾆﾇﾈﾉﾊﾊﾞﾊﾟﾋﾋﾞﾋﾟﾌﾌﾞﾌﾟﾍﾍﾞﾍﾟﾎﾎﾞﾎﾟ")
    )

    MatcherAssert.assertThat(
      OperationUtils.kanaZenToHan("まみむめもゃやゅゆょよらりるれろゎわをん"),
      `is`("ﾏﾐﾑﾒﾓｬﾔｭﾕｮﾖﾗﾘﾙﾚﾛﾜﾜｦﾝ")
    )
  }

  @Test
  fun mixTest() {
    MatcherAssert.assertThat(
      OperationUtils.hiraganaToKatakana("テストてすとﾃｽﾄ"),
      `is`("テストテストﾃｽﾄ")
    )

    MatcherAssert.assertThat(
      OperationUtils.kanaZenToHan("テストてすとﾃｽﾄ"),
      `is`("ﾃｽﾄﾃｽﾄﾃｽﾄ")
    )
  }
}