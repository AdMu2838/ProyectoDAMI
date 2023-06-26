package com.example.proyecto.services

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.media.Image
import android.media.ImageReader
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import com.example.proyecto.R

class CameraActivity : AppCompatActivity() {

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
        private val REQUEST_CODE_PERMISSIONS = 10
    }

    private lateinit var handler: Handler
    private lateinit var handlerThread: HandlerThread

    private lateinit var cameraManager: CameraManager
    private lateinit var textureView: TextureView
    private lateinit var captureSession: CameraCaptureSession
    private lateinit var cameraDevice: CameraDevice
    private lateinit var captureRequest: CaptureRequest.Builder
    private lateinit var imageReader: ImageReader
    private lateinit var btnFoto: Button

    private lateinit var outputDirectory: File
    private lateinit var dateFileName: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        val bundle = intent.extras
        dateFileName = bundle?.getString("fileName").toString()

        textureView = findViewById(R.id.backgroundCamera)
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        handlerThread = HandlerThread("videoThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)

        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                startCamera()
            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
        }

        imageReader = ImageReader.newInstance(1080, 1920, ImageFormat.JPEG, 1)
        imageReader.setOnImageAvailableListener(object : ImageReader.OnImageAvailableListener {
            override fun onImageAvailable(reader: ImageReader?) {
                val image = reader?.acquireLatestImage()
                val buffer = image?.planes?.get(0)?.buffer
                val bytes = ByteArray(buffer?.remaining() ?: 0)
                buffer?.get(bytes)

                outputDirectory = getOutputDirectory()

                val file = File(outputDirectory, dateFileName)
                val outputStream = FileOutputStream(file)
                outputStream.write(bytes)
                outputStream.close()

                image?.close()

                runOnUiThread {
                    Toast.makeText(this@CameraActivity, "Imagen Guardada", Toast.LENGTH_LONG).show()
                }
            }
        }, handler)

        btnFoto = findViewById(R.id.btn_camara)

        btnFoto.setOnClickListener {
            captureRequest = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureRequest.addTarget(imageReader.surface)
            captureSession.capture(captureRequest.build(), null, null)
        }

        if (allPermissionsGranted()) {
            Toast.makeText(this, "Permisos confirmados", Toast.LENGTH_LONG).show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun allPermissionsGranted(): Boolean {
        return REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Debes proporcionar los permisos para tomar fotos",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startCamera() {
        cameraManager.openCamera(
            cameraManager.cameraIdList[0],
            object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    cameraDevice = camera
                    captureRequest =
                        cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                    val surface = Surface(textureView.surfaceTexture)
                    captureRequest.addTarget(surface)
                    captureRequest.addTarget(imageReader.surface)

                    cameraDevice.createCaptureSession(
                        listOf(surface, imageReader.surface),
                        object : CameraCaptureSession.StateCallback() {
                            override fun onConfigured(session: CameraCaptureSession) {
                                captureSession = session
                                captureSession.setRepeatingRequest(
                                    captureRequest.build(),
                                    null,
                                    null
                                )
                            }

                            override fun onConfigureFailed(session: CameraCaptureSession) {
                                Log.e("CameraActivity", "Failed to configure camera capture session.")
                            }
                        },
                        handler
                    )
                }

                override fun onDisconnected(camera: CameraDevice) {
                    Log.e("CameraActivity", "Camera disconnected.")
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    Log.e("CameraActivity", "Camera error: $error")
                }
            },
            handler
        )
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, "Camera").apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }
}