package ru.iqchannels.sdk.ui

import android.app.Activity
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.io.FileOutputStream
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.iqchannels.sdk.Log
import ru.iqchannels.sdk.app.IQChannels
import ru.iqchannels.sdk.app.IQChannelsConfigRepository
import ru.iqchannels.sdk.configs.GetConfigsInteractorImpl
import ru.iqchannels.sdk.domain.models.PreFilledMessages
import ru.iqchannels.sdk.http.retrofit.NetworkModule
import ru.iqchannels.sdk.lib.InternalIO

class ChatViewModel : ViewModel() {

	private val multipleFilesQueue: MutableList<Uri> = mutableListOf()
	private val multipleTextsQueue: MutableList<String> = mutableListOf()

	private var preFilledSendingStarted = true
	private var lastTextFromQueueSent = false

	fun getConfigs() {
		viewModelScope.launch(Dispatchers.IO) {
			try {
				val interactor = GetConfigsInteractorImpl(NetworkModule.provideGetConfigApiService())
				val configs = interactor.getFileConfigs()
				Log.d("ChatViewModel", "success get chat file configs: $configs")
				IQChannelsConfigRepository.chatFilesConfig = configs
			} catch (e: Exception) {
				Log.e("ChatViewModel", e.message, e)
			}
		}
	}

	fun sendFile(uri: Uri, activity: Activity) {
		val file = prepareFile(uri, activity)?.also {
			Log.d("prefilledmsg", "prepareFile: $it")
		}
		IQChannels.sendFile(file, null)
	}

	fun prepareFile(uri: Uri, activity: Activity) = try {
		val resolver = activity.contentResolver
		val mimeTypeMap = MimeTypeMap.getSingleton()
		val mtype = resolver.getType(uri)
		val ext = mimeTypeMap.getExtensionFromMimeType(mtype)
		val file = FileUtils.createGalleryTempFile(activity, uri, ext)
		val `in` = resolver.openInputStream(uri)

		if (`in` == null) {
			Log.e(ChatFragment.TAG, "onGalleryResult: Failed to pick a file, no input stream")
			null
		} else {
			`in`.use { `in` ->
				val out = FileOutputStream(file)
				out.use { out ->
					InternalIO.copy(`in`, out)
				}
			}
			file
		}
	} catch (e: IOException) {
		Log.d("prefilledmsg", "prepareFile IOexception: ${e.message}")
		Log.e(ChatFragment.TAG, String.format("onGalleryResult: Failed to pick a file, e=%s", e))
		null
	} catch (e: Exception) {
		Log.d("prefilledmsg", "prepareFile exception: ${e.message}")
		null
	}

	fun sendMsgFromQueue(activity: Activity) {
		when {
			multipleTextsQueue.isNotEmpty() -> sendNextText()
			lastTextFromQueueSent -> {
				lastTextFromQueueSent = false
				sendNextFile(activity)
			}
		}
	}

	private fun sendNextText() {
		runCatching {
			if (multipleTextsQueue.size == 1) {
				lastTextFromQueueSent = true
			}
			multipleTextsQueue.removeFirst()
		}.getOrNull()
			?.let {
				Log.d("prefilledmsg", "sendNextText: $it")
				IQChannels.send(it, null)
			}
	}

	fun sendNextFile(activity: Activity) {
		runCatching { multipleFilesQueue.removeFirst() }
			.getOrNull()
			?.let {
				Log.d("prefilledmsg", "sendNextFile: $it")
				sendFile(it, activity)
			}
	}

	fun addMultipleFilesQueue(files: List<Uri>) {
		multipleFilesQueue.addAll(files)
	}

	fun getNextFileFromQueue() = runCatching {
		multipleFilesQueue.removeFirst()
	}.getOrNull()

	fun clearFilesQueue() = multipleFilesQueue.clear()

	fun applyPrefilledMessages(preFilledMessages: PreFilledMessages) {
		Log.d("prefilledmsg", "applyPrefilledMessages: $preFilledMessages")
		preFilledMessages.textMsg?.let {
			Log.d("prefilledmsg", "add textMsg size: ${it.size}")
			multipleTextsQueue.addAll(it)
		}

		preFilledMessages.fileMsg?.let {
			Log.d("prefilledmsg", "add fileMsg size: ${it.size}")
			multipleFilesQueue.addAll(it)
		}

		preFilledSendingStarted = false
	}

	fun startSendPrefilled(activity: Activity) {
		if (!preFilledSendingStarted) {
			Log.d("prefilledmsg", "startSendPrefilled")
			if (multipleTextsQueue.isEmpty()) {
				lastTextFromQueueSent = true
			}
			sendMsgFromQueue(activity)
			preFilledSendingStarted = true
		}
	}
}