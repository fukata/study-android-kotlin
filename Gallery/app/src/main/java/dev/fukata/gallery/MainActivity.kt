package dev.fukata.gallery

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.FileUtils
import android.provider.MediaStore
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.function.Consumer

class MainActivity : AppCompatActivity() {

    private val REQUEST_IMAGE_CAPTURE: Int = 1
    lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            if (applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                //Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                //    takePictureIntent.resolveActivity(packageManager)?.also {
                //        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                //    }
                //}

                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                    // Ensure that there's a camera activity to handle the intent
                    takePictureIntent.resolveActivity(packageManager)?.also {
                        // Create the File where the photo should go
                        val photoFile: File? = try {
                            createImageFile()
                        } catch (ex: IOException) {
                            // Error occurred while creating the File
                            Log.e("MainActivity", ex.toString())
                            null
                        }
                        // Continue only if the File was successfully created
                        photoFile?.also {
                            val photoURI: Uri = FileProvider.getUriForFile(
                                this,
                                "dev.fukata.gallery.fileprovider",
                                it
                            )
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                        }
                    }
                }
            } else {
                Log.d("MainActivity", "No feature camera")
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Log.d("MainActivity", "Successful take photo")

                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                Log.d("MainActivity", "fragments=${navHostFragment.childFragmentManager.fragments.size}")
                navHostFragment.childFragmentManager.fragments.forEach { fragment ->
                    Log.d("MainActivity", "fragment=${fragment.javaClass}")
                    if (fragment is FirstFragment) {
                        fragment.onDataChanged()
                    }
                }
            } else {
                Log.d("MainActivity", "Failed take photo. delete file=${this.currentPhotoPath}")
                File(this.currentPhotoPath).delete()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }


    /**
     * TODO: 撮影失敗、撮影キャンセル時にゴミファイルが残る可能性があるため、キャッシュディレクトリにファイルを作成後、撮影成功時にキャッシュディレクトリから保存用ディレクトリに移す方が良い？
     */
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }
}