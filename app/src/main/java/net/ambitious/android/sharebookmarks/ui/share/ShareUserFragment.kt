package net.ambitious.android.sharebookmarks.ui.share

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.breadcrumbs_recycler_view
import kotlinx.android.synthetic.main.fragment_share.users_recycler_view
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.ui.share.adapter.ShareUserListAdapter
import net.ambitious.android.sharebookmarks.util.PreferencesUtils
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ShareUserFragment : Fragment() {

  private val viewModel by viewModel<ShareUserViewModel>()
  private val preferences: PreferencesUtils.Data by inject()

  private lateinit var shareListAdapter: ShareUserListAdapter

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.fragment_share, container, false).apply {
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    context?.let {
      shareListAdapter = ShareUserListAdapter(it, preferences)
      users_recycler_view.layoutManager = LinearLayoutManager(context)
      users_recycler_view.adapter = shareListAdapter
    }
  }
}