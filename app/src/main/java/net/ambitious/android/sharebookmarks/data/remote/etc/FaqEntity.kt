package net.ambitious.android.sharebookmarks.data.remote.etc

data class FaqEntity(
  val faq: List<FaqDetailEntity>
) {
  data class FaqDetailEntity(
    val question: String,
    val answer: String
  )
}