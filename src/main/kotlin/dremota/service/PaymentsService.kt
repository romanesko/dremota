package dremota.service


import dremota.Payment
import dremota.models.IncomeDTO
import dremota.models.PaymentDTO
import dremota.models.toDTO
import dremota.plugins.Db

//val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")




object PaymentsService {
    fun getAllForUser(userId: Long): List<PaymentDTO> {
        return Db.paymentsQueries.selectForChatId(userId).executeAsList().map(Payment::toDTO)
    }

    fun getPaymentById(id: Long): PaymentDTO {
        return Db.paymentsQueries.selectById(id).executeAsOne().toDTO()
    }

    fun insert(userId: Long, totalAmount: Int, currency: String, invoicePayload: String, providerPaymentChargeId: String, telegramPaymentChargeId: String): Long {
        return Db.paymentsQueries.insert(userId, totalAmount.toLong(), currency, invoicePayload, providerPaymentChargeId, telegramPaymentChargeId).executeAsOne()
    }

    fun getTotalForUser(id: Long): List<IncomeDTO> {
        val payments =  Db.paymentsQueries.paymentsTotalForUser(id).executeAsList()
        return payments.map { IncomeDTO(it.amount!!,it.currency) }


    }

}


