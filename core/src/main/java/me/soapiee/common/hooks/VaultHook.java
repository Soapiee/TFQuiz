package me.soapiee.common.hooks;

import me.soapiee.common.TFQuiz;
import me.soapiee.common.enums.Message;
import me.soapiee.common.managers.MessageManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {

    private static Economy economy = null;
    private static Permission permissions = null;
    private static MessageManager messageManager;

    public VaultHook(TFQuiz main) {
        messageManager = main.getMessageManager();

        this.setupEconomy();
        this.setupPermissions();
    }

    private void setupEconomy() {
        final RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);

        if (rsp != null)
            economy = rsp.getProvider();
    }

    private void setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = Bukkit.getServicesManager().getRegistration(Permission.class);

        if (rsp != null)
            permissions = rsp.getProvider();
    }

    public boolean hasEconomyPlugin() {
        return economy != null;
    }

    public boolean hasPermissionPlugin() {
        return permissions != null;
    }

    public String deposit(OfflinePlayer target, double amount) {
        if (!hasEconomyPlugin()) return messageManager.get(Message.MISSINGVAULTHOOK);

        return economy.depositPlayer(target, amount).errorMessage;
    }

    public String getCurrencyName() {
        if (!hasEconomyPlugin()) return messageManager.get(Message.MISSINGVAULTHOOK);


        return economy.currencyNamePlural();
    }

    public Boolean setPermission(OfflinePlayer target, String permission) {
        if (!hasPermissionPlugin()) return false;

        return permissions.playerAdd(null, target, permission);
    }
}
