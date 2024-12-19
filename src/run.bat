@echo off

java RunFront %1 intermediate.atoms %3 
java RunBack intermediate.atoms %2 %3
