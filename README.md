# data_fusion
__Compilation__

To compile the code install ``maven`` and then run the command:

```mvn clean package```

__Obtaining the Data__

Please email one of the authors to obtain a copy of the data we used in our report. While the data is publically available, we did not want to host the data without the original authors permission. 

__Running the Code__

The package, ```main.java.edu.umass.cs.data_fusion.experiment.methods```, contains programs to run our experiments. Each of these classes has a main method to run the experiment. For instance, one can run the CRH experiment with:

```
java -Xmx3G -cp target/data_fusion-0.1-SNAPSHOT.jar main.java.edu.umass.cs.data_fusion.experiment.methods.RunCRH
```
