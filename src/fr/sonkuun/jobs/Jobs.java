package fr.sonkuun.jobs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.sonkuun.jobs.plugin.JobsPlugin;

public class Jobs {

	private JobsPlugin plugin;
	
	private Player player;
	
	/* Add the differents jobs variable here */
	public Job miner;
	public Job woodcutter;
	public Job fisher;
	public Job farmer;
	public Job monsterSlayer;
	public Job breeder;
	public Job digger;
	
	public Jobs(Player player, JobsPlugin plugin) {
		this.plugin = plugin;
		
		this.player = player;
		
		/* Add the differents jobs init here*/
		miner = new Job("Miner", 1, 0, player, plugin);
		woodcutter = new Job("Woodcutter", 1, 0, player, plugin);
		fisher = new Job("Fisher", 1, 0, player, plugin);
		farmer = new Job("Farmer", 1, 0, player, plugin);
		monsterSlayer = new Job("MonsterSlayer", 1, 0, player, plugin);
		breeder = new Job("Breeder", 1, 0, player, plugin);
		digger = new Job("Digger", 1, 0, player, plugin);
	}
	
	public Jobs(Player player) {
		this.player = player;
		
		/* Add the differents jobs init here*/
		miner = new Job("Miner", 1, 0, player, plugin);
		woodcutter = new Job("Woodcutter", 1, 0, player, plugin);
		fisher = new Job("Fisher", 1, 0, player, plugin);
		farmer = new Job("Farmer", 1, 0, player, plugin);
		monsterSlayer = new Job("MonsterSlayer", 1, 0, player, plugin);
		breeder = new Job("Breeder", 1, 0, player, plugin);
		digger = new Job("Digger", 1, 0, player, plugin);
	}
	
	public static Jobs fromPlayer(Player player, JobsPlugin plugin) {
		
		Jobs jobs = new Jobs(player);
		
		jobs.miner = Job.fromPlayer(player, "Miner", plugin);
		jobs.woodcutter = Job.fromPlayer(player, "Woodcutter", plugin);
		jobs.fisher = Job.fromPlayer(player, "Fisher", plugin);
		jobs.farmer = Job.fromPlayer(player, "Farmer", plugin);
		jobs.monsterSlayer = Job.fromPlayer(player, "MonsterSlayer", plugin);
		jobs.breeder = Job.fromPlayer(player, "Breeder", plugin);
		jobs.digger = Job.fromPlayer(player, "Digger", plugin);
		
		return jobs;
	}
	
	public void save() {
		miner.save();
		woodcutter.save();
		fisher.save();
		farmer.save();
		monsterSlayer.save();
		breeder.save();
		digger.save();
	}
	
	public static Inventory createJobsInventory(Player player, JobsPlugin plugin) {
		Inventory inventory = Bukkit.createInventory(null, 27, "Jobs");
		
		Jobs jobs = fromPlayer(player, plugin);

		/* Create custom item to show job progress */
		ItemStack mining_item = createItem(Material.DIAMOND_PICKAXE, jobs.miner);
		ItemStack woodcutting_item = createItem(Material.DIAMOND_AXE, jobs.woodcutter);
		ItemStack fishing_item = createItem(Material.FISHING_ROD, jobs.fisher);
		ItemStack farming_item = createItem(Material.DIAMOND_HOE, jobs.farmer);
		ItemStack monster_slayer_item = createItem(Material.DIAMOND_SWORD, jobs.monsterSlayer);
		ItemStack breeder_item = createItem(Material.LEAD, jobs.breeder);
		ItemStack digger_item = createItem(Material.DIAMOND_SHOVEL, jobs.digger);
		
		inventory.setItem(10, mining_item);
		inventory.setItem(11, woodcutting_item);
		inventory.setItem(12, digger_item);
		inventory.setItem(13, farming_item);
		inventory.setItem(14, fishing_item);
		inventory.setItem(15, breeder_item);
		inventory.setItem(16, monster_slayer_item);
		
		ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemMeta glassMeta = glass.getItemMeta();
		glassMeta.setDisplayName("");
		glass.setItemMeta(glassMeta);
		for(int index = 0; index < inventory.getSize(); index++) {
			/* First line of the inventory */
			if(index < 9) {
				inventory.setItem(index, glass);
			}
			/* Last line of the inventory */
			else if(index >= inventory.getSize() - 9) {
				inventory.setItem(index, glass);
			}
			/* Left and Right border of the inventory */
			else if(index % 9 == 0 || index % 9 == 8) {
				inventory.setItem(index, glass);
			}
		}
		
		inventory.setItem(26, createGoBackItem());
		
		return inventory;
	}
	
	public static ItemStack createItem(Material mat, Job job) {
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<String>();
		ChatColor white = ChatColor.WHITE;

		meta.setDisplayName(job.name + " progress");
		lore.add("");
		lore.add("Level : " + white + job.level);
		lore.add("Xp : " + white + job.xp);
		lore.add("Next level in : " + white + (job.expNeededToUp() - job.xp) + " xp");
		lore.add("");
		
		/* Job effect for this lvl */
		lore.addAll(jobEffectToString(job));
		lore.add("");
		
		meta.addEnchant(Enchantment.LUCK, 1, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static List<String> jobEffectToString(Job job) {
		
		int multiplication_tier = job.level / 100 + 2;
		int percent_chance = (job.level - 1) % 100 + 1;
		String effect = ChatColor.GOLD + "" + percent_chance + "% " + ChatColor.WHITE + "chance to get " 
					+ ChatColor.GOLD + "x" + multiplication_tier + " " + ChatColor.WHITE;
		List<String> result = new ArrayList<String>();
		
		switch (job.name) {
		case "Miner":
			result.add(effect += "ores");
			return result;
		
		case "Woodcutter":
			result.add(effect += "logs");
			return result;
			
		case "Digger":
			result.add(effect += "blocks");
			return result;
			
		case "Fisher":
			result.add(effect += "fish");
			return result;	
			
		case "Farmer":
			result.add(effect += "crops");
			return result;
			
		case "MonsterSlayer":
			result.add(effect += "drops");
			return result;
			
		case "Breeder":
			result.add(effect += "drops and");
			result.add(ChatColor.GOLD + "" + percent_chance/5.0 + "% " 
					+ ChatColor.WHITE + "chance to conserve the breeding item");
			return result;
			
		default:
			return result;
		}
	}
	
	public static ItemStack createGoBackItem() {
		ItemStack go_back_item = new ItemStack(Material.ARROW);
		ItemMeta meta = go_back_item.getItemMeta();
		
		meta.setDisplayName("Go back");
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		
		go_back_item.setItemMeta(meta);
		
		return go_back_item;
	}
	
}
