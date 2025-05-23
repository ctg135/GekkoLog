package salabaev.gekkolog.ui.pets.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import salabaev.gekkolog.data.gecko.Gecko
import salabaev.gekkolog.data.gecko.GeckoRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PetEditViewModel (private val repository: GeckoRepository): ViewModel() {
    private val _gecko = MutableLiveData<Gecko?>()
    val gecko: LiveData<Gecko?> = _gecko

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
}