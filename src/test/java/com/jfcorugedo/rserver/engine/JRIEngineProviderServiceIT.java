package com.jfcorugedo.rserver.engine;

import static com.jfcorugedo.rserver.common.collection.CollectionUtils.newList;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPGenericVector;
import org.rosuda.REngine.REXPInteger;
import org.rosuda.REngine.REXPString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Ignore("These tests require R environment installed")
public class JRIEngineProviderServiceIT {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(JRIEngineProviderServiceIT.class);
	
	public static final double[] TEST_POPULATION = new double[20];
	static{
		TEST_POPULATION[0] = 10;
		TEST_POPULATION[1] = 5;
		TEST_POPULATION[2] = 20;
		TEST_POPULATION[3] = 12;
		TEST_POPULATION[4] = 4;
		TEST_POPULATION[5] = 11;
		TEST_POPULATION[6] = 23;
		TEST_POPULATION[7] = 0;
		TEST_POPULATION[8] = 12;
		TEST_POPULATION[9] = 6;
		TEST_POPULATION[10] = 6;
		TEST_POPULATION[11] = 11;
		TEST_POPULATION[12] = 14;
		TEST_POPULATION[13] = 13;
		TEST_POPULATION[14] = 16;
		TEST_POPULATION[15] = 18;
		TEST_POPULATION[16] = 19;
		TEST_POPULATION[17] = 12;
		TEST_POPULATION[18] = 0;
		TEST_POPULATION[19] = 4;
	}
	
	public static final double[] TEST_BIG_POPULATION = new double[200];
	static{
		for(int i = 0 ; i < 10 ; i++) {
			for(int j = 0 ; j < 20 ; j++) {
				TEST_BIG_POPULATION[i*20+j] = TEST_POPULATION[j];
			}
		}
	}
	
	private static JRIEngineProviderService providerService;
	
	@BeforeClass
	public static void setUp() {
		providerService = new JRIEngineProviderService();
		providerService.setBlockFunction("source(\"/Users/jfcorugedo/Documents/git/kmd/kmd-math/blockFunction.R\")");
		providerService.setUpR();
	}

	@Test
	public void testBlockFunction() throws Exception{
	
		for(int i = 0 ; i < 100 ; i++) {
			REXP result = providerService.blockFunction(new REXPInteger(generateIds(200)), new REXPDouble(TEST_BIG_POPULATION));
			if(LOGGER.isInfoEnabled()) {
				LOGGER.info(blockResultToString(result));
			}
		}
	}
	
	@Test
	@Ignore
	public void testBlockDiscreteFunction() {
	
		REXP result = providerService.blockDiscreteFunction(new REXPInteger(generateIds(200)), new REXPString(generateRandomCities(200)));
		
		if(LOGGER.isInfoEnabled()) {
			LOGGER.info(blockResultToString(result));
		}
	}
	
	private String[] generateRandomCities(int sampleSize) {
		EnumeratedDistribution<String> cityDistribution = 
				new EnumeratedDistribution<String>(
													newList(
														new Pair<String, Double>("Madrid", 1.0),
														new Pair<String, Double>("London", 1.0),
														new Pair<String, Double>("New York", 1.0),
														new Pair<String, Double>("Boston", 1.0),
														new Pair<String, Double>("Paris", 1.0),
														new Pair<String, Double>("Rome", 1.0),
														new Pair<String, Double>("Oslo", 1.0)
													)); 
		return Arrays.stream(cityDistribution.sample(sampleSize)).map(city -> (String)city).collect(Collectors.toList()).toArray(new String[0]);
	}

	private int[] generateIds(int length) {
		int[] ids = new int[length];
		for(int i = 0 ; i < length ; i++) {
			ids[i] = i;
		}
		return ids;
	}
	
	private String blockResultToString(REXP result) {
		REXPGenericVector vector = (REXPGenericVector) result;
		String[] unit1 = ((REXPString)vector.asList().get(0)).asStrings();
		String[] unit2 = ((REXPString)vector.asList().get(1)).asStrings();
		double[] distance = ((REXPDouble)vector.asList().get(2)).asDoubles(); 
		
		StringBuilder sb = new StringBuilder("\n");
		
		for(int i = 0 ; i < unit1.length ; i++) {
			sb.append(unit1[i]).append("     ").append(unit2[i]).append("     ").append(distance[i]).append("\n");
		}
		
		return sb.toString();
	}
}
