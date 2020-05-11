package com.dataexport;

import lombok.Getter;
import net.runelite.client.game.ItemManager;

public class DataExportItem
{
	@Getter
	private final int id;

	@Getter
	private final int quantity;

	@Getter
	private final String name;

	DataExportItem(String name, int quantity, int id)
	{
		this.id = id;
		this.quantity = quantity;
		this.name = name;
	}
}
