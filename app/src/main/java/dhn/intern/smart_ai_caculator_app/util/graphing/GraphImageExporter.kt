package dhn.intern.smart_ai_caculator_app.util.graphing

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object GraphImageExporter {

    fun exportCanvasToPng(context: Context, bitmap: Bitmap): android.net.Uri {
        val cacheDir = File(context.cacheDir, "graph_images").apply { mkdirs() }
        val file = File(cacheDir, "graph_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }

    suspend fun saveBitmapToCache(context: Context, bitmap: Bitmap): Result<File> = withContext(Dispatchers.IO) {
        runCatching {
            val cacheDir = File(context.cacheDir, "graph_images").apply { mkdirs() }
            val file = File(cacheDir, "graph_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            file
        }
    }

    fun createShareIntent(context: Context, imageFile: File): Intent {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            imageFile
        )
        return Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    suspend fun shareGraphImage(context: Context, bitmap: Bitmap): Result<Unit> {
        return saveBitmapToCache(context, bitmap).map { file ->
            val shareIntent = Intent.createChooser(
                createShareIntent(context, file),
                "Chia sẻ đồ thị hàm số"
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(shareIntent)
        }
    }
}
