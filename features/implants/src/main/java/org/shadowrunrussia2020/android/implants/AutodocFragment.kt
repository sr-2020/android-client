package org.shadowrunrussia2020.android.implants

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
import org.shadowrunrussia2020.android.common.qr.QrDataOrError
import org.shadowrunrussia2020.android.common.qr.QrViewModel
import org.shadowrunrussia2020.android.common.utils.launchAsync
import org.shadowrunrussia2020.android.common.utils.russianHealthState
import org.shadowrunrussia2020.android.view.universal_list.ImplantItem
import org.shadowrunrussia2020.android.view.universal_list.UniversalAdapter

class AutodocFragment: Fragment() {
    private val universalAdapter by lazy { UniversalAdapter() }
    private val viewModel by lazy { ViewModelProviders.of(this)[ViewModel::class.java] }
    private lateinit var targetCharacterId: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.rigger_autodoc_screen, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val qrViewModel = ViewModelProviders.of(requireActivity()).get(QrViewModel::class.java)
        val qrData = qrViewModel.data?.qrData;
        qrViewModel.data = QrDataOrError(null, false)

        if (qrData == null) {
            findNavController().navigate(AutodocFragmentDirections.actionConnectToBody())
            return
        } else {
            launchAsync(requireActivity()) {
                viewModel.analyzeBody(qrData.payload)
                targetCharacterId = qrData.payload
            }
        }

        viewModel.character.observe({ this.lifecycle }) { ch ->
            ch?. let { character ->
                val analyzedBody = character.analyzedBody ?: return@let
                universalAdapter.clear()
                val implants = analyzedBody.implants
                universalAdapter.appendList(
                    implants
                        .sortedBy { it.slot }
                        .map { ImplantItem(it) {} })

                universalAdapter.apply()

                textViewEssence.text = "Эссенс: ${analyzedBody.essence}"
                textViewHealthState.text = russianHealthState(analyzedBody.healthState)

                buttonRevive.isEnabled = analyzedBody.healthState == HealthState.wounded
            }
        }

        implantsList.adapter = universalAdapter
        implantsList.layoutManager = LinearLayoutManager(requireContext())

        buttonHeal.setOnClickListener {
            launchAsync(requireActivity()) { viewModel.riggerHeal(targetCharacterId) }
        }

        buttonRevive.setOnClickListener {
            launchAsync(requireActivity()) { viewModel.riggerRevive(targetCharacterId) }
        }
    }
}