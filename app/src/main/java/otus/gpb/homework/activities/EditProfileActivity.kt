package otus.gpb.homework.activities

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class EditProfileActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        imageView = findViewById(R.id.imageview_photo)

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
                    // Respond to positive button press
                }
                .show()
        }
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
            .setPositiveButton("Дать доступ") { dialog, which -> launchPermissionCameraLauncher() }
            .setNegativeButton("Отмена") { dialog, which -> dialog.dismiss() }
            .show()
    }

    private fun showSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Доступ к камере отключен")
            .setMessage("Пожалуйста, разрешите доступ к камере в настройках приложения.")
            .setPositiveButton("Открыть настройки") { dialog: DialogInterface?, which: Int ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.setData(uri)
                startActivity(intent)
            }
            .setNegativeButton(
                "Отмена"
            ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
            .show()
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