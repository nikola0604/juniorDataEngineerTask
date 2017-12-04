package jrdataengtask.controllers;

import jrdataengtask.parser.Parser;
import jrdataengtask.psql.PostgresqlUtil;

public class IngestController
{
	private Parser parser;
	private PostgresqlUtil psqlUtil;
	
	public IngestController()
	{
		this.parser = new Parser();
		this.psqlUtil = new PostgresqlUtil();
	}
	
	public void ingest()
	{
		StringBuilder valuesForInsertionBuilder = new StringBuilder();
		
		parser.constructQueries(valuesForInsertionBuilder);
		psqlUtil.connect();
		psqlUtil.importData(valuesForInsertionBuilder.toString());
		psqlUtil.crossTab();
		psqlUtil.disconnect();
	}
}
