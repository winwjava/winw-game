package winw.game.quant.analysis;

import java.util.Arrays;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 * 趋势线
 * 
 * @author winw
 *
 */
@Deprecated
public class TrendLine {

	public static void main(String[] args) {
		double[][] data = { { 1, 3 }, {2, 5 }, {3, 7 }, {4, 14 }, {5, 11 }};
		SimpleRegression regression = new SimpleRegression();
		regression.addData(data);
		
		System.out.println(regression.getIntercept());
		// displays intercept of regression line

		System.out.println(regression.getSlope());
		// displays slope of regression line

		System.out.println(regression.getSlopeStdErr());
		// displays slope standard error
		System.out.println(regression.predict(1.5d));
				// displays predicted y value for x = 1.5
	}
	public static void main1(String[] args) {
		OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
		double[] y = new double[] { 11.0, 12.0, 13.0, 14.0, 15.0, 16.0 };
		double[][] x = new double[6][];
		x[0] = new double[] { 0, 0, 0, 0, 0 };
		x[1] = new double[] { 2.0, 0, 0, 0, 0 };
		x[2] = new double[] { 0, 3.0, 0, 0, 0 };
		x[3] = new double[] { 0, 0, 4.0, 0, 0 };
		x[4] = new double[] { 0, 0, 0, 5.0, 0 };
		x[5] = new double[] { 0, 0, 0, 0, 6.0 };
		regression.newSampleData(y, x);

		double[] beta = regression.estimateRegressionParameters();

		double[] residuals = regression.estimateResiduals();

		double[][] parametersVariance = regression.estimateRegressionParametersVariance();

		double regressandVariance = regression.estimateRegressandVariance();

		double rSquared = regression.calculateRSquared();

		double sigma = regression.estimateRegressionStandardError();

		System.out.println(Arrays.asList(beta));
		System.out.println(Arrays.asList(residuals));
		System.out.println(Arrays.asList(parametersVariance));
		System.out.println(regressandVariance);
		System.out.println(rSquared);
		System.out.println(sigma);
	}

}
