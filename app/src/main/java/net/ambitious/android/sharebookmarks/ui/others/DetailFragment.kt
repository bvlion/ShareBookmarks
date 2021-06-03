package net.ambitious.android.sharebookmarks.ui.others

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.databinding.FragmentOthersDetailBinding
import net.ambitious.android.sharebookmarks.util.Const
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailFragment : Fragment() {

  private val viewModel by viewModel<DetailViewModel>()
  private var isTerm = false
  private var _binding: FragmentOthersDetailBinding? = null
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ) = FragmentOthersDetailBinding.inflate(inflater, container, false)
    .also {
      arguments?.let { bundle ->
        isTerm = bundle.getBoolean(ARGS_IS_TERM)
      }
      _binding = it
    }.root

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.message.observe(viewLifecycleOwner, {
      if (it.isEmpty()) {
        binding.errorText.isVisible = true
      } else {
        binding.othersDetailMessage.isVisible = true
        binding.othersDetailMessage.loadData(
          String.format(Const.HTML_BODY, it),
          "text/html",
          "utf-8"
        )
      }
      binding.loading.isVisible = false
      activity?.setTitle(
        if (isTerm) {
          R.string.other_menu_terms_of_use
        } else {
          R.string.other_menu_privacy_policy
        }
      )
    })

    viewModel.getDetailMessages(isTerm)
  }

  companion object {
    private const val ARGS_IS_TERM = "is_term"

    fun newInstance(isTerm: Boolean) = DetailFragment().apply {
      arguments = Bundle().apply {
        putBoolean(ARGS_IS_TERM, isTerm)
      }
    }
  }
}