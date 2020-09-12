package fr.sonkuun.jobs.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import fr.sonkuun.jobs.Job;
import fr.sonkuun.jobs.log.Log;
import fr.sonkuun.jobs.plugin.JobsPlugin;
import fr.sonkuun.shop.plugin.ShopPlugin;

public class MonsterSlayerEvent {
	String TAG = "[" + this.getClass().getSimpleName() + "] ";

	private JobsPlugin plugin;

	public MonsterSlayerEvent(JobsPlugin plugin) {
		this.plugin = plugin;
	}

	public void monsterKillByPlayer(EntityDeathEvent event) {
		LivingEntity monster = (LivingEntity) event.getEntity();
		Player player = monster.getKiller();
		Job job = Job.fromPlayer(player, "MonsterSlayer", plugin);
		ItemStack main_hand = player.getInventory().getItemInMainHand();

		List<ItemStack> drops = new ArrayList<ItemStack>();
		for(ItemStack drop : event.getDrops()) {
			drops.add(drop);
		}
		event.getDrops().clear();

		for(ItemStack drop : drops) {
			event.getDrops().add(new ItemStack(drop.getType(), amountDropItem(drop, main_hand, job)));
		}
		
		EntityType type = monster.getType();

		int xp = plugin.getConfig().getInt("Config.Jobs.MonsterSlayer." + type.toString() + ".xp");
		job.addExp(xp);
		job.save();
		
		if(plugin.getConfig().getBoolean("Config.Plugin.Shop.enabled")) {
			double base_coins = plugin.getConfig().getDouble("Config.Jobs.MonsterSlayer." + type.toString() + ".coins");
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
