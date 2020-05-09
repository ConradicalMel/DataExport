package com.bankexport;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class BankExportSkill
{
	@Getter
	private final String name;

	@Getter
	private final int level;

	@Getter
	private final int experience;
}