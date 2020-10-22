package org.shadowrunrussia2020.android.abilities


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_buy_for_karma_details.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.character.CharacterViewModel
import org.shadowrunrussia2020.android.common.utils.showErrorMessage

class BuyForKarmaDetailsFragment : Fragment() {
    private val args: BuyForKarmaDetailsFragmentArgs by navArgs()
    private val feature by lazy { args.feature }
    private val mModel by lazy {
        ViewModelProviders.of(requireActivity()).get(CharacterViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_buy_for_karma_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textAbilityName.text = feature.humanReadableName
        textAbilityDescription.text = feature.description
        buy.text = "Купить (${feature.karmaCost} кармы)"

        mModel.getCharacter().observe(this, Observer {
            buy.isEnabled = it.karma.available >= feature.karmaCost
        })

        buy.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    mModel.buyFeatureForKarma(feature.id)
                    findNavController().popBackStack()
                } catch (e: Exception) {
                    showErrorMessage(requireContext(), "Ошибка. ${e.message}")
                }
            }

        }
    }
}
