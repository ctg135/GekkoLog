package salabaev.gekkolog.data.gecko

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GeckoRepository(private val dao: GeckoDao) {
    private val corotineScope = CoroutineScope(Dispatchers.Main)

    val geckoList: LiveData<List<Gecko>>? = dao.getGeckos()

    fun addGecko(gecko: Gecko){
        corotineScope.launch(Dispatchers.IO) {
            dao.addGecko(gecko)
        }
    }


}