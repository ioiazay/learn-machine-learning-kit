package com.odlsoon.basic_android_ml_kit.recognize_text

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.odlsoon.basic_android_ml_kit.R
import com.odlsoon.basic_android_ml_kit.utils.CameraHelper
import kotlinx.android.synthetic.main.recognize_text_act.*
import java.lang.StringBuilder

class RecognizeTextActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recognize_text_act)

        initListener()
    }

    private fun initListener(){
        btn_take.setOnClickListener {
            CameraHelper.openCameraFullPath(this, 1)
        }
    }

    private fun runTextRecognition(mSelectedImage: Bitmap) {
        val image = FirebaseVisionImage.fromBitmap(mSelectedImage)
        val recognizer = FirebaseVision.getInstance().onDeviceTextRecognizer

        recognizer.processImage(image)
            .addOnSuccessListener { texts ->
                processTextRecognitionResult(texts)
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    private fun processTextRecognitionResult(texts: FirebaseVisionText) {
        val blocks = texts.textBlocks
        var text = StringBuilder("")

        if (blocks.size == 0) {
            Toast.makeText(this, "No text found", Toast.LENGTH_SHORT).show()
            return
        }

        for (i in blocks.indices) {

            val lines = blocks[i].lines
            for (j in lines.indices) {

                val elements = lines[j].elements
                for (k in elements.indices) {
                    text.append(elements[k].text + " - ")
                }
            }
        }

        tv_text.text = text.toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(resultCode){
            Activity.RESULT_OK -> {
                when(requestCode){
                    1 -> {
                        val bitmap = CameraHelper.getImageBitmap()

                        Glide.with(this)
                            .load(bitmap)
                            .into(iv_target)

                        runTextRecognition(bitmap)
                        CameraHelper.addImageToGallery(this)
                    }
                }
            }
        }

    }
}
