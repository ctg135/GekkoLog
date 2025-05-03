package salabaev.gekkolog.ui.pets

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import salabaev.gekkolog.data.gecko.Gecko
import salabaev.gekkolog.data.gecko.GeckoRepository

class PetsViewModel(private val repository: GeckoRepository) : ViewModel() {
    // TODO: Implement the ViewModel

    val petList: LiveData<List<Gecko>>? = repository.geckoList
}