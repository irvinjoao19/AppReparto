package com.calida.dsige.reparto.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.*
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.MediaStore
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.exifinterface.media.ExifInterface
import com.calida.dsige.reparto.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.*
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.ceil

object Util {

    val FolderImg = "Dsige/CALIDDA_Lectura"
    val FolderImgSelfi = "CALIDDA_Reparto_Selfi"
    val Error = "Por favor volver a reintentar !"
    val Internet = "No se pudo enviar los datos por favor cerrar el aplicativo y volver a ingresar"
    val MessageInternet = "No cuentas con buena señal de internet deseas volver a enviar ?"
    val ButtonSiguiente = "Siguiente"

    private var FechaActual: String? = ""
    private var date: Date? = null

    private const val img_height_default = 800
    private const val img_width_default = 600

    fun getFecha(): String {
//        return "27/08/2019"
        date = Date()
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("dd/MM/yyyy")
        return format.format(date)
    }

    fun getFechaActual(): String {
        date = Date()
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        return format.format(date)
    }

    fun getHora(): String {
        date = Date()
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("HH:mm aaa")
        return format.format(date)
    }

    fun getHoraActual(): String {
        date = Date()
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("HH:mm:ss aaa")
        return format.format(date)
    }

    fun getFechaEditar(): String? {
        date = Date()
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("ddMMyyyy_HHmmssSSSS")
        FechaActual = format.format(date)
        return FechaActual
    }

    fun getFechaActualForPhoto(id: Int, tipo: Int): String {
        val date = Date()
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("ddMMyyyy_HHmmssSSSS")
        FechaActual = format.format(date)
        return id.toString() + "_" + tipo + "_" + FechaActual
    }

    fun getFechaActualForPhoto2(id: Int, tipo: Int): String {
        val date = Date()
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("ddMMyyyy_HHmmssSSSS")
        FechaActual = format.format(date)
        return String.format("%s_%s_%s.jpg", id, tipo, FechaActual)
    }

    fun getFechaActualRepartoPhoto(id: Int, codigo: String): String {
        val date = Date()
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("ddMMyyyy_HHmmssSSSS")
        FechaActual = format.format(date)
        return id.toString() + "_" + codigo + "_" + FechaActual
    }

    fun toggleTextInputLayoutError(textInputLayout: TextInputLayout,
                                   msg: String?) {
        textInputLayout.error = msg
        textInputLayout.isErrorEnabled = msg != null
    }


    // TODO SOBRE ADJUNTAR PHOTO

    @Throws(IOException::class)
    fun copyFile(sourceFile: File, destFile: File) {
        if (!sourceFile.exists()) {
            return
        }
        val source: FileChannel? = FileInputStream(sourceFile).channel
        val destination: FileChannel = FileOutputStream(destFile).channel
        if (source != null) {
            destination.transferFrom(source, 0, source.size())
        }
        source?.close()
        destination.close()
    }

    fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
        var result: String? = null
        val proj = arrayOf(MediaStore.Video.Media.DATA)
        @SuppressLint("Recycle") val cursor = Objects.requireNonNull(context).contentResolver.query(contentUri, proj, null, null, null)
        if (cursor != null) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            result = cursor.getString(columnIndex)
        }
        return result
    }

    fun getFolder(context: Context): File {
        val folder = File(context.getExternalFilesDir(null)!!.absolutePath)
        if (!folder.exists()) {
            val success = folder.mkdirs()
            if (!success) {
                folder.mkdir()
            }
        }
        return folder


//        val folder = File(Environment.getExternalStorageDirectory(), FolderImg)
//        if (!folder.exists()) {
//            val success = folder.mkdirs()
//            if (!success) {
//                folder.mkdir()
//            }
//        }
//        return folder
    }

    fun getFolderPhoto(): File {
        val folder = File(Environment.getExternalStorageDirectory(), FolderImg)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        return folder
    }
    // TODO SOBRE FOTO

    fun generateImageAsync(pathFile: String): Observable<Boolean> {
        return Observable.create { e ->
            e.onNext(comprimirImagen(pathFile, ""))
            e.onComplete()
        }
    }

    fun generateImageAsync(pathFile: String, fechaAsignacion: String): Observable<Boolean> {
        return Observable.create { e ->
            e.onNext(comprimirImagen(pathFile, String.format("%s %s", fechaAsignacion, getHoraActual())))
            e.onComplete()
        }
    }

    private fun comprimirImagen(pathFile: String, fechaAsignacion: String): Boolean {
        return try {
            val result = getRightAngleImage(pathFile, fechaAsignacion)
            result == pathFile
        } catch (ex: Exception) {
            Log.i("exception", ex.message!!)
            false
        }
    }


    private fun getDateTimeFormatString(date: Date): String {
        @SuppressLint("SimpleDateFormat") val df = SimpleDateFormat("dd/MM/yyyy - hh:mm:ss a")
        return df.format(date)
    }

    private fun getDateTimeFormatString(): String {
        val date = Date()
        @SuppressLint("SimpleDateFormat") val df = SimpleDateFormat("dd/MM/yyyy - hh:mm:ss a")
        return df.format(date)
    }

    private fun processingBitmapSetDateTime(bm1: Bitmap?, captionString: String?): Bitmap? {
        //Bitmap bm1 = null;
        var newBitmap: Bitmap? = null
        try {

            var config: Bitmap.Config? = bm1!!.config
            if (config == null) {
                config = Bitmap.Config.ARGB_8888
            }
            newBitmap = Bitmap.createBitmap(bm1.width, bm1.height, config)

            val newCanvas = Canvas(newBitmap!!)
            newCanvas.drawBitmap(bm1, 0f, 0f, null)

            if (captionString != null) {

                val paintText = Paint(Paint.ANTI_ALIAS_FLAG)
                paintText.color = Color.RED
                paintText.textSize = 22f
                paintText.style = Paint.Style.FILL
                paintText.setShadowLayer(0.7f, 0.7f, 0.7f, Color.YELLOW)

                val rectText = Rect()
                paintText.getTextBounds(captionString, 0, captionString.length, rectText)
                newCanvas.drawText(captionString, 0f, rectText.height().toFloat(), paintText)
            }

            //} catch (FileNotFoundException e) {
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return newBitmap
    }

    private fun copyBitmatToFile(filename: String, bitmap: Bitmap): String {
        return try {
            val f = File(filename)

            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, bos)
            val bitmapdata = bos.toByteArray()

            val fos = FileOutputStream(f)
            fos.write(bitmapdata)
            "true"

        } catch (ex: IOException) {
            ex.message.toString()
        }
    }

    private fun shrinkBitmap(file: String): Bitmap {
        val options = BitmapFactory.Options()
        options.inSampleSize = 4
        options.inJustDecodeBounds = true
        val heightRatio = ceil((options.outHeight / img_height_default.toFloat()).toDouble()).toInt()
        val widthRatio = ceil((options.outWidth / img_width_default.toFloat()).toDouble()).toInt()

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                options.inSampleSize = heightRatio
            } else {
                options.inSampleSize = widthRatio
            }
        }
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(file, options)
    }

    private fun shrinkBitmapOnlyReduce(file: String, captionString: String?) {

        val options = BitmapFactory.Options()
        options.inSampleSize = 4
        options.inJustDecodeBounds = true
        val heightRatio = ceil((options.outHeight / img_height_default.toFloat()).toDouble()).toInt()
        val widthRatio = ceil((options.outWidth / img_width_default.toFloat()).toDouble()).toInt()

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                options.inSampleSize = heightRatio
            } else {
                options.inSampleSize = widthRatio
            }
        }

        options.inJustDecodeBounds = false

        try {
            val b = BitmapFactory.decodeFile(file, options)
            var config: Bitmap.Config? = b.config
            if (config == null) {
                config = Bitmap.Config.ARGB_8888
            }
            val newBitmap = Bitmap.createBitmap(b.width, b.height, config)

            val newCanvas = Canvas(newBitmap)
            newCanvas.drawBitmap(b, 0f, 0f, null)

            if (captionString != null) {
                val paintText = Paint(Paint.ANTI_ALIAS_FLAG)
                paintText.color = Color.RED
                paintText.textSize = 22f
                paintText.style = Paint.Style.FILL
                paintText.setShadowLayer(0.7f, 0.7f, 0.7f, Color.YELLOW)

                val rectText = Rect()
                paintText.getTextBounds(captionString, 0, captionString.length, rectText)
                newCanvas.drawText(captionString, 0f, rectText.height().toFloat(), paintText)
            }

            val fOut = FileOutputStream(file)
            val imageName = file.substring(file.lastIndexOf("/") + 1)
            val imageType = imageName.substring(imageName.lastIndexOf(".") + 1)

            val out = FileOutputStream(file)
            if (imageType.equals("png", ignoreCase = true)) {
                newBitmap.compress(Bitmap.CompressFormat.PNG, 70, out)
            } else if (imageType.equals("jpeg", ignoreCase = true) || imageType.equals("jpg", ignoreCase = true)) {
                newBitmap.compress(Bitmap.CompressFormat.JPEG, 70, out)
            }
            fOut.flush()
            fOut.close()
            newBitmap.recycle()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    // TODO SOBRE ROTAR LA PHOTO

    private fun getRightAngleImage(photoPath: String, fechaAsignacion: String): String {

        try {
            val ei = ExifInterface(photoPath)
            val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            val degree: Int

            degree = when (orientation) {
                ExifInterface.ORIENTATION_NORMAL -> 0
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                ExifInterface.ORIENTATION_UNDEFINED -> 0
                else -> 90
            }

            return rotateImage(degree, photoPath, fechaAsignacion)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return photoPath
    }


    private fun rotateImage(degree: Int, imagePath: String, fechaAsignacion: String): String {

        if (degree <= 0) {
            if (fechaAsignacion.isEmpty()) {
                shrinkBitmapOnlyReduce(imagePath, getDateTimeFormatString(Date(File(imagePath).lastModified())))
            } else {
                shrinkBitmapOnlyReduce(imagePath, fechaAsignacion)
            }
            return imagePath
        }
        try {

            var b: Bitmap? = shrinkBitmap(imagePath)
            val matrix = Matrix()
            if (b!!.width > b.height) {
                matrix.setRotate(degree.toFloat())
                b = Bitmap.createBitmap(b, 0, 0, b.width, b.height, matrix, true)
                b = if (fechaAsignacion.isEmpty()) {
                    processingBitmap(b, getDateTimeFormatString(Date(File(imagePath).lastModified())))
                } else {
                    processingBitmap(b, fechaAsignacion)
                }
            }

            val fOut = FileOutputStream(imagePath)
            val imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1)
            val imageType = imageName.substring(imageName.lastIndexOf(".") + 1)

            val out = FileOutputStream(imagePath)
            if (imageType.equals("png", ignoreCase = true)) {
                b!!.compress(Bitmap.CompressFormat.PNG, 70, out)
            } else if (imageType.equals("jpeg", ignoreCase = true) || imageType.equals("jpg", ignoreCase = true)) {
                b!!.compress(Bitmap.CompressFormat.JPEG, 70, out)
            }
            fOut.flush()
            fOut.close()
            b!!.recycle()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return imagePath
    }

    private fun processingBitmap(bm1: Bitmap?, captionString: String?): Bitmap? {
        //Bitmap bm1 = null;
        var newBitmap: Bitmap? = null
        try {

            var config: Bitmap.Config? = bm1!!.config
            if (config == null) {
                config = Bitmap.Config.ARGB_8888
            }
            newBitmap = Bitmap.createBitmap(bm1.width, bm1.height, config)

            val newCanvas = Canvas(newBitmap!!)
            newCanvas.drawBitmap(bm1, 0f, 0f, null)

            if (captionString != null) {
                val paintText = Paint(Paint.ANTI_ALIAS_FLAG)
                paintText.color = Color.RED
                paintText.textSize = 22f
                paintText.style = Paint.Style.FILL
                paintText.setShadowLayer(0.7f, 0.7f, 0.7f, Color.YELLOW)

                val rectText = Rect()
                paintText.getTextBounds(captionString, 0, captionString.length, rectText)
                newCanvas.drawText(captionString, 0f, rectText.height().toFloat(), paintText)
            }

            //} catch (FileNotFoundException e) {
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return newBitmap
    }


    fun getAngleImage(photoPath: String, fecha: String): String {
        try {
            val ei = ExifInterface(photoPath)
            val orientation =
                    ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            val degree: Int

            degree = when (orientation) {
                ExifInterface.ORIENTATION_NORMAL -> 0
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                ExifInterface.ORIENTATION_UNDEFINED -> 0
                else -> 90
            }
            return rotateNewImage(degree, photoPath, fecha)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return photoPath
    }

    private fun rotateNewImage(degree: Int, imagePath: String, fecha: String): String {
        if (degree <= 0) {
            shrinkBitmapOnlyReduceCamera2(imagePath, fecha)
            return imagePath
        }
        try {

            var b: Bitmap? = BitmapFactory.decodeFile(imagePath)
            val matrix = Matrix()
            if (b!!.width > b.height) {
                matrix.setRotate(degree.toFloat())
                b = Bitmap.createBitmap(b, 0, 0, b.width, b.height, matrix, true)
                b = if (fecha.isEmpty()) {
                    processingBitmap(b, getDateTimeFormatString(Date(File(imagePath).lastModified())))
                } else {
                    processingBitmap(b, String.format("%s %s", fecha, getHoraActual()))
                }
            }

            val fOut = FileOutputStream(imagePath)
            val imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1)
            val imageType = imageName.substring(imageName.lastIndexOf(".") + 1)

            val out = FileOutputStream(imagePath)
            if (imageType.equals("png", ignoreCase = true)) {
                b!!.compress(Bitmap.CompressFormat.PNG, 80, out)
            } else if (imageType.equals("jpeg", ignoreCase = true) || imageType.equals(
                            "jpg",
                            ignoreCase = true
                    )
            ) {
                b!!.compress(Bitmap.CompressFormat.JPEG, 80, out)
            }
            fOut.flush()
            fOut.close()
            b!!.recycle()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return imagePath
    }

    private fun shrinkBitmapOnlyReduceCamera2(
            file: String, fecha: String
    ) {
        val b = BitmapFactory.decodeFile(file)

        val text = if (fecha.isEmpty()) getDateTimeFormatString() else String.format("%s %s", fecha, getHoraActual())
        var config: Bitmap.Config? = b.config
        if (config == null) {
            config = Bitmap.Config.ARGB_8888
        }
        val newBitmap = Bitmap.createBitmap(b.width, b.height, config)
        val newCanvas = Canvas(newBitmap)
        newCanvas.drawBitmap(b, 0f, 0f, null)

        val paintText = Paint(Paint.ANTI_ALIAS_FLAG)
        paintText.color = Color.RED
        paintText.textSize = 22f
        paintText.style = Paint.Style.FILL
        paintText.setShadowLayer(0.7f, 0.7f, 0.7f, Color.YELLOW)

        val rectText = Rect()
        paintText.getTextBounds(text, 0, text.length, rectText)
        newCanvas.drawText(text, 0f, rectText.height().toFloat(), paintText)

        val fOut = FileOutputStream(file)
        val imageName = file.substring(file.lastIndexOf("/") + 1)
        val imageType = imageName.substring(imageName.lastIndexOf(".") + 1)

        val out = FileOutputStream(file)
        if (imageType.equals("png", ignoreCase = true)) {
            newBitmap.compress(Bitmap.CompressFormat.PNG, 80, out)
        } else if (imageType.equals("jpeg", ignoreCase = true) || imageType.equals(
                        "jpg",
                        ignoreCase = true
                )
        ) {
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
        }
        fOut.flush()
        fOut.close()
        newBitmap.recycle()
    }


    fun getVersion(context: Context): String {
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return pInfo.versionName
    }

    @SuppressLint("HardwareIds", "MissingPermission")
    fun getImei(context: Context): String {

        val deviceUniqueIdentifier: String
        val telephonyManager: TelephonyManager? = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        deviceUniqueIdentifier = if (telephonyManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                telephonyManager.imei
            } else {
                @Suppress("DEPRECATION")
                telephonyManager.deviceId
            }
        } else {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        }
        return deviceUniqueIdentifier
    }

    fun getToken(context: Context): String? {
        return context.getSharedPreferences("TOKEN", MODE_PRIVATE).getString("token", "empty")
    }

    fun getNotificacionValid(context: Context): String? {
        return context.getSharedPreferences("TOKEN", MODE_PRIVATE).getString("update", "")
    }

    fun updateNotificacionValid(context: Context) {
        context.getSharedPreferences("TOKEN", MODE_PRIVATE).edit().putString("update", "").apply()
    }

    fun snackBarMensaje(view: View, mensaje: String) {
        val mSnackbar = Snackbar.make(view, mensaje, Snackbar.LENGTH_SHORT)
        mSnackbar.setAction("Ok") { mSnackbar.dismiss() }
        mSnackbar.show()
    }

    fun dialogMensaje(context: Context, title: String, mensaje: String) {
        val dialog = MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(mensaje)
                .setPositiveButton("Entendido") { dialog, _ ->
                    dialog.dismiss()
                }
        dialog.show()
    }


    fun toastMensaje(context: Context, mensaje: String) {
        Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
    }

    fun hideKeyboard(activity: Activity) {
        // TODO FOR ACTIVITIES
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun hideKeyboardFrom(context: Context, view: View) {
        // TODO FOR FRAGMENTS
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showKeyboard(edit: EditText, context: Context) {
        edit.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    fun getDateDialog(context: Context, input: TextInputEditText) {
        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(context, { _, year, monthOfYear, dayOfMonth ->
            val month = if (((monthOfYear + 1) / 10) == 0) "0" + (monthOfYear + 1).toString() else (monthOfYear + 1).toString()
            val day = if (((dayOfMonth + 1) / 10) == 0) "0$dayOfMonth" else dayOfMonth.toString()
            val fecha = "$day/$month/$year"
            input.setText(fecha)
        }, mYear, mMonth, mDay)
        datePickerDialog.show()
    }

    fun getHourDialog(context: Context, input: TextInputEditText) {
        val c = Calendar.getInstance()
        val mHour = c.get(Calendar.HOUR_OF_DAY)
        val mMinute = c.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            val hour = if (hourOfDay < 10) "0$hourOfDay" else hourOfDay.toString()
            val minutes = if (minute < 10) "0$minute" else minute.toString()
            val day = if (hourOfDay < 12) "a.m." else "p.m."
            input.setText(String.format("%s:%s %s", hour, minutes, day))
        }, mHour, mMinute, false)
        timePickerDialog.show()
    }

    fun clearNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1)
    }

    fun getLocationName(context: Context, input: TextInputEditText, location: Location, progressBar: ProgressBar) {
        val nombre = arrayOf("")
        try {
            val addressObservable = Observable.just(Geocoder(context).getFromLocation(location.latitude, location.longitude, 1)[0])
            addressObservable.subscribeOn(Schedulers.io())
                    .delay(1000, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Address> {
                        override fun onSubscribe(d: Disposable) {

                        }

                        override fun onNext(address: Address) {
                            nombre[0] = address.getAddressLine(0)
                        }

                        override fun onError(e: Throwable) {
                            toastMensaje(context, context.getString(R.string.try_again))
                            progressBar.visibility = View.GONE
                        }

                        override fun onComplete() {
                            input.setText(nombre[0])
                            progressBar.visibility = View.GONE
                        }
                    })
        } catch (e: IOException) {
            toastMensaje(context, e.toString())
            progressBar.visibility = View.GONE
        }
    }

    // todo nuevo

    fun externalMemoryAvailable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    fun getAvailableInternalMemorySize(): String? {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize: Long = stat.blockSizeLong
        val availableBlocks: Long = stat.availableBlocksLong
        return formatSize(availableBlocks * blockSize)
    }

    fun getTotalInternalMemorySize(): String? {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize: Long = stat.blockSizeLong
        val totalBlocks: Long = stat.blockCountLong
        return formatSize(totalBlocks * blockSize)
    }

    fun getAvailableExternalMemorySize(): String? {
        return if (externalMemoryAvailable()) {
            val path = Environment.getExternalStorageDirectory()
            val stat = StatFs(path.path)
            val blockSize: Long = stat.blockSizeLong
            val availableBlocks: Long = stat.availableBlocksLong
            formatSize(availableBlocks * blockSize)
        } else {
            "ERROR"
        }
    }

    fun getTotalExternalMemorySize(): String? {
        return if (externalMemoryAvailable()) {
            val path = Environment.getExternalStorageDirectory()
            val stat = StatFs(path.path)
            val blockSize: Long = stat.blockSizeLong
            val totalBlocks: Long = stat.blockCountLong
            formatSize(totalBlocks * blockSize)
        } else {
            "ERROR"
        }
    }

    fun formatSize(size: Long): String? {
        var size = size
        var suffix: String? = null
        if (size >= 1024) {
            suffix = "KB"
            size /= 1024
            if (size >= 1024) {
                suffix = "MB"
                size /= 1024
            }
        }
        val resultBuffer = StringBuilder(java.lang.Long.toString(size))
        var commaOffset = resultBuffer.length - 3
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',')
            commaOffset -= 3
        }
        if (suffix != null) resultBuffer.append(suffix)
        return resultBuffer.toString()
    }


    fun isNumeric(strNum: String): Boolean {
        try {
            val d = Integer.parseInt(strNum)
            Log.i("TAG", d.toString())
        } catch (nfe: NumberFormatException) {
            return false
        } catch (nfe: NullPointerException) {
            return false
        }
        return true
    }
}