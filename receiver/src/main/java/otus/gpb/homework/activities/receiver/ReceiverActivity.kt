package otus.gpb.homework.activities.receiver

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ReceiverActivity : AppCompatActivity() {

    private lateinit var titleTextView: TextView
    private lateinit var yearTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var posterImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receiver)

        titleTextView = findViewById(R.id.titleTextView)
        yearTextView = findViewById(R.id.yearTextView)
        descriptionTextView = findViewById(R.id.descriptionTextView)
        posterImageView = findViewById(R.id.posterImageView)

        val intent = intent
        if (intent.action == Intent.ACTION_SEND) {
            val title = intent.getStringExtra(TITLE_KEY)
            val year = intent.getStringExtra(YEAR_KEY)
            val description = intent.getStringExtra(DESCRIPTION_KEY)

            titleTextView.text = title
            yearTextView.text = year
            descriptionTextView.text = description

            val posterResId = when (title) {
                "Славные парни" -> R.drawable.niceguys
                else -> R.drawable.interstellar
            }

            posterImageView.setImageDrawable(getDrawable(posterResId))
        }
    }

    companion object {
        const val TITLE_KEY = "TITLE_KEY"
        const val YEAR_KEY = "YEAR_KEY"
        const val DESCRIPTION_KEY = "DESCRIPTION_KEY"
    }
}

