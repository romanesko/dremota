package dremota.models

enum class CommandType(val description: String) {
    START("Команда старта"),
    FEEDBACK("Обратная связь"),
    MESSAGE("Сообщение"),
    PAY("Принять оплату")
}