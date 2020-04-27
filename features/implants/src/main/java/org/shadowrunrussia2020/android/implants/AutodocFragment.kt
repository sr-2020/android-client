package org.shadowrunrussia2020.android.implants

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.rigger_autodoc_screen.*
import org.shadowrunrussia2020.android.common.models.HealthState
import org.shadowrunrussia2020.android.common.utils.launchAsync
import org.shadowrunrussia2020.android.common.utils.russianHealthState
import org.shadowrunrussia2020.android.common.utils.showErrorMessage
import org.shadowrunrussia2020.android.model.qr.Data
import org.shadowrunrussia2020.android.model.qr.Type
import org.shadowrunrussia2020.android.model.qr.maybeQrScanned
import org.shadowrunrussia2020.android.model.qr.startQrScan
import org.shadowrunrussia2020.android.view.universal_list.ImplantItem
import org.shadowrunrussia2020.android.view.universal_list.UniversalAdapter

class AutodocFragment : Fragment() {
    private val universalAdapter by lazy { UniversalAdapter() }
    private val characterViewModel by lazy { ViewModelProviders.of(this)[ViewModel::class.java] }
    private val autodocViewModel by lazy { ViewModelProviders.of(this)[AutoDocViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.rigger_autodoc_screen, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (autodocViewModel.state == AutoDocViewModel.State.WAITING_FOR_BODY_SCAN) {
            startQrScan(this, getString(R.string.scan_qr_patient))
        }

        characterViewModel.character.observe({ this.lifecycle }) { ch ->
            ch?.let { character ->
                val analyzedBody = character.analyzedBody ?: return@let
                universalAdapter.clear()
                val implants = analyzedBody.implants
                universalAdapter.appendList(
                    implants
                        .sortedBy { it.slot }
                        .map { ImplantItem(it) {
                            AlertDialog.Builder(requireContext())
                                .setTitle("Удаление импланта")
                                .setMessage("Вы действительно хотите удалить ${it.name}?")
                                .setPositiveButton(
                                    "ОК"
                                ) { _, _ ->
                                    autodocViewModel.implantToRemove = it.id
                                    autodocViewModel.state = AutoDocViewModel.State.WAITING_FOR_EMPTY_QR_SCAN
                                    startQrScan(this, getString(R.string.scan_qr_rewritable))
                                }
                                .setNegativeButton("Отмена", null)
                                .show()
                        } })

                universalAdapter.apply()

                textViewEssence.text = "Эссенс: ${analyzedBody.essence / 100}"
                textViewHealthState.text = russianHealthState(analyzedBody.healthState)

                buttonRevive.isEnabled = analyzedBody.healthState == HealthState.wounded
            }
        }

        implantsList.adapter = universalAdapter
        implantsList.layoutManager = LinearLayoutManager(requireContext())

        buttonHeal.setOnClickListener {
            launchAsync(requireActivity()) {
                autodocViewModel.targetCharacterId?.let {
                    characterViewModel.riggerHeal(
                        it
                    )
                }
            }
        }

        buttonRevive.setOnClickListener {
            launchAsync(requireActivity()) {
                autodocViewModel.targetCharacterId?.let {
                    characterViewModel.riggerRevive(
                        it
                    )
                }
            }
        }

        buttonInstallImplant.setOnClickListener {
            autodocViewModel.state = AutoDocViewModel.State.WAITING_FOR_IMPLANT_QR_SCAN
            startQrScan(this, getString(R.string.scan_qr_implant))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        maybeQrScanned(requireActivity(), requestCode, resultCode, data, onQrScanned = {qrData: Data ->
            when (autodocViewModel.state) {
                AutoDocViewModel.State.WAITING_FOR_BODY_SCAN -> {
                    if (qrData.type == Type.WOUNDED_BODY || qrData.type == Type.HEALTHY_BODY) {
                        launchAsync(requireActivity()) {
                            characterViewModel.analyzeBody(qrData.payload)
                        }
                        autodocViewModel.targetCharacterId = qrData.payload
                        autodocViewModel.state = AutoDocViewModel.State.IDLE
                    } else {
                        showErrorMessage(
                            requireActivity(),
                            "Отсканированный код не является кодом мясного тела"
                        )
                        findNavController().popBackStack()
                    }
                }
                AutoDocViewModel.State.WAITING_FOR_EMPTY_QR_SCAN -> {
                    if (qrData.type == Type.REWRITABLE) {
                        val targetCharacterId = autodocViewModel.targetCharacterId
                        val implantToRemove = autodocViewModel.implantToRemove
                        if (targetCharacterId != null && implantToRemove != null) {
                            launchAsync(requireActivity()) {
                                characterViewModel.uninstallImplant(
                                    targetCharacterId,
                                    implantToRemove,
                                    qrData.payload.toInt()
                                )
                            }
                        }
                    } else {
                        showErrorMessage(
                            requireActivity(),
                            "Отсканированный код не является пустышкой"
                        )
                    }
                    autodocViewModel.state = AutoDocViewModel.State.IDLE
                    autodocViewModel.implantToRemove = null
                }
                AutoDocViewModel.State.WAITING_FOR_IMPLANT_QR_SCAN -> {
                    if (qrData.type == Type.REWRITABLE) {
                        autodocViewModel.targetCharacterId?.let {
                            launchAsync(requireActivity()) {
                                characterViewModel.installImplant(it, qrData.payload.toInt())
                            }
                        }
                    } else {
                        showErrorMessage(
                            requireActivity(),
                            "Отсканированный код не является имплантом"
                        )
                    }
                    autodocViewModel.state = AutoDocViewModel.State.IDLE
                }
                AutoDocViewModel.State.IDLE -> {
                }
            }
        }, onScanCancelled = {
            if (autodocViewModel.state == AutoDocViewModel.State.WAITING_FOR_BODY_SCAN) {
                findNavController().popBackStack()
            }
        })
    }
}