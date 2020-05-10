package com.dataexport.localstorage;

import com.dataexport.DataExportItem;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import static net.runelite.client.RuneLite.RUNELITE_DIR;
import net.runelite.http.api.RuneLiteAPI;

@Slf4j
@Singleton
public class DataWriter
{
	private static final String FILE_EXTENSION = ".json";

	private static final File LOOT_RECORD_DIR = new File(RUNELITE_DIR, "Data Exports");

	private File playerFolder = LOOT_RECORD_DIR;

	private String name;

	@Inject
	public DataWriter()
	{
		LOOT_RECORD_DIR.mkdir();
	}

	public void setPlayerUsername(final String username)
	{
		if (username.equalsIgnoreCase(name))
		{
			return;
		}

		playerFolder = new File(LOOT_RECORD_DIR, username);
		playerFolder.mkdir();
		name = username;
	}

	private static String dataContainerToFileName(final String dataContainerName)
	{
		return dataContainerName.toLowerCase().trim() + FILE_EXTENSION;
	}

	public synchronized  boolean writeDataFile(String dataContainer, Map<Integer, DataExportItem> items)
	{
		final String fileName = dataContainerToFileName(dataContainer);
		final File lootFile = new File(playerFolder, fileName);

		try
		{
			final BufferedWriter file = new BufferedWriter(new FileWriter(String.valueOf(lootFile), false));
			for (Map.Entry<Integer, DataExportItem> item : items.entrySet())
			{
				// Convert entry to JSON
				final String dataAsString = RuneLiteAPI.GSON.toJson(item.getValue());
				file.append(dataAsString);
				file.newLine();
			}
			file.close();

			return true;
		}
		catch (IOException ioe)
		{
			log.warn("Error rewriting data to file {}: {}", fileName, ioe.getMessage());
			return false;
		}
	}
}
