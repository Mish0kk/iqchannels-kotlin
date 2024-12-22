package ru.iqchannels.sdk.schema

import com.google.gson.Gson

class RatingPollClientAnswerInput {
	var ProjectId: Long = 0
	var ClientId: Long = 0
	var RatingId: Long = 0
	var RatingPollQuestionId: Long = 0
	var Type: PollOptionType = PollOptionType.FCR
	var FCR: Boolean? = null
	var RatingPollAnswerId: Long? = null
	var AnswerInput: String? = null
	var AnswerStars: Long? = null
	var AnswerScale: Int? = null
	var AsTicketRating: Boolean? = null

	constructor(
		projectId: Long,
		clientId: Long,
		ratingId: Long,
		questionId: Long,
		type: PollOptionType,
		answerId: Long? = null,
		input: String? = null,
		stars: Long? = null,
		scaleValue: Int? = null,
		fcr: Boolean? = null,
		asTicketRating: Boolean? = null,
	) {
		ProjectId = projectId
		ClientId = clientId
		RatingId = ratingId
		RatingPollQuestionId = questionId
		Type = type
		RatingPollAnswerId = answerId
		AnswerInput = input
		AnswerStars = stars
		AnswerScale = scaleValue
		FCR = fcr
		AsTicketRating = asTicketRating
	}

	override fun toString(): String {
		return Gson().toJson(this)
	}
}