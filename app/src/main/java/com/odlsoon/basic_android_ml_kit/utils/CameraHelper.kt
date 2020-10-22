package com.odlsoon.basic_android_ml_kit.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object CameraHelper {
    private var path: String? = null
    private var uri : Uri? = null

    private val permissions = arrayOf(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.CAMERA
    )

    fun openCameraFullPath(context: Context, requestCode: Int){
        GeneralHelper.checkSinglePermissions(context as Activity,permissions, requestCode){
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(context.packageManager)?.also {

                    val photoFile: File? = try {
                        createImageFile(context)
                    } catch (ex: IOException) {
                        null
                    }

                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                            context,
                            "com.example.android.fileprovider",
                            it
                        )

                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        (context).startActivityForResult(takePictureIntent, requestCode)
                        path = it.absolutePath
                        uri = photoURI
                    }
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    fun getImagePath(): String? {
        return path
    }

    fun getImageUri(): Uri?{
        return uri
    }

    fun getImageBitmap(): Bitmap{
        val options = BitmapFactory.Options()
        val bitmap = BitmapFactory.decodeFile(path, options)

        val ei = ExifInterface(path)
        val orientation: Int = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        var rotatedBitmap: Bitmap? = null
        rotatedBitmap = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270)
            ExifInterface.ORIENTATION_NORMAL -> bitmap
            else -> bitmap
        }

        return rotatedBitmap
    }

    fun addImageToGallery(context: Context) {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(path)
            mediaScanIntent.data = Uri.fromFile(f)
            context.sendBroadcast(mediaScanIntent)
        }
    }

}