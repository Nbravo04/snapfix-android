// EfficientDetDetector.kt — EfficientDet Lite0 (ACTUALLY WORKS!)
package com.spotfix.android.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.util.Log
import com.spotfix.android.model.Detection
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

private const val TAG = "EfficientDetDetector"

class EfficientDetDetector(private val context: Context) {

    private var interpreter: Interpreter? = null
    private val threshold = 0.5f  // Confidence threshold

    /**
     * Flag indicating whether the model was loaded successfully.
     * If false, detect() will return empty results.
     */
    var isModelLoaded: Boolean = false
        private set

    private val labels = listOf(
        "person","bicycle","car","motorcycle","airplane","bus","train","truck","boat","traffic light",
        "fire hydrant","stop sign","parking meter","bench","bird","cat","dog","horse","sheep","cow",
        "elephant","bear","zebra","giraffe","backpack","umbrella","handbag","tie","suitcase","frisbee",
        "skis","snowboard","sports ball","kite","baseball bat","baseball glove","skateboard","surfboard",
        "tennis racket","bottle","wine glass","cup","fork","knife","spoon","bowl","banana","apple",
        "sandwich","orange","broccoli","carrot","hot dog","pizza","donut","cake","chair","couch",
        "potted plant","bed","dining table","toilet","tv","laptop","mouse","remote","keyboard","cell phone",
        "microwave","oven","toaster","sink","refrigerator","book","clock","vase","scissors","teddy bear",
        "hair drier","toothbrush"
    )

    init {
        try {
            Log.d(TAG, "Loading EfficientDet Lite0 model...")
            val model = loadModelFile()
            interpreter = Interpreter(model)
            isModelLoaded = true
            Log.d(TAG, "Model loaded successfully")

            // Log all input/output tensors
            interpreter?.let { interp ->
                val numInputs = interp.inputTensorCount
                val numOutputs = interp.outputTensorCount
                Log.d(TAG, "Number of inputs: $numInputs, outputs: $numOutputs")

                for (i in 0 until numInputs) {
                    val tensor = interp.getInputTensor(i)
                    Log.d(TAG, "Input $i: ${tensor.shape().contentToString()}, type: ${tensor.dataType()}")
                }

                for (i in 0 until numOutputs) {
                    val tensor = interp.getOutputTensor(i)
                    Log.d(TAG, "Output $i: ${tensor.shape().contentToString()}, type: ${tensor.dataType()}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load EfficientDet model", e)
            isModelLoaded = false
            interpreter = null
        }
    }

    companion object {
        /**
         * Check if a detector instance has its model loaded successfully.
         * Useful for UI to show appropriate error states.
         */
        fun isModelAvailable(detector: EfficientDetDetector): Boolean {
            return detector.isModelLoaded
        }
    }

    private fun loadModelFile(): MappedByteBuffer {
        val fd = context.assets.openFd("efficientdet_lite0.tflite")
        val inputStream = FileInputStream(fd.fileDescriptor)
        val channel = inputStream.channel
        return channel.map(FileChannel.MapMode.READ_ONLY, fd.startOffset, fd.declaredLength)
    }

    fun detect(originalBitmap: Bitmap): List<Detection> {
        // Return early if model failed to load
        if (!isModelLoaded || interpreter == null) {
            Log.w(TAG, "Cannot run detection: model not loaded")
            return emptyList()
        }

        Log.d(TAG, "Running inference on ${originalBitmap.width}x${originalBitmap.height}")

        // EfficientDet expects 320x320 input
        val resized = Bitmap.createScaledBitmap(originalBitmap, 320, 320, true)

        // Prepare input - EfficientDet uses UINT8 input (0-255)
        val tensorImage = TensorImage(DataType.UINT8)
        tensorImage.load(resized)
        val input = tensorImage.buffer

        // EfficientDet outputs 4 tensors:
        // 0: locations [1, 25, 4] - bounding boxes (ymin, xmin, ymax, xmax)
        // 1: classes [1, 25] - class indices
        // 2: scores [1, 25] - confidence scores
        // 3: num_detections [1] - number of valid detections

        val outputLocations = Array(1) { Array(25) { FloatArray(4) } }
        val outputClasses = Array(1) { FloatArray(25) }
        val outputScores = Array(1) { FloatArray(25) }
        val numDetections = FloatArray(1)

        val outputs = mapOf(
            0 to outputLocations,
            1 to outputClasses,
            2 to outputScores,
            3 to numDetections
        )

        try {
            interpreter?.runForMultipleInputsOutputs(arrayOf(input), outputs)
            Log.d(TAG, "Inference succeeded")
        } catch (e: Exception) {
            Log.e(TAG, "Inference failed", e)
            return emptyList()
        }

        val detections = mutableListOf<Detection>()
        val numValidDetections = numDetections[0].toInt().coerceAtMost(25)

        Log.d(TAG, "Number of detections from model: $numValidDetections")

        for (i in 0 until numValidDetections) {
            val score = outputScores[0][i]

            // Filter by confidence
            if (score < threshold) continue

            val classId = outputClasses[0][i].toInt()

            // Skip invalid class IDs
            if (classId < 0 || classId >= labels.size) continue

            val label = labels[classId]

            // Get bounding box (normalized coordinates 0-1)
            // Format: [ymin, xmin, ymax, xmax]
            val ymin = outputLocations[0][i][0]
            val xmin = outputLocations[0][i][1]
            val ymax = outputLocations[0][i][2]
            val xmax = outputLocations[0][i][3]

            // Convert to pixel coordinates
            val left = xmin * originalBitmap.width
            val top = ymin * originalBitmap.height
            val right = xmax * originalBitmap.width
            val bottom = ymax * originalBitmap.height

            // Validate bounding box
            if (left >= right || top >= bottom) continue
            if (left < 0 || top < 0 || right > originalBitmap.width || bottom > originalBitmap.height) continue

            detections.add(Detection(RectF(left, top, right, bottom), label, score))

            // Debug first few detections
            if (detections.size <= 3) {
                Log.d(TAG, "Detection ${detections.size}: $label (${(score * 100).toInt()}%) at [$left, $top, $right, $bottom]")
            }
        }

        Log.d(TAG, "Found ${detections.size} valid detections")
        return detections
    }

    fun close() {
        interpreter?.close()
    }
}
