package tests;

import java.util.List;

import csv.CsvFileParser;

public class Main
{
	public static void main(String[] args)
	{
		CsvFileParser csv = new CsvFileParser();
		List<Product> products = csv.parse(Product.class, "resources/products.csv");
		products.stream().forEach(x -> System.out.println(x.name + " " + (x.price + 10) + " " + x.symbol));
	}
}
