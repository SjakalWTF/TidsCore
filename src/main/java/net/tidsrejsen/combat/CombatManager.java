package net.tidsrejsen.combat;

import lombok.Getter;
import net.tidsrejsen.Main;
import net.tidsrejsen.util.ActionBarUtil;
import net.tidsrejsen.util.TitleUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CombatManager implements Listener {
    private final Map<UUID, Integer> combat = new ConcurrentHashMap<>();
    private final Set<UUID> admin = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Map<UUID, BukkitRunnable> combatTasks = new ConcurrentHashMap<>();
    @Getter
    private volatile boolean pvpEnabled = true;

    public CombatManager() {
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
    }

    public void tag(Player a, Player v) {
        if (!pvpEnabled) {
            a.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&4&lPvP &8&l» &7PvP er ikke aktiveret."));
            return;
        }
        if (admin.contains(a.getUniqueId()) || admin.contains(v.getUniqueId())) return;

        boolean aWasTagged = combat.containsKey(a.getUniqueId());
        boolean vWasTagged = combat.containsKey(v.getUniqueId());

        startCombat(a, 30, !aWasTagged);
        startCombat(v, 30, !vWasTagged);
    }

    private void startCombat(Player player, int seconds, boolean isNew) {
        UUID uuid = player.getUniqueId();

        // Cancel existing task if any
        if (combatTasks.containsKey(uuid)) {
            combatTasks.get(uuid).cancel();
        }

        combat.put(uuid, seconds);

        if (isNew) {
            sendCombatStart(player);
        }

        // Start new countdown task
        BukkitRunnable task = new BukkitRunnable() {
            int timeLeft = seconds;

            @Override
            public void run() {
                if (timeLeft <= 0 || !combat.containsKey(uuid)) {
                    endCombat(uuid);
                    return;
                }

                updateActionBar(uuid, timeLeft);
                combat.put(uuid, timeLeft);
                timeLeft--;
            }
        };

        task.runTaskTimer(Main.getInstance(), 0, 20);
        combatTasks.put(uuid, task);
    }

    private void endCombat(UUID uuid) {
        combat.remove(uuid);
        if (combatTasks.containsKey(uuid)) {
            combatTasks.get(uuid).cancel();
            combatTasks.remove(uuid);
        }

        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            notifyCombatEnd(uuid);
        }
    }

    public int getCombatTime(UUID u) {
        return combat.getOrDefault(u, 0);
    }

    public boolean toggleAdmin(UUID u) {
        if (admin.contains(u)) {
            admin.remove(u);
            return false;
        }
        admin.add(u);
        return true;
    }

    public boolean isAdmin(UUID u) {
        return admin.contains(u);
    }

    public void clearCombatData(UUID u) {
        endCombat(u);
    }

    public void clearAllCombatData() {
        new HashSet<>(combat.keySet()).forEach(this::endCombat);
    }

    public Set<UUID> getAdminList() {
        return Collections.unmodifiableSet(admin);
    }

    public void setAdminList(Set<UUID> list) {
        admin.clear();
        admin.addAll(list);
    }

    private void sendCombatStart(Player p) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&c&lCOMBAT &8» &7Du er nu i kamp. Log ikke ud!"));
    }

    private void updateActionBar(UUID u, int left) {
        Player p = Bukkit.getPlayer(u);
        if (p != null && p.isOnline()) {
            String message = ChatColor.translateAlternateColorCodes('&',
                    "&4&lCOMBAT &8» &7Du er i kamp i &c" + left + "s");
            ActionBarUtil.sendActionBar(p, message);
        }
    }

    private void notifyCombatEnd(UUID u) {
        Player p = Bukkit.getPlayer(u);
        if (p != null && p.isOnline()) {
            String message = ChatColor.translateAlternateColorCodes('&',
                    "&a&lCOMBAT &8» &7Du er ikke længere i kamp.");
            ActionBarUtil.sendActionBar(p, message);
            p.sendMessage(message);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player deceased = event.getEntity();
        UUID uuid = deceased.getUniqueId();

        if (combat.containsKey(uuid)) {
            clearCombatData(uuid);

            Player killer = deceased.getKiller();

            String deathMessage;
            if (killer != null) {
                deathMessage = ChatColor.translateAlternateColorCodes('&',
                        "&c&lCOMBAT &8» &7Du blev dræbt af &c" + killer.getName() + "&7.");
            } else {
                String cause = "ukendt årsag";
                if (deceased.getLastDamageCause() != null) {
                    cause = deceased.getLastDamageCause().getCause().name().toLowerCase().replace('_', ' ');
                }
                deathMessage = ChatColor.translateAlternateColorCodes('&',
                        "&c&lCOMBAT &8» &7Du døde af &c" + cause + "&7.");
            }

            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                if (deceased.isOnline()) {
                    ActionBarUtil.sendActionBar(deceased, deathMessage);
                    deceased.sendMessage(deathMessage);
                }

                if (killer != null && killer.isOnline()) {
                    String killerMsg = ChatColor.translateAlternateColorCodes('&',
                            "&c&lCOMBAT &8» &7Du har dræbt &c" + deceased.getName() + "&7.");
                    TitleUtil.sendTitle(killer, "", killerMsg, 10, 70, 20);
                    killer.sendMessage(killerMsg);

                    // Giv belønninger
                    ItemStack steak = new ItemStack(org.bukkit.Material.COOKED_BEEF, 2);
                    ItemMeta meta = steak.getItemMeta();
                    if (meta != null) {
                        meta.setDisplayName(ChatColor.RED + deceased.getName() + "'s kød");
                        steak.setItemMeta(meta);
                    }
                    killer.getInventory().addItem(steak);

                    double newHealth = Math.min(killer.getHealth() + 6.0, killer.getMaxHealth());
                    killer.setHealth(newHealth);

                    killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0));
                }
            });
        }
    }

    public void setPvP(boolean b) {
    }
}