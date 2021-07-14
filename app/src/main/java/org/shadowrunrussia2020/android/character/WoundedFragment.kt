package org.shadowrunrussia2020.android.character


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.firebase.iid.FirebaseInstanceId
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_show_qr.qrCodeImage
import kotlinx.android.synthetic.main.fragment_wounded.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.shadowrunrussia2020.android.LoginActivity
import org.shadowrunrussia2020.android.MainNavGraphDirections
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.common.di.ApplicationSingletonScope
import org.shadowrunrussia2020.android.common.models.Character
import org.shadowrunrussia2020.android.common.models.HealthState
import org.shadowrunrussia2020.android.model.qr.encode
import org.shadowrunrussia2020.android.model.qr.qrData
import org.shadowrunrussia2020.android.positioning.BeaconsScanner


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

        buttonLogout.setOnClickListener {
            ApplicationSingletonScope.DependencyProvider.dependency.session.invalidate()
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    ApplicationSingletonScope.ComponentProvider.components.clearAllTables()
                    FirebaseInstanceId.getInstance().deleteInstanceId()
                }
            }
            // We don't use Android's Navigation Component here because it somehow messes with back stack no matter what
            // we do (i.e. pressing "back" on login screen tries to go to the main screen).
            // Current implementation is taken from
            // https://medium.com/google-developer-experts/using-navigation-architecture-component-in-a-large-banking-app-ac84936a42c2
            requireActivity().stopService(Intent(activity, BeaconsScanner::class.java))
            startActivity(Intent(activity, LoginActivity::class.java))
        }
    }
    private fun onCharacterUpdate(character: Character) {
        if (character.healthState == HealthState.healthy) {
            findNavController().navigate(
                MainNavGraphDirections.actionGlobalCharacter(),
                NavOptions.Builder().setPopUpTo(R.id.main_nav_graph, true).build()
            )
        } else if (character.healthState == HealthState.wounded) {
            textViewTitle.text = getString(R.string.wounded_title)
            val medkit = character.modifiers.find { it.mID == "medkit-revive-modifier" }
            if (medkit != null) {
                if (medkit.enabled) {
                    textViewSubtitle.text = getString(R.string.wounded_subtitle_medkit_enabled)
                } else {
                    textViewSubtitle.text = getString(R.string.wounded_subtitle_medkit_cooldown)
                }
            } else {
                textViewSubtitle.text = getString(R.string.wounded_subtitle_no_medkit)
            }
        } else if (character.healthState == HealthState.clinically_dead) {
            textViewTitle.text = getString(R.string.clinical_death_title)
            textViewSubtitle.text = getString(R.string.clinical_death_subtitle)
        } else if (character.healthState == HealthState.biologically_dead) {
            textViewTitle.text = getString(R.string.absolute_death_title)
            textViewSubtitle.text = getString(R.string.absolute_death_subtitle)
        }

        textViewCharacterId.text = "Персонаж ${character.modelId}"

        // Needed to prevent code from being stale and thus invalid
        regenerateBodyQr(character)
    }

    private fun regenerateBodyQr(character: Character) {
        val barcodeEncoder = BarcodeEncoder()
        val bitmap =
            barcodeEncoder.encodeBitmap(encode(character.qrData),
                BarcodeFormat.QR_CODE, 400, 400)
        qrCodeImage.setImageBitmap(bitmap)
    }
}
