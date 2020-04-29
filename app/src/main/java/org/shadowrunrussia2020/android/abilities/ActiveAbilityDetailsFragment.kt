package org.shadowrunrussia2020.android.abilities


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
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
import org.shadowrunrussia2020.android.common.utils.russianQrType
import org.shadowrunrussia2020.android.common.utils.showErrorMessage
import org.shadowrunrussia2020.android.model.qr.encode
import org.shadowrunrussia2020.android.model.qr.maybeQrScanned
import org.shadowrunrussia2020.android.model.qr.mentalQrData
import org.shadowrunrussia2020.android.model.qr.startQrScan
import java.util.*
import java.util.concurrent.TimeUnit

class ActiveAbilityDetailsFragment : Fragment() {
    private val args: ActiveAbilityDetailsFragmentArgs by navArgs()
    private val ability: ActiveAbility by lazy { args.ability }
    private val disposer = CompositeDisposable()
    private val mModel by lazy {
        ViewModelProviders.of(requireActivity()).get(CharacterViewModel::class.java)
    }

    private val abilityUseModel by lazy {
        ViewModelProviders.of(this).get(ActiveAbilityUseViewModel::class.java)
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

        setUpButtons()
        setUpMentalQrRefresh()
        setUpCooldownAndAvailabilityCounter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposer.clear()
    }

    private fun setUpButtons() {
        if (totalTargets() == 0) {
            // Mental or no-target ability. There is only one button and it immediately
            // triggers ability usage upon click
            chooseTarget1.setOnClickListener { useUntargeted(); }
        } else {
            for (i in 0 until totalTargets()) {
                val b = listOf(chooseTarget1, chooseTarget2, chooseTarget3)[i]
                b.isEnabled = i == 0
                b.isVisible = true
                b.text = ability.targetsSignature[i].name
                b.setOnClickListener {
                    b.isEnabled = false
                    startQrScan(this, ability.targetsSignature[i].name)
                }
            }
        }
    }

    private fun setUpMentalQrRefresh() {
        mModel.getCharacter().observe(this, Observer {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap =
                barcodeEncoder.encodeBitmap(
                    encode(it.mentalQrData), BarcodeFormat.QR_CODE, 400, 400
                )
            qrCodeImage.setImageBitmap(bitmap)
        })
    }

    private fun setUpCooldownAndAvailabilityCounter() {
        disposer += Observable.interval(0, 10, TimeUnit.SECONDS)
            .observeOn(MainThreadSchedulers.androidUiScheduler)
            .subscribe {
                val currentTimestamp = System.currentTimeMillis()
                val validUntil = ability.validUntil
                if (validUntil != null) {
                    if (validUntil < currentTimestamp) {
                        findNavController().popBackStack()
                    } else {
                        textValidUntil.text =
                            "Закончится " + PrettyTime(Locale("ru")).format(Date(validUntil))
                        targetButtons.isVisible = true
                    }
                } else if (ability.cooldownUntil >= currentTimestamp) {
                    textValidUntil.text =
                        "Доступно " + PrettyTime(Locale("ru")).format(Date(ability.cooldownUntil))
                    targetButtons.isVisible = false
                } else {
                    textValidUntil.text = ""
                    targetButtons.isVisible = true
                }
            }
    }

    private fun useUntargeted() {
        chooseTarget1.isEnabled = false
        CoroutineScope(Dispatchers.Main).launch {
            try {
                mModel.useAbility(ability.id)
                if (ability.target == TargetType.show) {
                    qrCodeImage.visibility = View.VISIBLE
                } else {
                    findNavController().popBackStack()
                }
            } catch (e: Exception) {
                showErrorMessage(requireContext(), "Ошибка. ${e.message}")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        maybeQrScanned(requireActivity(), requestCode, resultCode, data, { fullQrData ->
            val buttons = listOf(chooseTarget1, chooseTarget2, chooseTarget3)
            val currentTargetInd = abilityUseModel.data.size
            val currentSignature = ability.targetsSignature[currentTargetInd]
            val b = buttons[currentTargetInd]
            if (!currentSignature.allowedTypes.contains(fullQrData.type)) {
                val allowedString = currentSignature.allowedTypes.joinToString { russianQrType(it) }
                showErrorMessage(requireContext(), "Неожиданный QR-код: ${russianQrType(fullQrData.type)}. Подходящие: $allowedString");
                b.isEnabled = true
                return@maybeQrScanned
            }

            b.text = "${currentSignature.name} ✓"
            abilityUseModel.data[currentSignature.field] = fullQrData.modelId

            if (abilityUseModel.data.size == totalTargets()) {
                try {
                    mModel.useAbility(ability.id, abilityUseModel.data)
                } catch (e: Exception) {
                    showErrorMessage(requireContext(), "Ошибка. ${e.message}")
                }
                findNavController().popBackStack()
            } else {
                buttons[currentTargetInd + 1].isEnabled = true
            }
        })
    }

    private fun totalTargets(): Int {
        return ability.targetsSignature.size
    }
}
