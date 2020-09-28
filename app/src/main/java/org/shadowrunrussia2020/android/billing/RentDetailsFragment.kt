package org.shadowrunrussia2020.android.billing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_rent_details.*
import org.shadowrunrussia2020.android.R

class RentDetailsFragment : Fragment() {
    private val args: RentDetailsFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_rent_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val r = args.rent
        valueId.text = r.rentaId.toString()
        valueDate.text = r.dateCreated.toString()
        valueNomenklatura.text = r.nomenklaturaName
        valueCorporation.text = r.corporation
        valueShop.text = r.shop
        valuePrice.text = r.finalPrice.toString()
    }

}