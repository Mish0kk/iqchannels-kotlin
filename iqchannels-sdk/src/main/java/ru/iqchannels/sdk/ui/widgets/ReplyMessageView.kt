package ru.iqchannels.sdk.ui.widgets

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import ru.iqchannels.sdk.R
import ru.iqchannels.sdk.app.IQChannels
import ru.iqchannels.sdk.schema.ChatMessage

class ReplyMessageView @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyleAttr: Int = 0
) : ConstraintLayout(context) {

	private val tvSenderName: TextView
	private val tvText: TextView
	private val tvFileName: TextView
	private val imageView: ImageView
	private val ibClose: ImageButton
	private val dividerStart: View

	init {
		inflate(context, R.layout.layout_reply_to_message, this)
		tvSenderName = findViewById(R.id.tvSenderName)
		tvText = findViewById(R.id.tv_text)
		tvFileName = findViewById(R.id.tvFileName)
		imageView = findViewById(R.id.iv_image)
		ibClose = findViewById(R.id.ib_close)
		dividerStart = findViewById(R.id.divider_start)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
			tvSenderName.setTypeface(Typeface.create(null, 700, false))
			tvText.setTypeface(Typeface.create(null, 400, false))
			tvFileName.setTypeface(Typeface.create(null, 400, false))
		}
	}

	fun showReplyingMessage(message: ChatMessage) {
		visibility = VISIBLE

		if (message.User != null) {
			tvSenderName.text =
				if (message.User?.DisplayName == "") message.User?.Name else message.User?.DisplayName
		} else if (message.Client != null) {
			tvSenderName.text = message.Client?.Name
		}

		if (message.Text != null && message.Text?.isNotEmpty() == true) {
			tvText.visibility = VISIBLE
			tvText.text = message.Text
		} else {
			tvText.visibility = GONE
		}

		if (message.File != null) {
			val imageUrl = message.File?.ImagePreviewUrl
			if (imageUrl != null) {
				tvFileName.visibility = GONE
				imageView.visibility = VISIBLE
				IQChannels.picasso()
					?.load(imageUrl)
					?.into(imageView)
			} else {
				imageView.visibility = GONE
				tvFileName.visibility = VISIBLE
				tvFileName.text = message.File?.Name
			}
		} else {
			tvFileName.visibility = GONE
			imageView.visibility = GONE
		}
	}

	fun setCloseBtnVisibility(visibility: Int) {
		ibClose.visibility = visibility
	}

	fun setVerticalDividerColor(@ColorRes id: Int) {
		dividerStart.setBackgroundColor(
			ContextCompat.getColor(context, id)
		)
	}

	fun setTvSenderNameColor(@ColorRes id: Int) {
		tvSenderName.setTextColor(
			ContextCompat.getColor(context, id)
		)
	}

	fun setTvTextColor(@ColorRes id: Int) {
		tvText.setTextColor(
			ContextCompat.getColor(context, id)
		)
	}

	fun setCloseBtnClickListener(listener: OnClickListener?) {
		ibClose.setOnClickListener(listener)
	}
}