package salabaev.gekkolog.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import salabaev.gekkolog.MainActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Запускаем основную Activity после загрузки
        startActivity(Intent(this, MainActivity::class.java))
        finish()  // Закрываем SplashActivity
    }
}