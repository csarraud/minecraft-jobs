package fr.sonkuun.jobs.event;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.sonkuun.jobs.Jobs;
import fr.sonkuun.jobs.log.Log;
import fr.sonkuun.jobs.plugin.JobsPlugin;

public class JobsListener implements Listener {
	String TAG = "[" + this.getClass().getSimpleName() + "] ";
	
	private JobsPlugin plugin;
	
	public JobsListener(JobsPlugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		if(!player.hasPlayedBefore()) {
			Jobs jobs = new Jobs(player, plugin);
			jobs.save();
		}
	}
	
	@EventHandler
	public void onBreakBlock(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Material material = event.getBlock().getType();
		
		switch (material) {
		case STONE:
		case BLACKSTONE:
		case COAL_ORE:
		case IRON_ORE:
		case GOLD_ORE:
		case LAPIS_ORE:
		case EMERALD_ORE:
		case DIAMOND_ORE:
		case REDSTONE_ORE:
		case NETHER_QUARTZ_ORE:
		case OBSIDIAN:
		case CRYING_OBSIDIAN:
		case NETHER_GOLD_ORE:
		case ANCIENT_DEBRIS:
		case BASALT:
		case GILDED_BLACKSTONE:
			plugin.miningEvent.oreBreak(event);
			break;

		case ACACIA_LOG:
		case BIRCH_LOG:
		case DARK_OAK_LOG:
		case JUNGLE_LOG:
		case OAK_LOG:
		case SPRUCE_LOG:
		case CRIMSON_STEM:
		case WARPED_STEM:
			plugin.woodcuttingEvent.logBreak(event);
			break;
			
		case DIRT:
		case GRASS_BLOCK:
		case COARSE_DIRT:
		case GRAVEL:
		case SAND:
		case RED_SAND:
		case SOUL_SAND:
		case SOUL_SOIL:
		case CLAY:
		case MYCELIUM:
			plugin.diggerEvent.blockDig(event);
		
		case MELON:
		case PUMPKIN:
		case POTATOES:
		case CARROT:
		case BEETROOT:
		case WHEAT:
		case COCOA:
		case CACTUS:
		case SUGAR_CANE:
		case BAMBOO:
		case NETHER_WART:
			plugin.farmerEvent.cropsBreak(event);
			break;
			
		default:
			break;
		}
	}
	
	@EventHandler
	public void onKill(EntityDeathEvent event) {
		Entity victim = event.getEntity();
		
		if(victim instanceof LivingEntity && isMonster(victim) && ((LivingEntity) victim).getKiller() != null) {
			plugin.monsterSlayerEvent.monsterKillByPlayer(event);
		}
		else if(victim instanceof LivingEntity && isAnimal(victim) && ((LivingEntity) victim).getKiller() != null) {
			plugin.breederEvent.animalKillByPlayer(event);
		}
	}
	
	@EventHandler
	public void onBreed(EntityBreedEvent event) {
		LivingEntity breeder = event.getBreeder();
		
		if(breeder instanceof Player) {
			plugin.breederEvent.animalBreedByPlayer(event);
		}
	}
	
	@EventHandler
	public void onFishing(PlayerFishEvent event) {
		Player player = event.getPlayer();
		State state = event.getState();
		
		switch (state) {
		case CAUGHT_FISH:
			plugin.fishingEvent.fishCatch(event);
			break;

		default:
			break;
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		String inventory_name = event.getView().getTitle();
		
		if(inventory_name.equalsIgnoreCase("Jobs") && event.getRawSlot() < event.getInventory().getSize()) {
			event.setCancelled(true);
			
			ItemStack clicked_item = event.getCurrentItem();
			if(clicked_item == null || clicked_item.getType().equals(Material.AIR)) {
				/* Nothing happen */
			}
			else if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Go back")) {
				player.closeInventory();
			}
		}
		
	}
	
	public boolean isMonster(Entity entity) {
		switch (entity.getType()) {
		case BLAZE:
		case CAVE_SPIDER:
		case CREEPER:
		case DROWNED:
		case ELDER_GUARDIAN:
		case ENDER_DRAGON:
		case ENDERMAN:
		case ENDERMITE:
		case EVOKER:
		case EVOKER_FANGS:
		case GHAST:
		case GIANT:
		case GUARDIAN:
		case HUSK:
		case ILLUSIONER:
		case IRON_GOLEM:
		case MAGMA_CUBE:
		case PHANTOM:
		case PIGLIN:
		case ZOMBIFIED_PIGLIN:
		case PILLAGER:
		case RAVAGER:
		case SHULKER:
		case SILVERFISH:
		case SKELETON:
		case SKELETON_HORSE:
		case SLIME:
		case SNOWMAN:
		case SPIDER:
		case VEX:
		case VINDICATOR:
		case WITCH:
		case WITHER:
		case WITHER_SKELETON:
		case ZOMBIE:
		case ZOGLIN:
		case ZOMBIE_HORSE:
		case ZOMBIE_VILLAGER:
			return true;
			
		default:
			return false;
		}
	}
	
	public boolean isAnimal(Entity entity) {
		switch (entity.getType()) {
		case COW:
		case MUSHROOM_COW:
		case SHEEP:
		case PIG:
		case CHICKEN:
		case HORSE:
		case TURTLE:
		case SQUID:
		case DOLPHIN:
		case PANDA:
		case WOLF:
		case PARROT:
		case CAT:
		case DONKEY:
		case FOX:
		case LLAMA:
		case OCELOT:
		case MULE:
		case TROPICAL_FISH:
		case COD:
		case SALMON:
		case RABBIT:
		case PUFFERFISH:
		case POLAR_BEAR:
		case BEE:
		case HOGLIN:
		case STRIDER:
		case ZOGLIN:
			return true;

		default:
			return false;
		}
	}
	
}
