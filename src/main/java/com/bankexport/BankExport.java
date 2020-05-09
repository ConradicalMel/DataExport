package com.bankexport;

import com.google.gson.Gson;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;

public class BankExport
{
	private Client client;

	private BankExportConfig config;

	private ItemManager itemManager;

	@Setter
	private ArrayList<BankExportSkill> arrayListSkills;

	@Setter
	private ArrayList<BankExportItem> arrayListBank;

	@Setter
	private ArrayList<BankExportItem> arrayListSeedVault;

	@Setter
	private ArrayList<BankExportItem> arrayListAllItems;

	private BankExportPluginPanel panel;

	private BankExportPlugin plugin;

	public BankExport(Client client, BankExportConfig config, ItemManager itemManager)
	{
		this.client = client;
		this.config = config;
		this.itemManager = itemManager;

		arrayListSkills = new ArrayList<BankExportSkill>();
		arrayListBank = new ArrayList<BankExportItem>();
		arrayListSeedVault = new ArrayList<BankExportItem>();
		arrayListAllItems = new ArrayList<BankExportItem>();
	}

	public void exportSetup()
	{
		//rebuildItemArrayList();
		SwingUtilities.invokeLater(() ->
		{
			final Gson gson = new Gson();
			final String json = gson.toJson(arrayListAllItems);
			final StringSelection contents = new StringSelection(json);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(contents, null);
		});
//		JOptionPane.showMessageDialog(panel,
//			"Setup data was copied to clipboard.",
//			"Export Setup Succeeded",
//			JOptionPane.PLAIN_MESSAGE);
	}

	public void rebuildSkillArrayList()
	{

	}

	public void rebuildItemArrayList()
	{
		arrayListAllItems = new ArrayList<BankExportItem>();

		arrayListAllItems.addAll(arrayListBank);

		for (BankExportItem item : arrayListAllItems)
		{
			System.out.println("Name: " + item.getName() + "\tQuantity: " + item.getQuantity() + "\tID: " + item.getId());
		}

		exportSetup();
	}
}
