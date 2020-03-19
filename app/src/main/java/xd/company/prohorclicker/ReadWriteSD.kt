package xd.company.prohorclicker

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.*

class ReadWriteSD(private val tag: String, private val activity:Activity) {
    fun checkExternalMedia() {
        val mExternalStorageAvailable: Boolean
        val mExternalStorageWriteable: Boolean
        val state = Environment.getExternalStorageState()
        when {
            Environment.MEDIA_MOUNTED == state -> { // Can read and write the media
                mExternalStorageWriteable = true
                mExternalStorageAvailable = mExternalStorageWriteable
            }
            Environment.MEDIA_MOUNTED_READ_ONLY == state -> { // Can only read the media
                mExternalStorageAvailable = true
                mExternalStorageWriteable = false
            }
            else -> { // Can't read or write
                mExternalStorageWriteable = false
                mExternalStorageAvailable = mExternalStorageWriteable
            }
        }
        Log.d(
            tag,
            "External Media: readable="
                    + mExternalStorageAvailable + " writable=" + mExternalStorageWriteable
        )
    }

    /** Method to write ascii text characters to file on SD card. Note that you must add a
     * WRITE_EXTERNAL_STORAGE permission to the manifest file or this method will throw
     * a FileNotFound Exception because you won't have write permission.  */
    fun writeToSDFile(filename: String, inf: String) {
        // Find the root of the external storage.
        val root = Environment.getExternalStorageDirectory()
        // Make dir for file
        val dir = File(root.absolutePath + "/download")
        dir.mkdirs()
        val file = File(dir, filename)
        try {
            val f = FileOutputStream(file)
            val pw = PrintWriter(f)
            pw.println(inf)
            Log.i(tag, "File $filename has been created")
            Log.d(tag, "File written to $file")
            pw.flush()
            pw.close()
            f.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Log.i(
                tag, "******* File not found. Did you" +
                        " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?"
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            }
            val state = Environment.getExternalStorageState()
            if (Environment.MEDIA_MOUNTED == state) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkPermission()) {
                        val sdcard = Environment.getExternalStorageDirectory()
                        val dir =  File(sdcard.absolutePath + "/text/")
                        dir.mkdir()
                        val file = File(dir, filename)
                        var os: FileOutputStream? = null
                        try {
                            os = FileOutputStream(file)
                            os.write("0".toByteArray())
                            os.close()
                        } catch (e:java.lang.Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        requestPermission()// Code for permission
                    }
                } else {
                    val sdcard = Environment.getExternalStorageDirectory()
                    val dir = File(sdcard.absolutePath + "/text/")
                    dir.mkdir()
                    val file =  File(dir, filename)
                    var os: FileOutputStream? = null
                    try {
                        os = FileOutputStream(file)
                        os.write("0".toByteArray())
                        os.close()
                    } catch (e:java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /** Method to read in a text file placed in the external directory of the application. The
     * method reads in all lines of the file sequentially.  */
    private fun readFile(filename: String): String{
        StringBuilder()
        return try{
            val sdcard = Environment.getExternalStorageDirectory()
            val path = File(sdcard.absolutePath + "/download", filename)
            val fis = FileInputStream(path)
            val isr = InputStreamReader(fis)
            val buff = BufferedReader(isr)
            val line = buff.readLine()
            Log.d(tag, line)
            fis.close()
            line
        }catch (e:java.lang.Exception){
            Log.e(tag, e.message)
            "error"
        }
    }

    fun deleteFile(filename: String): Boolean {
        return try {
            val sdcard = Environment.getExternalStorageDirectory()
            val path = File(sdcard.absolutePath + "/download", filename)
            path.delete()
        }catch (e: java.lang.Exception){
            false
        }
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(activity.applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
        }
    }
    fun getValues(filename: String, basicMeaning: String):String{
        var value = ""
        try {
            var info = readFile(filename)
            Log.d(tag, "File OfflineMultiple has been found")
            if(info == "error"){
                writeToSDFile(filename, basicMeaning)
                Log.d(tag, "File $filename has been created")
                value = basicMeaning
            }
            if(info == "")
                info = basicMeaning
            if(info != "error")
                value = info
        }
        catch (e:Exception) {
            Log.e(tag, e.message)
            writeToSDFile(filename, basicMeaning)
            Log.d(tag, "File $filename has been created")
            value = basicMeaning
        }
        return value
    }
}