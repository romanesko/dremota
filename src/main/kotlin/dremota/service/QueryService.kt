package dremota.service

import dremota.Queries
import dremota.models.QueriesDTO
import dremota.models.toDTO
import dremota.plugins.Db
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object QueryService {

    private val logger: Logger = LoggerFactory.getLogger("QueryService")


    fun getAll(offset: Long, limit: Long): List<QueriesDTO> {
        return Db.queriesQueries.selectAll(offset, limit).executeAsList().map(Queries::toDTO)
    }

    fun getForUser(userId: Long): List<QueriesDTO> {
        return Db.queriesQueries.selectAllByChatId(userId).executeAsList().map(Queries::toDTO)
    }


    fun insertRequest(chatId: Long, context: String, prefix: String, message: String): Long {

        try {
            return Db.queriesQueries.insert(
                    chat_id = chatId,
                    request = message,
                    context = context,
                    prefix = prefix,
                ).executeAsOne()

        } catch (e: Exception) {
            logger.error("Error while inserting request", e)
            throw e
        }


    }

    fun setResponse(output: String, reqId: Long) {
        Db.queriesQueries.setResponse(output, reqId)
    }

    fun countForUser(userId: Long): Long {
        return Db.queriesQueries.countByChatId(userId).executeAsOne()
    }

}