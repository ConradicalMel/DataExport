package com.bankexport;

import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ScriptCallbackEvent;
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
public class BankExportPlugin extends Plugin
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
	private BankExportConfig config;

	private BankExport bankExport;

//	private final static BufferedImage ICON = ImageUtil.getResourceStreamFromClass(BankExportPlugin.class, "banked.png");
//	private NavigationButton navButton;
//	private BankExportPluginPanel panel;
//	private boolean prepared = false;
	private int bankHash = -1;
	private int lastCheckTick = -1;

	@Provides
	BankExportConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BankExportConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		log.info("Bank Export started!");

		bankExport = new BankExport(client, config, itemManager);
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Bank Export stopped!");

	}

	@Subscribe
	public void onScriptCallbackEvent(ScriptCallbackEvent event)
	{
		if (!event.getEventName().equals("setBankTitle") || client.getTickCount() == lastCheckTick)
		{
			return;
		}

		// Check if the contents have changed.
		final ItemContainer c = client.getItemContainer(InventoryID.BANK);
		if (c == null)
		{
			return;
		}

		final Item[] widgetItems = c.getItems();
		if (widgetItems == null || widgetItems.length == 0)
		{
			return;
		}

		ArrayList<BankExportItem> arrayList = new ArrayList<>();

		for (Item widgetItem : widgetItems)
		{
			ItemComposition itemComposition = itemManager.getItemComposition(widgetItem.getId());
			String name = itemComposition.getName();
			BankExportItem item = new BankExportItem(name, widgetItem.getQuantity(), widgetItem.getId());
			arrayList.add(item);
		}

		final int curHash = arrayList.hashCode();
		if (bankHash != curHash)
		{
			bankHash = curHash;
			//SwingUtilities.invokeLater(() -> panel.setBankMap(m));
		}

		lastCheckTick = client.getTickCount();

		for (BankExportItem item : arrayList)
		{
			System.out.println("Name: " + item.getName() + "\tQuantity: " + item.getQuantity() + "\tID: " + item.getId());
		}

		bankExport.setArrayListBank(arrayList);
		bankExport.rebuildItemArrayList();

	}
}
