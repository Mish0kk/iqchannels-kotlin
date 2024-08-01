package ru.iqchannels.sdk.configs

import ru.iqchannels.sdk.schema.ChatFilesConfig
import ru.iqchannels.sdk.schema.UploadedFile

interface GetConfigsInteractor {

	suspend fun getFileConfigs(): ChatFilesConfig?

	fun getFile(fileId: String): UploadedFile?
}