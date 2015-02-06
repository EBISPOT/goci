package uk.ac.ebi.spot.goci.curation.service;

import org.springframework.stereotype.Service;

import java.text.DecimalFormat;

/**
 * Created by emma on 21/01/15.
 *
 * @author emma
 *         <p>
 *         These methods were directly copied from Dani's code in SNPbatchLoader
 */
@Service
public class AssociationCalculationService {

    public AssociationCalculationService() {
    }

    /**
     * This method calculates the p-value float based on the mantissa and exponent
     *
     * @param pvalueMantissa
     * @param pvalueExponent
     * @return Float, pvalue float
     */

    public Float calculatePvalueFloat(Integer pvalueMantissa, Integer pvalueExponent) {
        double calculatedPvalueFloat = (pvalueMantissa * Math.pow(10, pvalueExponent));
        Float pvalueFloat = Float.valueOf((float) calculatedPvalueFloat);
        return pvalueFloat;
    }

    /**
     * This method calculates the confidence interval based on the standard error - formatting code taken from Kent's Coldfusion code.
     *
     * @param orpc_stderr The standard error
     * @param orpc_num    The odds-ratio or beta value for the SNP
     * @return String The confidence interval for the odds-ratio or beta value
     */

    public String setRange(double orpc_stderr, double orpc_num) {
        double delta = (100000 * orpc_stderr * 1.96) / 100000;
        double low = orpc_num - delta;
        double high = orpc_num + delta;
        String lowval, highval;

        if (low < 0.001) {
            DecimalFormat df = new DecimalFormat("#.#####");
            lowval = df.format(low);
            highval = df.format(high);
        } else if (low >= 0.001 && low < 0.01) {
            DecimalFormat df = new DecimalFormat("#.####");
            lowval = df.format(low);
            highval = df.format(high);
        } else if (low >= 0.01 && low < 0.1) {
            DecimalFormat df = new DecimalFormat("#.###");
            lowval = df.format(low);
            highval = df.format(high);
        } else {
            DecimalFormat df = new DecimalFormat("#.##");
            lowval = df.format(low);
            highval = df.format(high);
        }

        String orpc_range = ("[" + lowval + "-" + highval + "]");

        return orpc_range;
    }

    /**
     * This method reverses the confidence interval for SNPs where the reciprocal odds-ratio was provided.
     *
     * @param interval The confidence interval
     * @return String The reversed confidence interval
     */
    public String reverseCI(String interval) {
        String newInterval;
        String ci = interval.replace("[", "");
        ci = ci.replace("]", "");

        if (ci.equals("NR")) {
            newInterval = interval;
        } else if (ci.matches("[\\d\\s.-]+")) {
            String[] num = ci.split("-");
            double one = Double.parseDouble(num[0].trim());
            double two = Double.parseDouble(num[1].trim());

            double high = ((100 / one) / 100);
            double low = ((100 / two) / 100);

            String lowval, highval;

            if (low < 0.001) {
                DecimalFormat df = new DecimalFormat("#.#####");
                lowval = df.format(low);
                highval = df.format(high);
            } else if (low >= 0.001 && low < 0.01) {
                DecimalFormat df = new DecimalFormat("#.####");
                lowval = df.format(low);
                highval = df.format(high);
            } else if (low >= 0.01 && low < 0.1) {
                DecimalFormat df = new DecimalFormat("#.###");
                lowval = df.format(low);
                highval = df.format(high);
            } else {
                DecimalFormat df = new DecimalFormat("#.##");
                lowval = df.format(low);
                highval = df.format(high);
            }

            newInterval = ("[" + lowval + "-" + highval + "]");
        } else {
            newInterval = null;

        }

        return newInterval;
    }

}
