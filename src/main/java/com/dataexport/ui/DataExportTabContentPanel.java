package com.dataexport.ui;

import com.dataexport.DataExport;
import com.dataexport.DataExportConfig;
import com.dataexport.DataExportItem;
import com.dataexport.DataExportPlugin;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import net.runelite.api.Constants;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;

public class DataExportTabContentPanel extends JPanel
{
	private final ItemManager itemManager;

	private final DataExportPlugin plugin;

	private final DataExportPluginPanel panel;

	private final DataExportConfig config;

	private final DataExport dataExport;

	private static final ImageIcon EXPORT_ICON;

	private static final ImageIcon DOWNLOAD_ICON;

	JButton exportButton = new JButton();

	JButton downloadButton = new JButton();

	private final JLabel exportLabel = new JLabel("Export");

	private final JLabel downloadLabel = new JLabel("Download");

	static
	{
		BufferedImage exportIcon = ImageUtil.getResourceStreamFromClass(DataExportPlugin.class, "/export_icon.png");
		BufferedImage downloadIcon = ImageUtil.getResourceStreamFromClass(DataExportPlugin.class, "/export_icon.png");

		EXPORT_ICON = new ImageIcon(exportIcon);
		DOWNLOAD_ICON = new ImageIcon(downloadIcon);
	}

	DataExportTabContentPanel(DataExportPlugin plugin, DataExportPluginPanel panel, DataExportConfig config, DataExport dataExport, ItemManager itemManager, ImageIcon icon, String dataContainer)
	{
		this.plugin = plugin;
		this.panel = panel;
		this.config = config;
		this.dataExport = dataExport;
		this.itemManager = itemManager;

		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARKER_GRAY_COLOR);

		JPanel wrapped = new JPanel(new BorderLayout());
		wrapped.setLayout(new BorderLayout());
		wrapped.setBackground(ColorScheme.DARK_GRAY_COLOR);
		wrapped.setVisible(true);

		JLabel iconLabel = new JLabel(icon);
		iconLabel.setMinimumSize(new Dimension(Constants.ITEM_SPRITE_WIDTH, Constants.ITEM_SPRITE_HEIGHT));
		wrapped.add(iconLabel, BorderLayout.WEST);

		JPanel buttonContainer = new JPanel();
		buttonContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		buttonContainer.setLayout(new GridLayout(2, 1));
		buttonContainer.setBorder(new EmptyBorder(5, 7, 5, 7));

		exportButton = new JButton();
		exportButton.setIcon(EXPORT_ICON);
		exportButton.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		exportButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e))
				{
					//dataExport.exportAllItems();
					panel.rebuild();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				exportButton.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR.darker());
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				exportButton.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR);
			}
		});

		downloadButton = new JButton();
		downloadButton.setIcon(DOWNLOAD_ICON);
		downloadButton.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		downloadButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e))
				{
					plugin.dataWriter.writeDataFile(dataContainer, plugin.dataExport.getMapItems());
					panel.rebuild();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				downloadButton.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR.darker());
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				downloadButton.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR);
			}
		});
		buttonContainer.add(exportButton);
		buttonContainer.add(downloadButton);
		wrapped.add(buttonContainer, BorderLayout.EAST);
		add(wrapped);

		updateVisibility();
	}

	private void updateVisibility()
	{
		if (!config.displayExport())
		{
			exportButton.setVisible(false);
		}

		if (!config.displayDownload())
		{
			downloadButton.setVisible(false);
		}
	}
}
