package org.shadowrunrussia2020.android.magic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.magic_screen.*
import org.shadowrunrussia2020.android.common.di.MainActivityScope
import org.shadowrunrussia2020.android.view.universal_list.*


class MagicFragment : Fragment() {

    private val viewModel by lazy { ViewModelProviders.of(this)[MagicViewModel::class.java] }
    private val router by lazy { (activity as MainActivityScope).router }
    private val uinversalAdapter by lazy { UniversalAdapter() }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.magic_screen, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.character.observe({ this.lifecycle }) { ch ->
            ch?.let { character ->
                uinversalAdapter.clear()
                uinversalAdapter.appendList(character.spells.map { MagicSpellItem(it) { router.goToCastSpellScreen(it.id) }  })
                uinversalAdapter.apply()
            }
        }

        availableSpellsView.adapter = uinversalAdapter
        availableSpellsView.layoutManager = LinearLayoutManager(requireContext())
    }



}