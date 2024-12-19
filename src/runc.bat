@echo off

javac .\phase3_generator\Generator.java
javac .\phase2_parser\Parse.java
javac .\RunBack.java
javac .\RunFront.java

java RunFront %1 intermediate.atoms %3 
java RunBack intermediate.atoms %2 %3
