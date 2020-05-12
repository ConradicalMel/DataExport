package com.dataexport.ui;

import com.dataexport.DataExport;
import com.dataexport.DataExportConfig;
import com.dataexport.DataExportPlugin;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.client.util.ImageUtil;

@Slf4j
public class DataExportPluginPanel extends PluginPanel
{
	//private final BankExport bankExport;

	private final ItemManager itemManager;

	private final DataExportPlugin plugin;

	private final DataExportConfig config;

	private final DataExport dataExport;

	private static final ImageIcon BANK_ICON;

	private static ImageIcon SEED_VAULT_ICON;

	private static final ImageIcon INVENTORY_ICON;

	private static final ImageIcon EQUIPMENT_ICON;

	DataExportTabPanel bankTabPanel;

	DataExportTabPanel seedVaultTabPanel;

	DataExportTabPanel inventoryTabPanel;

	DataExportTabPanel equipmentTabPanel;

	private final PluginErrorPanel errorPanel = new PluginErrorPanel();

	final JPanel wrapperPanel = new JPanel();

	private JPanel containerContainer = new JPanel();

	private final JPanel tabContainer = new JPanel();


	private final JButton exportButton = new JButton();

	private final JButton downloadButton = new JButton();

	private static final Color HOVER_COLOR = ColorScheme.DARKER_GRAY_HOVER_COLOR;

	private Map<Tab, DataExportTabPanel> containers = new LinkedHashMap<>();

	//private final JLabel statusLabel;

	//private final DataExportPlugin plugin;

	static
	{
		BufferedImage bankIcon = ImageUtil.getResourceStreamFromClass(DataExportPlugin.class, "/panel_icon.png");
		BufferedImage seedVaultIcon = ImageUtil.getResourceStreamFromClass(DataExportPlugin.class, "/panel_icon.png");
		BufferedImage inventoryIcon = ImageUtil.getResourceStreamFromClass(DataExportPlugin.class, "/panel_icon.png");
		BufferedImage equipmentIcon = ImageUtil.getResourceStreamFromClass(DataExportPlugin.class, "/panel_icon.png");

		BANK_ICON = new ImageIcon(bankIcon);
		SEED_VAULT_ICON = new ImageIcon(seedVaultIcon);
		INVENTORY_ICON = new ImageIcon(inventoryIcon);
		EQUIPMENT_ICON = new ImageIcon(equipmentIcon);
	}

	public DataExportPluginPanel(ItemManager itemManager, DataExportPlugin plugin, DataExportConfig config, DataExport dataExport)
	{
		super(true);

		this.itemManager = itemManager;
		this.plugin = plugin;
		this.config = config;
		this.dataExport = dataExport;

		setBackground(ColorScheme.DARK_GRAY_COLOR);
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(7, 7, 7, 7));

		wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.Y_AXIS));
		wrapperPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);

		containerContainer.setLayout(new GridLayout(0, 1, 0, 8));
		containerContainer.setBackground(ColorScheme.DARK_GRAY_COLOR);
		containerContainer.setVisible(true);

		Arrays.asList(Tab.CONTAINER_TABS).forEach(t ->
		{
			DataExportTabPanel p = new DataExportTabPanel(plugin, this, config, dataExport, itemManager, t, t.getName(), t.getFilePrefix(), "Not ready");
			containers.put(t, p);
		});

		containers.forEach((tab, panel) ->
			containerContainer.add(panel));

		wrapperPanel.add(containerContainer);

		this.add(wrapperPanel);

		updateVisibility();
		rebuild();
	}

	public void updateVisibility()
	{
		containerContainer.removeAll();

//		Arrays.asList(Tab.CONTAINER_TABS).forEach(t ->
//		{
//			if (containers.get(t) != null)
//			{
//				String status = containers.get(t).getStatus();
//				DataExportTabPanel p = new DataExportTabPanel(plugin, this, config, dataExport, itemManager, t, t.getName(), t.getFilePrefix(), status);
//				containers.put(t, p);
//			}
//			else
//			{
//				DataExportTabPanel p = new DataExportTabPanel(plugin, this, config, dataExport, itemManager, t, t.getName(), t.getFilePrefix(), "Not ready");
//				containers.put(t, p);
//			}
//		});

		log.debug("Containers: {}", containers.values());

		containers.forEach((t, p) ->
		{
			if (p.isVisibility())
			{
				containerContainer.add(p);
			}
		});

		rebuild();
	}

	public void setVisibility(Tab tab, boolean visibility)
	{
		log.debug("Containers: {}", containers.values());

		Map<Tab, DataExportTabPanel> containersTemp = new LinkedHashMap<>();

		containers.forEach((t, p) ->
		{
			if (p.isVisibility() && t.getName().compareTo(p.getTitle()) != 0)
			{
				setVisibility(Tab.ALL_ITEMS, true);
			}
			if (tab.getName().equals(t.getName()))
			{
				DataExportTabPanel panel = containers.get(tab);
				panel.setVisibility(visibility);
				containersTemp.put(t, panel);
			}

			containersTemp.put(t, p);
		});

		containers = containersTemp;
	}

	public void updateTab(String container, String newStatus)
	{
		containers.forEach((tab, panel) ->
		{
			if (panel.getTitle().equals(container))
			{
				panel.updateStatus(newStatus);
			}
			containers.put(tab, panel);
		});

		containers.forEach((tab, panel) ->
			containerContainer.add(panel));

		rebuild();
	}

	public void rebuild()
	{
		revalidate();
		repaint();
	}

	public void exportContainer(String containerName)
	{

	}
}
