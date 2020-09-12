package fr.sonkuun.jobs.plugin;

import org.bukkit.plugin.java.JavaPlugin;

import fr.sonkuun.jobs.event.FarmerEvent;
import fr.sonkuun.jobs.event.FishingEvent;
import fr.sonkuun.jobs.event.BreederEvent;
import fr.sonkuun.jobs.event.DiggerEvent;
import fr.sonkuun.jobs.event.JobsListener;
import fr.sonkuun.jobs.event.MiningEvent;
import fr.sonkuun.jobs.event.MonsterSlayerEvent;
import fr.sonkuun.jobs.event.WoodcuttingEvent;

public class JobsPlugin extends JavaPlugin {
	
	private String TAG = this.getClass().getSimpleName().toString();
	public JobsPlugin plugin;
	
	public JobsListener jobsListener;
	public MiningEvent miningEvent;
	public WoodcuttingEvent woodcuttingEvent;
	public FishingEvent fishingEvent;
	public FarmerEvent farmerEvent;
	public MonsterSlayerEvent monsterSlayerEvent;
	public BreederEvent breederEvent;
	public DiggerEvent diggerEvent;
	
	private CustomCommandExecutor commandExecutor;
	
	@Override
	public void onEnable() {
		plugin = this;
		
		loadConfig();
		
		loadCommand();

		loadListener();
		
		System.out.println(TAG + " properly enabled !");
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public void loadConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	public void loadCommand() {
		commandExecutor = new CustomCommandExecutor(this);

		this.getCommand("jobs").setExecutor(commandExecutor);
		this.getCommand("reset").setExecutor(commandExecutor);
		this.getCommand("give").setExecutor(commandExecutor);
	}

	public void loadListener() {
		jobsListener = new JobsListener(plugin);
		miningEvent = new MiningEvent(plugin);
		woodcuttingEvent = new WoodcuttingEvent(plugin);
		fishingEvent = new FishingEvent(plugin);
		farmerEvent = new FarmerEvent(plugin);
		monsterSlayerEvent = new MonsterSlayerEvent(plugin);
		breederEvent = new BreederEvent(plugin);
		diggerEvent = new DiggerEvent(plugin);

		this.getServer().getPluginManager().registerEvents(jobsListener, this);
	}
	
}
