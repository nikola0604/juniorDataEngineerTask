package jrdataengtask.psql;

import java.sql.*;
import java.util.*;

public class PostgresqlUtil
{
	static private Connection con = null;
	
	static
	{
		try
		{
			Class.forName("org.postgresql.Driver");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void connect()
	{
		try
		{
			String url = "jdbc:postgresql://localhost/piratedb";
			con = DriverManager.getConnection(url, "postgres", "postgres");
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public void importData(String valuesForInsertionBuilder)
	{
		try
		{
			String updateSql = "INSERT INTO combExpenditureOverThreshold " +
					"VALUES " + valuesForInsertionBuilder +
					" ON CONFLICT (expenseType, expenseArea, supplier, transactionNumber) DO NOTHING;";
			int insertCount;
			Statement statement = con.createStatement();
			
			insertCount = statement.executeUpdate(updateSql);
			System.out.println("No. of rows inserted into combExpenditureOverThreshold: " + insertCount);
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public void crossTab()
	{
		StringBuilder colNamesForCrossTabBuilder = new StringBuilder();
		ArrayList<String> listOfExpenseAreas = new ArrayList<>();
		String colNamesForCrossTab = "";
		String cell;
		
		try
		{
			Statement statement = con.createStatement();
			ResultSet rs;
			ResultSetMetaData rsMetaData;
			int colCount, i=0;
			
			/*
			Fetching all distinct values for expenseArea and generating a string
			which will be used in crosstab as column names
			*/
			String colNames = "SELECT DISTINCT expenseArea FROM combExpenditureOverThreshold";
			rs = statement.executeQuery(colNames);
			
			while (rs.next())
			{
				listOfExpenseAreas.add(rs.getString(1));
				colNamesForCrossTabBuilder.append("\"").append(rs.getString(1)).append("\"").append(" float,");
			}
			colNamesForCrossTab = colNamesForCrossTab.concat(colNamesForCrossTabBuilder.deleteCharAt(colNamesForCrossTabBuilder.length()-1).toString());

			/*
			Forming an sql query which uses PostgreSQLs crosstab() function to generate the pivoted table
			Executing the query and displaying data in a readable format
			*/
			String sql = "SELECT * FROM crosstab('SELECT expenseType, expenseArea, SUM(apAmount) " +
					"FROM combExpenditureOverThreshold GROUP BY expenseType, expenseArea ORDER BY 1', " +
					"'SELECT DISTINCT expenseArea FROM combExpenditureOverThreshold') AS ct(expenseType character varying(200),"
					+ colNamesForCrossTab + ");";
			
			rs = statement.executeQuery(sql);
			rsMetaData = rs.getMetaData();
			colCount = rsMetaData.getColumnCount();
			int listOfExpenseAreasSize = listOfExpenseAreas.size();
			
			printLines(colCount);
			System.out.format("%-48s|", "expenseType");
			while(listOfExpenseAreasSize > i)
				System.out.format("%-48s|", listOfExpenseAreas.get(i++));
			System.out.println("");
			printLines(colCount);
			printLines(colCount);

			while (rs.next())
			{
				for (i=1 ; i <= colCount; i++)
				{
					cell = rs.getString(i);
					
					if(cell!=null)
						System.out.format("%-48s|", cell);
					else
						System.out.format("%-48s|", "0");
				}
				System.out.println("");
				printLines(colCount);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public void disconnect()
	{
		try
		{
			con.close();
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public void printLines(int colCount)
	{
		int i;
		
		for (i=1 ; i < colCount; i++)
			System.out.print("---------------------------------------------------");
		System.out.println("");
	}
}

