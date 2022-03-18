package androidx.constraintlayout.solver.widgets.analyzer;

import androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer;
import java.util.ArrayList;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class RunGroup {
    public static int index;
    int direction;
    WidgetRun firstRun;
    int groupIndex;
    WidgetRun lastRun;
    public int position = 0;
    public boolean dual = false;
    ArrayList<WidgetRun> runs = new ArrayList<>();

    public RunGroup(WidgetRun widgetRun, int i) {
        this.firstRun = null;
        this.lastRun = null;
        int i2 = index;
        this.groupIndex = i2;
        index = i2 + 1;
        this.firstRun = widgetRun;
        this.lastRun = widgetRun;
        this.direction = i;
    }

    public void add(WidgetRun widgetRun) {
        this.runs.add(widgetRun);
        this.lastRun = widgetRun;
    }

    private long traverseStart(DependencyNode dependencyNode, long j) {
        WidgetRun widgetRun = dependencyNode.run;
        if (widgetRun instanceof HelperReferences) {
            return j;
        }
        int size = dependencyNode.dependencies.size();
        long j2 = j;
        for (int i = 0; i < size; i++) {
            Dependency dependency = dependencyNode.dependencies.get(i);
            if (dependency instanceof DependencyNode) {
                DependencyNode dependencyNode2 = (DependencyNode) dependency;
                if (dependencyNode2.run != widgetRun) {
                    j2 = Math.max(j2, traverseStart(dependencyNode2, dependencyNode2.margin + j));
                }
            }
        }
        if (dependencyNode != widgetRun.start) {
            return j2;
        }
        long wrapDimension = j + widgetRun.getWrapDimension();
        return Math.max(Math.max(j2, traverseStart(widgetRun.end, wrapDimension)), wrapDimension - widgetRun.end.margin);
    }

    private long traverseEnd(DependencyNode dependencyNode, long j) {
        WidgetRun widgetRun = dependencyNode.run;
        if (widgetRun instanceof HelperReferences) {
            return j;
        }
        int size = dependencyNode.dependencies.size();
        long j2 = j;
        for (int i = 0; i < size; i++) {
            Dependency dependency = dependencyNode.dependencies.get(i);
            if (dependency instanceof DependencyNode) {
                DependencyNode dependencyNode2 = (DependencyNode) dependency;
                if (dependencyNode2.run != widgetRun) {
                    j2 = Math.min(j2, traverseEnd(dependencyNode2, dependencyNode2.margin + j));
                }
            }
        }
        if (dependencyNode != widgetRun.end) {
            return j2;
        }
        long wrapDimension = j - widgetRun.getWrapDimension();
        return Math.min(Math.min(j2, traverseEnd(widgetRun.start, wrapDimension)), wrapDimension - widgetRun.start.margin);
    }

    public long computeWrapSize(ConstraintWidgetContainer constraintWidgetContainer, int i) {
        WidgetRun widgetRun;
        DependencyNode dependencyNode;
        DependencyNode dependencyNode2;
        WidgetRun widgetRun2 = this.firstRun;
        long j = 0;
        if (widgetRun2 instanceof ChainRun) {
            if (((ChainRun) widgetRun2).orientation != i) {
                return 0L;
            }
        } else if (i == 0) {
            if (!(widgetRun2 instanceof HorizontalWidgetRun)) {
                return 0L;
            }
        } else if (!(widgetRun2 instanceof VerticalWidgetRun)) {
            return 0L;
        }
        DependencyNode dependencyNode3 = (i == 0 ? constraintWidgetContainer.horizontalRun : constraintWidgetContainer.verticalRun).start;
        DependencyNode dependencyNode4 = (i == 0 ? constraintWidgetContainer.horizontalRun : constraintWidgetContainer.verticalRun).end;
        boolean contains = widgetRun2.start.targets.contains(dependencyNode3);
        boolean contains2 = this.firstRun.end.targets.contains(dependencyNode4);
        long wrapDimension = this.firstRun.getWrapDimension();
        if (contains && contains2) {
            long traverseStart = traverseStart(this.firstRun.start, 0L);
            long traverseEnd = traverseEnd(this.firstRun.end, 0L);
            long j2 = traverseStart - wrapDimension;
            WidgetRun widgetRun3 = this.firstRun;
            int i2 = widgetRun3.end.margin;
            if (j2 >= (-i2)) {
                j2 += i2;
            }
            int i3 = widgetRun3.start.margin;
            long j3 = ((-traverseEnd) - wrapDimension) - i3;
            if (j3 >= i3) {
                j3 -= i3;
            }
            float biasPercent = widgetRun3.widget.getBiasPercent(i);
            if (biasPercent > 0.0f) {
                j = (((float) j3) / biasPercent) + (((float) j2) / (1.0f - biasPercent));
            }
            float f = (float) j;
            long j4 = (f * biasPercent) + 0.5f + wrapDimension + (f * (1.0f - biasPercent)) + 0.5f;
            WidgetRun widgetRun4 = this.firstRun;
            return (widgetRun4.start.margin + j4) - widgetRun4.end.margin;
        } else if (contains) {
            return Math.max(traverseStart(this.firstRun.start, dependencyNode2.margin), this.firstRun.start.margin + wrapDimension);
        } else if (contains2) {
            return Math.max(-traverseEnd(this.firstRun.end, dependencyNode.margin), (-this.firstRun.end.margin) + wrapDimension);
        } else {
            return (widgetRun.start.margin + this.firstRun.getWrapDimension()) - this.firstRun.end.margin;
        }
    }
}
