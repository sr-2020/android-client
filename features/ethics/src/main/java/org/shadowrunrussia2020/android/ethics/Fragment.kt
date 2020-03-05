package org.shadowrunrussia2020.android.ethics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.ethics_screen.*
import org.shadowrunrussia2020.android.common.di.MainActivityScope
import org.shadowrunrussia2020.android.common.utils.encode
import org.shadowrunrussia2020.android.common.utils.qrData
import org.shadowrunrussia2020.android.view.universal_list.*

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

                // TODO(aeremin) Demonstrate states also

                universalAdapter.appendList(
                    character.ethicTrigger.map { EthicTriggerItem(it) {
                        // TODO(aeremin): Activate ethics trigger
                    }})

                universalAdapter.apply()

            }
        }

        ethicTriggersList.adapter = universalAdapter
        ethicTriggersList.layoutManager = LinearLayoutManager(requireContext())
    }



}

