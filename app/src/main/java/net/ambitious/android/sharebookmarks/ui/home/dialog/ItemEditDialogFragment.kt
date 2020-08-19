package net.ambitious.android.sharebookmarks.ui.home.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.util.Const.ItemType

class ItemEditDialogFragment : DialogFragment() {

  private lateinit var itemType: ItemType
  private var itemId: Long = 0
  private var itemName: String? = null
  private var itemUrl: String? = null

  private lateinit var editTitleLayout: TextInputLayout
  private lateinit var editTitleArea: TextInputEditText
  private lateinit var editUrlLayout: TextInputLayout
  private lateinit var editUrlArea: TextInputEditText

  private lateinit var listener: OnClickListener

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val bundle = arguments ?: return
    itemType = bundle.getSerializable(ARG_KEY_ITEM_TYPE) as ItemType
    itemId = bundle.getLong(ARG_KEY_ITEM_ID)
    itemName = bundle.getString(ARG_KEY_ITEM_NAME)
    itemUrl = bundle.getString(ARG_KEY_ITEM_URL)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ) = super.onCreateView(inflater, container, savedInstanceState).apply {
    editTitleArea.setText(itemName)
    if (itemType == ItemType.ITEM) {
      editTitleLayout.hint = getString(R.string.dialog_item_hint)
      editUrlArea.setText(itemUrl)
    } else {
      editTitleLayout.hint = getString(R.string.dialog_folder_hint)
      editUrlLayout.visibility = View.GONE
    }
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    try {
      listener =
        if (parentFragment != null) {
          parentFragment as OnClickListener
        } else {
          context as OnClickListener
        }
    } catch (_: ClassCastException) {
      throw ClassCastException("Activity must implement OnClickListener.")
    }
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
    AlertDialog.Builder(activity)
        .setTitle(
            if (itemId > 0) {
              if (itemType == ItemType.ITEM) {
                R.string.dialog_item_update_title
              } else {
                R.string.dialog_folder_update_title
              }
            } else {
              if (itemType == ItemType.ITEM) {
                R.string.dialog_item_add_title
              } else {
                R.string.dialog_folder_add_title
              }
            }
        )
        .setView(
            View.inflate(activity, R.layout.dialog_item_edit, null).apply {
              editTitleLayout = findViewById(R.id.edit_title_layout)
              editTitleArea = findViewById(R.id.edit_title_area)
              editUrlLayout = findViewById(R.id.edit_url_layout)
              editUrlArea = findViewById(R.id.edit_url_area)
            })
        .setNegativeButton(R.string.dialog_cancel_button, null)
        .setPositiveButton(
            if (itemId > 0) {
              R.string.dialog_update_button
            } else {
              R.string.dialog_add_button
            }, null
        )
        .create().apply {
          setOnShowListener {
            getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
              if (hasError()) {
                return@setOnClickListener
              }
              listener.onEdited(
                  itemId,
                  editTitleArea.text.toString(),
                  if (editUrlLayout.visibility == View.VISIBLE) {
                    editUrlArea.text.toString()
                  } else {
                    null
                  }
              )
              dismiss()
            }
          }
        }

  private fun hasError(): Boolean {
    var error = false

    if (editTitleArea.text.toString().isEmpty()) {
      error = true
      editTitleLayout.error = if (itemType == ItemType.ITEM) {
        getString(R.string.dialog_item_error)
      } else {
        getString(R.string.dialog_folder_error)
      }
    }

    if (editUrlLayout.visibility == View.VISIBLE && editUrlArea.text.toString().isEmpty()) {
      editUrlLayout.error = getString(R.string.dialog_url_error)
      error = true
    }

    return error
  }

  interface OnClickListener {
    fun onEdited(itemId: Long, itemName: String, itemUrl: String?)
  }

  companion object {
    const val TAG = "ItemEditDialogFragment"

    private const val ARG_KEY_ITEM_ID = "item_id"
    private const val ARG_KEY_ITEM_TYPE = "item_type"
    private const val ARG_KEY_ITEM_NAME = "item_name"
    private const val ARG_KEY_ITEM_URL = "item_url"

    fun newInstance(
      itemId: Long, itemType: ItemType, itemName: String?, itemUrl: String?
    ) = ItemEditDialogFragment().apply {
      arguments = Bundle().apply {
        putLong(ARG_KEY_ITEM_ID, itemId)
        putSerializable(ARG_KEY_ITEM_TYPE, itemType)
        putString(ARG_KEY_ITEM_NAME, itemName)
        putString(ARG_KEY_ITEM_URL, itemUrl)
      }
    }
  }
}