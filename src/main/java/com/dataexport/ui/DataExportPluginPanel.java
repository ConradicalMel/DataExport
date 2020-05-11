package com.dataexport.ui;

import com.dataexport.DataExport;
import com.dataexport.DataExportConfig;
import com.dataexport.DataExportPlugin;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.client.util.ImageUtil;

public class DataExportPluginPanel extends PluginPanel
{
	//private final BankExport bankExport;

	private final ItemManager itemManager;

	private final DataExportPlugin plugin;

	private final DataExportConfig config;

	private final DataExport dataExport;

	private static final ImageIcon BANK_ICON;

	private static final ImageIcon SEED_VAULT_ICON;

	private static final ImageIcon INVENTORY_ICON;

	private static final ImageIcon EQUIPMENT_ICON;

	DataExportTabContentPanel bankTabPanel;

	DataExportTabContentPanel seedVaultTabPanel;

	DataExportTabContentPanel inventoryTabPanel;

	DataExportTabContentPanel equipmentTabPanel;

	private final PluginErrorPanel errorPanel = new PluginErrorPanel();

	final JPanel wrapperPanel = new JPanel();

	private JPanel containerContainer = new JPanel();

	private final JPanel tabContainer = new JPanel();


	private final JButton exportButton = new JButton();

	private final JButton downloadButton = new JButton();

	private static final Color HOVER_COLOR = ColorScheme.DARKER_GRAY_HOVER_COLOR;

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

		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(7, 7, 7, 7));

		wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.Y_AXIS));
		wrapperPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		containerContainer.setLayout(new GridLayout(0, 1, 0, 8));
		containerContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		containerContainer.setVisible(true);

		bankTabPanel = new DataExportTabContentPanel(plugin, this, config, dataExport, itemManager, BANK_ICON, "container_bank");
		seedVaultTabPanel = new DataExportTabContentPanel(plugin, this, config, dataExport, itemManager, SEED_VAULT_ICON, "container_seed_vault");
		inventoryTabPanel = new DataExportTabContentPanel(plugin, this, config, dataExport, itemManager, INVENTORY_ICON, "container_inventory");
		equipmentTabPanel = new DataExportTabContentPanel(plugin, this, config, dataExport, itemManager, EQUIPMENT_ICON, "container_equipment");

		containerContainer.add(bankTabPanel);
		containerContainer.add(seedVaultTabPanel);
		containerContainer.add(equipmentTabPanel);
		containerContainer.add(inventoryTabPanel);

		wrapperPanel.add(containerContainer);

		this.add(wrapperPanel);

		rebuild();
	}

	public void updateVisibility()
	{
		if (!config.includeBank())
		{
			bankTabPanel.setVisible(false);
		}
		else
		{
			bankTabPanel.setVisible(true);
		}

		if (!config.includeSeedVault())
		{
			seedVaultTabPanel.setVisible(false);
		}
		else
		{
			seedVaultTabPanel.setVisible(true);
		}

		if (!config.includeInventory())
		{
			inventoryTabPanel.setVisible(false);
		}
		else
		{
			inventoryTabPanel.setVisible(true);
		}

		if (!config.includeEquipment())
		{
			equipmentTabPanel.setVisible(false);
		}
		else
		{
			equipmentTabPanel.setVisible(true);
		}
	}

	public void rebuild()
	{
		updateVisibility();

		bankTabPanel = new DataExportTabContentPanel(plugin, this, config, dataExport, itemManager, BANK_ICON, "container_bank");
		seedVaultTabPanel = new DataExportTabContentPanel(plugin, this, config, dataExport, itemManager, SEED_VAULT_ICON, "container_seed_vault");
		inventoryTabPanel = new DataExportTabContentPanel(plugin, this, config, dataExport, itemManager, INVENTORY_ICON, "container_inventory");
		equipmentTabPanel = new DataExportTabContentPanel(plugin, this, config, dataExport, itemManager, EQUIPMENT_ICON, "container_equipment");

		revalidate();
		repaint();
	}
}
