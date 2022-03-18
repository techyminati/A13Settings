package com.android.settingslib.net;
/* loaded from: classes.dex */
public class NetworkCycleData {
    private long mEndTime;
    private long mStartTime;
    private long mTotalUsage;

    public long getStartTime() {
        return this.mStartTime;
    }

    public long getEndTime() {
        return this.mEndTime;
    }

    public long getTotalUsage() {
        return this.mTotalUsage;
    }

    /* loaded from: classes.dex */
    public static class Builder {
        private NetworkCycleData mObject = new NetworkCycleData();

        public Builder setStartTime(long j) {
            getObject().mStartTime = j;
            return this;
        }

        public Builder setEndTime(long j) {
            getObject().mEndTime = j;
            return this;
        }

        public Builder setTotalUsage(long j) {
            getObject().mTotalUsage = j;
            return this;
        }

        protected NetworkCycleData getObject() {
            return this.mObject;
        }

        public NetworkCycleData build() {
            return getObject();
        }
    }
}
