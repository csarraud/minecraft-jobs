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


public class MiningEvent {
	String TAG = "[" + this.getClass().getSimpleName() + "] ";

	private JobsPlugin plugin;

	public MiningEvent(JobsPlugin plugin) {
		this.plugin = plugin;
	}

	public void oreBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Job job = Job.fromPlayer(player, "Miner", plugin);
		ItemStack main_hand = event.getPlayer().getInventory().getItemInMainHand();
		Block block = event.getBlock();
		Material block_type = block.getType();
		Location loc = block.getLocation();

		if(isPickaxe(main_hand)) {

			if(block_type != Material.IRON_ORE && block_type != Material.GOLD_ORE && block_type != Material.GILDED_BLACKSTONE
					&& block_type != Material.STONE && block_type != Material.BLACKSTONE
					&& block_type != Material.BASALT &&  !main_hand.containsEnchantment(Enchantment.SILK_TOUCH)) {
				event.setDropItems(false);
				Material drop_mat = oreFromBlock(block_type);
				ItemStack drop = new ItemStack(drop_mat, amountDropItem(drop_mat, main_hand, job));
				if(drop.getAmount() != 0) {
					player.getWorld().dropItem(loc, drop);
				}
			}

			int xp = plugin.getConfig().getInt("Config.Jobs.Miner." + block_type.toString() + ".xp", 1);
			job.addExp(xp);
			job.save();

			if(plugin.getConfig().getBoolean("Config.Plugin.Shop.enabled")) {
				double base_coins = plugin.getConfig().getDouble("Config.Jobs.Miner." + block_type.toString() + ".coins", 0.1);
				try {
					ShopPlugin.addCoins(player, base_coins * (job.level + 100) / 100);
				} catch (Exception e) {
					System.err.println(TAG + "Can't access to ShopPlugin class : " + e.toString());
					Log.e(player, "Can't add coins to your purse.");
				}
			}
		}

	}

	public boolean isPickaxe(ItemStack item) {
		switch (item.getType()) {
		case WOODEN_PICKAXE:
		case STONE_PICKAXE:
		case IRON_PICKAXE:
		case GOLDEN_PICKAXE:
		case DIAMOND_PICKAXE:
		case NETHERITE_PICKAXE:
			return true;

		default:
			return false;
		}
	}

	public int amountDropItem(Material drop_mat, ItemStack main_hand, Job job) {
		int multiplication_tier = job.level / 100 + 2;
		int chance_to_multiply_drop = (job.level - 1) % 100 + 1; /* If lvl 100 or 200 ... (100 - 1) % 100 = 99*/
		int fortune_level = main_hand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
		int amount = 0;

		Material item_type = main_hand.getType();

		switch (drop_mat) {
		case ANCIENT_DEBRIS:
		case OBSIDIAN:
			if(!(item_type.equals(Material.DIAMOND_PICKAXE) || item_type.equals(Material.NETHERITE_PICKAXE))) {
				return 0;
			}
			amount = 1;
			break;
		case CRYING_OBSIDIAN:
			if(!(item_type.equals(Material.GOLDEN_PICKAXE) || item_type.equals(Material.DIAMOND_PICKAXE) 
					|| item_type.equals(Material.NETHERITE_PICKAXE))) {
				return 0;
			}
			amount = 1;			
			break;
		case GOLD_NUGGET:
			amount = multiplyByFortuneLevel(4, fortune_level);
			break;
		case COAL:
		case EMERALD:
		case DIAMOND:
			if(item_type.equals(Material.WOODEN_PICKAXE) || item_type.equals(Material.STONE_PICKAXE)) {
				return 0;
			}
			amount = multiplyByFortuneLevel(1, fortune_level);
			break;
		case QUARTZ:
			amount = multiplyByFortuneLevel(1, fortune_level);
			break;
		case LAPIS_LAZULI:
			if(item_type.equals(Material.WOODEN_PICKAXE)) {
				return 0;
			}
			amount = multiplyByFortuneLevel(6, fortune_level);
			break;
		case REDSTONE:
			if(item_type.equals(Material.WOODEN_PICKAXE) || item_type.equals(Material.STONE_PICKAXE)) {
				return 0;
			}
			amount = multiplyByFortuneLevel(4, fortune_level);
			break;

		default:
			break;
		}

		if(new Random(1998).nextInt(100) < chance_to_multiply_drop) {
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

	public Material oreFromBlock(Material block_type) {
		switch(block_type) {
		case COAL_ORE:
			return Material.COAL;
		case LAPIS_ORE:
			return Material.LAPIS_LAZULI;
		case EMERALD_ORE:
			return Material.EMERALD;
		case DIAMOND_ORE:
			return Material.DIAMOND;
		case REDSTONE_ORE:
			return Material.REDSTONE;
		case NETHER_QUARTZ_ORE:
			return Material.QUARTZ;
		case NETHER_GOLD_ORE:
			return Material.GOLD_NUGGET;

		default:
			return block_type;
		}
	}
}
