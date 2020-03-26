package org.shadowrunrussia2020.android.implants

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_implant_details.*
import org.shadowrunrussia2020.android.common.models.Implant

class DetailsFragment : Fragment() {
    private val args: DetailsFragmentArgs by navArgs()
    private val implant: Implant by lazy { args.implant }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_implant_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textImplantName.text = implant.name
        textImplantDescription.text = implant.description
    }
}
