package org.allsparks.echo;

import org.allsparks.echo.observe.EchoDecisionRecord;

public final class EchoDecision {
    private final EchoDecisionRecord record;

    public EchoDecision(EchoDecisionRecord record) {
        this.record = record;
    }

    public EchoDecisionRecord record() {
        return record;
    }
}
