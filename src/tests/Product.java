package tests;

import java.util.Date;

import csv.CsvName;

public class Product
{
	public String name;
	public String type;
	public double price;
	public char symbol;
	
	@CsvName("status")
	public boolean old;
	public Date release;
}
