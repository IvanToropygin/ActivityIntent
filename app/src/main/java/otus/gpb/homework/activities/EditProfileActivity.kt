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
            val name = name.text.toString()
            val surname = surname.text.toString()
            val age = age.text.toString()

            val intent = Intent(this, FillFormActivity::class.java)
            val profileData: Profile

            if (name.isNotBlank() || surname.isNotBlank() || age.isNotBlank()) {
                profileData = Profile(name, surname, age.toInt())
                intent.putExtra(FillFormActivity.EXTRA_PROFILE_KEY, profileData)
            }

            startForResult.launch(intent) }
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
        TODO("В качестве реализации метода отправьте неявный Intent чтобы поделиться профилем. В качестве extras передайте заполненные строки и картинку")
    }
}