package fr.sonkuun.jobs.event;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import fr.sonkuun.jobs.Job;
import fr.sonkuun.jobs.log.Log;
import fr.sonkuun.jobs.plugin.JobsPlugin;
import fr.sonkuun.shop.plugin.ShopPlugin;

public class FarmerEvent {
	String TAG = "[" + this.getClass().getSimpleName() + "] ";

	private JobsPlugin plugin;

	public FarmerEvent(JobsPlugin plugin) {
		this.plugin = plugin;
	}

	public void cropsBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Job job = Job.fromPlayer(player, "Farmer", plugin);
		ItemStack main_hand = event.getPlayer().getInventory().getItemInMainHand();
		Block block = event.getBlock();
		Material block_type = block.getType();
		Location loc = block.getLocation();

		if(isFullyGrown(block)) {
			if(!lootSeed(block_type)) {
				event.setDropItems(false);
			}
			ItemStack drop = new ItemStack(block_type, amountDropItem(block_type, main_hand, job));
			player.getWorld().dropItem(loc, drop);
			
			int xp = plugin.getConfig().getInt("Config.Jobs.Farmer." + block_type.toString() + ".xp");
			job.addExp(xp);
			job.save();

			if(plugin.getConfig().getBoolean("Config.Plugin.Shop.enabled")) {
				double base_coins = plugin.getConfig().getDouble("Config.Jobs.Farmer." + block_type.toString() + ".coins");
				try {
					ShopPlugin.addCoins(player, base_coins * (job.level + 100) / 100);
				} catch (Exception e) {
					System.err.println(TAG + "Can't access to ShopPlugin class : " + e.toString());
					Log.e(player, "Can't add coins to your purse.");
				}
			}
		}
	}

	public boolean lootSeed(Material type) {
		switch (type) {
		case WHEAT:
		case BEETROOT:
			return true;

		default:
			return false;
		}
	}

	public boolean isFullyGrown(Block block) {      
		if(block.getBlockData() instanceof Ageable) {
			Ageable crop = (Ageable) block.getBlockData();
			return (crop.getMaximumAge() == crop.getAge());
		}
		return false;
	}

	public int amountDropItem(Material drop_mat, ItemStack main_hand, Job job) {
		Random random = new Random();
		int multiplication_tier = job.level / 100 + 2;
		int chance_to_multiply_drop = (job.level - 1) % 100 + 1; /* If lvl 100 or 200 ... (100 - 1) % 100 = 99*/
		int amount = 1;

		int fortune_level = 0;
		if(isHoe(main_hand) && main_hand.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
			fortune_level = main_hand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
		}

		switch (drop_mat) {
		case POTATOES:
		case CARROT:
		case COCOA:
		case NETHER_WART:
			amount = multiplyByFortuneLevel(3, fortune_level);
			break;

		case MELON:
			amount = multiplyByFortuneLevel(5, fortune_level);
			break;

		default:
			break;
		}

		if(random.nextInt(100) < chance_to_multiply_drop) {
			amount *= multiplication_tier;
		}
		else {
			amount *= multiplication_tier - 1;
		}

		return amount;
	}

	public int multiplyByFortuneLevel(int baseAmount, int fortune_level) {
		Random random = new Random();

		if(fortune_level == 0) {
			return baseAmount;
		}
		else if(fortune_level == 1) {
			if(random.nextInt(1000) < 670)
				return baseAmount;
			else
				return baseAmount * 2;
		}
		else if(fortune_level == 2) {
			int rand = random.nextInt(1000);
			if(rand < 500)
				return baseAmount;
			else if(rand < 750)
				return baseAmount * 2;
			else
				return baseAmount * 3;
		}
		else { /* Fortune 3 */
			int rand = random.nextInt(1000);
			if(rand < 400)
				return baseAmount;
			else if(rand < 600)
				return baseAmount * 2;
			else if(rand < 800)
				return baseAmount * 3;
			else
				return baseAmount * 4;
		}
	}

	public boolean isHoe(ItemStack item) {
		switch (item.getType()) {
		case WOODEN_HOE:
		case STONE_HOE:
		case GOLDEN_HOE:
		case IRON_HOE:
		case DIAMOND_HOE:
		case NETHERITE_HOE:
			return true;

		default:
			return false;
		}
	}
}
