import java.util.logging.FileHandler;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.SimpleLayout;

public class LoggingBenchmark {

	private static java.util.logging.Logger javaLogging = java.util.logging.Logger.getLogger(LoggingBenchmark.class.getName());
	private static org.apache.log4j.Logger log4j = org.apache.log4j.Logger.getLogger(LoggingBenchmark.class.getName());
	
	public static void main(String[] args) throws Exception {

		// Processamento sem mensagem de debug
		long t1 = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			Math.cos(i);
		}
		long t2 = System.currentTimeMillis();
		
		// Processamento com mensagem de debug
		long t3 = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			Math.cos(i);
			System.out.println(".");
		}
		long t4 = System.currentTimeMillis();
		
		//Agora, trocando o System.out por java.util.logging
		long t5 = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			Math.cos(i);
			javaLogging.warning("x");
		}
		long t6 = System.currentTimeMillis();
		
		// Ainda java.util.loggging, mas gravando em arquivo
		javaLogging.addHandler(new FileHandler("logs/javalogging.log"));
		long t7 = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			Math.cos(i);
			javaLogging.warning("x");
		}
		long t8 = System.currentTimeMillis();
		
		// Log4J, no Console
		long t9 = System.currentTimeMillis();
		log4j.addAppender(new ConsoleAppender(new SimpleLayout()));
		for (int i = 0; i < 1000000; i++) {
			Math.cos(i);
			log4j.debug("y");
		}
		long t10 = System.currentTimeMillis();
		
		// Log4J, em Arquivo
		log4j.addAppender(new FileAppender(new SimpleLayout(), "logs/log4j.log"));
		long t11 = System.currentTimeMillis();
		for (int i = 0; i < 1000000; i++) {
			Math.cos(i);
			log4j.debug("y");
		}
		long t12 = System.currentTimeMillis();
		
		// Calculo do tempo gasto
		long tempoSemSysOutPrtln = (t2 - t1)*1000;
		long tempoComSysOutPrtln = (t4 - t3)*1000;
		long tempoComJavaLoggingConsole = (t6 - t5)*1000;
		long tempoComJavaLoggingArquivo = (t8 - t7)*1000;
		long tempoComLog4JConsole = (t10 - t9)*1000;
		long tempoComLog4JArquivo = (t12 - t11)*1000;
		
		long sysoutSlowDown = (tempoComSysOutPrtln/tempoSemSysOutPrtln)*100;
		long javaLoggingConsoleSlowDown = (tempoComJavaLoggingConsole/tempoSemSysOutPrtln)*100;
		long javaLoggingArquivoSlowDown = (tempoComJavaLoggingArquivo/tempoSemSysOutPrtln)*100;
		long log4jConsoleSlowDown = (tempoComLog4JConsole/tempoSemSysOutPrtln)*100;
		long log4jArquivoSlowDown = (tempoComLog4JArquivo/tempoSemSysOutPrtln)*100;
		
		System.out.println("Sem System.out.println: " + tempoSemSysOutPrtln + " segs");
		System.out.println("Com System.out.println: " + tempoComSysOutPrtln + " segs (" + sysoutSlowDown + "% mais lento)");
		System.out.println("Com java.util.logging no Console: " + tempoComJavaLoggingConsole + " segs (" + javaLoggingConsoleSlowDown + "% mais lento)");
		System.out.println("Com java.util.logging em Arquivo: " + tempoComJavaLoggingArquivo + " segs (" + javaLoggingArquivoSlowDown + "% mais lento)");
		System.out.println("Com Log4J no Console: " + tempoComLog4JConsole + " segs (" + log4jConsoleSlowDown + "% mais lento)");
		System.out.println("Com Log4J no Arquivo: " + tempoComLog4JArquivo + " segs (" + log4jArquivoSlowDown + "% mais lento)");
	}

}
