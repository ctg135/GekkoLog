package salabaev.gekkolog.ui.reminder

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.transition.Visibility
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import salabaev.gekkolog.R
import salabaev.gekkolog.data.GeckosDatabase
import salabaev.gekkolog.data.gecko.Gecko
import salabaev.gekkolog.data.gecko.GeckoRepository
import salabaev.gekkolog.data.reminder.Reminder
import salabaev.gekkolog.data.reminder.ReminderRepository
import salabaev.gekkolog.databinding.FragmentEventBinding
import salabaev.gekkolog.databinding.FragmentReminderBinding
import salabaev.gekkolog.ui.event.GeckoDropdownAdapter
import salabaev.gekkolog.ui.utils.DatePickerHelper
import java.util.Calendar

class ReminderFragment : Fragment() {

    companion object {
        fun newInstance() = ReminderFragment()
    }

    private var _binding: FragmentReminderBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ReminderViewModel
    private var currentDateReminder: Long? = null
    private var selectedGeckoId: Int? = null
    private lateinit var geckoList: LiveData<List<Gecko>>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReminderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val daoReminder = GeckosDatabase.getInstance(requireContext()).ReminderDao()
        val daoGeckos = GeckosDatabase.getInstance(requireContext()).GeckoDao()
        viewModel = ReminderViewModel(
            ReminderRepository(daoReminder),
            GeckoRepository(daoGeckos)
        )
        geckoList = viewModel.geckoRepository.geckoList ?: daoGeckos.getGeckos()

        observeViewModel()
        loadData()
        setupViews()
    }

    // Функция для привязки к моделти
    private fun observeViewModel() {
        viewModel.reminder.observe(viewLifecycleOwner) { reminder ->
            reminder?.let { updateUI(it) }
        }
    }

    // Функция для загркзки данных из БД
    private fun loadData() {
        selectedGeckoId = arguments?.getInt("geckoId")
        val reminderType = arguments?.getString("reminderType") ?: ""
        arguments?.getInt("reminderId")?.let { reminderId ->
            if (reminderId != 0) {
                viewModel.loadReminder(reminderId)
            } else {
                // Устанавливаем выбранного геккона для нового события
                selectedGeckoId?.let { geckoId ->
                    viewModel.geckoRepository.getGecko(geckoId).observe(viewLifecycleOwner) { gecko ->
                        gecko?.let {
                            binding.reminderPet.setText(it.name, false)
                        }
                    }
                }
                // Загрузка даты
                var currentDate = Calendar.getInstance().apply {
                    set(Calendar.DAY_OF_MONTH, get(Calendar.DAY_OF_MONTH) + 1)
                }.timeInMillis
                arguments?.getLong("date")?.let {
                    if(it != 0L) currentDate = it
                }
                viewModel.reminder.value?.date = currentDate
                currentDateReminder = currentDate
                binding.reminderDate.setText(DatePickerHelper.formatDateTime(currentDate))
                // Первоначальная установка типа
                val typeDropdown = binding.reminderType
                when (reminderType) {
                    "FEED" -> {
                        typeDropdown.setText("Кормление", false)
                        typeDropdown.tag = "FEED"
                    }
                    "OTHER" -> {
                        typeDropdown.setText("Другое", false)
                        typeDropdown.tag = "OTHER"
                    }
                    "HEALTH" -> {
                        typeDropdown.setText("Здоровье", false)
                        typeDropdown.tag = "HEALTH"
                    }
                }
            }
        }
    }

    // Установка параметров view
    private fun setupViews() {
        if (arguments?.getInt("reminderId") == 0) {
            binding.doneButton.visibility = View.GONE
        }

        binding.doneButton.setOnClickListener { doneReminder() }
        binding.saveButton.setOnClickListener { alertSaveReminder() }
        binding.deleteReminderButton.setOnClickListener { alertDeleteReminder() }
        binding.reminderDate.setOnClickListener { showReminderDatePicker() }
        val types = listOf(
            Pair("FEED", "Кормление"),
            Pair("OTHER", "Другое"),
            Pair("HEALTH", "Здоровье")
        )

        val adapterType = ArrayAdapter(
            requireContext(),
            R.layout.item_dropdown_menu_1line,
            types.map { it.second }
        )

        val typeDropdown = binding.reminderType
        typeDropdown.setAdapter(adapterType)
        typeDropdown.setOnItemClickListener { _, _, position, _ ->
            typeDropdown.tag = types[position].first
        }

        geckoList.observe(viewLifecycleOwner) { geckos ->
            val adapter = GeckoDropdownAdapter(
                requireContext(),
                geckos,
                binding.reminderPet
            )
            binding.reminderPet.setAdapter(adapter)

            // Устанавливаем обработчик выбора
            viewModel.geckoRepository.geckoList?.observe(viewLifecycleOwner) { geckos ->
                val adapter = GeckoDropdownAdapter(
                    requireContext(),
                    geckos,
                    binding.reminderPet
                )
                binding.reminderPet.setAdapter(adapter)

                // Устанавливаем обработчик выбора типа
                binding.reminderPet.setOnItemClickListener { _, _, position, _ ->
                    selectedGeckoId = geckos[position].id
                    binding.reminderPet.setText(geckos[position].name, false)
                }

                // Устанавливаем текущее значение, если geckoId передан или есть в событии
                selectedGeckoId?.let { geckoId ->
                    geckos.find { it.id == geckoId }?.let { gecko ->
                        binding.reminderPet.setText(gecko.name, false)
                    }
                }
            }
        }
    }

    // Обновление UI
    private fun updateUI(reminder: Reminder) {

        binding.remainderDescription.setText(reminder.description)

        reminder.geckoId?.let { geckoId ->
            viewModel.geckoRepository.getGecko(geckoId).observe(viewLifecycleOwner) { gecko ->
                gecko?.let {
                    binding.reminderPet.setText(it.name ?: "Без имени", false)
                    selectedGeckoId = geckoId
                }
            }
        }

        binding.reminderDate.setText(
            DatePickerHelper.formatDateTime(
                reminder.date!!))

        // Обновление типа события
        val typeDropdown = binding.reminderType as MaterialAutoCompleteTextView
        when (reminder.type) {
            "FEED" -> {
                typeDropdown.setText("Кормление", false)
                typeDropdown.tag = "FEED"
            }
            "OTHER" -> {
                typeDropdown.setText("Другое", false)
                typeDropdown.tag = "OTHER"
            }
            "HEALTH" -> {
                typeDropdown.setText("Здоровье", false)
                typeDropdown.tag = "HEALTH"
            }
        }

    }

    private fun doneReminder() {

        val reminder = Reminder().apply {
            id = arguments?.getInt("reminderId") ?: 0
            description = binding.remainderDescription.text.toString()
            date = currentDateReminder ?: viewModel.reminder.value?.date
            geckoId = selectedGeckoId ?: viewModel.reminder.value?.geckoId
            type = binding.reminderType.tag?.toString() ?: ""
        }

        val bundle = bundleOf(
            "reminderId" to reminder.id,
            "geckoId" to reminder.geckoId,
            "eventType" to reminder.type,
            "eventId" to 0)
        binding.root.findNavController()
            .navigate(R.id.action_reminderFragment_to_eventFragment, bundle)
    }

    private fun saveReminder() {
        val reminder = Reminder().apply {
            id = arguments?.getInt("reminderId") ?: 0
            description = binding.remainderDescription.text.toString()
            date = currentDateReminder ?: viewModel.reminder.value?.date
            geckoId = selectedGeckoId ?: viewModel.reminder.value?.geckoId
            type = binding.reminderType.tag?.toString() ?: ""
        }

        viewModel.saveReminder(reminder)
        findNavController().popBackStack()
    }

    private fun alertSaveReminder() {
        if (binding.reminderPet.text.toString() == "") {
            Snackbar.make(binding.root,
                "Ошибка! Необходимо выбрать питомца",
                Snackbar.LENGTH_SHORT).show()
            return
        } else if (binding.reminderDate.text.toString() == ""){
            Snackbar.make(binding.root,
                "Ошибка! Необходимо добавить дату",
                Snackbar.LENGTH_SHORT).show()
        } else {
            saveReminder()
        }
    }

    private fun deleteReminder() {
        val reminderId = arguments?.getInt("reminderId") ?: 0
        viewModel.deleteReminder(reminderId)
    }

    private fun alertDeleteReminder() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder
            .setMessage("Вы уверены?")
            .setTitle("Удаление напоминания")
            .setCancelable(true)
            .setPositiveButton("Да"){ _, _ ->
                deleteReminder()
            }
            .setNegativeButton("Нет"){ dialog, _ ->
                dialog.cancel()
            }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    // Функция для отображения выбора даты
    private fun showReminderDatePicker() {
        DatePickerHelper.showDateTimePickerDialog(
            requireContext(),
            initialDate = currentDateReminder ?: viewModel.reminder.value?.date,
            onDateTimeSelected = { dateMillis, formattedDate ->
                currentDateReminder = dateMillis
                binding.reminderDate.setText(formattedDate)
            }
        )
    }
}