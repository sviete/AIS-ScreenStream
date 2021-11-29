package info.dvkr.screenstream.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.elvishew.xlog.XLog
import info.dvkr.screenstream.R
import info.dvkr.screenstream.data.other.getLog
import info.dvkr.screenstream.data.settings.Settings
import info.dvkr.screenstream.databinding.FragmentAboutBinding
import info.dvkr.screenstream.ui.viewBinding
import org.koin.android.ext.android.inject

class AboutFragment : Fragment(R.layout.fragment_about) {

    private val settings: Settings by inject()
    private var settingsLoggingVisibleCounter: Int = 0
    private var version: String = ""

    private val binding by viewBinding { fragment -> FragmentAboutBinding.bind(fragment.requireView()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val packageName = requireContext().packageName

        runCatching {
            version = requireContext().packageManager.getPackageInfo(packageName, 0).versionName
            binding.tvFragmentAboutVersion.text = getString(R.string.about_fragment_app_version, version)
        }.onFailure {
            XLog.e(getLog("onViewCreated", "getPackageInfo"), it)
        }

        binding.tvFragmentAboutVersion.setOnClickListener {
            settingsLoggingVisibleCounter++
            if (settingsLoggingVisibleCounter >= 5) {
                settings.loggingVisible = true
                Toast.makeText(requireContext(), "Logging option enabled", Toast.LENGTH_LONG).show()
            }
        }

        binding.bFragmentAboutRate.setOnClickListener {
            openStringUrl("market://details?id=$packageName") {
                openStringUrl("https://play.google.com/store/apps/details?id=$packageName")
            }
        }

        binding.bFragmentAboutSources.setOnClickListener {
            openStringUrl("https://github.com/sviete/AIS-ScreenStream")
        }

        binding.bFragmentPrivacyPolicy.setOnClickListener {
            openStringUrl("https://github.com/sviete/AIS-ScreenStream/blob/master/PrivacyPolicy.md")
        }

        binding.bFragmentLicense.setOnClickListener {
            openStringUrl("https://github.com/sviete/AIS-ScreenStream/blob/master/LICENSE")
        }

    }

    private fun openStringUrl(url: String, onFailure: () -> Unit = {}) {
        runCatching {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }.onFailure {
            runCatching { onFailure.invoke() }
        }
    }
}