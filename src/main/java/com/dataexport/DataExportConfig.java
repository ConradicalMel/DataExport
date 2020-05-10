package com.dataexport;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("dataexport")
public interface DataExportConfig extends Config
{
	@ConfigItem(
		keyName = "includeBank",
		name = "Include bank",
		description = "Include bank in data export"
	)
	default boolean includeBank()
	{
		return false;
	}

	@ConfigItem(
		keyName = "includeSeedVault",
		name = "Include seed vault",
		description = "Include seed vault in data export"
	)
	default boolean includeSeedVault()
	{
		return false;
	}

	@ConfigItem(
		keyName = "includeInventory",
		name = "Include inventory",
		description = "Include inventory in data export"
	)
	default boolean includeInventory()
	{
		return false;
	}

	@ConfigItem(
		keyName = "includeEquipment",
		name = "Include equipment",
		description = "Include equipment in data export"
	)
	default boolean includeEquipment()
	{
		return false;
	}
}
