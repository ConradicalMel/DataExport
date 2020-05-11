package com.dataexport;

import com.dataexport.localstorage.DataWriter;
import com.dataexport.ui.DataExportPluginPanel;
import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@PluginDescriptor(
	name = "Data Export"
)
public class DataExportPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private ItemManager itemManager;

	@Inject
	private SkillIconManager skillIconManager;

	@Inject
	private DataExportConfig config;

	@Inject
	private KeyManager keyManager;

	@Inject
	public DataWriter dataWriter;

	private DataExportPluginPanel panel;

	public DataExport dataExport;

	private NavigationButton navButton;

	private int hashBank = -1;

	private int hashSeedVault = -1;

	private int hashInventory = -1;

	private int hashEquipment = -1;

	int hashAllItems = -1;

	private int hashSkills = -1;

	private int lastTick = -1;

	private static final Logger logger = LoggerFactory.getLogger(DataExportPlugin.class);

	@Provides
	DataExportConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DataExportConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		log.info("Data Export started!");
		dataExport = new DataExport(client, config, itemManager, this);

		this.panel = new DataExportPluginPanel(itemManager, this, config, dataExport);

		final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "/data_export_icon.png");

		navButton = NavigationButton.builder()
			.tooltip("Data Exporter")
			.icon(icon)
			.priority(6)
			.panel(panel)
			.build();

		clientToolbar.addNavigation(navButton);

		clientThread.invokeLater(() ->
		{
			switch (client.getGameState())
			{
				case STARTING:
				case UNKNOWN:
					return false;
			}

			SwingUtilities.invokeLater(() ->
			{
				panel.rebuild();
			});

			return true;
		});
	}

	@Override
	protected void shutDown() throws Exception
	{
		logger.debug("Data Export stopped!");

		clientToolbar.removeNavigation(navButton);

	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals("dataexport"))
		{
			return;
		}

		Map<Integer, DataExportItem> mapBlank = new HashMap<>();
		if (!config.includeBank())
		{
			dataExport.setMapBank(mapBlank);
		}
		if (config.includeSeedVault())
		{
			dataExport.setMapSeedVault(mapBlank);
		}
		if (config.includeInventory())
		{
			dataExport.setMapInventory(mapBlank);
		}
		if (config.includeEquipment())
		{
			dataExport.setMapEquipment(mapBlank);
		}

		panel.rebuild();
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		int tick = client.getTickCount();
		if (tick == lastTick)
		{
			return;
		}
		lastTick = tick;

		ItemContainer itemContainer = event.getItemContainer();
		int itemContainerId = event.getContainerId();

		if (itemContainer == null)
		{
			return;
		}

		final Item[] widgetItems = itemContainer.getItems();
		if (widgetItems == null || widgetItems.length == 0)
		{
			return;
		}

		int hash = hashItems(widgetItems);
		//log.info("New hash: " + hash);

		Map<Integer, DataExportItem> mapContainer = new HashMap<>();
		Map<Integer, DataExportItem> mapItems = dataExport.getMapItems();

		for (Item widgetItem : widgetItems)
		{
			ItemComposition itemComposition = itemManager.getItemComposition(widgetItem.getId());

			String name = itemComposition.getName();
			int quantity = widgetItem.getQuantity();
			int id = widgetItem.getId();

			if (itemComposition.getPlaceholderTemplateId() != -1)
			{
				quantity = 0;
			}

			if (name != null && quantity > 0 && id != -1)
			{
				DataExportItem item = new DataExportItem(name, quantity, id);
				mapContainer.putIfAbsent(id, item);
				dataExport.addItemAll(id, item);
			}
		}

		if (mapContainer.size() < 2)
		{
			return;
		}

		if (itemContainerId == InventoryID.BANK.getId() && config.includeBank() && hash != hashBank)
		{
			logger.debug("Bank hash: " + hashBank + "   ->   " + hash);
			hashBank = hash;
			updateBankData(mapContainer);
		}
		else if (itemContainerId == InventoryID.SEED_VAULT.getId() && config.includeSeedVault() && hash != hashSeedVault)
		{
			logger.debug("Seed vault hash: " + hashSeedVault + "   ->   " + hash);
			hashSeedVault = hash;
			updateSeedVaultData(mapContainer);
		}
		else if (itemContainerId == InventoryID.INVENTORY.getId() && config.includeInventory() && hash != hashInventory)
		{
			logger.debug("Inventory hash: " + hashInventory + "   ->   " + hash);
			hashInventory = hash;
			updateInventoryData(mapContainer);
		}
		else if (itemContainerId == InventoryID.EQUIPMENT.getId() && config.includeEquipment() && hash != hashEquipment)
		{
			logger.debug("Equipment hash: " + hashEquipment + "   ->   " + hash);
			hashEquipment = hash;
			updateEquipmentData(mapContainer);
		}

		dataExport.rebuildItemArrayList();
	}

	private void updateBankData(Map<Integer, DataExportItem> map)
	{
		System.out.println("Updating bank!");
		dataExport.setMapBank(map);
		dataWriter.writeDataFile("container_bank", map);
		logger.debug("Bank Container Map: {}", map);
	}

	private void updateSeedVaultData(Map<Integer, DataExportItem> map)
	{
		System.out.println("Updating seed vault!");
		dataExport.setMapSeedVault(map);
		dataWriter.writeDataFile("container_seed_vault", map);
		logger.debug("Seed Vault Container Map: {}", map);
	}

	private void updateInventoryData(Map<Integer, DataExportItem> map)
	{
		System.out.println("Updating inventory!");
		dataExport.setMapInventory(map);
		dataWriter.writeDataFile("container_inventory", map);
		logger.debug("Inventory Container Map: {}", map);
	}

	private void updateEquipmentData(Map<Integer, DataExportItem> map)
	{
		System.out.println("Updating equipment!");
		dataExport.setMapEquipment(map);
		dataWriter.writeDataFile("container_equipment", map);
		logger.debug("Equipment Container Map: {}", map);
	}

	private int hashItems(final Item[] items)
	{
		final Map<Integer, Integer> mapCheck = new HashMap<>(items.length);
		for (Item item : items)
		{
			mapCheck.put(item.getId(), item.getQuantity());
		}

		return mapCheck.hashCode();
	}
}
