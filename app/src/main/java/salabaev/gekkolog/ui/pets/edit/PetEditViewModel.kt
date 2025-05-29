package salabaev.gekkolog.ui.pets.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import salabaev.gekkolog.data.gecko.Gecko
import salabaev.gekkolog.data.gecko.GeckoRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import salabaev.gekkolog.data.event.EventRepository
import salabaev.gekkolog.ui.utils.NiceDateFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PetEditViewModel (private val repository: GeckoRepository,
private val eventRepository: EventRepository): ViewModel() {
    private val _gecko = MutableLiveData<Gecko?>()
    val gecko: LiveData<Gecko?> = _gecko

    private val _lastWeight = MutableLiveData<String?>()
    val lastWeight: LiveData<String?> = _lastWeight
    private val _lastFeed = MutableLiveData<String?>()
    val lastFeed: LiveData<String?> = _lastFeed
    private val _lastShed = MutableLiveData<String?>()
    val lastShed: LiveData<String?> = _lastShed

    fun loadGecko(geckoId: Int) {
        viewModelScope.launch {
            viewModelScope.launch {
                repository.getGecko(geckoId).observeForever { gecko ->
                    _gecko.postValue(gecko)
                }
            }
        }
    }

    fun saveGecko(gecko: Gecko) {
        viewModelScope.launch {
            if (gecko.id == 0) {
                repository.addGecko(gecko)
            } else {
                repository.updateGecko(gecko)
            }
        }
    }

    fun deleteGecko(geckoId: Int) {
        viewModelScope.launch {
            if (geckoId != 0) {
                repository.deleteGecko(geckoId)
            }
        }
    }

    fun getLastFeed(geckoId: Int){
        if (geckoId == 0) return
        viewModelScope.launch(Dispatchers.IO) {
            eventRepository.getLastFeed(geckoId)?.let { event ->
                var result: String
                var date = Calendar.getInstance()
                date.timeInMillis = event.date!!
                result = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date.time)
                when(event.feedType) {
                    "CA" -> { result += " (Кальций)" }
                    "VIT" -> { result += " (Витамины)" }
                }
                _lastFeed.postValue(result)
            }
        }
    }

    fun getLastWeight(geckoId: Int) {
        if (geckoId == 0) return
        viewModelScope.launch(Dispatchers.IO) {
            eventRepository.getLastWeight(geckoId)?.let { event ->
                var result: String = event.weight.toString() + " г."
                _lastWeight.postValue(result)
            }
        }
    }

    fun getLastShed(geckoId: Int){
        if (geckoId == 0) return
        viewModelScope.launch(Dispatchers.IO) {
            eventRepository.getLastShed(geckoId)?.let { event ->
                var result: String
                var date = Calendar.getInstance()
                date.timeInMillis = event.date!!
                result = NiceDateFormatter.getNiceAge(date.timeInMillis)
                event.shedSuccess?.let {
                    if(!it) { result += " Не справился сам" }
                }
                _lastShed.postValue(result)
            }
        }
    }
}