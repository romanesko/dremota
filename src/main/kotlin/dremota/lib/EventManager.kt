package dremota.lib


interface Event


data class BotCommandsChangedEvent(val message: String) : Event
class PriceChangedEvent() : Event
class SettingsChangedEvent() : Event
data class ReferralAcceptedEvent(val userId: Long, val message: String) : Event

object EventManager {
    data class ReferralAcceptedForNewcomerEvent(val userId: Long) : Event
    private val listeners = mutableMapOf<Class<*>, MutableList<(Event) -> Unit>>()

    fun <T : Event> addListener(eventType: Class<T>, listener: (T) -> Unit) {
        val eventListeners = listeners.getOrPut(eventType) { mutableListOf() }
        @Suppress("UNCHECKED_CAST")
        eventListeners.add(listener as (Event) -> Unit)
    }

    fun <T : Event> notify(event: T) {
//        GlobalScope.launch {
            listeners[event::class.java]?.forEach { listener ->
                listener(event)
            }
//        }
    }
}