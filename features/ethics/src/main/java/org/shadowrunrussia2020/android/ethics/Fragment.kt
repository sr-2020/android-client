package org.shadowrunrussia2020.android.ethics

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.ethics_screen.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.shadowrunrussia2020.android.common.models.EthicTrigger
import org.shadowrunrussia2020.android.common.utils.showErrorMessage
import org.shadowrunrussia2020.android.view.universal_list.EthicCooldownItem
import org.shadowrunrussia2020.android.view.universal_list.EthicStateItem
import org.shadowrunrussia2020.android.view.universal_list.EthicTriggerItem
import org.shadowrunrussia2020.android.view.universal_list.UniversalAdapter

class Fragment : Fragment() {

    private val universalAdapter by lazy { UniversalAdapter() }

    private val viewModel by lazy { ViewModelProviders.of(this)[ViewModel::class.java] }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.ethics_screen, container, false)

    override fun onResume() {
        super.onResume()

        viewModel.character.observe({ this.lifecycle }) { ch ->
            ch?.let { character ->

                universalAdapter.clear()

                universalAdapter.appendList(
                    character.ethicState
                        .sortedBy { it.scale }
                        .map { EthicStateItem(it) })

                if (character.ethicLockedUntil > System.currentTimeMillis()) {
                    universalAdapter.appendList(
                        character.ethicTrigger
                            .filter { it.kind == "crysis" }
                            .sortedBy { it.description }
                            .map { EthicTriggerItem(it) { onTriggerActivated(it) } })
                    universalAdapter.appendList(
                        listOf(
                            EthicCooldownItem(character.ethicLockedUntil) {
                                try {
                                    CoroutineScope(Dispatchers.IO).launch { viewModel.refresh() }
                                } catch (e: Exception) {
                                    showErrorMessage(requireContext(), "Ошибка. ${e.message}")
                                }
                            }
                        )
                    )
                } else {
                    universalAdapter.appendList(
                        character.ethicTrigger
                            .filter { it.kind == "principle" }
                            .sortedBy { it.description }
                        .map { EthicTriggerItem(it) { onTriggerActivated(it) }})

                    universalAdapter.appendList(
                        character.ethicTrigger
                            .filter { it.kind != "principle" }
                            .sortedBy { it.description }
                        .map { EthicTriggerItem(it) { onTriggerActivated(it) }})
                }

                universalAdapter.apply()
            }
        }

        ethicTriggersList.adapter = universalAdapter
        ethicTriggersList.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun onTriggerActivated(trigger: EthicTrigger) {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(R.string.confirmation_message)
            .setPositiveButton(R.string.confirm_change
            ) { _, _ ->
                try {
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.ethicTrigger(trigger.id)
                    }
                } catch (e: Exception) {
                    showErrorMessage(requireActivity(), "${e.message}")
                }
            }
            .setNegativeButton(R.string.cancel_change) {_, _ -> }
        // Create the AlertDialog object and return it
        builder.create().show()
    }

}

