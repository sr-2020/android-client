package org.shadowrunrussia2020.android


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
import kotlinx.android.synthetic.main.activity_show_qr.*
import kotlinx.android.synthetic.main.fragment_character_overview.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.shadowrunrussia2020.android.character.CharacterViewModel
import org.shadowrunrussia2020.android.character.models.Character
import org.shadowrunrussia2020.android.qr.Data
import org.shadowrunrussia2020.android.qr.Type
import org.shadowrunrussia2020.android.qr.encode
import org.shadowrunrussia2020.android.qr.showErrorMessage


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
            Observer { data: Character? ->
                if (data != null) {
                    if (data.healthState == "healthy") {
                        findNavController().navigate(
                            MainNavGraphDirections.actionGlobalCharacter(),
                            NavOptions.Builder().setPopUpTo(R.id.main_nav_graph, true).build()
                        )
                    }

                    val barcodeEncoder = BarcodeEncoder()
                    val bitmap =
                        barcodeEncoder.encodeBitmap(
                            encode(
                                Data(
                                    Type.DIGITAL_SIGNATURE,
                                    0,
                                    (data.timestamp / 1000 + 3600).toInt(),
                                    data.modelId
                                )
                            ), BarcodeFormat.QR_CODE, 400, 400
                        )
                    qrCodeImage.setImageBitmap(bitmap)
                }
            }
        )

        swipeRefreshLayout.setOnRefreshListener { refreshData() }
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
}
