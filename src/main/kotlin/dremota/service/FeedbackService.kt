package dremota.service

import dremota.Feedback
import dremota.models.FeedbackDTO
import dremota.models.toDTO
import dremota.plugins.Db

object FeedbackService {
    fun saveFeedback(userId: Long, message: String) {
        Db.feedbackQueries.insert(userId, message)
    }

    fun getAllFeedback(): List<FeedbackDTO> {
        return Db.feedbackQueries.selectAll().executeAsList().map(Feedback::toDTO)
    }

    fun getAllForUser(userId: Long): List<FeedbackDTO> {
        return Db.feedbackQueries.selectAllByUserId(userId).executeAsList().map(Feedback::toDTO)
    }

    fun markAsRead(id: Long) {
        Db.feedbackQueries.markAsRead(id)
    }

    fun hasUnreadFeedback(): List<Long> {
        return Db.feedbackQueries.usersWithUnread().executeAsList()
    }
}