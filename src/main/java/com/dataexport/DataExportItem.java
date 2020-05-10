package com.dataexport;

import lombok.Getter;
import net.runelite.client.game.ItemManager;

public class DataExportItem
{
	@Getter
	private final String name;

	@Getter
	private final int quantity;

	@Getter
	private final int id;

	DataExportItem(String name, int quantity, int id)
	{
		this.name = name;
		this.quantity = quantity;
		this.id = id;
	}
}
