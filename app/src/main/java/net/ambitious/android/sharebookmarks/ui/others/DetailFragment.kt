package net.ambitious.android.sharebookmarks.ui.others

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.ambitious.android.sharebookmarks.R
import net.ambitious.android.sharebookmarks.data.remote.etc.EtcApi
import org.koin.android.ext.android.inject
import java.util.Locale

class DetailFragment : Fragment() {

  private val etcApi: EtcApi by inject()
  private val scope = CoroutineScope(Dispatchers.IO)

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.fragment_others_detail, container, false).apply {
    arguments?.let {
      val lang = if (Locale.getDefault() == Locale.JAPAN) {
        "ja"
      } else {
        "en"
      }
      val textView = findViewById<TextView>(R.id.others_detail_message)
      val loadView = findViewById<ProgressBar>(R.id.loading)

      scope.launch {
        if (it.getBoolean(ARGS_IS_TERM)) {
          setMessage(loadView, textView, etcApi.getTermsOfUse(lang).message)
        } else {
          setMessage(loadView, textView, etcApi.getPrivacyPolicy(lang).message)
        }
      }
    }
  }

  override fun onPause() {
    super.onPause()
    scope.coroutineContext.cancelChildren()
  }

  private suspend fun setMessage(loadView: ProgressBar, textView: TextView, message: String) =
    withContext(Dispatchers.Main) {
      textView.text = HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_COMPACT)
      textView.visibility = View.VISIBLE
      loadView.visibility = View.GONE
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