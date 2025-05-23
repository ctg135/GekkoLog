package salabaev.gekkolog.ui.event

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import salabaev.gekkolog.data.GeckosDatabase
import salabaev.gekkolog.data.event.Event
import salabaev.gekkolog.data.event.EventRepository
import salabaev.gekkolog.data.gecko.GeckoRepository
import salabaev.gekkolog.databinding.FragmentEventBinding

class EventFragment : Fragment() {

    companion object {
        fun newInstance() = EventFragment()
    }

    private var _binding: FragmentEventBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: EventViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val daoEvents = GeckosDatabase.getInstance(requireContext()).EventDao()
        val daoGeckos = GeckosDatabase.getInstance(requireContext()).GeckoDao()
        viewModel = EventViewModel(
            EventRepository(daoEvents),
            GeckoRepository(daoGeckos)
        )

        observeViewModel()
        loadData()
        setupViews()
    }

    // Загрузка данных события из БД
    private fun loadData() {
        arguments?.getInt("eventId")?.let { eventId ->
            if (eventId != 0) {
                viewModel.loadEvent(eventId)
            }
        }
    }

    // Привязка модели к записи в БД
    private fun observeViewModel() {
        viewModel.event.observe(viewLifecycleOwner) { event ->
            event?.let { updateUI(it) }
        }
    }

    // Функция настройки UI
    private fun setupViews() {
        // Настройка UI

        // По входным данным отображет одну из 5 форм
        arguments?.getString("eventType")?.let { eventType ->
            when (eventType) {
                "FEED" -> {
                    // Форма кормления
                    Log.d("TEST", "База кормит")
                }
                "SHED" -> {
                    // Форма линьки
                    Log.d("TEST", "База линяет")
                }
                "WEIGHT" -> {
                    // Форма взвешивания
                    Log.d("TEST", "База весит")
                }
                "HEALTH" -> {
                     // Форма о здоровье
                    Log.d("TEST", "База лечит")
                }
                "OTHER" -> {
                    // Форма другое
                    Log.d("TEST", "База дружит")
                }
                else -> {}
            }
        }
    }

    // Обновление UI
    private fun updateUI(event: Event) {
        // TODO Установка новых значений полей при обновлении модели
    }


}