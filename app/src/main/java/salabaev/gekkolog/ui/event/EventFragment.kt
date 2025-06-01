package salabaev.gekkolog.ui.event

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import salabaev.gekkolog.data.GeckosDatabase
import salabaev.gekkolog.data.event.Event
import salabaev.gekkolog.data.event.EventRepository
import salabaev.gekkolog.data.gecko.Gecko
import salabaev.gekkolog.data.gecko.GeckoRepository
import salabaev.gekkolog.data.reminder.ReminderRepository
import salabaev.gekkolog.databinding.FragmentEventBinding
import salabaev.gekkolog.ui.utils.DatePickerHelper
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar

class EventFragment : Fragment() {

    companion object {
        private const val REQUEST_IMAGE_PICK = 1001
        fun newInstance() = EventFragment()
    }

    private var _binding: FragmentEventBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: EventViewModel
    private var currentPhotoUri: Uri? = null
    private var currentDateEvent: Long? = null
    private lateinit var geckoList: LiveData<List<Gecko>>
    private var selectedGeckoId: Int? = null

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

        val db = GeckosDatabase.getInstance(requireContext())
        val daoEvents = db.EventDao()
        val daoGeckos = db.GeckoDao()
        val daoRemainder = db.ReminderDao()
        viewModel = EventViewModel(
            EventRepository(daoEvents),
            GeckoRepository(daoGeckos),
            ReminderRepository(daoRemainder)
        )
        // Получаем список гекконов
        geckoList = viewModel.geckoRepository.geckoList ?: daoGeckos.getGeckos()

        observeViewModel()
        loadData()
        setupViews()
    }

    // Загрузка данных события из БД
    private fun loadData() {
        // Получаем geckoId из аргументов
        selectedGeckoId = arguments?.getInt("geckoId")
        // Если существующее событие, то подгружаем из памяти
        arguments?.getInt("eventId")?.let { eventId ->
            if (eventId != 0) {
                viewModel.loadEvent(eventId)
            } else {
                val currentDate = Calendar.getInstance().timeInMillis
                viewModel.event.value?.date = currentDate
                currentDateEvent = currentDate
                binding.eventDate.setText(DatePickerHelper.formatDateTime(currentDate))

                // Устанавливаем выбранного геккона для нового события
                selectedGeckoId?.let { geckoId ->
                    viewModel.geckoRepository.getGecko(geckoId).observe(viewLifecycleOwner) { gecko ->
                        gecko?.let {
                            binding.eventPet.setText(it.name, false)
                        }
                    }
                }
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
        binding.layoutFeed.visibility = View.GONE
        binding.layoutShed.visibility = View.GONE
        binding.layoutWeight.visibility = View.GONE
        binding.layoutHealthOther.visibility = View.GONE
        arguments?.getString("eventType")?.let { eventType ->
            when (eventType) {
                "FEED" -> {
                    binding.layoutFeed.visibility = View.VISIBLE
                }
                "SHED" -> {
                    binding.layoutShed.visibility = View.VISIBLE
                }
                "WEIGHT" -> {
                    binding.layoutWeight.visibility = View.VISIBLE
                }
                "HEALTH" -> {
                    binding.layoutHealthOther.visibility = View.VISIBLE
                }
                "OTHER" -> {
                    binding.layoutHealthOther.visibility = View.VISIBLE
                }
                else -> {}
            }
        }

        // Настройка обработчиков
        binding.eventImage.setOnClickListener { selectImageFromGallery() }
        binding.saveButton.setOnClickListener { saveEvent() }
        binding.deleteButton.setOnClickListener { alertDeleteEvent() }
        binding.eventDate.setOnClickListener { showEventDatePicker() }
        // Выпадающий список питомцев
        geckoList.observe(viewLifecycleOwner) { geckos ->
            val adapter = GeckoDropdownAdapter(
                requireContext(),
                geckos,
                binding.eventPet
            )
            binding.eventPet.setAdapter(adapter)

            // Устанавливаем обработчик выбора
            viewModel.geckoRepository.geckoList?.observe(viewLifecycleOwner) { geckos ->
                val adapter = GeckoDropdownAdapter(
                    requireContext(),
                    geckos,
                    binding.eventPet
                )
                binding.eventPet.setAdapter(adapter)

                // Устанавливаем обработчик выбора
                binding.eventPet.setOnItemClickListener { _, _, position, _ ->
                    selectedGeckoId = geckos[position].id
                    binding.eventPet.setText(geckos[position].name, false)
                }

                // Устанавливаем текущее значение, если geckoId передан или есть в событии
                selectedGeckoId?.let { geckoId ->
                    geckos.find { it.id == geckoId }?.let { gecko ->
                        binding.eventPet.setText(gecko.name, false)
                    }
                }
            }
        }
        arguments?.getInt("reminderId")?.let {
            binding.eventPet.isEnabled = false
        }
    }

    // Обновление UI
    private fun updateUI(event: Event) {
        // Установка имени геккона
        event.geckoId?.let { geckoId ->
            viewModel.geckoRepository.getGecko(geckoId).observe(viewLifecycleOwner) { gecko ->
                gecko?.let {
                    binding.eventPet.setText(it.name ?: "Без имени", false)
                    selectedGeckoId = geckoId
                }
            }
        }
        binding.eventDate.setText(
            DatePickerHelper.formatDateTime(
                event.date!!))
        // Установка необходимых полей
        arguments?.getString("eventType")?.let { eventType ->
            when (eventType) {
                "FEED" -> {
                    when (event.feedType) {
                        "CA" -> { binding.radioCalcium.isChecked = true }
                        "VIT" -> { binding.radioVitamin.isChecked = true }
                    }
                    event.feedSucces?.let { binding.checkFeedDeny.isChecked = it }
                    event.description?.let { binding.eventFeedDescription.setText(it) }
                }
                "SHED" -> {
                    event.shedSuccess?.let { binding.eventShedSuccess.isChecked = it }
                }
                "WEIGHT" -> {
                    event.weight?.let { binding.eventWeight.setText(it.toString()) }
                }
                "HEALTH" -> {
                    event.description?.let { binding.eventDescription.setText(it) }
                    event.photoPath?.let {
                        Glide.with(this)
                            .load(File(it))
                            .into(binding.eventImage)
                    }
                }
                "OTHER" -> {
                    event.description?.let { binding.eventDescription.setText(it) }
                    event.photoPath?.let {
                        Glide.with(this)
                            .load(File(it))
                            .into(binding.eventImage)
                    }
                }
                else -> {}
            }
        }
    }

    private fun saveEvent() {
        val event = Event().apply {
            id = arguments?.getInt("eventId") ?: 0
            date = currentDateEvent ?: viewModel.event.value?.date
            geckoId = selectedGeckoId ?: viewModel.event.value?.geckoId
            arguments?.getString("eventType")?.let { eventType ->
                when (eventType) {
                    "FEED" -> {
                        type = "FEED"
                        if (binding.radioCalcium.isChecked){
                            feedType = "CA"
                        } else if (binding.radioVitamin.isChecked) {
                            feedType = "VIT"
                        }
                        feedSucces = binding.checkFeedDeny.isChecked
                        description = binding.eventFeedDescription.text.toString()
                    }
                    "SHED" -> {
                        type = "SHED"
                        shedSuccess = binding.eventShedSuccess.isChecked
                    }
                    "WEIGHT" -> {
                        type = "WEIGHT"
                        weight = binding.eventWeight.text.toString().toFloatOrNull()
                    }
                    "HEALTH" -> {
                        type = "HEALTH"
                        description = binding.eventDescription.text.toString()
                        viewModel.event.observe(viewLifecycleOwner) { eventViewModel ->
                            if (currentPhotoUri != null) {
                                // Если выбрано новое фото
                                photoPath = saveImageToInternalStorage(currentPhotoUri!!).absolutePath
                            } else if (eventViewModel?.photoPath != null) {
                                // Если фото не изменлось
                                photoPath = eventViewModel.photoPath
                            } else {
                                // Если фото не было выбрано
                                photoPath = null
                            }
                        }
                        // При создании события
                        if (id == 0 && currentPhotoUri != null) {
                            photoPath = saveImageToInternalStorage(currentPhotoUri!!).absolutePath
                        }
                    }
                    "OTHER" -> {
                        type = "OTHER"
                        description = binding.eventDescription.text.toString()
                        viewModel.event.observe(viewLifecycleOwner) { eventViewModel ->
                            if (currentPhotoUri != null) {
                                // Если выбрано новое фото
                                photoPath = saveImageToInternalStorage(currentPhotoUri!!).absolutePath
                            } else if (eventViewModel?.photoPath != null) {
                                // Если фото не изменлось
                                photoPath = eventViewModel.photoPath
                            } else {
                                // Если фото не было выбрано
                                photoPath = null
                            }
                        }
                        // При создании события
                        if (id == 0 && currentPhotoUri != null) {
                            photoPath = saveImageToInternalStorage(currentPhotoUri!!).absolutePath
                        }
                        // При создании события
                        if (id == 0 && currentPhotoUri != null) {
                            photoPath = saveImageToInternalStorage(currentPhotoUri!!).absolutePath
                        }
                    }
                }
            }
        }
        viewModel.saveEvent(event)

        arguments?.getInt("reminderId")?.let {
            viewModel.deleteReminder(it)
        }

        findNavController().popBackStack()
    }

    // Функция сохранения загруженного файла в хранилище
    private fun saveImageToInternalStorage(uri: Uri): File {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val file = File(requireContext().filesDir, "event_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { output ->
            inputStream?.copyTo(output)
        }
        return file
    }

    // Функция для запроса картинки из галлереи
    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    // При получении изображения из галлереи
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                currentPhotoUri = uri
                Glide.with(this)
                    .load(uri)
                    .into(binding.eventImage)
            }
        }
    }

    // Удаление из БД записи
    private fun deleteEvent() {
        val eventId = arguments?.getInt("eventId") ?: 0
        viewModel.deleteEvent(eventId)
    }

    // Предупреждение перед удалением записи
    private fun alertDeleteEvent() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder
            .setMessage("Вы уверены?")
            .setTitle("Удаление записи")
            .setCancelable(true)
            .setPositiveButton("Да"){ _, _ ->
                deleteEvent()
            }
            .setNegativeButton("Нет"){ dialog, _ ->
                dialog.cancel()
            }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    // Функция для отображения выбора даты
    private fun showEventDatePicker() {
        DatePickerHelper.showDateTimePickerDialog(
            requireContext(),
            initialDate = currentDateEvent ?: viewModel.event.value?.date,
            onDateTimeSelected = { dateMillis, formattedDate ->
                currentDateEvent = dateMillis
                binding.eventDate.setText(formattedDate)
            }
        )
    }
}
