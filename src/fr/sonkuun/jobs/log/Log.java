package fr.sonkuun.jobs.log;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Log {
	
	public static void i(Player player, String message) {
		player.sendMessage(ChatColor.GREEN + "# " + ChatColor.WHITE + message);
	}
	
	public static void e(Player player, String message) {
		player.sendMessage(ChatColor.RED + "# " + ChatColor.WHITE + message);
	}
	
	public static void w(Player player, String message) {
		player.sendMessage(ChatColor.YELLOW + "# " + ChatColor.WHITE + message);
	}
	
	public static void d(Player player, String message) {
		player.sendMessage(ChatColor.BLUE + "# " + ChatColor.WHITE + message);
	}
	
}