package org.shadowrunrussia2020.android.abilities


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_active_ability_details.*
import kotlinx.android.synthetic.main.fragment_spell_details.textAbilityDescription
import kotlinx.android.synthetic.main.fragment_spell_details.textAbilityName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.ocpsoft.prettytime.PrettyTime
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.character.CharacterViewModel
import org.shadowrunrussia2020.android.common.models.ActiveAbility
import org.shadowrunrussia2020.android.common.models.TargetType
import org.shadowrunrussia2020.android.common.utils.MainThreadSchedulers
import org.shadowrunrussia2020.android.common.utils.plusAssign
import org.shadowrunrussia2020.android.common.utils.showErrorMessage
import org.shadowrunrussia2020.android.model.qr.*
import java.util.*
import java.util.concurrent.TimeUnit

class ActiveAbilityDetailsFragment : Fragment() {
    private val args: ActiveAbilityDetailsFragmentArgs by navArgs()
    private val ability: ActiveAbility by lazy { args.ability }
    private val disposer = CompositeDisposable()

    private val mModel by lazy {
        ViewModelProviders.of(requireActivity()).get(CharacterViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_active_ability_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textAbilityName.text = ability.humanReadableName
        textAbilityDescription.text = ability.description

        disposer += Observable.interval(0, 10, TimeUnit.SECONDS)
            .observeOn(MainThreadSchedulers.androidUiScheduler)
            .subscribe {
                val currentTimestamp = System.currentTimeMillis()
                val validUntil = ability.validUntil
                if (validUntil != null) {
                    textValidUntil.text =
                        "Закончится " + PrettyTime(Locale("ru")).format(Date(validUntil))
                } else if (ability.cooldownUntil >= currentTimestamp) {
                    textValidUntil.text =
                        "Доступно " + PrettyTime(Locale("ru")).format(Date(ability.cooldownUntil))
                    useAbility.isEnabled = false
                } else {
                    textValidUntil.text = ""
                }
            }

        useAbility.setOnClickListener {
            when (ability.target) {
                TargetType.none -> useOnSelf()
                TargetType.scan -> chooseTarget()
                TargetType.show -> useAndShowQr()
            }
        }

        mModel.getCharacter().observe(this, Observer {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap =
                barcodeEncoder.encodeBitmap(
                    encode(it.mentalQrData), BarcodeFormat.QR_CODE, 400, 400
                )
            qrCodeImage.setImageBitmap(bitmap)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposer.clear()
    }

    private fun useOnSelf() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                mModel.useAbility(ability.id)
                findNavController().popBackStack()
            } catch (e: Exception) {
                showErrorMessage(requireContext(), "Ошибка. ${e.message}")
            }
        }
    }

    private fun useAndShowQr() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                mModel.useAbility(ability.id)
                qrCodeImage.visibility = View.VISIBLE
                useAbility.isEnabled = false
            } catch (e: Exception) {
                showErrorMessage(requireContext(), "Ошибка. ${e.message}")
            }
        }
    }

    private fun chooseTarget() {
        startQrScan(this, "Выбор цели способности.")
    }

    private fun useOnTarget(qrData: FullQrData) {
        val eventData: HashMap<String, Any> = when (qrData.type) {
            Type.HEALTHY_BODY, Type.WOUNDED_BODY -> hashMapOf(
                "targetCharacterId" to qrData.modelId.toInt()
            )
            else -> {
                // TODO(aeremin): Support abilities having other kinds of target
                showErrorMessage(requireContext(), "Ошибка. Неожиданный QR-код."); return
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            try {
                mModel.useAbility(ability.id, eventData)
            } catch (e: Exception) {
                showErrorMessage(requireContext(), "Ошибка. ${e.message}")
            }
            findNavController().popBackStack()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        maybeQrScanned(requireActivity(), requestCode, resultCode, data, {
            useOnTarget(it)
        })
    }
}
