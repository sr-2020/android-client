package org.shadowrunrussia2020.android.character


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_show_qr.qrCodeImage
import kotlinx.android.synthetic.main.fragment_character_overview.swipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_wounded.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.shadowrunrussia2020.android.MainNavGraphDirections
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.common.models.Character
import org.shadowrunrussia2020.android.common.utils.Type
import org.shadowrunrussia2020.android.common.utils.Data
import org.shadowrunrussia2020.android.common.utils.encode
import org.shadowrunrussia2020.android.common.utils.showErrorMessage
import java.util.concurrent.TimeUnit


class WoundedFragment : Fragment() {
    private val mModel by lazy {
        ViewModelProviders.of(activity!!).get(CharacterViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wounded, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mModel.getCharacter().observe(
            this,
            Observer { data: Character? -> if (data != null) onCharacterUpdate(data) }
        )

        swipeRefreshLayout.setOnRefreshListener { refreshData() }

        buttonDebugSelfRevive.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                        mModel.postEvent("revive")
                    }
                } catch (e: Exception) {
                    showErrorMessage(requireActivity(), "${e.message}")
                }
            }
        }
    }

    private fun refreshData() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(CoroutineScope(Dispatchers.IO).coroutineContext) { mModel.refresh() }
            } catch (e: Exception) {
                showErrorMessage(requireContext(), "${e.message}")
            }
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun onCharacterUpdate(character: Character) {
        if (character.healthState == "healthy") {
            findNavController().navigate(
                MainNavGraphDirections.actionGlobalCharacter(),
                NavOptions.Builder().setPopUpTo(R.id.main_nav_graph, true).build()
            )
        }

        textViewTitle.text =
            if (character.healthState == "wounded") getString(R.string.wounded)
            else getString(R.string.clinically_dead)

        // Needed to prevent code from being stale and thus invalid
        regenerateBodyQr(character)
    }

    private fun regenerateBodyQr(character: Character) {
        val barcodeEncoder = BarcodeEncoder()
        val bitmap =
            barcodeEncoder.encodeBitmap(
                encode(
                    Data(
                        Type.WOUNDED_BODY,
                        0,
                        (TimeUnit.MILLISECONDS.toSeconds(character.timestamp) + TimeUnit.HOURS.toSeconds(1)).toInt(),
                        character.modelId
                    )
                ), BarcodeFormat.QR_CODE, 400, 400
            )
        qrCodeImage.setImageBitmap(bitmap)
    }
}
