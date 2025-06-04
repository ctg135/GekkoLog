package salabaev.gekkolog.ui.journal

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.text.set
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import salabaev.gekkolog.R
import salabaev.gekkolog.data.GeckosDatabase
import salabaev.gekkolog.data.event.EventRepository
import salabaev.gekkolog.data.gecko.GeckoRepository
import salabaev.gekkolog.data.reminder.ReminderRepository
import salabaev.gekkolog.databinding.FragmentJournalBinding
import salabaev.gekkolog.ui.home.EventAdapter
import salabaev.gekkolog.ui.home.NotificationAdapter
import salabaev.gekkolog.ui.utils.DatePickerHelper
import java.util.Calendar

class JournalFragment : Fragment() {

    companion object {
        fun newInstance() = JournalFragment()
    }

    private var _binding: FragmentJournalBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: JournalViewModel

    private var selectedDate: Long? = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }.timeInMillis

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        binding.datePicker.setText("")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJournalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db: GeckosDatabase = GeckosDatabase.getInstance(requireContext())
        val eventRepository = EventRepository(db.EventDao())
        val reminderRepository = ReminderRepository(db.ReminderDao())
        val geckoRepository = GeckoRepository(db.GeckoDao())
        viewModel = JournalViewModel(eventRepository, reminderRepository, geckoRepository)
        // Список уведомлений за сегодня
        binding.notificationsRecycler.layoutManager = LinearLayoutManager(requireContext())
        // Список событий за сегодня
        binding.eventsRecycler.layoutManager = LinearLayoutManager(requireContext())
        //
        binding.datePicker.setOnClickListener { showDatePicker() }


        // Обработчики кликов по FAB
        binding.fabAddEvent.setOnClickListener {
            showEventTypeDialog()
        }
        binding.fabAddReminder.setOnClickListener {
            val bundle = bundleOf(
                "geckoId" to (arguments?.getInt("geckoId") ?: 0),
                "reminderType" to "OTHER"
            )
            binding.root.findNavController()
                .navigate(R.id.action_navigation_journal_to_reminderFragment, bundle)
        }

        // Observe today's notifications
        viewModel.getGeckos { geckosMap ->
            viewModel.todayNotifications.observe(viewLifecycleOwner) { notifications ->
                if (notifications.isNullOrEmpty()) {
                    binding.emptyNotifications.visibility = View.VISIBLE
                    binding.notificationsRecycler.visibility = View.GONE
                } else {
                    binding.emptyNotifications.visibility = View.GONE
                    binding.notificationsRecycler.visibility = View.VISIBLE
                    binding.notificationsRecycler.adapter = NotificationAdapter(
                        notifications,
                        geckosMap,
                        onEditClick = { reminder ->
                            val bundle = bundleOf(
                                "reminderId" to reminder.id
                            )
                            binding.root.findNavController()
                                .navigate(R.id.action_navigation_journal_to_reminderFragment, bundle)
                        },
                        onCompleteClick = { reminder ->
                            val bundle = bundleOf(
                                "reminderId" to reminder.id,
                                "geckoId" to reminder.geckoId,
                                "eventType" to reminder.type,
                                "eventId" to 0)
                            binding.root.findNavController()
                                .navigate(R.id.action_navigation_journal_to_eventFragment, bundle)
                        }
                    )
                }
            }
            viewModel.todayEvents.observe(viewLifecycleOwner) { events ->
                if (events.isNullOrEmpty()) {
                    binding.eventsRecycler.visibility = View.GONE
                    binding.emptyEvents.visibility = View.VISIBLE
                } else {
                    binding.emptyEvents.visibility = View.GONE
                    binding.eventsRecycler.visibility = View.VISIBLE
                    binding.eventsRecycler.adapter = EventAdapter(
                        events,
                        geckosMap,
                        onItemClick = { event ->
                            val bundle = bundleOf("eventId" to event.id,
                                "eventType" to event.type)
                            binding.root.findNavController()
                                .navigate(R.id.action_navigation_journal_to_eventFragment, bundle)
                        }
                    )
                }
            }
        }

        updateList()
    }

    private fun updateList() {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = selectedDate!!
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startOfDay = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val endOfDay = calendar.timeInMillis

        // Load data
        viewModel.loadTodayNotifications(startOfDay, endOfDay)
        viewModel.loadTodayEvents(startOfDay, endOfDay)
    }


    private fun showEventTypeDialog() {
        val eventTypes = resources.getStringArray(R.array.event_types)
        val eventTypeValues = resources.getStringArray(R.array.event_type_values)

        AlertDialog.Builder(requireContext())
            .setTitle("Тип события?")
            .setItems(eventTypes) { _, which ->

                val selectedType = eventTypeValues[which]
                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                builder.apply {
                    setMessage("Выберите тип события")
                }
                val bundle = bundleOf(
                    "eventType" to selectedType,
                    "eventId" to 0,
                    "geckoId" to (arguments?.getInt("geckoId") ?: 0))
                binding.root.findNavController()
                    .navigate(R.id.action_navigation_journal_to_eventFragment, bundle)
            }
            .setNegativeButton("Отмена", null)
            .create()
            .show()
    }

    // Отображение диалогового окна с выбором даты рождения
    private fun showDatePicker() {
        DatePickerHelper.showDatePickerDialog(
            requireContext(),
            initialDate = selectedDate,
            maxDate = null,
            onDateSelected = { dateMillis, formattedDate ->
                selectedDate = dateMillis
                binding.datePicker.setText(formattedDate)
                updateList()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}