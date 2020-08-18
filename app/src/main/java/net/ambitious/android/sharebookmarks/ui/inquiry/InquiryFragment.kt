package net.ambitious.android.sharebookmarks.ui.inquiry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.databinding.FragmentInquiryBinding
import net.ambitious.android.sharebookmarks.util.PreferencesUtils
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class InquiryFragment() : Fragment(), InquiryViewModel.OnClickListener {

  private val viewModel by viewModel<InquiryViewModel>()
  private val preferences: PreferencesUtils.Data by inject()
  private lateinit var binding: FragmentInquiryBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentInquiryBinding.inflate(inflater, container, false)
    binding.vm = viewModel
    binding.lifecycleOwner = this
    binding.listener = this
    return binding.root
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    if (viewModel.postResult.value == null) {
      viewModel.postResult.observe(
          viewLifecycleOwner,
          {
            view?.run {
              Snackbar.make(
                  this, if (it) {
                R.string.send_complete
              } else {
                R.string.send_error
              }, Snackbar.LENGTH_LONG
              ).show()
            }
          })
    }

    binding.inquiryText.addTextChangedListener { text ->
      viewModel.inquiryCheck(text.toString())
    }

    binding.inquiryMail.addTextChangedListener { text ->
      viewModel.mailAddressCheck(text.toString())
    }

    viewModel.initializeView()
    preferences.userEmail?.let {
      viewModel.setInitialMailAddress(it)
    }
  }

  override fun onSendClick() {
    viewModel.postMessage()
  }
}