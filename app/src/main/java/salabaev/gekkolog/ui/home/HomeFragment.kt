package salabaev.gekkolog.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import salabaev.gekkolog.data.GeckosDatabase
import salabaev.gekkolog.data.event.EventRepository
import salabaev.gekkolog.data.gecko.GeckoRepository
import salabaev.gekkolog.data.reminder.ReminderRepository
import salabaev.gekkolog.databinding.FragmentHomeBinding
import java.util.Calendar

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
        // Setup notifications RecyclerView
        binding.notificationsRecycler.layoutManager = LinearLayoutManager(requireContext())

        // Setup events RecyclerView
        binding.eventsRecycler.layoutManager = LinearLayoutManager(requireContext())


        // Observe today's notifications
        viewModel.getGeckos { geckosMap ->
            viewModel.todayNotifications.observe(viewLifecycleOwner) { notifications ->
                binding.notificationsRecycler.adapter = NotificationAdapter(
                    notifications,
                    geckosMap,
                    onEditClick = { reminder ->
                        Log.d("TEST", "Going to edit remainder " + reminder.id.toString())
                        // Navigate to ReminderFragment for editing
                        // findNavController().navigate(...)
                    },
                    onCompleteClick = { reminder ->
                        Log.d("TEST", "Going to done reminder " + reminder.id.toString())
                        // Navigate to EventFragment to create event
                        // findNavController().navigate(...)
                    }
                )
            }

            // Observe today's events
            viewModel.todayEvents.observe(viewLifecycleOwner) { events ->
                binding.eventsRecycler.adapter = EventAdapter(
                    events,
                    geckosMap,
                    onItemClick = { event ->
                        Log.d("TEST", "Going to event " + event.id.toString())
                        // Navigate to EventFragment for editing
                        // findNavController().navigate(...)
                    }
                )
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

        // Load data
        viewModel.loadTodayNotifications(startOfDay, endOfDay)
        viewModel.loadTodayEvents(startOfDay, endOfDay)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}