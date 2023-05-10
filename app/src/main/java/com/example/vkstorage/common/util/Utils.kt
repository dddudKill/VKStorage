package com.example.vkstorage.common.util

import com.example.vkstorage.R
import com.example.vkstorage.domain.model.MyFile
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

fun calculateHash(file: File): String {
    val md = MessageDigest.getInstance("SHA-256")
    val fis = FileInputStream(file)
    val buffer = ByteArray(8192)
    var read: Int
    while (fis.read(buffer).also { read = it } != -1) {
        md.update(buffer, 0, read)
    }
    fis.close()
    val digest = md.digest()
    return digest.fold("") { str, it -> str + "%02x".format(it) }
}

fun getAllFiles(file: File): List<File> {
    if (file.listFiles().isNullOrEmpty()) return emptyList()
    val files = buildList<File> {
        for (path in file.listFiles()!!) {
            if (path.isDirectory) {
                addAll(getAllFiles(path))
            } else {
                add(path)
            }
        }
    }
    return files
}

fun File.calculateFileSize(): Long {
    var size = this.length()
    if (this.isFile) return size
    val files = this.listFiles()
    if (files != null) {
        for (file in files) {
            size += if (file.isDirectory) {
                file.calculateFileSize()
            } else {
                file.length()
            }
        }
    }
    return size
}

fun Long.toReadableFileSize(): String {
    var size = this
    if (size < 1024) return "${size}B"
    size /= 1024
    if (size < 1024) return "${size}KB"
    size /= 1024
    if (size < 1024) return "${size}MB"
    size /= 1024
    return "${size}GB"
}

fun getFileIcon(file: File): Int {
    if (file.isDirectory) return R.drawable.baseline_folder_24
    return when (file.extension) {
        "jpg" -> R.drawable.icon_jpg
        "jpeg" -> R.drawable.icon_jpg
        "png" -> R.drawable.icon_png
        "gif" -> R.drawable.icon_giff
        "bmp" -> R.drawable.icon_bmp
        "tiff" -> R.drawable.icon_tiff
        "mp4" -> R.drawable.icon_mp4
        "avi" -> R.drawable.icon_avi
        "mov" -> R.drawable.icon_mov
        "flv" -> R.drawable.icon_flv
        "mpeg" -> R.drawable.icon_mpeg
        "mp3" -> R.drawable.icon_mp3
        "wav" -> R.drawable.icon_wav
        "wma" -> R.drawable.icon_wma
        "pdf" -> R.drawable.icon_mpeg
        "doc" -> R.drawable.icon_doc
        "docx" -> R.drawable.icon_docx
        "ppt" -> R.drawable.icon_ppt
        "txt" -> R.drawable.icon_txt
        "csv" -> R.drawable.icon_csv
        "xml" -> R.drawable.icon_xml
        "xsl" -> R.drawable.icon_xsl
        "html" -> R.drawable.icon_html
        "ps" -> R.drawable.icon_ps
        "eps" -> R.drawable.icon_eps
        "svg" -> R.drawable.icon_svg
        "crd" -> R.drawable.icon_crd
        "pub" -> R.drawable.icon_pub
        "zip" -> R.drawable.icon_zip
        "rar" -> R.drawable.icon_rar
        "iso" -> R.drawable.icon_iso
        "exe" -> R.drawable.icon_exe
        "dll" -> R.drawable.icon_dll
        "java" -> R.drawable.icon_java
        "dwg" -> R.drawable.icon_dwg
        "mbd" -> R.drawable.icon_mdb
        "raw" -> R.drawable.icon_raw
        else -> R.drawable.baseline_insert_drive_file_24
    }
}

fun File.toMyFile(): MyFile {
    return MyFile(this.absolutePath)
}
