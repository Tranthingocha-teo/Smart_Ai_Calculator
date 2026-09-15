package dhn.intern.smart_ai_caculator_app.ui.components.aiCalculator

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import dhn.intern.smart_ai_caculator_app.R
import dhn.intern.smart_ai_caculator_app.domain.ocr.MathOcrEngine
import dhn.intern.smart_ai_caculator_app.domain.ocr.MlKitMathOcrEngine
import dhn.intern.smart_ai_caculator_app.domain.ocr.model.MathOcrResult
import dhn.intern.smart_ai_caculator_app.navigation.NavScreen
import dhn.intern.smart_ai_caculator_app.ui.components.NavBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject

@Composable
fun AiScanScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onSwitchToChat: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val cameraPermission = Manifest.permission.CAMERA
    val activity = context as? Activity

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, cameraPermission) == PackageManager.PERMISSION_GRANTED
        )
    }

    var requestedPermission by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasPermission = granted
    }

    LaunchedEffect(hasPermission, requestedPermission) {
        if (!hasPermission && !requestedPermission) {
            requestedPermission = true
            launcher.launch(cameraPermission)
        }
    }

    val shouldShowRationale = activity?.let {
        ActivityCompat.shouldShowRequestPermissionRationale(it, cameraPermission)
    } ?: false

    Box(modifier = modifier) {
        when {
            hasPermission -> {
                SuccessPermission(
                    navController = navController,
                    onSwitchToChat = onSwitchToChat
                )
            }

            requestedPermission && !hasPermission && !shouldShowRationale -> {
                RequestPermission()
            }

            !hasPermission -> {
                BlockPermission(context)
            }
        }
    }
}

@Composable
fun SuccessPermission(
    navController: NavHostController,
    onSwitchToChat: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val ocrEngine: MathOcrEngine = koinInject()
    val mlKitEngine: MlKitMathOcrEngine = koinInject()
    val executor = remember { ContextCompat.getMainExecutor(context) }
    val snackbarHostState = remember { SnackbarHostState() }

    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var camera: Camera? by remember { mutableStateOf(null) }
    var isFlashOn by remember { mutableStateOf(false) }
    var isScanning by remember { mutableStateOf(false) }
    var scanResult by remember { mutableStateOf<MathOcrResult?>(null) }

    // Turn off torch when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            camera?.let { cam ->
                if (cam.cameraInfo.hasFlashUnit()) {
                    cam.cameraControl.enableTorch(false)
                }
            }
        }
    }

    suspend fun performOcr(bitmap: Bitmap): MathOcrResult {
        val mlKitResult = try {
            mlKitEngine.recognize(bitmap)
        } catch (e: Exception) {
            null
        }

        return if (mlKitResult != null && mlKitResult.formattedExpression.isNotBlank()) {
            mlKitResult
        } else {
            ocrEngine.recognize(bitmap)
        }
    }

    fun showNoFormulaSnackbar() {
        coroutineScope.launch {
            val action = snackbarHostState.showSnackbar(
                message = context.getString(R.string.math_scan_no_formula),
                actionLabel = context.getString(R.string.math_scan_manual_input),
                duration = SnackbarDuration.Short
            )
            if (action == SnackbarResult.ActionPerformed) {
                scanResult = MathOcrResult(
                    rawExpression = "",
                    formattedExpression = "",
                    symbols = emptyList(),
                    averageConfidence = 1.0f
                )
            }
        }
    }

    // Gallery Picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            isScanning = true
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    val stream = context.contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(stream)
                    stream?.close()
                    if (bitmap != null) {
                        val result = performOcr(bitmap)
                        withContext(Dispatchers.Main) {
                            isScanning = false
                            if (result.symbols.isEmpty() || result.formattedExpression.isBlank()) {
                                showNoFormulaSnackbar()
                            } else {
                                scanResult = result
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            isScanning = false
                            showNoFormulaSnackbar()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        isScanning = false
                        showNoFormulaSnackbar()
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Camera Stream
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            onImageCaptureReady = { capture -> imageCapture = capture },
            onCameraReady = { cam -> camera = cam }
        )

        // Modern Math Reticle Overlay with animated scanning line
        ScanReticleOverlay(
            modifier = Modifier.fillMaxSize(),
            isScanning = isScanning
        )

        // Top & Bottom Controls
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 110.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            NavBar(
                navController = navController,
                title = R.string.math_scan_title,
                contentColor = Color.White
            )

            ButtonScanChatAi(
                onGalleryClick = {
                    if (!isScanning) {
                        galleryLauncher.launch("image/*")
                    }
                },
                onCaptureClick = {
                    val capture = imageCapture
                    if (capture != null && !isScanning) {
                        isScanning = true
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        capture.takePicture(
                            executor,
                            object : ImageCapture.OnImageCapturedCallback() {
                                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                                    coroutineScope.launch(Dispatchers.Default) {
                                        try {
                                            val fullBitmap = imageProxyToBitmap(imageProxy)
                                            val croppedBitmap = cropToReticle(fullBitmap)
                                            val result = performOcr(croppedBitmap)
                                            withContext(Dispatchers.Main) {
                                                isScanning = false
                                                if (result.symbols.isEmpty() || result.formattedExpression.isBlank()) {
                                                    showNoFormulaSnackbar()
                                                } else {
                                                    scanResult = result
                                                }
                                            }
                                        } catch (e: Exception) {
                                            withContext(Dispatchers.Main) {
                                                isScanning = false
                                                showNoFormulaSnackbar()
                                            }
                                        } finally {
                                            imageProxy.close()
                                        }
                                    }
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    isScanning = false
                                    Toast.makeText(context, "Capture error: ${exception.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                },
                onFlashClick = {
                    camera?.let { cam ->
                        if (cam.cameraInfo.hasFlashUnit()) {
                            val nextFlash = !isFlashOn
                            cam.cameraControl.enableTorch(nextFlash)
                            isFlashOn = nextFlash
                        }
                    }
                },
                isFlashOn = isFlashOn
            )
        }

        // Non-intrusive Snackbar for fallback/retry
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 130.dp, start = 16.dp, end = 16.dp)
        )

        // Result Bottom Sheet
        scanResult?.let { result ->
            MathScanResultBottomSheet(
                scanResult = result,
                onDismiss = { scanResult = null },
                onInsertToCalculator = { formula ->
                    scanResult = null
                    navController.navigate(NavScreen.BasicCaculatorScreen.createRoute(formula))
                },
                onDrawGraph = { formula ->
                    scanResult = null
                    navController.navigate(NavScreen.GraphingCalculatorScreen.createRoute(formula))
                },
                onAskAi = { formula ->
                    camera?.let { cam ->
                        if (cam.cameraInfo.hasFlashUnit()) {
                            cam.cameraControl.enableTorch(false)
                            isFlashOn = false
                        }
                    }
                    scanResult = null
                    onSwitchToChat(formula)
                },
                onScanAgain = { scanResult = null }
            )
        }
    }
}

/**
 * Crops an upright captured camera bitmap to the bounds of the scanning reticle.
 * This shrinks processing area from 12M pixels down to ~0.2M pixels (< 50ms).
 */
private fun cropToReticle(bitmap: Bitmap): Bitmap {
    val left = (bitmap.width * 0.08f).toInt().coerceIn(0, bitmap.width - 1)
    val right = (bitmap.width * 0.92f).toInt().coerceIn(left + 1, bitmap.width)
    val width = (right - left).coerceAtLeast(1)

    val centerFractionY = 0.45f
    val heightFraction = 0.28f
    val top = ((centerFractionY - heightFraction / 2f) * bitmap.height).toInt().coerceIn(0, bitmap.height - 1)
    val bottom = (top + (heightFraction * bitmap.height).toInt()).coerceIn(top + 1, bitmap.height)
    val height = (bottom - top).coerceAtLeast(1)

    return try {
        Bitmap.createBitmap(bitmap, left, top, width, height)
    } catch (e: Exception) {
        bitmap
    }
}

/**
 * Converts CameraX ImageProxy to an upright Android Bitmap.
 */
private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap {
    val buffer = imageProxy.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    var bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    if (bitmap != null && imageProxy.imageInfo.rotationDegrees != 0) {
        val matrix = Matrix().apply {
            postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
        }
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    return bitmap ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
}

@Composable
fun RequestPermission() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.ai_calculator_no_requesting), color = Color.White)
        Spacer(modifier = Modifier.height(12.dp))
        CircularProgressIndicator(color = Color.White)
    }
}

@Composable
fun BlockPermission(context: Context) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.ai_calculator_block_permission),
            color = Color.White
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null)
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }) {
            Text(text = stringResource(R.string.ai_calculator_open_setting))
        }
    }
}
