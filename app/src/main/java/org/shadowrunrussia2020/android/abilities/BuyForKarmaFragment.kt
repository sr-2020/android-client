package org.shadowrunrussia2020.android.abilities


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_active_abilities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.character.CharacterViewModel
import org.shadowrunrussia2020.android.common.di.ApplicationSingletonScope
import org.shadowrunrussia2020.android.common.utils.showErrorMessage
import org.shadowrunrussia2020.android.model.charter.CharacterWebService
import org.shadowrunrussia2020.android.view.universal_list.BuyableFeatureItem
import org.shadowrunrussia2020.android.view.universal_list.UniversalAdapter


class BuyForKarmaFragment : Fragment() {
    private val universalAdapter by lazy { UniversalAdapter() }
    private val service = ApplicationSingletonScope.DependencyProvider.provideDependency<ApplicationSingletonScope.Dependency>()
        .retrofit.create(CharacterWebService::class.java)
    private val mModel by lazy {
        ViewModelProviders.of(requireActivity()).get(CharacterViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_buy_for_karma, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val features = service.availableFeatures().await().body()!!
                withContext(Dispatchers.Main) {
                    universalAdapter.clear()

                    universalAdapter.appendList(
                        features.map {
                            BuyableFeatureItem(it) {
                                findNavController().navigate(
                                    BuyForKarmaFragmentDirections.actionBuyForKarma(
                                        it
                                    )
                                )
                            }
                        })

                    universalAdapter.apply()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showErrorMessage(
                        requireContext(),
                        "Ошибка сервера при получении списка доступных способностей."
                    )
                }
            }
        }

        availableAbilitiesView.adapter = universalAdapter
        availableAbilitiesView.layoutManager = LinearLayoutManager(requireContext())

        mModel.getCharacter().observe(this, Observer {
            (activity as AppCompatActivity?)!!.supportActionBar!!.subtitle = "Доступно: ${it.karma.available}"
        })
    }

    override fun onPause() {
        super.onPause()
        (activity as AppCompatActivity?)!!.supportActionBar!!.subtitle = ""
    }
}
