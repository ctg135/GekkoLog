package salabaev.gekkolog.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import salabaev.gekkolog.R
import salabaev.gekkolog.data.GeckosDatabase
import salabaev.gekkolog.data.event.EventRepository
import salabaev.gekkolog.data.gecko.GeckoRepository
import salabaev.gekkolog.data.reminder.ReminderRepository
import salabaev.gekkolog.databinding.FragmentHomeBinding
import java.util.Calendar
import androidx.navigation.findNavController

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db: GeckosDatabase = GeckosDatabase.getInstance(requireContext())
        val eventRepository = EventRepository(db.EventDao())
        val reminderRepository = ReminderRepository(db.ReminderDao())
        val geckoRepository = GeckoRepository(db.GeckoDao())
        viewModel = HomeViewModel(eventRepository, reminderRepository, geckoRepository)
        // Список уведомлений за сегодня
        binding.notificationsRecycler.layoutManager = LinearLayoutManager(requireContext())
        // Список событий за сегодня
        binding.eventsRecycler.layoutManager = LinearLayoutManager(requireContext())
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
                                .navigate(R.id.action_navigation_home_to_reminderFragment, bundle)
                        },
                        onCompleteClick = { reminder ->
                            val bundle = bundleOf(
                                "reminderId" to reminder.id,
                                "geckoId" to reminder.geckoId,
                                "eventType" to reminder.type,
                                "eventId" to 0)
                            binding.root.findNavController()
                                .navigate(R.id.action_navigation_home_to_eventFragment, bundle)
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
                                .navigate(R.id.action_navigation_home_to_eventFragment, bundle)
                        }
                    )
                }
            }
        }
        // Get today's date range (start and end of day)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startOfDay = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val endOfDay = calendar.timeInMillis

        binding.fabAddEvent.setOnClickListener {
            showEventTypeDialog()
        }
        binding.fabAddReminder.setOnClickListener {
            val bundle = bundleOf(
                "geckoId" to (arguments?.getInt("geckoId") ?: 0),
                "reminderType" to "OTHER"
            )
            binding.root.findNavController()
                .navigate(R.id.action_navigation_home_to_reminderFragment, bundle)
        }
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
                    .navigate(R.id.action_navigation_home_to_eventFragment, bundle)
            }
            .setNegativeButton("Отмена", null)
            .create()
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}