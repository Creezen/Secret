package com.jayce.vexis.client

import android.content.Context
import com.jayce.vexis.client.AndroidTool.toast
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import java.io.File
import java.io.FileOutputStream

object AITool {

    private fun aiModel(context: Context) {
        kotlin.runCatching {
            val model = Module.load(assetFilePath(context, "model/androidModel.pt"))
            val tensor = Tensor.fromBlob(floatArrayOf(2.5f, 1.5f), longArrayOf(1, 2))
            val result = model.forward(IValue.from(tensor)).toTensor()
            result.dataAsFloatArray[0].toast()
        }.onFailure {
            it.printStackTrace()
        }
    }

    private fun assetFilePath(context: Context, assetName: String): String {
        val file = File(context.filesDir, assetName);
        if (file.exists() && file.length() > 0) {
            return file.absolutePath;
        }

        if (file.exists().not()) {
            file.parentFile?.mkdir()
            file.createNewFile()
        }

        context.assets.open(assetName).use { ins ->
            FileOutputStream(file).use { ous ->
                val buffer = ByteArray(4 * 1024)
                while (true) {
                    val read = ins.read(buffer)
                    if (read != -1) {
                        ous.write(buffer, 0, read);
                    } else break
                }
                ous.flush();
            }
        }
        return  file.absolutePath
    }
}