package org.dlrg.lette.telegrambot;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.model.Update;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dlrg.lette.telegrambot.menu.AdminMenu;
import org.dlrg.lette.telegrambot.menu.SenderMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class WebhookController {
    private static final Logger log = LogManager.getLogger(WebhookController.class);

    private WebhookConfig webhookConfig;
    private AuthConfig authConfig;
    private SenderMenu senderMenu;
    private AdminMenu adminMenu;

    @Autowired
    public WebhookController(WebhookConfig webhookConfig, AuthConfig authConfig, SenderMenu senderMenu, AdminMenu adminMenu) {
        this.webhookConfig = webhookConfig;
        this.authConfig = authConfig;
        this.senderMenu = senderMenu;
        this.adminMenu = adminMenu;
    }

    // Update
    @RequestMapping(method = RequestMethod.POST, path = "/update/{uuid}")
    public ResponseEntity receiveUpdate(@PathVariable("uuid") String uuid, @RequestBody String updateString) {

        // Parse Update String to Object
        Update update = BotUtils.parseUpdate(updateString);

        log.debug("Update received, UUID: " + uuid);
        if (uuid.equalsIgnoreCase(webhookConfig.getAdminUUID())) {
            // Admin Bot
            log.debug("Admin Update Received, processing in asynchronous task...");
            new Thread(() -> adminMenu.processUpdate(update, authConfig.getAdminBotToken(), authConfig.getSenderBotToken())).start();
        } else if (uuid.equalsIgnoreCase(webhookConfig.getSenderUUID())) {
            // Sender Bot
            log.debug("Sender Update Received, processing in asynchronous task...");
            new Thread(() -> senderMenu.processUpdate(update, authConfig.getSenderBotToken())).start();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
