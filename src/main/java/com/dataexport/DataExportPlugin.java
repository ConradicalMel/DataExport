package com.dataexport;

import com.google.inject.Provides;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;

@Slf4j
@PluginDescriptor(
	name = "Bank Export"
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

	private DataExport dataExport;

	private int hashBank = -1;
	private int hashSeedVault = -1;
	private int hashInventory = -1;
	private int hashEquipment = -1;
	private int hashSkills = -1;

	@Provides
	DataExportConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DataExportConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		log.info("Bank Export started!");

		dataExport = new DataExport(client, config, itemManager);
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Bank Export stopped!");

	}

	@Subscribe
	public void onScriptCallbackEvent(ScriptCallbackEvent event)
	{
//		if (!event.getEventName().equals("setBankTitle") || client.getTickCount() == lastCheckTick)
//		{
//			return;
//		}
//
//		// Check if the contents have changed.
//		final ItemContainer c = client.getItemContainer(InventoryID.BANK);
//		if (c == null)
//		{
//			return;
//		}
//
//		final Item[] widgetItems = c.getItems();
//		if (widgetItems == null || widgetItems.length == 0)
//		{
//			return;
//		}
//
//		ArrayList<DataExportItem> arrayList = new ArrayList<>();
//
//		for (Item widgetItem : widgetItems)
//		{
//			ItemComposition itemComposition = itemManager.getItemComposition(widgetItem.getId());
//			String name = itemComposition.getName();
//			DataExportItem item = new DataExportItem(name, widgetItem.getQuantity(), widgetItem.getId());
//			arrayList.add(item);
//		}
//
//		final int curHash = arrayList.hashCode();
//		if (bankHash != curHash)
//		{
//			bankHash = curHash;
//			//SwingUtilities.invokeLater(() -> panel.setBankMap(m));
//		}
//
//		lastCheckTick = client.getTickCount();
//
//		for (DataExportItem item : arrayList)
//		{
//			System.out.println("Name: " + item.getName() + "\tQuantity: " + item.getQuantity() + "\tID: " + item.getId());
//		}
//
//		dataExport.setArrayListBank(arrayList);
//		dataExport.rebuildItemArrayList();

	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
//		if (event.getGroupId() != WidgetID.SEED_VAULT_GROUP_ID || !config.seedVaultValue())
//		{
//			return;
//		}
//
//		updateSeedVaultData();
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
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

		Map<Integer, DataExportItem> mapContainer = new HashMap<>();

		for (Item widgetItem : widgetItems)
		{
			ItemComposition itemComposition = itemManager.getItemComposition(widgetItem.getId());

			String name = itemComposition.getName();
			int quantity = widgetItem.getQuantity();
			int id = widgetItem.getId();

			if (name != null && quantity > 0 && id != -1)
			{
				DataExportItem item = new DataExportItem(name, quantity, id);
				mapContainer.putIfAbsent(id, item);
			}
		}

		if (mapContainer == null)
		{
			return;
		}

		Map<Integer, DataExportItem> mapItems = dataExport.getMapItems();
		mapItems.putAll(mapContainer);
		dataExport.setMapItems(mapItems);

		if (itemContainerId == InventoryID.BANK.getId() && config.includeBank() && hash != hashBank)
		{
			hashBank = hash;
			updateBankData(mapContainer);
		}
		else if (itemContainerId == InventoryID.SEED_VAULT.getId() && config.includeSeedVault() && hash != hashSeedVault)
		{
			hashSeedVault = hash;
			updateSeedVaultData(mapContainer);
		}
		else if (itemContainerId == InventoryID.INVENTORY.getId() && config.includeInventory() && hash != hashInventory)
		{
			hashInventory = hash;
			updateInventoryData(mapContainer);
		}
		else if (itemContainerId == InventoryID.EQUIPMENT.getId() && config.includeEquipment() && hash != hashEquipment)
		{
			hashEquipment = hash;
			updateEquipmentData(mapContainer);
		}

		dataExport.rebuildItemArrayList();
	}

	private void updateBankData(Map<Integer, DataExportItem> map)
	{
		System.out.println("Updating bank!");
		dataExport.setMapBank(map);
	}

	private void updateSeedVaultData(Map<Integer, DataExportItem> map)
	{
		System.out.println("Updating seed vault!");
		dataExport.setMapSeedVault(map);
	}

	private void updateInventoryData(Map<Integer, DataExportItem> map)
	{
		System.out.println("Updating inventory!");
		dataExport.setMapInventory(map);
	}

	private void updateEquipmentData(Map<Integer, DataExportItem> map)
	{
		System.out.println("Updating equipment!");
		dataExport.setMapEquipment(map);
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
