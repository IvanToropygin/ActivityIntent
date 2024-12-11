package otus.gpb.homework.activities

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class EditProfileActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var editProfileBtn: Button
    private lateinit var name: TextView
    private lateinit var surname: TextView
    private lateinit var age: TextView
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        imageView = findViewById(R.id.imageview_photo)
        editProfileBtn = findViewById(R.id.button4)
        name = findViewById(R.id.textview_name)
        surname = findViewById(R.id.textview_surname)
        age = findViewById(R.id.textview_age)


        findViewById<Toolbar>(R.id.toolbar).apply {
            inflateMenu(R.menu.menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.send_item -> {
                        openSenderApp()
                        true
                    }
                    else -> false
                }
            }
        }

        imageView.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setNegativeButton(resources.getString(R.string.make_photo)) { _, _ ->
                    launchPermissionCameraLauncher()
                }.setPositiveButton(resources.getString(R.string.choose_photo)) { _, _ ->
                    takePicture.launch("image/*")
                }
                .show()
        }

        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val returnedProfile: Profile? = result.data?.getParcelableExtra(FillFormActivity.EXTRA_PROFILE_KEY)
                name.text = returnedProfile?.name
                surname.text = returnedProfile?.surname
                age.text = returnedProfile?.age.toString()
            }
        }

        editProfileBtn.setOnClickListener {

            val intent = Intent(this, FillFormActivity::class.java)
            val profileData: Profile

            if (checkTextViewsNotEmpty()) {
                profileData = Profile(name = name.text.toString(),
                    surname = surname.text.toString(),
                    age = age.text.toString().toInt())
                intent.putExtra(FillFormActivity.EXTRA_PROFILE_KEY, profileData)
            }

            startForResult.launch(intent) }
    }

    private fun checkTextViewsNotEmpty(): Boolean {
        return (name.text.toString().isNotBlank() ||
                surname.text.toString().isNotBlank() ||
                age.text.toString().isNotBlank())
    }

    private fun launchPermissionCameraLauncher() {
        permissionCameraLauncher.launch(Manifest.permission.CAMERA)
    }

    private val permissionCameraLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                imageView.setImageResource(R.drawable.cat)
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this, Manifest.permission.CAMERA
                    )
                ) {
                    showRationaleDialog()
                } else showSettingsDialog()
            }
        }

    private fun showRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Необходим доступ к камере")
            .setMessage("Это необходимо для того, чтобы сделать фото профиля.")
            .setPositiveButton("Дать доступ") { _, _ -> launchPermissionCameraLauncher() }
            .setNegativeButton(getString(R.string.abort)) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Доступ к камере отключен")
            .setMessage("Пожалуйста, разрешите доступ к камере в настройках приложения.")
            .setPositiveButton("Открыть настройки") { _: DialogInterface?, _: Int ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.setData(uri)
                startActivity(intent)
            }
            .setNegativeButton(
                R.string.abort
            ) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
            .show()
    }

    private val takePicture = registerForActivityResult(ActivityResultContracts.GetContent()) {
        uri ->
        if (uri != null) {
            populateImage(uri)
            imageUri = uri
        }
    }

    /**
     * Используйте этот метод чтобы отобразить картинку полученную из медиатеки в ImageView
     */
    private fun populateImage(uri: Uri) {
        val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
        imageView.setImageBitmap(bitmap)
    }

    private fun openSenderApp() {
//        "В качестве реализации метода отправьте неявный Intent чтобы поделиться профилем. В качестве extras передайте заполненные строки и картинку"
        if (!checkTextViewsNotEmpty()) {
            Toast.makeText(this, "Need fill the profile before sending!", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(EXTRA_NAME_KEY, name.text)
            putExtra(EXTRA_SURNAME_KEY, surname.text)
            putExtra(EXTRA_AGE_KEY, age.text)
            putExtra(EXTRA_AVATAR_KEY, imageUri)
            setPackage("org.telegram.messenger")
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "Telegram app not install yet", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_NAME_KEY = "EXTRA_NAME_KEY"
        const val EXTRA_SURNAME_KEY = "EXTRA_SURNAME_KEY"
        const val EXTRA_AGE_KEY = "EXTRA_AGE_KEY"
        const val EXTRA_AVATAR_KEY = "EXTRA_AVATAR_KEY"
    }
}