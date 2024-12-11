package otus.gpb.homework.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class FillFormActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var surnameInput: EditText
    private lateinit var ageInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_fill_form)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        nameInput = findViewById(R.id.et_name)
        surnameInput = findViewById(R.id.et_surname)
        ageInput = findViewById(R.id.et_age)


        val intentData: Profile? = intent.getParcelableExtra(EXTRA_PROFILE_KEY)
        if (intentData != null) {
            nameInput.setText(intentData.name)
            surnameInput.setText(intentData.surname)
            ageInput.setText(intentData.age.toString())
        }

        findViewById<Button>(R.id.btn_apply).setOnClickListener {
            returnToEditProfileActivity()
        }
    }

    private fun returnToEditProfileActivity() {
        val nameInput = nameInput.text.toString()
        val surnameInput = surnameInput.text.toString()
        val ageInput = ageInput.text.toString()
        if (nameInput.isNotBlank() && surnameInput.isNotBlank() && ageInput.isNotBlank()) {
            val profile = Profile(nameInput, surnameInput, ageInput.toInt())
            val dataIntent = Intent()
            dataIntent.putExtra(EXTRA_PROFILE_KEY, profile)
            setResult(RESULT_OK, dataIntent)
            finish()
        } else Toast.makeText(this, "Все поля должны быть заполнены!", Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_PROFILE_KEY = "EXTRA_PROFILE_KEY"
    }
}