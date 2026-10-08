package cn.ZeroEngine.Engine.api.v3.feature.tick;

import cn.ZeroEngine.Engine.api.v3.SF;

public abstract class STick {

    private long taskId = -1;
    private boolean sync = false;

    public abstract void onCode(long sfTick);

    public STick sync() {
        this.sync = true;
        return this;
    }

    public STick async() {
        this.sync = false;
        return this;
    }

    public STick timer(long periodTicks) {
        return timer(0, periodTicks);
    }

    public STick timer(long delayTicks, long periodTicks) {
        start(delayTicks, periodTicks, true);
        return this;
    }

    public STick later(long delayTicks) {
        start(delayTicks, 0, false);
        return this;
    }

    public STick now() {
        start(0, 0, false);
        return this;
    }

    private void start(long delay, long period, boolean repeat) {
        cancel();
        SF sf = SF.sf();
        if (sf == null) return;
        TickManager tm = sf.tick();
        if (tm == null) return;

        if (sync) {
            if (repeat) {
                tm.runSyncTimer(() -> safeRun(tm.now()), Math.max(1, delay), Math.max(1, period));
            } else {
                tm.runSyncLater(() -> safeRun(tm.now()), Math.max(1, delay));
            }
            return;
        }

        if (repeat) {
            taskId = tm.runTimer(this::onCode, delay, period);
        } else {
            taskId = tm.runLater(this::onCode, delay);
        }
    }

    private void safeRun(long tick) {
        try {
            onCode(tick);
        } catch (Throwable t) {
            SF sf = SF.sf();
            if (sf != null) sf.error("[STick] error at sfTick=" + tick, t);
        }
    }

    public void cancel() {
        if (taskId != -1) {
            SF sf = SF.sf();
            if (sf != null) {
                TickManager tm = sf.tick();
                if (tm != null) tm.cancel(taskId);
            }
            taskId = -1;
        }
    }

    public boolean isRunning() {
        return taskId != -1;
    }
}
