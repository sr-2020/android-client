package org.shadowrunrussia2020.android.abilities


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_active_abilities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.shadowrunrussia2020.android.R
import org.shadowrunrussia2020.android.common.di.ApplicationSingletonScope
import org.shadowrunrussia2020.android.model.charter.CharacterWebService
import org.shadowrunrussia2020.android.view.universal_list.BuyableFeatureItem
import org.shadowrunrussia2020.android.view.universal_list.UniversalAdapter

class BuyForKarmaFragment : Fragment() {
    private val universalAdapter by lazy { UniversalAdapter() }
    private val service = ApplicationSingletonScope.DependencyProvider.provideDependency<ApplicationSingletonScope.Dependency>()
        .retrofit.create(CharacterWebService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_buy_for_karma, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            val features = service.availableFeatures().await().body()!!
            withContext(Dispatchers.Main) {
                universalAdapter.clear()

                universalAdapter.appendList(
                    features.map {
                        BuyableFeatureItem(it)
                    })

                universalAdapter.apply()
            }
        }

        availableAbilitiesView.adapter = universalAdapter
        availableAbilitiesView.layoutManager = LinearLayoutManager(requireContext())
    }
}
