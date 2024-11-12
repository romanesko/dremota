package dremota.service

import dremota.lib.EventManager
import dremota.lib.ReferralAcceptedEvent
import dremota.models.ReferralDTO
import dremota.models.UserDTO
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object ReferralService {

    fun getSettings(): ReferralDTO {

        val referral = SettingsService.getSettingsByKey("referral")

        return if (referral.isNullOrEmpty()) {
            return ReferralDTO(
                enabled = true,
                holderReceives = 0,
                holderMessage = "",
                referrerReceives = 0,
                referrerMessage = ""
            )
        } else Json.decodeFromString(referral)

    }

    fun updateSettings(settings: ReferralDTO) {

        Json.encodeToString(settings).let { json ->
            SettingsService.setByKey("referral", json)
        }
    }

    fun getUserIdByReferralCode(referralCode: String?): Long? {
        if (referralCode == null) return null
        return UsersService.findByReferralCode(referralCode)?.chatId
    }

    fun processReferral(newRegisteredUser: UserDTO, referralCode: String?) {
        if (referralCode == null) {
            return
        }

        val referral = getSettings()

        if (!referral.enabled) {
            return
        }

        UsersService.findByReferralCode(referralCode)?.let { referralOwner ->

            if (referral.holderReceives > 0) {
                BalanceService.create(
                    referralOwner.chatId,
                    referral.holderReceives,
                    "За регистрацию по рефералу (владелец)"
                )
                if (referral.holderMessage.isNotEmpty()){
                    EventManager.notify(ReferralAcceptedEvent(referralOwner.chatId, referral.holderMessage))
                }
            }

            if (referral.referrerReceives > 0) {
                BalanceService.create(
                    newRegisteredUser.chatId,
                    referral.referrerReceives,
                    "За регистрацию по рефералу (получатель)"
                )
                if (referral.referrerMessage.isNotEmpty()){
                EventManager.notify(ReferralAcceptedEvent(newRegisteredUser.chatId, referral.referrerMessage))
                }
            }
        }


    }
}