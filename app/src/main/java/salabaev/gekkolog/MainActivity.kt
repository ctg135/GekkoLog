package salabaev.gekkolog

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import salabaev.gekkolog.data.GeckosDatabase
import salabaev.gekkolog.data.gecko.Gecko
import salabaev.gekkolog.data.gecko.GeckoRepository
import salabaev.gekkolog.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_pets,
                R.id.navigation_journal,
                R.id.navigation_info
            )
        )

        navView.setupWithNavController(navController)

//        print("test")
//        Log.d("TEST", "TEST")
//        // Example of usage
//        val gecko = Gecko()
//        gecko.name = "nigga"
//        gecko.gender = "M"
//        gecko.morph = "Black"
//        gecko.feedPeriod = 7
//        val db = GeckosDatabase.getInstance(application)
//        val geckoDao = db.GeckoDao()
//        val geckoRepo = GeckoRepository(geckoDao)
//        geckoRepo.addGecko(gecko)
//        geckoRepo.geckoList?.observe(this){ geckos ->
//            Log.d("TEST", "Данные обновились! Количество: ${geckos.size}")
//            Log.d("TEST", "Имя первого геккона: ${geckos[0].name}")
//        }

//        Не работает
//        geckoDao.addGecko(gecko)
//
//        Log.d("TEST", "total values: ${geckoRepo.geckoList?.value?.size}")
//        Log.d("TEST", geckoRepo.geckoList?.value?.get(0)?.name.toString())
    }
}