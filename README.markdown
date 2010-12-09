Learnin Java Logging by Examples
================================

This repository is composed by a set of Eclipse projects.

cli-logging.benchmark
---------------------
This is a very simple code to benchmark System.out.println, java.util.logging, Apache Log4J and Apache Commons Logging performance.
 
Sem System.out.println: 65000 segs
Com System.out.println: 7159000 segs (11000% mais lento)
Com java.util.logging no Console: 72558000 segs (111600% mais lento)
Com java.util.logging em Arquivo: 128846000 segs (198200% mais lento)
Com Log4J no Console: 9128000 segs (14000% mais lento)
Com Log4J no Arquivo: 16354000 segs (25100% mais lento)
