package net.ambitious.android.sharebookmarks.ui.share

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import net.ambitious.android.sharebookmarks.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class ShareUserFragment : Fragment() {

  private val viewModel by viewModel<ShareUserViewModel>()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.fragment_share, container, false).apply {
  }
}