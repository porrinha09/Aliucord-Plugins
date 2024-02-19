import android.content.Context
import com.aliucord.annotations.AliucordPlugin
import com.aliucord.api.CommandsAPI
import com.aliucord.api.CommandsAPI.CommandResult
import com.aliucord.entities.Message
import com.aliucord.entities.Plugin
import com.aliucord.utils.DiscordUtils
import com.discord.models.message.MessageReference
import com.discord.stores.StoreStream
import rx.Subscription

@AliucordPlugin
class ClearMessages : Plugin() {

    override fun start(context: Context) {
        commands.registerCommand("clear", "Clear a specified number of your messages", { ctx, args ->
            val messagesToDelete = args[0].toIntOrNull() ?: return@registerCommand CommandResult.invalidNumberOfArguments()
            if (messagesToDelete <= 0) return@registerCommand CommandResult.invalidNumberOfArguments()

            val channel = DiscordUtils.getChannelById(StoreStream.getChannelsSelected().first()) ?: return@registerCommand CommandResult.failed()

            val messages = ArrayList<Message>()
            var messagesDeleted = 0

            for (message in StoreStream.getMessages(channel)) {
                if (message.authorId == ctx.user.id) {
                    messages.add(message)
                    if (messages.size >= messagesToDelete) break
                }
            }

            for (message in messages) {
                DiscordUtils.deleteMessage(channel.id, message.id)
                messagesDeleted++
            }

            CommandResult.success("Deleted $messagesDeleted messages.")
        }, arrayOf(CommandsAPI.CommandArgument("number", CommandsAPI.CommandArgumentType.NUMBER)))
    }

    override fun stop(context: Context) {
        commands.unregisterAll()
    }
}
