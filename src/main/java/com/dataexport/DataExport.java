package com.dataexport;

import com.google.gson.Gson;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.client.game.ItemManager;

public class DataExport
{
	private final Client client;

	private final DataExportConfig config;

	private final ItemManager itemManager;

	@Setter
	private Map<Integer, DataExportItem> mapBank;

	@Setter
	private Map<Integer, DataExportItem> mapSeedVault;

	@Setter
	private Map<Integer, DataExportItem> mapInventory;

	@Setter
	private Map<Integer, DataExportItem> mapEquipment;

	@Setter
	private Map<Integer, DataExportItem> mapSkills;

	@Getter
	@Setter
	private Map<Integer, DataExportItem> mapItems;

	int hashAllItems;

	private DataExportPluginPanel panel;

	private DataExportPlugin plugin;

	public DataExport(Client client, DataExportConfig config, ItemManager itemManager)
	{
		this.client = client;
		this.config = config;
		this.itemManager = itemManager;

		hashAllItems = -1;

		mapBank = new HashMap<>();
		mapSeedVault = new HashMap<>();
		mapInventory = new HashMap<>();
		mapEquipment = new HashMap<>();
		mapItems = new HashMap<>();
	}

	public void exportData()
	{
		SwingUtilities.invokeLater(() ->
		{
			final Gson gson = new Gson();
			final String json = gson.toJson(mapItems);
			final StringSelection contents = new StringSelection(json);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(contents, null);
//			JOptionPane.showMessageDialog(panel,
//				"Setup data was copied to clipboard.",
//				"Export Setup Succeeded",
//				JOptionPane.PLAIN_MESSAGE);
		});
	}

	public void rebuildSkillArrayList()
	{

	}

	public void rebuildItemArrayList()
	{
		int quantity = 0;

		for (Map.Entry<Integer, DataExportItem> entry : mapItems.entrySet())
		{
			DataExportItem item = entry.getValue();

			quantity = getTotalQuantityForItem(item.getId());
			DataExportItem temp = new DataExportItem(item.getName(), quantity, item.getId());
			mapItems.replace(item.getId(), temp);

			System.out.println("ID: " + item.getId() + "\tQuantity: " + item.getQuantity() + "\t\tName: " + item.getName());
		}

		exportData();
	}

	private int getTotalQuantityForItem(int id)
	{
		int total = 0;

		if (mapBank.containsKey(id) && mapBank != null)
		{
			total += mapBank.get(id).getQuantity();
		}
		if (mapSeedVault.containsKey(id) && mapSeedVault != null)
		{
			total += mapSeedVault.get(id).getQuantity();
		}
		if (mapInventory.containsKey(id) && mapInventory != null)
		{
			total += mapInventory.get(id).getQuantity();
		}
		if (mapEquipment.containsKey(id) && mapEquipment != null)
		{
			total += mapEquipment.get(id).getQuantity();
		}

		return total;
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
