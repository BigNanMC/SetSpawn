package me.bignan.setspawn;

import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Main extends org.bukkit.plugin.java.JavaPlugin
{
  FileConfiguration config = getConfig();
  
  public Main() {}
  
  public void onEnable() { Bukkit.getServer().getConsoleSender().sendMessage("SetSpawn > ENABLED ");
    saveDefaultConfig();
  }
  
  public void onDisable()
  {
    Bukkit.getServer().getConsoleSender().sendMessage("SetSpawn > DISABLED ");
  }
  
  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
  {
    Player p = (Player)sender;
    String prefix = getConfig().getString(ChatColor.translateAlternateColorCodes('&', "prefix"));
    if (cmd.getName().equalsIgnoreCase("spawn"))
    {
      if (getConfig().getConfigurationSection("spawn") == null)
      {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + getConfig().getString("NoSpawnMessage")));
        return true;
      }
      World w = Bukkit.getServer().getWorld(getConfig().getString("spawn.world"));
      double x = getConfig().getDouble("spawn.x");
      double y = getConfig().getDouble("spawn.y");
      double z = getConfig().getDouble("spawn.z");
      p.teleport(new Location(w, x, y, z));
      p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + getConfig().getString("SpawnMessage")));
      
      return true;
    }
    if (!sender.hasPermission("spawn.set"))
    {
      sender.sendMessage(ChatColor.RED + "You dont have permission to set the servers spawn");
      return true;
    }
    if (cmd.getName().equalsIgnoreCase("setspawn"))
    {
      getConfig().set("spawn.world", p.getLocation().getWorld().getName());
      getConfig().set("spawn.x", Double.valueOf(p.getLocation().getX()));
      getConfig().set("spawn.y", Double.valueOf(p.getLocation().getY()));
      getConfig().set("spawn.z", Double.valueOf(p.getLocation().getZ()));
      getConfig().set("MonsterSpawnLimit", Integer.valueOf(Bukkit.getServer().getMonsterSpawnLimit()));
      getConfig().set("AnimalSpawnLimit", Integer.valueOf(Bukkit.getServer().getAnimalSpawnLimit()));
      getConfig().set("DefaultGameMode", getServer().getDefaultGameMode());
      saveConfig();
      p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + getConfig().getString("SetSpawnMessage")));
      p.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
      p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Locations:"));
      p.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
      p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cX &7= &a") + p.getLocation().getX());
      p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cY &7= &a") + p.getLocation().getY());
      p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cZ &7= &a") + p.getLocation().getZ());
      p.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
      return true; }
    ConfigurationSection cs;
    if (cmd.getName().equalsIgnoreCase("setwarp")) {
      if (!(sender instanceof Player)) {
        sender.sendMessage(getConfig().getString("NoWarpConsole"));
        return true;
      }
      if (!p.hasPermission("warp.set")) {
        p.sendMessage(getConfig().getString("NoPermission"));
        return true;
      }
      if (args.length == 0) {
        p.sendMessage(getConfig().getString("SetWarpUsage"));
        return true;
      }
      Location loc = p.getLocation();
      getConfig().createSection(args[0].toLowerCase());
      cs = getConfig().getConfigurationSection(args[0].toLowerCase());
      cs.set("X", Double.valueOf(loc.getX()));
      cs.set("Y", Double.valueOf(loc.getY()));
      cs.set("Z", Double.valueOf(loc.getZ()));
      cs.set("world", loc.getWorld().getName());
      saveConfig();
      p.sendMessage(getConfig().getString("CreateWarp")
        .replace("%warp%", args[0])
        .replace('&', '§'));
    }
    if (cmd.getName().equalsIgnoreCase("warp")) {
      if (!(sender instanceof Player)) {
        sender.sendMessage(getConfig().getString("NoWarpConsole"));
        return true;
      }
      if (!p.hasPermission("warp.use")) {
        p.sendMessage(getConfig().getString("ErrorWarp"));
        return true;
      }
      if (args.length == 0) {
        p.sendMessage(getConfig().getString("WarpUsage")
          .replace('&', '§'));
        for (String key : getConfig().getKeys(false)) {
          p.sendMessage(key);
        }
        return true;
      }
      if (getConfig().getConfigurationSection(args[0].toLowerCase()) == null) {
        p.sendMessage(getConfig().getString("NoWarpMessage")
          .replace("%warp%", args[0])
          .replace('&', '§'));
        return true;
      }
      ConfigurationSection cs1 = getConfig().getConfigurationSection(args[0].toLowerCase());
      Location loc = new Location(Bukkit.getWorld(cs1.getString("world")), cs1.getDouble("X"), cs1.getDouble("Y"), cs1.getDouble("Z"));
      p.teleport(loc);
      p.sendMessage(getConfig().getString("WarpMessage")
        .replace("%warp%", args[0])
        .replace('&', '§'));
    }
    if (cmd.getName().equalsIgnoreCase("delwarp")) {
      if ((sender.hasPermission("delwarp.use")) && 
        (!getConfig().contains(args[0]))) {
        p.sendMessage("§cInvalid warp");
        return true;
      }
      getConfig().set(args[0], null);
      saveConfig();
      return true;
    }
    if (!sender.hasPermission("setspawn.reload")) {
      sender.sendMessage(ChatColor.RED + "You dont have permission to reload SetSpawn by BigNan!");
    }
    if (cmd.getName().equalsIgnoreCase("spawnreload"))
    {
      reloadConfig();
      p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + getConfig().getString("ReloadMessage")));
    }
    return true;
  }
}
