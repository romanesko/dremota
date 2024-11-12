package dremota.service


import dremota.Balance
import dremota.lib.dateFormat
import dremota.models.BalanceDTO
import dremota.models.toDTO
import dremota.plugins.Db
import java.time.LocalDateTime


object BalanceService {
    fun getAllForUser(userId: Long): List<BalanceDTO> {

        val balances = Db.balanceQueries.selectForChatId(userId).executeAsList().map(Balance::toDTO)

        for (b in balances) {
            if (b.paymentId != null) {
                b.payment = PaymentsService.getPaymentById(b.paymentId)
            }
        }

        return balances


    }

    fun create(userId: Long, days: Long, rationale: String, payment: Long? = null) {

        var paidUntil = UsersService.getPaidUntil(userId)

        if (paidUntil == null || paidUntil.isBefore(LocalDateTime.now())) {
            paidUntil = LocalDateTime.now()
        }

        val paidUntilStr = paidUntil!!.plusDays(days).format(dateFormat)

        Db.balanceQueries.insert(
            chat_id = userId,
            days = days,
            due = paidUntilStr,
            payment = payment,
            rationale = rationale
        )

        UsersService.updatePaidUntil(userId, paidUntilStr)

    }


}


