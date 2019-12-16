package com.cse308.server.algorithm;

import com.cse308.server.models.Cluster;
import com.cse308.server.models.Precinct;

public class Move {
    private Precinct precinct;
    private Cluster from;
    private Cluster to;

    public Precinct getPrecinct() { return precinct; }

    public Move(Precinct precinct, Cluster from, Cluster to) {
        this.precinct = precinct;
        this.from = from;
        this.to = to;
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
