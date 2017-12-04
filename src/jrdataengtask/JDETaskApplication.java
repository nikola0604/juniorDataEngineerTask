package jrdataengtask;

import jrdataengtask.controllers.IngestController;

public class JDETaskApplication
{
	private static IngestController ingestController = new IngestController();
	
	public static void main(String[] args)
	{
		ingestController.ingest();
	}
}
