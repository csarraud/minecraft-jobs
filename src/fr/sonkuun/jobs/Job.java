package fr.sonkuun.jobs;

import org.bukkit.entity.Player;

import fr.sonkuun.jobs.log.Log;
import fr.sonkuun.jobs.plugin.JobsPlugin;

public class Job {
	
	private JobsPlugin plugin;
	
	public String name;
	public int level;
	public int xp;
	
	private Player player;
	
	public Job(String name, int level, int xp, Player player, JobsPlugin plugin) {
		this.name = name;
		this.level = level;
		this.xp = xp;
		
		this.player = player;
		
		this.plugin = plugin;
	}
	
	public static Job fromPlayer(Player player, String job_name, JobsPlugin plugin) {
		String uuid_to_string = player.getUniqueId().toString();
		
		int level = plugin.getConfig().getInt("Users." + uuid_to_string + ".Job." + job_name + ".Level", 1);
		int xp = plugin.getConfig().getInt("Users." + uuid_to_string + ".Job." + job_name + ".Xp", 0);
				
		return new Job(job_name, level, xp, player, plugin);
	}
	
	public void save() {
		String uuid_to_string = player.getUniqueId().toString();
		
		plugin.getConfig().set("Users." + uuid_to_string + ".Job." + name + ".Level", level);
		plugin.getConfig().set("Users." + uuid_to_string + ".Job." + name + ".Xp", xp);

		plugin.saveConfig();
	}
	
	public String toString() {
		return "Job : " + name + ", lvl : " + level + ", xp : " + xp; 
	}
	
	public void addLevel(int level) {
		this.level += level;
	}
	
	public void addExp(int exp) {
		xp += exp;
		
		Log.i(player, "+" + exp + " " + name.toLowerCase() + " xp.");

		while(isLevelUp()) {
			level += 1;	
			Log.i(player, "Your " + name.toLowerCase() + " job leveled up ! " + name + " level : " + level);
		}
	}

	public boolean isLevelUp() {
		if(xp >= expNeededToUp()) {
			return true;
		}
		else {
			return false;
		}
	}

	public int expNeededToUp() {
		int XP_LEVEL_ONE = plugin.getConfig().getInt("Config.Experience.xp_level_one");
		double first_coef = plugin.getConfig().getDouble("Config.Experience.first_coef");
		double second_coef = plugin.getConfig().getDouble("Config.Experience.second_coef");
		double substract_value = plugin.getConfig().getDouble("Config.Experience.substract_value");
		double third_coef = plugin.getConfig().getDouble("Config.Experience.third_coef");
		return (int) (XP_LEVEL_ONE * (Math.pow(level, first_coef)) - second_coef * XP_LEVEL_ONE * (Math.pow(level - substract_value, third_coef)));
	}
}
