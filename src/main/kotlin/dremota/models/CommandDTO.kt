package dremota.models

import dremota.Command
import dremota.lib.fromDbBool
import kotlinx.serialization.Serializable

@Serializable
data class CommandDTO(
    val key: String,
    val description: String,
    val type: CommandType,
    val message: String,
    val postMessage: String,
    val showInMenu: Boolean,
    val enabled: Boolean,
    val system: Boolean? = null,

    )


fun Command.toDTO(): CommandDTO {

    val type = runCatching { CommandType.valueOf(this.type) }.getOrElse { CommandType.MESSAGE }

    return CommandDTO(
        key,
        description,
        type,
        message,
        post_message,
        fromDbBool(show_in_menu),
        fromDbBool(enabled),
        fromDbBool(system),

        )
}