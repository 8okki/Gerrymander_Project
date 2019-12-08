package com.cse308.server.algorithm;

import com.cse308.server.models.Cluster;
import com.cse308.server.models.Precinct;

public class Move {
    private Cluster to;
    private Cluster from;
    private Precinct precinct;

    public Move(Cluster to, Cluster from, Precinct precinct) {
        this.to = to;
        this.from = from;
        this.precinct = precinct;
    }

    public void execute() {
        from.removePrecinct(precinct);
        to.addPrecinct(precinct);
    }

    public void undo() {
        to.removePrecinct(precinct);
        from.addPrecinct(precinct);
    }
}
