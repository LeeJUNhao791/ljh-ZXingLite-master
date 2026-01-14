package com.king.zxing.app

import android.widget.Toast
import com.google.zxing.Result
import com.king.camera.scan.AnalyzeResult
import com.king.camera.scan.CameraScan
import com.king.camera.scan.analyze.Analyzer
import com.king.zxing.DecodeConfig
import com.king.zxing.BarcodeCameraScanActivity
import com.king.zxing.analyze.MultiFormatAnalyzer

/**
 * 连续扫码（识别多种格式）示例
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * <p>
 * <a href="https://github.com/jenly1314">Follow me</a>
 */
class MultiFormatScanActivity : BarcodeCameraScanActivity() {

    private var toast: Toast? = null

    override fun initCameraScan(cameraScan: CameraScan<Result>) {
        super.initCameraScan(cameraScan)
        // 根据需要设置CameraScan相关配置
        cameraScan.setPlayBeep(true)
    }

    override fun createAnalyzer(): Analyzer<Result>? {
        // 初始化解码配置
        val decodeConfig = DecodeConfig().apply {
            // 设置是否支持扫垂直的条码
            isSupportVerticalCode = true
            // 设置是否支持识别反色码，黑白颜色反转
            isSupportLuminanceInvert = true
        }
        // 多格式分析器（支持的条码格式主要包含：一维码和二维码）
        return MultiFormatAnalyzer(decodeConfig)
    }

    /**
     * 布局ID；通过覆写此方法可以自定义布局
     *
     * @return 布局ID
     */
    override fun getLayoutId(): Int {
        return super.getLayoutId()
    }

    private var lastScanTime: Long = 0

    override fun onScanResultCallback(result: AnalyzeResult<Result>) {
        // 获取当前时间戳作为起始时间
        val startTime = System.currentTimeMillis()

        // 停止分析
        cameraScan.setAnalyzeImage(false)

        // 计算与上次扫描的时间间隔
        val currentTime = System.currentTimeMillis()
        val interval = if (lastScanTime > 0) {
            currentTime - lastScanTime
        } else {
            0
        }
        lastScanTime = currentTime

        // 处理扫码结果相关逻辑（只显示一行，超出则用省略号显示）
        val originalText = result.result.text
        val displayText = if (originalText.length > 20) {
            "${originalText.substring(0, 15)}..."
        } else {
            originalText
        }

        // 为了准确计算解码耗时，应该在处理完结果后再获取当前时间
        val textWithInterval = "$displayText\n扫码: ${interval}ms"
        showToast(textWithInterval)

        // 计算解码耗时（从接收到结果到处理完成）
        val decodeTime = System.currentTimeMillis() - startTime
        // 更新显示文本以包含解码时间
        val updatedText = "$displayText\n扫码: ${interval}ms，解码: ${decodeTime}ms"
        // 由于Toast已经显示，这里可以考虑不重新显示，或者使用其他方式更新UI
        // 此处仅作演示，实际应用中可能需要更优雅的处理方式
        showToast(updatedText)

        // 继续分析
        cameraScan.setAnalyzeImage(true)
    }

    private fun showToast(text: String) {
        toast?.cancel()
        toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
        toast?.show()
    }
}
