package net.ambitious.android.sharebookmarks.ui.share

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_share.users_recycler_view
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.data.local.contact.Contact
import net.ambitious.android.sharebookmarks.ui.share.adapter.ShareUserListAdapter
import net.ambitious.android.sharebookmarks.ui.share.adapter.ShareUserListAdapter.OnUserCompleteListener
import net.ambitious.android.sharebookmarks.util.AnalyticsUtils
import net.ambitious.android.sharebookmarks.util.PreferencesUtils
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ShareUserFragment : Fragment(), OnUserCompleteListener {

  private val viewModel by viewModel<ShareUserViewModel>()
  private val preferences: PreferencesUtils.Data by inject()
  private val analyticsUtils: AnalyticsUtils by inject()

  private lateinit var shareListAdapter: ShareUserListAdapter

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.fragment_share, container, false).apply {
    viewModel.share.observe(
        viewLifecycleOwner,
        { shareListAdapter.setShares(it) })

    viewModel.saved.observe(
        viewLifecycleOwner,
        {
          Toast.makeText(context, R.string.share_end_done, Toast.LENGTH_LONG)
              .show()
          activity?.setResult(AppCompatActivity.RESULT_OK)
          activity?.finish()
        }
    )
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    context?.let {
      shareListAdapter = ShareUserListAdapter(it, preferences, this)
      users_recycler_view.layoutManager = LinearLayoutManager(context)
      users_recycler_view.adapter = shareListAdapter
    }
  }

  override fun onEditDone(contact: Contact) {
    analyticsUtils.logOtherTap("Share", "onEditDone")
    if (contact.email.isNotEmpty()) {
      viewModel.addList(contact)
    }
  }

  override fun onDelete(position: Int) {
    analyticsUtils.logOtherTap("Share", "onDelete")
    viewModel.deleteList(position)
  }

  fun setFolderId(folderId: Long) {
    viewModel.getShares(folderId)
  }

  fun setContactList(contacts: List<Contact>) {
    shareListAdapter.setContacts(contacts)
  }

  fun isChanged() = viewModel.changed.value ?: false

  fun getInvalidMails() = ArrayList(viewModel.share.value!!).filter {
    !Patterns.EMAIL_ADDRESS.matcher(it.userEmail).matches()
  }

  fun saveShare() {
    viewModel.save()
  }
}