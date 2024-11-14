package otus.gpb.homework.activities.sender

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import otus.gpb.homework.activities.receiver.R

class SenderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sender)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val buttonOpenMaps: Button = findViewById(R.id.to_maps_button)
        val buttonSendEmail: Button = findViewById(R.id.send_email_button)
        val buttonOpenReceiver: Button = findViewById(R.id.open_receiver_button)

        buttonOpenMaps.setOnClickListener {
            openGoogleMaps()
        }

        buttonSendEmail.setOnClickListener {
            sendEmail()
        }

        buttonOpenReceiver.setOnClickListener {
            openReceiver()
        }
    }

    private fun openReceiver() {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            addCategory(Intent.CATEGORY_DEFAULT)
            intent.putExtra(TITLE_KEY, "Славные парни")
            intent.putExtra(YEAR_KEY, "2016")
            intent.putExtra(
                DESCRIPTION_KEY,
                """
                    Что бывает, когда напарником брутального костолома становится субтильный лопух?
                     Наемный охранник Джексон Хили и частный детектив Холланд Марч вынуждены работать
                      в паре, чтобы распутать плевое дело о пропавшей девушке, которое оборачивается
                       преступлением века. Смогут ли парни разгадать сложный ребус, если у каждого
                        из них – свои, весьма индивидуальные методы.
                """.trimIndent()
            )
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "Нет приложения для обработки Intent", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendEmail() {
        val to = "android@otus.ru"
        val theme = "Hello from Kotlin!"
        val message = "This is a test email sent from my Kotlin application."

        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$to")
            putExtra(Intent.EXTRA_SUBJECT, theme)
            putExtra(Intent.EXTRA_TEXT, message)
        }

        startActivity(emailIntent)
    }

    private fun openGoogleMaps() {
        val latitudeMoscow = 55.7558
        val longitudeMoscow = 37.6173
        val restaurants = "Restaurants"
        val uri = Uri.parse("geo:$latitudeMoscow,$longitudeMoscow?q=$restaurants")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        startActivity(intent)
    }

    companion object {
        const val TITLE_KEY = "TITLE_KEY"
        const val YEAR_KEY = "YEAR_KEY"
        const val DESCRIPTION_KEY = "DESCRIPTION_KEY"
    }
}