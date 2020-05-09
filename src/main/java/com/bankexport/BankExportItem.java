package com.bankexport;

import lombok.Getter;

public class BankExportItem
{
	@Getter
	private final String name;

	@Getter
	private final int quantity;

	@Getter
	private final int id;

	BankExportItem(String name, int quantity, int id)
	{
		this.name = name;
		this.quantity = quantity;
		this.id = id;
	}
}
