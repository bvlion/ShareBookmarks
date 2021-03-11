package net.ambitious.android.sharebookmarks.data.remote.etc

import androidx.annotation.Keep

@Keep
data class FaqEntity(val faq: List<FaqDetail>) {
  data class FaqDetail(
    val question: String,
    val answer: String
  )
}