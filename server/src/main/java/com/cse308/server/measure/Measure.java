package com.cse308.server.measure;


import com.cse308.server.models.Cluster;
import com.cse308.server.models.Precinct;
import com.cse308.server.models.State;
import org.locationtech.jts.geom.*;

/**
 *
 * @author Mavericks
 */
public enum Measure implements MeasureFunction {

    // Types of measure

    // Fast
    PARTISAN_FAIRNESS{
        @Override
        /**
         * Partisan fairness:
         * 100% - underrepresented party's winning margin
         * OR
         * underrepresented party's losing margin
         * (We want our underrepresented party to either win by a little or lose by a lot - fewer wasted votes)
         */
        public double calculateMeasure(Cluster cluster) {
            int totalVote = 0;
            int totalRepVote = 0;
            int totalClusters = 0;
            int totalRepClusters = 0;
            State state = cluster.getState();
            for (Cluster c : state.getClusters()) {
                totalVote += c.getRepVote() + c.getDemVote();
                totalRepVote += c.getRepVote();
                totalClusters += 1;
                if (c.getRepVote() > c.getDemVote()) {
                    totalClusters += 1;
                }
            }
            int idealDistrictChange = ((int) Math.round(totalClusters * ((1.0 * totalRepVote) / totalVote))) - totalRepClusters;
            // End temporary section
            if (idealDistrictChange == 0) {
                return 1.0;
            }
            int rv = cluster.getRepVote();
            int dv = cluster.getDemVote();
            int tv = rv + dv;
            int margin = rv - dv;
            if (tv == 0)
                return 1.0;
            int win_v = Math.max(rv, dv);
            int loss_v = Math.min(rv, dv);
            int inefficient_V;
            if (idealDistrictChange * margin > 0) {
                inefficient_V = win_v - loss_v;
            } else {
                inefficient_V = loss_v;
            }
            return 1.0 - ((inefficient_V * 1.0) / tv);        }
    },

    // Slow
    REOCK_COMPACTNESS {
        @Override
        public double calculateMeasure(Cluster cluster) {
            MultiPolygon shape = cluster.getMulti();
            Geometry boundingCircle = cluster.getBoundingCircle();
            return shape.getArea() / boundingCircle.getArea();
        }
    },

    // Very slow
    CONVEX_HULL_COMPACTNESS{
        @Override
        public double calculateMeasure(Cluster cluster) {
            MultiPolygon shape = cluster.getMulti();
            Geometry convexHull = cluster.getConvexHull();
            return shape.getArea() / convexHull.getArea();
        }
    },

    // Fast, Doesn't anneal
    EDGE_COMPACTNESS {
        @Override
        /**
            Compactness:
            perimeter / (circle perimeter for same area)
        */
        public double calculateMeasure(Cluster cluster) {
            double internalEdges = cluster.getInternalEdges();
            double totalEdges = internalEdges + cluster.getExternalEdges();
            if (totalEdges == 0)
                return 0;
            return internalEdges / totalEdges;
        }
    },

    // Fast, Doesn't anneal
    EFFICIENCY_GAP {
        @Override
        /**
         * Wasted votes:
         * Statewide: abs(Winning party margin - losing party votes)
         */
        public double calculateMeasure(Cluster cluster) {
            int iv_r = 0;
            int iv_d = 0;
            int tv = 0;
            State state = cluster.getState();
            for (Cluster c : state.getClusters()) {
                int rv = c.getRepVote();
                int dv = c.getDemVote();
                if (rv > dv) {
                    iv_d += dv;
                    iv_r += (rv - dv);
                } else if (dv > rv) {
                    iv_r += rv;
                    iv_d += (dv - rv);
                }
                tv += rv;
                tv += dv;
            }
            return 1.0 - ((Math.abs(iv_r - iv_d) * 1.0) / tv);
        }
        /**
         * Wasted votes:
         * abs(Winning party margin - losing party votes)
         */
        public double rateEfficiencyGap(Cluster cluster) {
            int rv = cluster.getRepVote();
            int dv = cluster.getDemVote();
            int tv = rv + dv;
            if (tv == 0) {
                return 1.0;
            }
            int win_v = Math.max(rv, dv);
            int loss_v = Math.min(rv, dv);
            int inefficient_V = Math.abs(loss_v - (win_v - loss_v));
            return 1.0 - ((inefficient_V * 1.0) / tv);
        }
    },

    // Fast, Doesn't anneal
    POPULATION_EQUALITY {
        @Override
        public double calculateMeasure(Cluster cluster) {
            // we will square before we return--this gives lower measure value for greater error
            State state = cluster.getState();
            int idealPopulation = state.getPopulation() / state.getClusters().size();
            int truePopulation = cluster.getPopulation();
            if (idealPopulation >= truePopulation) {
                return 1 - Math.pow(Math.abs(idealPopulation - (double)truePopulation) / idealPopulation, 1.25);
            }
            return 1 - Math.pow(Math.abs(truePopulation - (double)idealPopulation) / idealPopulation, 1.25);
        }
    },

    // Very slow - NaN
    POPULATION_HOMOGENEITY {
        @Override
        /**
         * calculate square error of population, normalized to 0,1
         */
        public double calculateMeasure(Cluster cluster) {
            if (cluster.getPrecincts().size() == 0)
                return 0;
            double sum = cluster.getPrecincts().stream().mapToDouble(Precinct::getPopulationDensity).sum();
            final double mean = sum / cluster.getPrecincts().size();
            double sqError = cluster.getPrecincts().stream().mapToDouble((precinct) -> (Math.pow(precinct.getPopulationDensity() - mean, 2))).sum();
            sqError /= cluster.getPrecincts().size();

            return 1.0 - Math.tanh(Math.sqrt(sqError/mean)/(mean));
        }
    },

    // Fast - NaN
    COMPETITIVENESS {
        @Override
        /**
         * COMPETITIVENESS:
         * 1.0 - margin of victory
         */
        public double calculateMeasure(Cluster cluster) {
            int rv = cluster.getRepVote();
            int dv = cluster.getDemVote();
            System.out.println(rv + " " + dv);
            return 1.0 - (((double) Math.abs(rv - dv)) / (rv + dv));
        }
    },

    // Fast
    GERRYMANDER_REPUBLICAN {
        @Override
        /**
         * GERRYMANDER_REPUBLICAN:
         * Partisan fairness, but always working in the REP's favor
         */
        public double calculateMeasure(Cluster cluster) {
            int rv = cluster.getRepVote();
            int dv = cluster.getDemVote();
            int tv = rv + dv;
            int margin = rv - dv;
            if (tv == 0) {
                return 1.0;
            }
            int win_v = Math.max(rv, dv);
            int loss_v = Math.min(rv, dv);
            int inefficient_V;
            if (margin > 0) {
                inefficient_V = win_v - loss_v;
            } else {
                inefficient_V = loss_v;
            }
            return 1.0 - ((inefficient_V * 1.0) / tv);
        }
    },

    // Fast
    GERRYMANDER_DEMOCRATIC {
        @Override
        /**
         * GERRYMANDER_DEMOCRAT:
         * Partisan fairness, but always working in the DEM's favor
         */
        public double calculateMeasure(Cluster cluster) {
            int rv = cluster.getRepVote();
            int dv = cluster.getDemVote();
            int tv = rv + dv;
            int margin = dv - rv;
            if (tv == 0) {
                return 1.0;
            }
            int win_v = Math.max(rv, dv);
            int loss_v = Math.min(rv, dv);
            int inefficient_V;
            if (margin > 0) {
                inefficient_V = win_v - loss_v;
            } else {
                inefficient_V = loss_v;
            }
            return 1.0 - ((inefficient_V * 1.0) / tv);
        }
    };

    // Weight for each measure type
    public double weight;

    public abstract double calculateMeasure(Cluster cluster);

}
