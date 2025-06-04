package salabaev.gekkolog.ui.pets.edit

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import salabaev.gekkolog.data.GeckosDatabase
import salabaev.gekkolog.databinding.FragmentPetEditBinding
import java.io.File
import salabaev.gekkolog.data.gecko.Gecko
import salabaev.gekkolog.data.gecko.GeckoRepository
import java.io.FileOutputStream
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.navigation.Navigation
import salabaev.gekkolog.ui.utils.DatePickerHelper
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import salabaev.gekkolog.R
import salabaev.gekkolog.data.event.EventRepository

class PetEditFragment : Fragment() {

    companion object {
        private const val REQUEST_IMAGE_PICK = 1001


        fun newInstance(geckoId: Int = 0): PetEditFragment {
            return PetEditFragment().apply {
                val gecko = bundleOf("geckoId" to geckoId)
                arguments = gecko
            }
        }
    }

    private var _binding: FragmentPetEditBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PetEditViewModel

    // Переменные для получения данных из форм
    private var currentPhotoUri: Uri? = null
    private var selectedBirthDate: Long? = null
    private var geckoLastFeed: String? = null
    private var geckoLastWeight: String? = null
    private var geckoLastShed: LiveData<String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPetEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dao = GeckosDatabase.getInstance(requireContext()).GeckoDao()
        val eventsDao = GeckosDatabase.getInstance(requireContext()).EventDao()
        viewModel = PetEditViewModel(GeckoRepository(dao), EventRepository(eventsDao))

        loadGeckoData()
        setupViews()
        observeViewModel()
    }

    // Начальная настройка UI
    private fun setupViews() {
        // Панель наверху экрана
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        // Выбор аватарки
        binding.geckoImage.setOnClickListener { selectImageFromGallery() }
        // Сохранение питомца
        binding.saveButton.setOnClickListener { savePet() }
        // Удаление питомца
        binding.deleteButton.setOnClickListener { alertDeletePet() }
        // Выбор периода для кормления
        binding.feedPeriodEdit.setOnClickListener { showFeedPeriodPicker() }
        // Для выбора возраста
        binding.birthDateEdit.setOnClickListener { showBirthDatePicker() }

        // Для выбора пола
        val genders = listOf(
            // "M" - значение для базы, "Самец" - отображаемый текст
            Pair("M", getString(salabaev.gekkolog.R.string.male)),
            // "F" - значение для базы, "Самка" - отображаемый текст
            Pair("F", getString(salabaev.gekkolog.R.string.female)))

        val adapter = ArrayAdapter(
            requireContext(),
            salabaev.gekkolog.R.layout.item_dropdown_menu_1line,
            genders.map { it.second }
        )

        val genderDropdown = binding.genderEdit
        genderDropdown.setAdapter(adapter)

        genderDropdown.setOnItemClickListener { _, _, position, _ ->
            // Сохраняем выбранное значение ("M" или "F") в тег
            genderDropdown.tag = genders[position].first
        }

        // Если геккон только создается, то не отображать его статистику
        arguments?.getInt("geckoId")?.let { geckoId ->
            if (geckoId == 0) {
                binding.statisticsLayout.visibility = View.GONE
            }
        }

        // Обработчики кнопок панели со статистикой
        binding.lastAteValue.setOnClickListener { createFoodEvent() }
        binding.lastShedValue.setOnClickListener { createShedEvent() }
        binding.lastWeightValue.setOnClickListener { createWeightEvent() }
    }

    private fun observeViewModel() {
        viewModel.gecko.observe(viewLifecycleOwner) { gecko ->
            gecko?.let { updateUI(it) }
        }
        viewModel.lastWeight.observe(viewLifecycleOwner) { value ->
            value?.let { binding.lastWeightValue.text = it }
        }
        viewModel.lastShed.observe(viewLifecycleOwner) { value ->
            value?.let { binding.lastShedValue.text = it }
        }
        viewModel.lastFeed.observe(viewLifecycleOwner) { value ->
            value?.let { binding.lastAteValue.text = it }
        }
    }

    // Загрузка данных питомца из БД
    private fun loadGeckoData() {
        arguments?.getInt("geckoId")?.let { geckoId ->
            if (geckoId != 0) {
                // Загрузка данных геккона
                viewModel.loadGecko(geckoId)
                viewModel.getLastFeed(geckoId)
                viewModel.getLastShed(geckoId)
                viewModel.getLastWeight(geckoId)
            }
        }
    }

    // Обновление UI
    private fun updateUI(gecko: Gecko) {
        // Текстовые поля
        binding.nameEdit.setText(gecko.name)
        binding.morphEdit.setText(gecko.morph)
        binding.feedPeriodEdit.setText(gecko.feedPeriod?.toString() ?: "1")
        Log.d("TEST", "show")

        gecko.birthDate?.let {
            selectedBirthDate = it
            binding.birthDateEdit.setText(DatePickerHelper.formatDate(it))
        }

        // Загрузка аватарки питомца
        gecko.photoPath?.let { path: String ->
            Glide.with(this)
                .load(File(path))
                .into(binding.geckoImage)
        }

        // Выпадающий список для выбора пола
        val genderDropdown = binding.genderEdit as MaterialAutoCompleteTextView
        when (gecko.gender) {
            "M" -> {
                genderDropdown.setText(getString(salabaev.gekkolog.R.string.male), false)
                genderDropdown.tag = "M"
            }
            "F" -> {
                genderDropdown.setText(getString(salabaev.gekkolog.R.string.female), false)
                genderDropdown.tag = "F"
            }
        }
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
                    .into(binding.geckoImage)
            }
        }
    }


    // Сохранение изображения во внешнюю память
    private fun saveImageToInternalStorage(uri: Uri): File {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val file = File(requireContext().filesDir, "gecko_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { output ->
            inputStream?.copyTo(output)
        }
        return file
    }

    // Сохранение в БД
    private fun savePet() {
        val gecko = Gecko().apply {
            id = arguments?.getInt("geckoId") ?: 0
            name = binding.nameEdit.text.toString()
            morph = binding.morphEdit.text.toString()
            gender = binding.genderEdit.tag?.toString() ?: ""
            feedPeriod = binding.feedPeriodEdit.text.toString().toIntOrNull() ?: 1
            birthDate = selectedBirthDate ?: viewModel.gecko.value?.birthDate
            viewModel.gecko.observe(viewLifecycleOwner) { geckoViewModel ->
                if (currentPhotoUri != null) {
                    // Если выбрано новое фото
                    photoPath = saveImageToInternalStorage(currentPhotoUri!!).absolutePath
                } else if (geckoViewModel?.photoPath != null) {
                    // Если фото не изменялось
                    photoPath = geckoViewModel.photoPath
                }
                else {
                    // Если фото не было выбрано
                    photoPath = null
                }
            }
            // Если геккон только создан и было выбрано фото
            if (id == 0 && currentPhotoUri != null) {
                photoPath = saveImageToInternalStorage(currentPhotoUri!!).absolutePath
            }
        }
        viewModel.saveGecko(gecko)
        findNavController().popBackStack()
    }

    // Подтверждение удаления
    private fun alertDeletePet() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder
            .setMessage("Вы уверены?")
            .setTitle("Удаление питомца")
            .setCancelable(true)
            .setPositiveButton("Да"){ _, _ ->
                deletePet()
            }
            .setNegativeButton("Нет"){ dialog, _ ->
                dialog.cancel()
            }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    // Функция для удаления из БД
    private fun deletePet() {
        val geckoId = arguments?.getInt("geckoId") ?: 0
        if (geckoId != 0) {
            viewModel.deleteGecko(geckoId)
            viewModel.gecko.value?.photoPath?.let { path ->
                File(path).takeIf { it.exists() }?.delete()
            }
        }
        findNavController().popBackStack()
    }

    // Функция для выбора периода кормления
    private fun showFeedPeriodPicker() {
        val currentValue = binding.feedPeriodEdit.text.toString().toIntOrNull() ?: 1
        val dialogView = LayoutInflater.from(requireContext()).inflate(salabaev.gekkolog.R.layout.dialog_number_picker, null)
        val numberPicker = dialogView.findViewById<NumberPicker>(salabaev.gekkolog.R.id.numberPicker).apply {
            minValue = 1
            maxValue = 14
            value = currentValue.coerceIn(1, 14)
            wrapSelectorWheel = false
        }

        AlertDialog.Builder(requireContext()).apply {
            setTitle("Выберите период кормления (дни)")
            setView(dialogView)
            setPositiveButton("Oк") { _, _ ->
                // Сохраняем выбранное значение
                binding.feedPeriodEdit.setText(numberPicker.value.toString())
            }
            setNegativeButton("Отмена", null)
        }.show()
    }

    // Отображение диалогового окна с выбором даты рождения
    private fun showBirthDatePicker() {
        DatePickerHelper.showDatePickerDialog(
            requireContext(),
            initialDate = selectedBirthDate ?: viewModel.gecko.value?.birthDate,
            maxDate = System.currentTimeMillis(),
            onDateSelected = { dateMillis, formattedDate ->
                selectedBirthDate = dateMillis
                binding.birthDateEdit.setText(formattedDate)
            }
        )
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        arguments?.getInt("geckoId")?.let { geckoId ->
            if (geckoId != 0) {
                inflater.inflate(salabaev.gekkolog.R.menu.pet_edit_menu, menu)
            }
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    // Обработка меню наверху экрана
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                true
            }
            salabaev.gekkolog.R.id.pet_edit_item_reminder -> {
                createReminder()
                true
            }
            salabaev.gekkolog.R.id.pet_edit_item_ate -> {
                createFoodEvent()
                true
            }
            salabaev.gekkolog.R.id.pet_edit_item_shed -> {
                createShedEvent()
                true
            }
            salabaev.gekkolog.R.id.pet_edit_item_weight -> {
                createWeightEvent()
                true
            }
            salabaev.gekkolog.R.id.pet_edit_item_health -> {
                createHealthEvent()
                true
            }
            salabaev.gekkolog.R.id.pet_edit_item_other -> {
                createOtherEvent()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Функции для создания событий
    private fun createFoodEvent() {
        val bundle = bundleOf(
            "eventType" to "FEED",
            "eventId" to 0,
            "geckoId" to (arguments?.getInt("geckoId") ?: 0))
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_petEditFragment_to_eventFragment, bundle)
    }

    private fun createShedEvent() {
        var geckoId = arguments?.getInt("geckoId") ?: 0
        val bundle = bundleOf(
            "eventType" to "SHED",
            "eventId" to 0,
            "geckoId" to (arguments?.getInt("geckoId") ?: 0))
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_petEditFragment_to_eventFragment, bundle)
    }

    private fun createWeightEvent() {
        val bundle = bundleOf(
            "eventType" to "WEIGHT",
            "eventId" to 0,
            "geckoId" to (arguments?.getInt("geckoId") ?: 0))
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_petEditFragment_to_eventFragment, bundle)
    }

    private fun createHealthEvent() {
        val bundle = bundleOf(
            "eventType" to "HEALTH",
            "eventId" to 0,
            "geckoId" to (arguments?.getInt("geckoId") ?: 0))
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_petEditFragment_to_eventFragment, bundle)
    }

    private fun createOtherEvent() {
        val bundle = bundleOf(
            "eventType" to "OTHER",
            "eventId" to 0,
            "geckoId" to (arguments?.getInt("geckoId") ?: 0))
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_petEditFragment_to_eventFragment, bundle)
    }

    // Для создания какого-либо напоминания
    private fun createReminder() {
        val bundle = bundleOf(
            "geckoId" to (arguments?.getInt("geckoId") ?: 0),
            "reminderType" to "OTHER"
        )
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_petEditFragment_to_reminderFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}