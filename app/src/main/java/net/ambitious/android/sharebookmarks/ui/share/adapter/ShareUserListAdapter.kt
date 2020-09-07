package net.ambitious.android.sharebookmarks.ui.share.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.R.layout
import net.ambitious.android.sharebookmarks.data.local.contact.Contact
import net.ambitious.android.sharebookmarks.data.local.share.Share
import net.ambitious.android.sharebookmarks.util.PreferencesUtils

class ShareUserListAdapter(
  private val context: Context,
  private val preferences: PreferencesUtils.Data
) : Adapter<ViewHolder>() {

  private val _shares = arrayListOf<Share>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = UserViewHolder(
      LayoutInflater.from(parent.context)
          .inflate(layout.row_share_user, parent, false)
  )

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    (holder as UserViewHolder).apply {
      when (position) {
        0 -> {
          Glide.with(context)
              .load(preferences.userIcon)
              .placeholder(R.drawable.ic_person_add)
              .circleCrop()
              .into(userImage)
          userNameEdit.isVisible = false
          userNameArea.isVisible = true
          userName.text = preferences.userName
          userEmail.text = preferences.userEmail
          userDelete.isVisible = false
        }
        itemCount - 1 -> {
          Glide.with(context)
              .load(R.drawable.ic_person_add)
              .circleCrop()
              .into(userImage)
          userNameEdit.isVisible = true
          userNameEdit.setAdapter(
              ContactUserArrayAdapter(
                  context,
                  arrayListOf() // TODO
              )
          )
          userNameArea.isVisible = false
          userDelete.isVisible = false
        }
        else -> {
          Glide.with(context)
              .load(_shares[position + 1].userIcon)
              .circleCrop()
              .into(userImage)
          userNameEdit.isVisible = false
          userNameArea.isVisible = true
          userName.text = _shares[position + 1].userName ?: _shares[position + 1].userEmail
          userEmail.text = _shares[position + 1].userEmail
          userEmail.isVisible = _shares[position + 1].userName != null
          userDelete.isVisible = true
        }
      }
    }
  }

  override fun getItemCount() = _shares.size + 2

  fun setShares(shares: List<Share>) {
    _shares.clear()
    _shares.addAll(shares)
    notifyDataSetChanged()
  }

  class UserViewHolder internal constructor(view: View) :
      ViewHolder(view) {
    val userImage = view.findViewById(R.id.share_user_image) as AppCompatImageView
    val userNameEdit = view.findViewById(R.id.share_user_edit) as MaterialAutoCompleteTextView
    val userNameArea = view.findViewById(R.id.share_display) as LinearLayout
    val userName = view.findViewById(R.id.share_user_name) as AppCompatTextView
    val userEmail = view.findViewById(R.id.share_user_email) as AppCompatTextView
    val userDelete = view.findViewById(R.id.share_user_delete) as AppCompatImageView
  }
}