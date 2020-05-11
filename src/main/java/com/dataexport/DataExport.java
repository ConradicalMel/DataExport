package com.dataexport;

import com.dataexport.ui.DataExportPluginPanel;
import com.google.gson.Gson;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;

public class DataExport
{
	private final Client client;

	private final DataExportConfig config;

	private final ItemManager itemManager;

	@Getter
	@Setter
	private Map<Integer, DataExportItem> mapBank;

	@Getter
	@Setter
	private Map<Integer, DataExportItem> mapSeedVault;

	@Getter
	@Setter
	private Map<Integer, DataExportItem> mapInventory;

	@Getter
	@Setter
	private Map<Integer, DataExportItem> mapEquipment;

	@Getter
	@Setter
	private Map<Integer, DataExportItem> mapSkills;

	@Getter
	@Setter
	private Map<Integer, DataExportItem> mapItems;

	@Getter
	@Setter
	private ArrayList<Integer> arrayListItems;

	int hashAllItems;

	private DataExportPluginPanel panel;

	private DataExportPlugin plugin;

	public DataExport(Client client, DataExportConfig config, ItemManager itemManager, DataExportPlugin plugin)
	{
		this.client = client;
		this.config = config;
		this.itemManager = itemManager;
		this.plugin = plugin;

		hashAllItems = -1;

		mapBank = new HashMap<>();
		mapSeedVault = new HashMap<>();
		mapInventory = new HashMap<>();
		mapEquipment = new HashMap<>();
		mapItems = new HashMap<>();
		arrayListItems = new ArrayList<>();
	}

	public void exportAllItems()
	{
		plugin.dataWriter.writeDataFile("container_all_items", mapItems);
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

	public void exportBank()
	{
		plugin.dataWriter.writeDataFile("container_bank", mapBank);
		SwingUtilities.invokeLater(() ->
		{
			final Gson gson = new Gson();
			final String json = gson.toJson(mapBank);
			final StringSelection contents = new StringSelection(json);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(contents, null);
			JOptionPane.showMessageDialog(panel,
				"Setup data was copied to clipboard.",
				"Export Setup Succeeded",
				JOptionPane.PLAIN_MESSAGE);
		});
	}
	public void rebuildSkillArrayList()
	{

	}

	public void rebuildItemArrayList()
	{
		int quantity = 0;

		int hash = mapItems.hashCode();
		if (hashAllItems == hash)
		{
			return;
		}
		System.out.println("New all items hash: " + hashAllItems);
		hashAllItems = hash;

		for (Map.Entry<Integer, DataExportItem> entry : mapItems.entrySet())
		{
			DataExportItem item = entry.getValue();
			quantity = getTotalQuantityForItem(item.getId());
			DataExportItem temp = new DataExportItem(item.getName(), quantity, item.getId());
			mapItems.replace(item.getId(), temp);
			//System.out.println("ID: " + item.getId() + "\tQuantity: " + item.getQuantity() + "\t\tName: " + item.getName());
		}
		System.out.println("Export data");
		exportAllItems();
	}

	private int getTotalQuantityForItem(int id)
	{
		int total = 0;

		if (mapBank.containsKey(id) && mapBank.size() > 1)
		{
			total += mapBank.get(id).getQuantity();
		}
		if (mapSeedVault.containsKey(id) && mapSeedVault.size() > 1)
		{
			total += mapSeedVault.get(id).getQuantity();
		}
		if (mapInventory.containsKey(id) && mapInventory.size() > 1)
		{
			total += mapInventory.get(id).getQuantity();
		}
		if (mapEquipment.containsKey(id) && mapEquipment.size() > 1)
		{
			total += mapEquipment.get(id).getQuantity();
		}

		return total;
	}

	public void addItemAll(int id, DataExportItem item)
	{
		DataExportItem item2 = mapItems.get(id);

		if (item2 != null)
		{
			//System.out.println("Already in list: " + item.getName());
			return;
		}

		//System.out.println("Adding to list: " + item.getName());
		mapItems.put(id, item);
	}
}
