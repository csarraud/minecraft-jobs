package fr.sonkuun.jobs.event;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import fr.sonkuun.jobs.Job;
import fr.sonkuun.jobs.log.Log;
import fr.sonkuun.jobs.plugin.JobsPlugin;
import fr.sonkuun.shop.plugin.ShopPlugin;

public class DiggerEvent {
	String TAG = "[" + this.getClass().getSimpleName() + "] ";

	private JobsPlugin plugin;

	public DiggerEvent(JobsPlugin plugin) {
		this.plugin = plugin;
	}

	public void blockDig(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Job job = Job.fromPlayer(player, "Digger", plugin);
		ItemStack main_hand = event.getPlayer().getInventory().getItemInMainHand();
		Block block = event.getBlock();
		Material block_type = block.getType();
		Location loc = block.getLocation();

		if(isShovel(main_hand)) {
			event.setDropItems(false);
			Material drop_type = lootFromBlock(block_type, main_hand);
			ItemStack drop = new ItemStack(drop_type, amountDropItem(block_type, main_hand, job));
			player.getWorld().dropItem(loc, drop);

			int xp = plugin.getConfig().getInt("Config.Jobs.Digger." + block_type.toString() + ".xp");
			job.addExp(xp);
			job.save();

			if(plugin.getConfig().getBoolean("Config.Plugin.Shop.enabled")) {
				double base_coins = plugin.getConfig().getDouble("Config.Jobs.Digger." + block_type.toString() + ".coins");
				try {
					ShopPlugin.addCoins(player, base_coins * (job.level + 100) / 100);
				} catch (Exception e) {
					System.err.println(TAG + "Can't access to ShopPlugin class : " + e.toString());
					Log.e(player, "Can't add coins to your purse.");
				}
			}
		}
	}

	public boolean isShovel(ItemStack item) {
		switch(item.getType()) {
		case WOODEN_SHOVEL:
		case STONE_SHOVEL:
		case IRON_SHOVEL:
		case GOLDEN_SHOVEL:
		case DIAMOND_SHOVEL:
		case NETHERITE_SHOVEL:
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

		if(drop_mat.equals(Material.CLAY_BALL)) {
			amount = 4;
		}

		if(random.nextInt(100) < chance_to_multiply_drop) {
			amount *= multiplication_tier;
		}
		else {
			amount *= multiplication_tier - 1;
		}

		return amount;
	}

	public Material lootFromBlock(Material block_type, ItemStack shovel) {

		if(shovel.containsEnchantment(Enchantment.SILK_TOUCH)) {
			return block_type;
		}

		Random random = new Random();
		int fortune_level = shovel.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);

		switch (block_type) {
		case GRAVEL:			
			if((fortune_level == 0 && random.nextInt(100) < 10)
					|| (fortune_level == 1 && random.nextInt(100) < 14)
					|| (fortune_level == 2 && random.nextInt(100) < 25)
					|| fortune_level == 3) {
				return Material.FLINT;
			}

			return Material.GRAVEL;

		case CLAY:
			return Material.CLAY_BALL;

		case MYCELIUM:
		case GRASS_BLOCK:
			return Material.DIRT;

		default:
			return block_type;
		}
	}
}
