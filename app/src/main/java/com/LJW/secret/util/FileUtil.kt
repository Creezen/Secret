package com.ljw.secret.util

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import com.ljw.secret.Env
import com.ljw.secret.util.DataUtil.toast
import java.io.File
import java.io.InputStream
import java.io.RandomAccessFile

object FileUtil {

    fun getFilePathByUri(uri: Uri): String? {
        val documentID = DocumentsContract.getDocumentId(uri)
        val splitInfo = documentID.split(":")
        val authority = uri.authority
        Log.e("FileUtil.getFilePathByUri","splitInfo: $splitInfo  " +
                "uri:  $uri")
        if (authority.isNullOrEmpty()) {
            Log.e("FileUtil.getFilePathByUri","authority is null")
            return null
        }
        when (getDocumentType(authority)){
            DocumentType.EXTERNAL -> {
                Log.e("FileUtil.getFilePathByUri","Document is in EXTERNAL")
                if ("primary".equals(splitInfo[0], true)) {
                    return "${Environment.getExternalStorageDirectory()}/${splitInfo[1]}"
                }
            }
            DocumentType.DOWNLOAD -> {
                Log.e("FileUtil.getFilePathByUri","Document is in DOWNLOAD")
                val uuri = MediaStore.Downloads.EXTERNAL_CONTENT_URI
                return getDataColumn(uuri, splitInfo[1])
            }
            DocumentType.MEDIA -> {
                Log.e("FileUtil.getFilePathByUri","Document is MEDIA")
                val contentUri = getMediaUri(splitInfo[0])
                contentUri?.let {
                    return getDataColumn(it, splitInfo[1])
                }?:run{
                    Log.e("FileUtil.getFilePathByUri","contentUri:  $contentUri")
                }
            }
            else -> {
                Log.e("FileUtil.getFilePathByUri","Document is in null")
                return null
            }
        }
        return null
    }

    private fun getDocumentType(authority: String): DocumentType {
        Log.e("FileUtil.getDocumentType","authority  $authority")
        when (authority) {
            "com.android.externalstorage.documents" -> return DocumentType.EXTERNAL
            "com.android.providers.downloads.documents" -> return DocumentType.DOWNLOAD
            "com.android.providers.media.documents" -> return DocumentType.MEDIA
            else -> return DocumentType.UNKNOWN
        }
    }

    private fun getMediaUri(type: String): Uri?{
        return when(type) {
            "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            "document" -> MediaStore.Files.getContentUri("external")
            else -> null
        }
    }

    private fun getDataColumn(uri: Uri, args: String): String? {
        val selection = "_id=?"
        val selectionArgs = arrayOf(args)
        val column = "_data"
        val projection = arrayOf(column)
        kotlin.runCatching {
            val cursor = Env.context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            Log.e("FileUtil.getDataColumn","cursor: ${cursor?.count}   uri： $uri")
            cursor?.use {
                if (it.moveToFirst()) {
                    val path = it.getString(it.getColumnIndexOrThrow(column))
                    Log.e("FileUtil.getDataColumn","get path for media: $path")
                    return path
                }
            }
        }.onFailure {
            Log.e("FileUtil.getDataColumn","error: $it")
            return null
        }
        return null
    }

    fun downloadFileByNet(
        inputStream: InputStream,
        fileName: String,
        downloadFunc: ((Int) -> Unit)
    ) {
        val savePath = getDir(Dir.EXT_PUBLIC_DOWNLOAD)?.path
        if(savePath.isNullOrEmpty()) {
            return
        }
        val file = File("$savePath/$fileName")
        if (file.exists()) {
            file.delete()
        }
        file.createNewFile()
        val randomAccessFile = RandomAccessFile(file, "rw")
        randomAccessFile.use { accessFile ->
            val bytes = ByteArray(1024)
            var length: Int
            var totalByte = 0
            while ((inputStream.read(bytes).also { length = it }) != -1) {
                accessFile.write(bytes, 0, length)
                totalByte += length
                downloadFunc(totalByte)
            }
        }
    }

    fun isFileDownload(filePath: String, totalSize: Long): Boolean {
        val path = getDir(Dir.EXT_PUBLIC_DOWNLOAD)?.path
        val file = File("$path/$filePath")
        if (file.exists().not()) {
            return false
        }
        return file.length() == totalSize
    }

    enum class DocumentType{
        EXTERNAL, DOWNLOAD, MEDIA, UNKNOWN
    }

    enum class Dir{
        LOC_PUBLIC_DATA,
        LOC_PUBLIC_DATACACHE,
        LOC_PUBLIC_SYS,
        LOC_PRIVATE_CACHE,
        LOC_PRIVATE_FILE,
        LOC_PRIVATE_USER,
        EXT_PUBLIC_ROOT,
        EXT_PUBLIC_PIC,
        EXT_PUBLIC_DOWNLOAD,
        EXT_PRIVATE_FILE,
        EXT_PRIVATE_CACHE,
        EXT_PRIVATE_FILEUSER
    }

    /**
     * LOC: 表示使用的是本地内存中的文件系统
     * EXT: 表示使用外部存储空间中的文件系统 （推荐）
     * PUBLIC: 表示使用公共文件目录，其他应用可以访问
     * PRIVATE: 表示使用应用私有的目录，必须传入context
     * @param context 应用私有目录必须传入context
     * @param userDir 带有USER的表示目录路径可以自定义，必须给userDir设参数
     */
    private fun getDir(dirType: Dir, context: Context? = null, userDir: String? = null) = when(dirType) {
        Dir.LOC_PUBLIC_DATA -> Environment.getDataDirectory()
        Dir.LOC_PUBLIC_DATACACHE -> Environment.getDownloadCacheDirectory()
        Dir.LOC_PUBLIC_SYS -> Environment.getRootDirectory()
        Dir.LOC_PRIVATE_CACHE -> context?.getCacheDir()
        Dir.LOC_PRIVATE_FILE -> context?.getFilesDir()
        Dir.LOC_PRIVATE_USER -> context?.getDir(userDir, Context.MODE_PRIVATE)
        Dir.EXT_PUBLIC_ROOT -> Environment.getExternalStorageDirectory()
        Dir.EXT_PUBLIC_PIC -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        Dir.EXT_PUBLIC_DOWNLOAD -> Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        Dir.EXT_PRIVATE_FILE -> context?.getExternalFilesDir(null)
        Dir.EXT_PRIVATE_CACHE -> context?.getExternalCacheDir()
        Dir.EXT_PRIVATE_FILEUSER -> context?.getExternalFilesDir(userDir)
    }

    private fun guide(context: Context) {
        //   /data
        e(Environment.getDataDirectory())
        //   /data/cache
        e(Environment.getDownloadCacheDirectory())
        //   /system
        e(Environment.getRootDirectory())

        //   /data/user/0/com.example.tianji/cache
        e(context.getCacheDir())
        //   /data/user/0/com.example.tianji/files
        e(context.getFilesDir())
        //   /data/user/0/com.example.tianji/app_LJW
        e(context.getDir("LJW", Context.MODE_PRIVATE))

        //   /storage/emulated/0
        e(Environment.getExternalStorageDirectory())
        //   /storage/emulated/0/Pictures
        e(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES))
        //   /storage/emulated/0/Download
        e(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))

        //   /storage/emulated/0/Android/data/com.example.tianji/files
        e(context.getExternalFilesDir(null))
        //   /storage/emulated/0/Android/data/com.example.tianji/cache
        e(context.getExternalCacheDir())
        //   /storage/emulated/0/Android/data/com.example.tianji/files/TianJi
        e(context.getExternalFilesDir("TianJi"))
    }

    private fun e(file: File?) {
        Log.e("file", "$file")
    }

}