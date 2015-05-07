package edu.umass.cs.data_fusion.util;


import edu.umass.cs.data_fusion.data_structures.*;
import edu.umass.cs.data_fusion.data_structures.author.AuthorListAttribute;
import edu.umass.cs.data_fusion.evaluation.EvaluationMetrics;
import edu.umass.cs.data_fusion.evaluation.EvaluationMetricsWithTolerance;
import edu.umass.cs.data_fusion.evaluation.tolerance.ToleranceMatchFunction;
import edu.umass.cs.data_fusion.load.LoadBooks;
import edu.umass.cs.data_fusion.load.LoadStocks;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

public class HTMLOutput {

    private static String  correctColor = "\"green\"";
    private static String  incorrectColor = "\"red\"";
    private static String  unknownColor = "\"black\"";
    
    public static void writeHTMLOutput(String[] orderedNames, RecordCollection predicted, RecordCollection gold, String filename, boolean goldOnly,EvaluationMetrics eval) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(filename);
            writer.print(htmlOutput(orderedNames, predicted, gold, goldOnly,eval));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
    }
    
    public static String htmlOutput(String[] orderedNames, RecordCollection predicted, RecordCollection gold, boolean goldOnly,EvaluationMetrics eval) {
        StringBuffer sb = new StringBuffer(1000);
        sb.append("<html><body>\n<table border=\"3\">\n<tr>");
        sb.append("<td><b>Entity</b></td>");
        for (String attrName : orderedNames) {
            sb.append("<td><b>").append(attrName).append("</b></td>");
        }
        sb.append("</tr>");
        Set<Entity> entities = goldOnly ? gold.getEntities() : predicted.getEntities();
        for (Entity e : entities) {
            List<Record> pred = predicted.getRecords(e);
            List<Record> gld = gold.getRecords(e);
            if (pred.size() == 1 && (gld.size() == 1 || gld.size() == 0))
                sb.append(tableRow(pred.get(0), (gld.size() == 1) ? gld.get(0) : null,orderedNames,eval));
//            else
//                System.err.println("There is a problem printing the html output for entity: " + e);
        }
        sb.append("</table></html>");
        return sb.toString();
    }
    
    private static String tableRow(Record predicted, Record gold, String[] attributeOrdering, EvaluationMetrics eval) {
        
        assert gold == null || predicted.getEntity() == gold.getEntity();
        StringBuffer sb = new StringBuffer(100);
        sb.append("<tr>").append("<td>");
        sb.append(predicted.getEntity().getIdentifier()).append("</td>");
        for (String attrName : attributeOrdering) {
            sb.append("<td>");
            
            // Figure out what color to make the entry
            // If it is correct
            String color = unknownColor;
            if (predicted.hasAttribute(attrName) && gold != null && gold.hasAttribute(attrName))
                if (eval.matches(predicted.getEntity(),predicted.getAttribute(attrName),gold.getAttribute(attrName)))
                    color = correctColor;
                else
                    color = incorrectColor;
            else if (gold != null && gold.hasAttribute(attrName) && !predicted.hasAttribute(attrName))
                color = incorrectColor;
            
            String tolerance = "";
            if (eval instanceof EvaluationMetricsWithTolerance) {
                ToleranceMatchFunction func = ((EvaluationMetricsWithTolerance) eval).getToleranceMatchFunction(predicted.getEntity(),attrName);
                tolerance = func.toleranceDescription();
            }
            
            if (predicted.hasAttribute(attrName)) {
                Attribute attr = predicted.getAttribute(attrName);
                if (attr instanceof StringAttribute)
                    sb.append("<font color=").append(color).append(">").append(((StringAttribute) attr).getStringValue()).append("</font>");
                else if (attr instanceof FloatAttribute)
                    sb.append("<font color=").append(color).append(">").append(String.format("%g", ((FloatAttribute) attr).getFloatValue())).append("</font>");
                else if (attr instanceof AuthorListAttribute)
                    sb.append("<font color=").append(color).append(">").append(String.format("%s", ((AuthorListAttribute) attr).toString())).append("</font>");
            } else {
                sb.append("---");
            }

            
            if (gold != null && gold.hasAttribute(attrName)) {
                sb.append("<br><br><b>");
                Attribute attr = gold.getAttribute(attrName);
                if (attr instanceof StringAttribute)
                    sb.append(((StringAttribute) attr).getStringValue());
                else if (attr instanceof FloatAttribute)
                    sb.append(String.format("%g %s", ((FloatAttribute) attr).getFloatValue(),tolerance));
                else if (attr instanceof AuthorListAttribute)
                    sb.append(((AuthorListAttribute) attr).toString());
                sb.append("</b>");
            }  else {
                sb.append("<br><br><b>---</b>");
            }

            sb.append("</td>");
        }
        sb.append("</tr>");
        return  sb.toString();
    }
}
