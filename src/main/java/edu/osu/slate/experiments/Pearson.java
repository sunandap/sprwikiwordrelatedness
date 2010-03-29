/* Copyright 2010 Speech and Language Technologies Lab, The Ohio State University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.osu.slate.experiments;

import java.util.Vector;

/**
 *
 * @author weale
 *
 */
public class Pearson {

/**
  * Calculate the Pearson correlation coefficient of X and Y.
  *
  * @param X original human relatedness values
  * @param Y metric relatedness values
  * @return
  */
  public static double GetCorrelation(Vector<Double> xVect, Vector<Double> yVect) {
    double meanX = 0.0, meanY = 0.0;
    for(int i = 0; i < xVect.size(); i++)
    {
        meanX += xVect.elementAt(i);
        meanY += yVect.elementAt(i);
    }

    meanX /= xVect.size();
    meanY /= yVect.size();

    double sumXY = 0.0, sumX2 = 0.0, sumY2 = 0.0;
    for(int i = 0; i < xVect.size(); i++)
    {
      sumXY += ((xVect.elementAt(i) - meanX) * (yVect.elementAt(i) - meanY));
      sumX2 += Math.pow(xVect.elementAt(i) - meanX, 2.0);
      sumY2 += Math.pow(yVect.elementAt(i) - meanY, 2.0);
    }

    return (sumXY / (Math.sqrt(sumX2) * Math.sqrt(sumY2)));
  }//end: Pearson2(X,Y)

  /**
   * Calculate the Pearson correlation coefficient of X and log(Y).
   * 
   * @param X original human relatedness values
   * @param Y non-log metric relatedness values 
   * @return
   *
   private static double LogPearson(Vector<Double> X, Vector<Double> Y) {
		
		for(int i = 0; i < Y.size(); i++) {
			//System.out.println(Math.log10(Y.elementAt(i)));
			Y.set(i, Math.log10(Y.elementAt(i)));
		}
		
		double meanX = 0.0, meanY = 0.0;
		for(int i = 0; i < X.size(); i++) {
			meanX += X.elementAt(i);
			meanY += Y.elementAt(i);
		}
		meanX /= X.size();
		meanY /= Y.size();
		
		double sumXY = 0.0, sumX2 = 0.0, sumY2 = 0.0;
		for(int i = 0; i < X.size(); i++) {
			sumXY += ((X.elementAt(i) - meanX) * (Y.elementAt(i) - meanY));
			sumX2 += Math.pow(X.elementAt(i) - meanX, 2.0);
			sumY2 += Math.pow(Y.elementAt(i) - meanY, 2.0);
		}
		
		return (sumXY / (Math.sqrt(sumX2) * Math.sqrt(sumY2)));
	}//end: LogPearson2(X,Y)
	*/
  
	/**
	 * 
	 * @param X
	 * @param Y
	 * @return
	 *
	private static double WeightedPearson(Vector<Double> X, Vector<Double> Y) {
		
		double sumX = 0.0, sumY = 0.0;
		for(int i = 0; i < X.size(); i++) {
			sumX += X.elementAt(i);
			sumY += Y.elementAt(i);
		}
		
		double wSumX = 0.0, wSumY = 0.0;
		for(int i = 0; i < X.size(); i++) {
			wSumX += X.elementAt(i) * X.elementAt(i);
			wSumY += X.elementAt(i) * Y.elementAt(i);
		}
		
		double meanX = sumX / wSumX;
		double meanY = sumY / wSumX;
		
		double sumXY = 0.0, sumX2 = 0.0, sumY2 = 0.0;
		for(int i = 0; i < X.size(); i++) {
			sumXY += X.elementAt(i) * ((X.elementAt(i) - meanX) * (Y.elementAt(i) - meanY));
			sumX2 += X.elementAt(i) * Math.pow(X.elementAt(i) - meanX, 2.0);
			sumY2 += X.elementAt(i) * Math.pow(Y.elementAt(i) - meanY, 2.0);
		}
		
		sumXY /= wSumX;
		sumX2 /= wSumX;
		sumY2 /= wSumX;
		
		return (sumXY / (Math.sqrt(sumX2)*Math.sqrt(sumY2)));
	}*/
	
	/*
	 * 
	 * @param X
	 * @param Y
	 * @param alpha
	 * @return
	 * 
	private static double[] absolutePositive(Vector<Double> X, Vector<Double> Y, double alpha) {
		double maxX = X.elementAt(0);
		for(int i = 0; i<X.size(); i++) {
			maxX = Math.max(maxX, X.elementAt(i));
		}
		
		double minValid = maxX * alpha;
		double minYValid = 10.0;
		for(int i = 0; i < X.size(); i++) {
			if(X.elementAt(i) > minValid) {
				minYValid = Math.min(minYValid, Y.elementAt(i));
			}
		}
		
		int numPositive = 0;
		int numNegative = 0;
		
		int truePositive = 0;
		int falsePositive = 0;
		int trueNegative = 0;
		int falseNegative = 0;
		
		for(int i=0; i<X.size(); i++) {
			
			
			if(X.elementAt(i) > minValid) {
				numPositive++;
				if(Y.elementAt(i) > minYValid) {
					truePositive++;
				} else {
					falseNegative++;
				}
			} else {
				numNegative++;
				if(Y.elementAt(i) > minYValid) {
					falsePositive++;
				} else {
					trueNegative++;
				}
			}
		}
		
		double truePositiveRate=truePositive / ((double) numPositive);
		double falsePositiveRate=falsePositive / ((double) numPositive);
		double acc = (truePositive+trueNegative) / ((double) X.size());
		
		double[] ret = {truePositiveRate, falsePositiveRate, acc};
		return ret;
	}*/
}
