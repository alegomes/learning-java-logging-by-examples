import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

import org.apache.commons.logging.LogFactory;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.SimpleLayout;

public class LoggingBenchmark {

	static int LOOP = 1000000; 
		
	public static void main(String[] args) throws Exception {

		/*
		 * Explain!
		 */
		long[][][] benchmarks = new long[6][3][4];
		
		for (int i = 0; i < 6; i++) {
			benchmarks[i] = benchmark();
		}
		
		for (long[][] benchmark : benchmarks) {
			System.out.println(
					
					// System.out numbers
					benchmark[0][0] + "," + // no sysout
					benchmark[0][1] + "," + // sysout
					benchmark[0][2] + "," + // 0 
					benchmark[0][3] + "," + // sysout slow down
					
					// JDK Logging numbers
					benchmark[1][0] + "," + // console time
					benchmark[1][1] + "," + // file time
					benchmark[1][2] + "," + // console slowdown
					benchmark[1][3] + "," + // file slowdown
					
					// Log4J numbers
					benchmark[2][0] + "," + // console time
					benchmark[2][1] + "," + // file time
					benchmark[2][2] + "," + // console slowdown
					benchmark[2][3]         // file slowdown
			);
		}
	}

	
	/**
	 * Benchmark System.out, JDK1.4 and Log4J logging APIs
	 * 
	 * @return 
	 * @throws Exception
	 */
	private static long[][] benchmark() throws Exception {
		
		long[] sysoutBenchmark = benchmarkSysOut();
		
		long[] jdkBenchmark = benchmarkJDKLogging(sysoutBenchmark[0]);
		
		long[] log4jBenchmark = benchmarkLog4J(sysoutBenchmark[0]);
		
		/*
		 * Gotta play a little more with classloaders and
		 * multiple Apache Commons Logging configurations.
		 */
		 //benchmarkCommonsLogging();
		
		return new long[][] {
				sysoutBenchmark,
				jdkBenchmark,
				log4jBenchmark
		};
		
	}
	
	/**
	 * System.out benchmark
	 * 
	 * @return long[] {timeScenarion1, timeScenario2, scenario1SlowDown, scenario2SlowDown}
	 */
	private static long[] benchmarkSysOut() {
		
		// Processamento sem mensagem de debug
		long t1 = System.currentTimeMillis();
		for (int i = 0; i < LOOP; i++) {
			Math.cos(i);
		}
		long t2 = System.currentTimeMillis();
		
		// Processamento com mensagem de debug
		long t3 = System.currentTimeMillis();
		for (int i = 0; i < LOOP; i++) {
			Math.cos(i);
			System.out.println("a");
		}
		long t4 = System.currentTimeMillis();
		
		long tempoSemSysOutPrtln = (t2 - t1)*1000;
		long tempoComSysOutPrtln = (t4 - t3)*1000;
		long sysoutSlowDown = (tempoComSysOutPrtln/tempoSemSysOutPrtln)*100;
		
		
		return new long[] {tempoSemSysOutPrtln, tempoComSysOutPrtln, 0, sysoutSlowDown};
	}

	/**
	 * JDK Logging (java.util.logging) benchmark
	 * 
	 * @param threshold 
	 * @return long[] {timeScenarion1, timeScenario2, scenario1SlowDown, scenario2SlowDown}
	 * @throws IOException
	 */
	private static long[] benchmarkJDKLogging(long threshold) throws IOException {
		
		java.util.logging.Logger jdkLogging = java.util.logging.Logger.getLogger(LoggingBenchmark.class.getName());
		
		//Agora, trocando o System.out por java.util.logging
	    long t5 = System.currentTimeMillis();
		for (int i = 0; i < LOOP; i++) {
			Math.cos(i);
			jdkLogging.warning("b");
		}
		long t6 = System.currentTimeMillis();
		
		// Ainda java.util.loggging, mas gravando em arquivo
		FileHandler jdkLoggingFile = new FileHandler("logs/javalogging.log");
		jdkLoggingFile.setFormatter(new SimpleFormatter());
		jdkLogging.setUseParentHandlers(false);
		jdkLogging.addHandler(jdkLoggingFile);
		long t7 = System.currentTimeMillis();
		for (int i = 0; i < LOOP; i++) {
			Math.cos(i);
			jdkLogging.warning("c");
		}
		long t8 = System.currentTimeMillis();
		
		long tempoComJavaLoggingConsole = (t6 - t5)*1000;
		long tempoComJavaLoggingArquivo = (t8 - t7)*1000;
		long javaLoggingConsoleSlowDown = (tempoComJavaLoggingConsole/threshold)*100;
		long javaLoggingArquivoSlowDown = (tempoComJavaLoggingArquivo/threshold)*100;
		
		return new long[] {tempoComJavaLoggingConsole, tempoComJavaLoggingArquivo, javaLoggingConsoleSlowDown, javaLoggingArquivoSlowDown};

	}

	
	/**
	 * Log4J Benchmark
	 * 
	 * @param threshold
	 * @return long[] {timeScenarion1, timeScenario2, scenario1SlowDown, scenario2SlowDown}
	 * @throws IOException
	 */
	private static long[] benchmarkLog4J(long threshold) throws IOException {
		
		org.apache.log4j.Logger log4j = org.apache.log4j.Logger.getLogger(LoggingBenchmark.class.getName());

		// Log4J, no Console
		long t9 = System.currentTimeMillis();
		log4j.addAppender(new ConsoleAppender(new SimpleLayout()));
		for (int i = 0; i < LOOP; i++) {
			Math.cos(i);
			log4j.debug("d");
		}
		long t10 = System.currentTimeMillis();
		
		// Log4J, em Arquivo
		log4j.removeAllAppenders();
		log4j.addAppender(new FileAppender(new SimpleLayout(), "logs/log4j.log"));
		long t11 = System.currentTimeMillis();
		for (int i = 0; i < LOOP; i++) {
			Math.cos(i);
			log4j.debug("e");
		}
		long t12 = System.currentTimeMillis();
		
		long tempoComLog4JConsole = (t10 - t9)*1000;
		long tempoComLog4JArquivo = (t12 - t11)*1000;
		
		long log4jConsoleSlowDown = (tempoComLog4JConsole/threshold)*100;
		long log4jArquivoSlowDown = (tempoComLog4JArquivo/threshold)*100;
		
		return new long[] {tempoComLog4JConsole, tempoComLog4JArquivo, log4jConsoleSlowDown, log4jArquivoSlowDown};
	}

	
	/**
	 * Apache Commons Logging Benchmark
	 * 
	 * @param threshold
	 * @return
	 */
	private static long[] benchmarkCommonsLogging(long threshold) {
		
		org.apache.commons.logging.Log jcl = null; 

		
		// Apache Commons Logging com java.util.logging, em Console
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.Jdk14Logger");
		System.setProperty("java.util.logging.config.file", "logging-console.properties");
		jcl = LogFactory.getLog(LoggingBenchmark.class.getName());
		long t13 = System.currentTimeMillis();
		for (int i = 0; i < LOOP; i++) {
			Math.cos(i);
			jcl.debug("f");
		}
		long t14 = System.currentTimeMillis();
		
		// Apache Commons Logging com java.util.logging, em Arquivo
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.Jdk14Logger");
		System.setProperty("java.util.logging.config.file", "logging-arquivo.properties");
//		jclFactory.release();
//		jclFactory.releaseAll();
		LogFactory.releaseAll();
		jcl = LogFactory.getLog(LoggingBenchmark.class.getName());
		long t15 = System.currentTimeMillis();
		for (int i = 0; i < LOOP; i++) {
			Math.cos(i);
			jcl.debug("g");
		}
		long t16 = System.currentTimeMillis();
		
		// Apache Commons Logging com Log4J, em Console
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.Log4JLogger");
		System.setProperty("log4j.configuration", "log4j-console.properties");
		// A definicao de variaveis de ambiente so funciona se for feita antes
		// da fabricacao do primeiro logger. Depois disso, ja era. Valera 
		// sempre a primeira definicao.
		jcl = LogFactory.getLog(LoggingBenchmark.class.getName());
		
		long t17 = System.currentTimeMillis();
		for (int i = 0; i < LOOP; i++) {
			Math.cos(i);
			jcl.debug("h");
		}
		long t18 = System.currentTimeMillis();
		
		// Apache Commons Logging com Log4J, em Arquivo
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.Log4JLogger");
		System.setProperty("log4j.configuration", "log4j-arquivo.properties");
		// A definicao de variaveis de ambiente so funciona se for feita antes
		// da fabricacao do primeiro logger. Depois disso, ja era. Valera 
		// sempre a primeira definicao.
		jcl = LogFactory.getLog(LoggingBenchmark.class.getName());
		long t19 = System.currentTimeMillis();
		for (int i = 0; i < LOOP; i++) {
			Math.cos(i);
			jcl.debug("i");
		}
		long t20 = System.currentTimeMillis();
		
		long tempoComJCLeJavaLoggingConsole = (t14 - t13)*1000;
		long tempoComJCLeJavaLoggingArquivo = (t16 - t15)*1000;
		long tempoComJCLeLog4JConsole = (t18 - t17)*1000;
		long tempoComJCLeLog4JArquivo = (t20 - t19)*1000;
		
		long jclJavaLoggingConsoleSlowDown = (tempoComJCLeJavaLoggingConsole/threshold)*100;
		long jclJavaLoggingArquivoSlowDown = (tempoComJCLeJavaLoggingArquivo/threshold)*100;
		long jclLog4JConsoleSlowDown = (tempoComJCLeLog4JConsole/threshold)*100;
		long jclLog4JArquivoSlowDown = (tempoComJCLeLog4JArquivo/threshold)*100;
		
		return null;
	}
}
