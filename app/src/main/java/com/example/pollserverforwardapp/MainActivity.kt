package com.example.pollserverforwardapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.pollserverforwardapp.models.ImageWithDataUploadResponse
import com.example.pollserverforwardapp.network.RetrofitApiClient
import com.example.pollserverforwardapp.network.RetrofitApiInterface
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private val SEND_SMS_PERMISSION_REQUEST_CODE = 1001
    private val REQUEST_IMAGE_CAPTURE = 101
    val description = "Description of the image"
    private val REQUEST_CAMERA_PERMISSION = 100
    private lateinit var slotEditText: EditText
    private lateinit var calendar: Calendar
    private lateinit var dateEditText: EditText
    private lateinit var doctorNameEdT: EditText
    private lateinit var imageView: ImageView
    private var imageFilePath: String? = null
    private lateinit var confirmButton: Button

    companion object{
        val apiInterface : RetrofitApiInterface = RetrofitApiClient.getUpload().create(RetrofitApiInterface::class.java)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        confirmButton= findViewById(R.id.confirmButton)
        slotEditText = findViewById(R.id.editTextTime)
        dateEditText = findViewById(R.id.editTextDate)
        doctorNameEdT = findViewById(R.id.doctorNameEdT)
        imageView = findViewById(R.id.imageView)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val camaraButton: Button = findViewById(R.id.camaraButton)
        confirmButton.visibility = View.GONE

        slotEditText.setOnClickListener {
            val items = arrayOf("09-10 am", "10-11 am","11-12 am","12-01 pm","01-02 pm", "03-04 pm", "05-06 pm")

            val builder = AlertDialog.Builder(this)
            builder.setItems(items) { dialog, which ->
                val selectedValue = items[which]
                slotEditText.setText(selectedValue)
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        calendar = Calendar.getInstance()
        if (!isSmsPermissionGranted()) {
            requestSmsPermission()
        }

        dateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        camaraButton.setOnClickListener {
            val slot = slotEditText.text.toString()
            val date = dateEditText.text.toString()
            val doctorName = doctorNameEdT.text.toString()

            if (slot.isNotEmpty() && date.isNotEmpty() && doctorName.isNotEmpty()) { doctorNameEdT.clearFocus()
               dispatchCapturePictureIntent()
            } else {
                Toast.makeText(this@MainActivity, "Please fill all the required fields", Toast.LENGTH_SHORT).show()
            }
        }

        confirmButton.setOnClickListener {
            val slot = slotEditText.text.toString()
            val date = dateEditText.text.toString()
            val doctorName = doctorNameEdT.text.toString()
            progressBar.visibility = View.VISIBLE

            if (slot.isNotEmpty() && date.isNotEmpty() && doctorName.isNotEmpty() && imageFilePath != null) {
                val imageFile = File(imageFilePath!!)
                val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)
                val slotValue = slotEditText.text.toString()
                val dateValue = dateEditText.text.toString()
                val doctorNameValue = doctorNameEdT.text.toString()
                val timeRequestBody = slotValue.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val dateRequestBody = dateValue.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                val doctorNameRequestBody = doctorNameValue.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                println("response--data-$slotValue")
                println("response--timeRequestBody-$timeRequestBody $dateRequestBody $doctorNameRequestBody ")
                println("response--body-$body")
                apiInterface.uploadDataWithImage(file = body , slot = timeRequestBody, date = dateRequestBody, doctor_name = doctorNameRequestBody)
                    .enqueue(object : Callback<ImageWithDataUploadResponse> {
                        override fun onResponse(call: Call<ImageWithDataUploadResponse>, response: Response<ImageWithDataUploadResponse>) {
                            if (response.isSuccessful) {
                                progressBar.visibility = View.INVISIBLE
                                println("response---"+ response.body().toString())
                                val responseData = response.body()
                                val intent = Intent(this@MainActivity, ForwardMsgActivity::class.java)
                                intent.putExtra("responseData", responseData)
                                startActivity(intent)
                                finish()
                                Toast.makeText(this@MainActivity, "Data uploaded successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                progressBar.visibility = View.INVISIBLE
                                Toast.makeText(this@MainActivity, "Failed to upload data: ${response.message()}", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<ImageWithDataUploadResponse>, t: Throwable) {
                            // Handle failure
                            progressBar.visibility = View.INVISIBLE
                            Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            } else {
                // Show a message if any required fields are empty
                Toast.makeText(this@MainActivity, "Please fill all the required fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun dispatchCapturePictureIntent() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        } else {
            // Proceed with the camera capture intent
            startCameraPictureIntent()
        }
    }

    private fun startCameraPictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.pollserverforwardapp.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun showDatePickerDialog() {
        val currentDate = Calendar.getInstance()

        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val dayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, dayOfMonth ->
                calendar.set(selectedYear, selectedMonth, dayOfMonth)
                updateDateEditText()
            },
            year,
            month,
            dayOfMonth
        )
        datePickerDialog.datePicker.minDate = currentDate.timeInMillis
        datePickerDialog.show()
    }

    private fun updateDateEditText() {
        val dateFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
        val selectedDate = sdf.format(calendar.time)
        dateEditText.setText(selectedDate)
    }

    private fun showTimePickerDialog() {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(
            this,
            { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)
                updateTimeEditText()
            },
            hour,
            minute,
            false
        )
        timePickerDialog.show()
    }

    private fun updateTimeEditText() {
        val timeFormat = "%02d:%02d"
        val selectedHour = calendar.get(Calendar.HOUR_OF_DAY)
        val selectedMinute = calendar.get(Calendar.MINUTE)
        val timeString = String.format(timeFormat, selectedHour, selectedMinute)
        slotEditText.setText(timeString)
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir).apply {
            imageFilePath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            imageFilePath?.let { path ->
                val imageFile = File(path)
                if (imageFile.exists()) {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, Uri.fromFile(imageFile))
                    val rotatedBitmap = rotateImageIfRequired(bitmap, imageFilePath!!)
                    imageView.setImageBitmap(rotatedBitmap)
                    confirmButton.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun rotateImageIfRequired(bitmap: Bitmap, imageFilePath: String): Bitmap {
        val ei = ExifInterface(imageFilePath)
        val orientation = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270)
            ExifInterface.ORIENTATION_NORMAL -> bitmap
            else -> bitmap
        }
    }

    private fun rotateImage(source: Bitmap, angle: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun isSmsPermissionGranted(): Boolean {
        return checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestSmsPermission() {
        requestPermissions(arrayOf(Manifest.permission.SEND_SMS), SEND_SMS_PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCameraPictureIntent()
            } else {
                // Permission denied, inform the user or disable the camera feature
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}