package fr.sonkuun.jobs.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.sonkuun.jobs.Job;
import fr.sonkuun.jobs.Jobs;
import fr.sonkuun.jobs.log.Log;


public class CustomCommandExecutor implements CommandExecutor {

	private JobsPlugin plugin;

	private String[] jobs_to_string = {"Miner", "Woodcutter", "Digger", "Farmer", "Fisher", "Breeder", "MonsterSlayer"};
	
	public CustomCommandExecutor(JobsPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String msg, String[] args) {

		if(sender instanceof Player) {
			Player player = (Player) sender;

			switch(command.getName()) {
			case "jobs":
				player.openInventory(Jobs.createJobsInventory(player, plugin));
				return true;
				
			case "reset":
				if(args.length == 1) {
					if(args[0].equalsIgnoreCase("all")) {
						new Jobs(player, plugin).save();
						Log.d(player, "All jobs properly reset");
					}
					else {
						for(String job : jobs_to_string) {
							if(args[0].equalsIgnoreCase(job)) {
								new Job(job, 1, 0, player, plugin).save();
								Log.d(player, job + " properly reset");
							}
						}
					}
					return true;
				}
				return false;
				
			case "give":
				if(args.length == 3) {
					String job_name = args[0];
					String level_or_xp = args[1];
					int value;
					
					try{
						value = Integer.parseInt(args[2]);
					}
					catch(NumberFormatException e) {
						return false;
					}
					Log.d(player, "Job : " + job_name);
					Job job = null;
					for(String job_str : jobs_to_string) {
						if(job_name.equalsIgnoreCase(job_str)) {
							job = Job.fromPlayer(player, job_str, plugin);
							break;
						}
					}
					
					if(level_or_xp.equalsIgnoreCase("level")) {
						job.addLevel(value);
						Log.d(player, job.name + " +" + value + " level");
					}
					else if(level_or_xp.equalsIgnoreCase("xp")) {
						job.addExp(value);
						Log.d(player, job + " +" + value + " xp");
					}
					else {
						return false;
					}
					
					job.save();
					
				}
				return true;
			}
		}

		return false;
	}

}
