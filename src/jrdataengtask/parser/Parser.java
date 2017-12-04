package jrdataengtask.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Parser
{
	public void constructQueries(StringBuilder valuesForInsertionBuilder)
	{
		FileReader fileReader;
		String line;
		
		try
		{
			fileReader = new FileReader("10L Comb_Expenditure_over_Threshold_Report_August_17.csv");
			BufferedReader in = new BufferedReader(fileReader);
			
			in.readLine();
			while((line = in.readLine()) != null)
			{
				valuesForInsertionBuilder.append("('" + line.replaceAll(",", "','") + "'),");
			}
//
			valuesForInsertionBuilder.deleteCharAt(valuesForInsertionBuilder.length()-1);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
