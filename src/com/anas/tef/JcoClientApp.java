package com.anas.tef;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoAttributes;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoFunctionTemplate;
import com.sap.conn.jco.JCoListMetaData;
import com.sap.conn.jco.JCoMetaData;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoRecord;
import com.sap.conn.jco.JCoRecordMetaData;
import com.sap.conn.jco.JCoRepository;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.ext.DestinationDataProvider;

public class JcoClientApp {

	static final String DESTINATION_NAME = "DESTINATION_WITHOUT_POOL";

	static void createDestinationDataFile(String destinationName,
			Properties connectionProperties) {
		File destCfg = new File(destinationName + ".jcoDestination");
		try {
			FileOutputStream fos = new FileOutputStream(destCfg, false);
			connectionProperties.store(fos, "for tests only !");
			fos.close();
		} catch (Exception e) {
			throw new RuntimeException(
					"Unable to create the destination files", e);
		}
	}

	public static void main(String[] args) throws JCoException {
		Properties connectionProperties = new Properties();
		JCoDestination destination = null;
		JCoAttributes attrs = null;
		JCoRepository rep = null;
		JCoFunction jcofunc = null;
		
		connectionProperties.setProperty(DestinationDataProvider.JCO_ASHOST,
				"192.168.0.10");
		connectionProperties.setProperty(DestinationDataProvider.JCO_SYSNR,
				"00");
		connectionProperties.setProperty(DestinationDataProvider.JCO_CLIENT,
				"001");
		connectionProperties.setProperty(DestinationDataProvider.JCO_USER,
				"BCUSER");
		connectionProperties.setProperty(DestinationDataProvider.JCO_PASSWD,
				"MySAP85i");
		connectionProperties
				.setProperty(DestinationDataProvider.JCO_LANG, "de");
		createDestinationDataFile(DESTINATION_NAME, connectionProperties);

		destination = JCoDestinationManager.getDestination(DESTINATION_NAME);

		if (destination != null)
			System.out.println("Destination is not null!!!");

		attrs = destination.getAttributes();
		String str = attrs.getClient() + "\n" + attrs.getDestination() + "\n"
				+ attrs.getHost();
		str = str + "\n" + attrs.getLanguage() + "\n" + attrs.getSystemID()
				+ "\n" + attrs.getSystemNumber();
		System.out.println(str);
		rep = destination.getRepository();
		if (rep != null)

			System.out.println("Repository is not null!!! \t" + rep.getName());

		jcofunc = rep.getFunctionTemplate("BAPI_SFLIGHT_GETLIST").getFunction();
		JCoRecordMetaData rec = rep.getStructureDefinition("BAPISFLIST");
		int count = rec.getFieldCount();
		for (int i = 0; i < count; i++) {
			System.out.println(i + 1 + ": " + rec.getDescription(i) + "\t"
					+ rec.getName(i));
		}
		// System.out.println(count);
		if (jcofunc != null) {
			System.out.println("BAPI_SFLIGHT_GETLIST"
					+ " is found in SAP(R).\t" + jcofunc.getName());
		}

		jcofunc.getImportParameterList().setValue("FROMCOUNTRYKEY", "DE");
		jcofunc.getImportParameterList().setValue("FROMCITY", "FRANKFURT");
		jcofunc.getImportParameterList().setValue("TOCOUNTRYKEY", "US");
		jcofunc.getImportParameterList().setValue("TOCITY", "NEW YORK");
		jcofunc.getImportParameterList().setValue("MAXREAD", 12);

		try {
			jcofunc.execute(destination);
		} catch (AbapException e) {
			System.out.println(e.toString());

			return;
		}
		JCoTable table = jcofunc.getTableParameterList().getTable("FLIGHTLIST");

		if (table != null) {
			System.out.println("Table is not null ");
		}

		System.out.println("Anzahl der Datensaetze: " + table.getNumRows());
		System.out
				.println("====================================================================================================");
		System.out.println("CARRID" + "||\t" + "CONNID" + "||\t" + "FLDATE" + "||\t"
				+ "AIRPFROM" + "||\t" + "AIRPTO" + "||\t" + "DEPTIME" + "||\t"
				+ "SEATSMAX" + "||\t" + "SEATSOCC" + "||\t");
		System.out
		.println("============================================================================================================");
		
		for (int i = 0; i < table.getNumRows(); i++)

		{
			//int seatsmax = 0,seatsocc = 0, free = 0;
			table.setRow(i);
			for (int j = 0; j < rec.getFieldCount(); j++) {
				/*
				 * int free = table.getInt("SEATSMAX") -
				 * table.getInt("SEATSOCC") ;
				 * System.out.println(table.getString(
				 * "CARRID")+"\t"+table.getString("CONNID")+"\t"+
				 * table.getString
				 * ("FLDATE")+"\t"+table.getString("AIRPFROM")+"\t"
				 * +table.getString("AIRPTO")+"\t"+
				 * table.getString("DEPTIME")+"\t"
				 * +table.getInt("SEATSMAX")+"\t"+
				 * table.getInt("SEATSOCC")+"\t"+free);
				 */
				
				System.out.print(table.getString(rec.getName(j))+" ||\t");
				
				
			}
			System.out.println();
			System.out
			.println("==============================================================================");
		}
	}
}
