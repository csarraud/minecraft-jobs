package fr.sonkuun.jobs.event;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import fr.sonkuun.jobs.Job;
import fr.sonkuun.jobs.log.Log;
import fr.sonkuun.jobs.plugin.JobsPlugin;
import fr.sonkuun.shop.plugin.ShopPlugin;

public class FishingEvent {
	String TAG = "[" + this.getClass().getSimpleName() + "] ";

	private JobsPlugin plugin;

	public FishingEvent(JobsPlugin plugin) {
		this.plugin = plugin;
	}

	public void fishCatch(PlayerFishEvent event) {
		Player player = event.getPlayer();
		Job job = Job.fromPlayer(player, "Fisher", plugin);
		ItemStack main_hand = event.getPlayer().getInventory().getItemInMainHand();

		if (event.getCaught() == null) {
			return;
		}

		Item item = (Item) event.getCaught();
		Material type = item.getItemStack().getType();

		if(item.getItemStack().getMaxStackSize() != 1) {
			item.setItemStack(new ItemStack(type, amountDropItem(type, main_hand, job)));
		}

		int xp = plugin.getConfig().getInt("Config.Jobs.Fisher." + type.toString() + ".xp", 1);
		job.addExp(xp);
		job.save();

		if(plugin.getConfig().getBoolean("Config.Plugin.Shop.enabled")) {
			double base_coins = plugin.getConfig().getDouble("Config.Jobs.Fisher." + type.toString() + ".coins", 1.0);
			try {
				ShopPlugin.addCoins(player, base_coins * (job.level + 100) / 100);
			} catch (Exception e) {
				System.err.println(TAG + "Can't access to ShopPlugin class : " + e.toString());
				Log.e(player, "Can't add coins to your purse.");
			}
		}
	}

	public int amountDropItem(Material drop_mat, ItemStack main_hand, Job job) {
		Random random = new Random();
		int multiplication_tier = job.level / 100 + 2;
		int chance_to_multiply_drop = (job.level - 1) % 100 + 1; /* If lvl 100 or 200 ... (100 - 1) % 100 = 99*/
		int amount = 1;

		if(random.nextInt(100) < chance_to_multiply_drop) {
			amount *= multiplication_tier;
		}
		else {
			amount *= multiplication_tier - 1;
		}

		return amount;
	}

}
