package cn.ZeroEngine.Engine.api.v2.feature.chat;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import cn.ZeroEngine.EngineLibraries.ZeroServer.Engine.v2.feature.chat.ZeroChatManager;

public interface ChatHandler {

    int priority();

    void handle(ChatContext ctx);

    interface ChatContext {
        Player player();
        String rawMessage();
        Component formattedMessage();
        void formattedMessage(Component component);
        boolean consumed();
        void consume();
        boolean cancelled();
        void cancel();
        ZeroChatManager.ChatChannel channel();
        void channel(ZeroChatManager.ChatChannel channel);
    }
}
