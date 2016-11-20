package no.stelar7.api.l4j8.tests.championmastery;

import no.stelar7.api.l4j8.basic.DataCall;
import no.stelar7.api.l4j8.basic.DataCall.DataCallBuilder;
import no.stelar7.api.l4j8.basic.constants.Champion;
import no.stelar7.api.l4j8.basic.constants.api.*;
import no.stelar7.api.l4j8.pojo.championmastery.ChampionMastery;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ChampionMasteryTest
{
	
	@SuppressWarnings("unchecked")
	@Test
	public void testMultiple()
	{
		// Generate data for call
		final DataCallBuilder builder = DataCall.builder();
		builder.asVerbose(true);
		builder.withServer(Server.EUW);
		builder.withRegion(Server.EUW);
		builder.withEndpoint(URLEndpoint.CHAMPIONMASTERY);
		builder.withURLData(Constants.PLATFORM_ID_PLACEHOLDER, Platform.EUW1.getCode());
		builder.withURLData(Constants.SUMMONER_ID_PLACEHOLDER, "19613950");
		
		// Get the response
		final List<ChampionMastery> data = (List<ChampionMastery>) builder.build();
		
		data.forEach(inner ->
			             // Make sure all the data is returned as expected
			             Assert.assertTrue("the summonerId changed?", inner.getPlayerId() == 19613950L));
	}
	
	@Test
	public void testSingle()
	{
		// Generate data for call
		final DataCallBuilder builder = DataCall.builder();
		builder.withEndpoint(URLEndpoint.CHAMPIONMASTERY_BY_ID);
		builder.asVerbose(true);
		
		builder.withServer(Server.EUW);
		builder.withRegion(Server.EUW);
		builder.withURLData(Constants.PLATFORM_ID_PLACEHOLDER, Platform.EUW1.getCode());
		builder.withURLData(Constants.SUMMONER_ID_PLACEHOLDER, "19613950");
		builder.withURLData(Constants.CHAMPION_ID_PLACEHOLDER, String.valueOf(Champion.LEONA.getId()));
		
		// Get the response
		final ChampionMastery data = (ChampionMastery) builder.build();
		
		// Make sure all the data is returned as expected
		Assert.assertTrue("ChampionId has changed?", data.getChampionId().equals(Champion.LEONA.getId()));
		Assert.assertTrue("Max level is not 5?", data.getChampionLevel() == 5);
		Assert.assertTrue("Points less than 80k", data.getChampionPoints() > 80000);
		Assert.assertTrue("0 Points?", data.getChampionPointsSinceLastLevel() > 0L);
		Assert.assertTrue("There is another level?", data.getChampionPointsUntilNextLevel() == 0L);
		Assert.assertNotNull("Chest has been removed implemented", data.getChestGranted());
		Assert.assertNotNull("Last play date DATE is NULL", data.getLastPlayTimeAsDate());
		Assert.assertEquals("last play date and DATE do not match", data.getLastPlayTime(), (Long) data.getLastPlayTimeAsDate()
		                                                                                               .toInstant()
		                                                                                               .toEpochMilli());
		Assert.assertTrue("SummonerId changed?", data.getPlayerId() == 19613950L);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testTop()
	{
		// Generate data for call
		final DataCallBuilder builder = DataCall.builder();
		builder.asVerbose(true);
		builder.withServer(Server.EUW);
		builder.withRegion(Server.EUW);
		builder.withEndpoint(URLEndpoint.CHAMPIONMASTERY_TOP);
		builder.withURLData("{platformId}", Platform.EUW1.getCode());
		builder.withURLData("{summonerId}", "19613950");
		builder.withURLParameter("count", "5");
		
		// Get the response
		final List<ChampionMastery> data = (List<ChampionMastery>) builder.build();
		
		data.forEach(inner ->
			             // Make sure all the data is returned as expected
			             Assert.assertTrue("SummonerId changed?", inner.getPlayerId() == 19613950L));
		
	}
	
}