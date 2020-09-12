package fr.sonkuun.jobs.event;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import fr.sonkuun.jobs.Job;
import fr.sonkuun.jobs.log.Log;
import fr.sonkuun.jobs.plugin.JobsPlugin;
import fr.sonkuun.shop.plugin.ShopPlugin;

public class WoodcuttingEvent {
	String TAG = "[" + this.getClass().getSimpleName() + "] ";

	private JobsPlugin plugin;

	public WoodcuttingEvent(JobsPlugin plugin) {
		this.plugin = plugin;
	}

	public void logBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Job job = Job.fromPlayer(player, "Woodcutter", plugin);
		ItemStack main_hand = event.getPlayer().getInventory().getItemInMainHand();
		Block block = event.getBlock();
		Material block_type = block.getType();
		Location loc = block.getLocation();

		if(isAxe(main_hand)) {
			event.setDropItems(false);
			ItemStack drop = new ItemStack(block_type, amountDropItem(block_type, main_hand, job));
			player.getWorld().dropItem(loc, drop);

			int xp = plugin.getConfig().getInt("Config.Jobs.Breeder." + block_type.toString() + ".Kill.xp");
			job.addExp(xp);
			job.save();
			
			if(plugin.getConfig().getBoolean("Config.Plugin.Shop.enabled")) {
				double base_coins = plugin.getConfig().getDouble("Config.Jobs.Breeder." + block_type.toString() + ".Kill.coins");
				try {
					ShopPlugin.addCoins(player, base_coins * (job.level + 100) / 100);
				} catch (Exception e) {
					System.err.println(TAG + "Can't access to ShopPlugin class : " + e.toString());
					Log.e(player, "Can't add coins to your purse.");
				}
			}
		}
	}

	public boolean isAxe(ItemStack item) {
		switch(item.getType()) {
		case WOODEN_AXE:
		case STONE_AXE:
		case IRON_AXE:
		case GOLDEN_AXE:
		case DIAMOND_AXE:
		case NETHERITE_AXE:
			return true;
		default:
			return false;
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
