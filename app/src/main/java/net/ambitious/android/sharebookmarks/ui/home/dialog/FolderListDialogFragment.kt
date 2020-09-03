package net.ambitious.android.sharebookmarks.ui.home.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.data.local.item.Item
import net.ambitious.android.sharebookmarks.util.Const

class FolderListDialogFragment : DialogFragment() {

  private var itemId: Long = 0
  private lateinit var folderList: ArrayList<Item>

  private var selectedId: Long = 0
  private lateinit var folderItemNameList: Array<String>

  private lateinit var listener: OnSetListener

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val bundle = arguments ?: return
    itemId = bundle.getLong(ARG_KEY_SELF_ID)
    @Suppress("UNCHECKED_CAST")
    folderList = bundle.getSerializable(ARG_KEY_FOLDER_LIST) as ArrayList<Item>
    folderItemNameList = folderList.map { it.name }.toTypedArray()
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    try {
      listener =
        if (parentFragment != null) {
          parentFragment as OnSetListener
        } else {
          context as OnSetListener
        }
    } catch (_: ClassCastException) {
      throw ClassCastException("Activity must implement OnSetListener.")
    }
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
    AlertDialog.Builder(activity)
        .setSingleChoiceItems(folderItemNameList, 0) { _, which ->
          selectedId = folderList[which].id!!
        }
        .setTitle(R.string.dialog_move_title)
        .setNegativeButton(R.string.dialog_cancel_button, null)
        .setPositiveButton(R.string.dialog_move_button) { _, _ ->
          listener.onSet(itemId, selectedId)
        }.create().apply {
          setOnShowListener {
            setOnDismissListener {
              listener.onCancel()
            }
          }
        }

  interface OnSetListener {
    fun onSet(selfId: Long, parentId: Long)
    fun onCancel()
  }

  companion object {
    const val TAG = "FolderListDialogFragment"

    private const val ARG_KEY_SELF_ID = "self_id"
    private const val ARG_KEY_FOLDER_LIST = "folder_list"

    fun newInstance(itemId: Long, folderList: ArrayList<Item>) = FolderListDialogFragment().apply {
      folderList.add(0, Const.HOME_FOLDER)
      arguments = Bundle().apply {
        putLong(ARG_KEY_SELF_ID, itemId)
        putSerializable(ARG_KEY_FOLDER_LIST, folderList)
      }
    }
  }
}