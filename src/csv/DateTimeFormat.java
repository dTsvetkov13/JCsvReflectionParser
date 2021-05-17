package csv;

public enum DateTimeFormat
{
	yyyy_MM_dd("yyyy-MM-dd"), 
	dd_MM_yyyy("dd/MM/yyyy"), 
	dd_MMM_yyyy("dd-MMM-yyyy"), 
	E_MMM_dd_yyyy("E, MMM dd yyyy");
	
	private String value;
	
	private DateTimeFormat(String value)
	{
		this.value = value;
	}
	
	public String getValue()
	{
		return value;
	}
}
