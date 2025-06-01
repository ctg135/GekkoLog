package salabaev.gekkolog.data.gecko

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GeckoRepository(private val dao: GeckoDao) {
    private val corotineScope = CoroutineScope(Dispatchers.Main)

    val geckoList: LiveData<List<Gecko>>? = dao.getGeckos()

    fun addGecko(gecko: Gecko){
        corotineScope.launch(Dispatchers.IO) {
            dao.addGecko(gecko)
        }
    }

    fun updateGecko(gecko: Gecko){
        corotineScope.launch(Dispatchers.IO) {
            dao.updateGecko(gecko)
        }
    }

    fun getGecko(geckoId: Int): LiveData<Gecko> {
        return dao.getGecko(geckoId)
    }

    fun deleteGecko(geckoId: Int) {
        corotineScope.launch(Dispatchers.IO) {
            dao.deleteGecko(geckoId)
        }
    }
    suspend fun getGeckoSync(geckoId: Int): Gecko {
        return withContext(Dispatchers.IO) {
            // Здесь реализация синхронного получения геккона из БД
            // Например, если вы используете Room:
            return@withContext dao.getGeckoSync(geckoId)
        }
    }
}