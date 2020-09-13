package net.ambitious.android.sharebookmarks.ui.share.adapter

import android.content.Context
import android.graphics.Color
import android.util.Base64
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.R.layout
import net.ambitious.android.sharebookmarks.data.local.contact.Contact
import net.ambitious.android.sharebookmarks.data.local.share.Share
import net.ambitious.android.sharebookmarks.ui.InfoViewHolder
import net.ambitious.android.sharebookmarks.util.PreferencesUtils

class ShareUserListAdapter(
  private val context: Context,
  private val preferences: PreferencesUtils.Data,
  private val listener: OnUserCompleteListener
) : Adapter<ViewHolder>() {

  private val _shares = arrayListOf<Share>()
  private val _contacts = arrayListOf<Contact>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    if (viewType == VIEW_TYPE_BANNER) {
      InfoViewHolder(
          LayoutInflater.from(parent.context)
              .inflate(layout.row_info_banner, parent, false)
      )
    } else {
      UserViewHolder(
          LayoutInflater.from(parent.context)
              .inflate(layout.row_share_user, parent, false)
      )
    }

  override fun getItemViewType(position: Int) = if (position == 0) {
    VIEW_TYPE_BANNER
  } else {
    VIEW_TYPE_CONTENT
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    if (position == 0) {
      (holder as InfoViewHolder).run {
        infoMessage.setText(R.string.share_email_message)
      }
      return
    }

    (holder as UserViewHolder).run {
      when (position) {
        1 -> {
          userImage.isVisible = true
          Glide.with(context)
              .load(preferences.userIcon)
              .circleCrop()
              .into(userImage)
          addUserImage.isVisible = false
          userNameEdit.isVisible = false
          userNameArea.isVisible = true
          userName.text = preferences.userName
          userEmail.text = preferences.userEmail
          userDelete.isVisible = false
        }
        itemCount - 1 -> {
          userImage.isVisible = false
          addUserImage.isVisible = true
          userNameEdit.isVisible = true
          userNameEdit.setAdapter(
              ContactUserArrayAdapter(
                  context,
                  _contacts,
                  listener
              )
          )
          userNameEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
              listener.onEditDone(Contact("", userNameEdit.text.toString(), null, null))
            }
            false
          }
          userNameEdit.text = null
          userNameEdit.requestFocus()
          userNameArea.isVisible = false
          userDelete.isVisible = false
        }
        else -> {
          userImage.isVisible = true
          val setImage = _shares[position - HEADER_COUNT].userIcon?.let {
            try {
              Glide.with(context)
                  .load(Base64.decode(it, Base64.DEFAULT))
                  .circleCrop()
                  .into(userImage)
              return@let true
            } catch (e: Exception) {
              FirebaseCrashlytics.getInstance().recordException(e)
            }
            false
          } ?: false
          if (!setImage) {
            Glide.with(context)
                .load(R.drawable.ic_account_circle)
                .circleCrop()
                .into(userImage)
          }
          addUserImage.isVisible = false
          userNameEdit.isVisible = false
          userNameArea.isVisible = true
          userName.text = _shares[position - HEADER_COUNT].userName
              ?: _shares[position - HEADER_COUNT].userEmail
          userEmail.text = _shares[position - HEADER_COUNT].userEmail
          if (!Patterns.EMAIL_ADDRESS.matcher(
                  _shares[position - HEADER_COUNT].userEmail
              ).matches()
          ) {
            userEmail.setTextColor(Color.RED)
            userEmail.setText(R.string.share_email_invalid)
            userEmail.isVisible = true
          } else {
            userEmail.setTextColor(
                ContextCompat.getColor(
                    context,
                    android.R.color.tab_indicator_text
                )
            )
            userEmail.isVisible = _shares[position - HEADER_COUNT].userName != null
          }
          userDelete.isVisible = true
          userDelete.setOnClickListener {
            listener.onDelete(position - HEADER_COUNT)
          }
        }
      }
    }
  }

  override fun getItemCount() = _shares.size + HEADER_COUNT + FOOTER_COUNT

  fun setShares(shares: List<Share>) {
    _shares.clear()
    _shares.addAll(shares)
    notifyDataSetChanged()
  }

  fun setContacts(contacts: List<Contact>) {
    _contacts.clear()
    _contacts.addAll(contacts)
    notifyDataSetChanged()
  }

  interface OnUserCompleteListener {
    fun onEditDone(contact: Contact)
    fun onDelete(position: Int)
  }

  class UserViewHolder internal constructor(view: View) : ViewHolder(view) {
    val userImage = view.findViewById(R.id.share_user_image) as AppCompatImageView
    val addUserImage = view.findViewById(R.id.add_user_image) as AppCompatImageView
    val userNameEdit = view.findViewById(R.id.share_user_edit) as MaterialAutoCompleteTextView
    val userNameArea = view.findViewById(R.id.share_display) as LinearLayout
    val userName = view.findViewById(R.id.share_user_name) as AppCompatTextView
    val userEmail = view.findViewById(R.id.share_user_email) as AppCompatTextView
    val userDelete = view.findViewById(R.id.share_user_delete) as AppCompatImageView
  }

  companion object {
    const val VIEW_TYPE_BANNER = 0
    const val VIEW_TYPE_CONTENT = 1

    const val HEADER_COUNT = 2
    const val FOOTER_COUNT = 1
  }
}