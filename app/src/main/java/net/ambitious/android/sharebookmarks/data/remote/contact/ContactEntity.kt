package net.ambitious.android.sharebookmarks.data.remote.contact

import androidx.annotation.Keep

@Keep
data class ContactEntity(
  val email: String,
  val text: String
)