package fr.sonkuun.jobs.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import fr.sonkuun.jobs.Job;
import fr.sonkuun.jobs.log.Log;
import fr.sonkuun.jobs.plugin.JobsPlugin;
import fr.sonkuun.shop.plugin.ShopPlugin;

public class BreederEvent {
	String TAG = "[" + this.getClass().getSimpleName() + "] ";

	private JobsPlugin plugin;

	public BreederEvent(JobsPlugin plugin) {
		this.plugin = plugin;
	}

	public void animalBreedByPlayer(EntityBreedEvent event) {
		EntityType type = event.getEntityType();
		Player player = (Player) event.getBreeder();
		Job job = Job.fromPlayer(player, "Breeder", plugin);

		if(conserveItem(job)) {
			ItemStack item = event.getBredWith();
			item.setAmount(2);
			player.getWorld().dropItem(player.getLocation().add(0, 0.5, 0), item);
		}

		int xp = plugin.getConfig().getInt("Config.Jobs.Breeder." + type.toString() + ".Breed.xp");
		job.addExp(xp);
		job.save();

		if(plugin.getConfig().getBoolean("Config.Plugin.Shop.enabled")) {
			double base_coins = plugin.getConfig().getDouble("Config.Jobs.Breeder." + type.toString() + ".Kill.coins");
			try {
				ShopPlugin.addCoins(player, base_coins * (job.level + 100) / 100);
			} catch (Exception e) {
				System.err.println(TAG + "Can't access to ShopPlugin class : " + e.toString());
				Log.e(player, "Can't add coins to your purse.");
			}
		}
	}

	public boolean conserveItem(Job job) {
		Random random = new Random();

		if(random.nextInt(1000) < job.level * 0.2 * 10) {
			return true;
		}

		return false;
	}

	public void animalKillByPlayer(EntityDeathEvent event) {
		LivingEntity animal = (LivingEntity) event.getEntity();
		EntityType type = animal.getType();
		Player player = animal.getKiller();
		Job job = Job.fromPlayer(player, "Breeder", plugin);
		ItemStack main_hand = player.getInventory().getItemInMainHand();

		List<ItemStack> drops = new ArrayList<ItemStack>();
		for(ItemStack drop : event.getDrops()) {
			drops.add(drop);
		}
		event.getDrops().clear();

		for(ItemStack drop : drops) {
			event.getDrops().add(new ItemStack(drop.getType(), amountDropItem(drop, main_hand, job)));
		}

		int xp = plugin.getConfig().getInt("Config.Jobs.Breeder." + type.toString() + ".Kill.xp");
		job.addExp(xp);
		job.save();
		
		if(plugin.getConfig().getBoolean("Config.Plugin.Shop.enabled")) {
			double base_coins = plugin.getConfig().getDouble("Config.Jobs.Breeder." + type.toString() + ".Kill.coins");
			try {
				ShopPlugin.addCoins(player, base_coins * (job.level + 100) / 100);
			} catch (Exception e) {
				System.err.println(TAG + "Can't access to ShopPlugin class : " + e.toString());
				Log.e(player, "Can't add coins to your purse.");
			}
		}
		
	}

	public int amountDropItem(ItemStack item, ItemStack main_hand, Job job) {

		if(item.getMaxStackSize() == 1) {
			return 1;
		}

		Random random = new Random();
		int multiplication_tier = job.level / 100 + 2;
		int chance_to_multiply_drop = (job.level - 1) % 100 + 1; /* If lvl 100 or 200 ... (100 - 1) % 100 = 99*/
		int amount = item.getAmount();

		if(random.nextInt(100) < chance_to_multiply_drop) {
			amount *= multiplication_tier;
		}
		else {
			amount *= multiplication_tier - 1;
		}

		return amount;
	}
}
