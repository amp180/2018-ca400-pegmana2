package space.pegman.redditMod.service.Monitoring.Triggers;

import space.pegman.redditMod.domain.Database.Stats;

public class Util {

    public static double standardize(double count, final double mean, final double stdDev){
        return //formula for Gaussian probability of a value
                (1/(Math.sqrt(2*Math.PI)*stdDev))
                        * Math.exp(-0.5*(Math.pow((count-mean)/stdDev , 2)));
    }

    public static double standardize(double count, Stats stats){
        return standardize(count, stats.getMeanCount(), stats.getStdDev());
    }


}
