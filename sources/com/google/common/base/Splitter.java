package com.google.common.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes2.dex */
public final class Splitter {
    private final int limit;
    private final boolean omitEmptyStrings;
    private final Strategy strategy;
    private final CharMatcher trimmer;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public interface Strategy {
        Iterator<String> iterator(Splitter splitter, CharSequence charSequence);
    }

    private Splitter(Strategy strategy) {
        this(strategy, false, CharMatcher.none(), Integer.MAX_VALUE);
    }

    private Splitter(Strategy strategy, boolean z, CharMatcher charMatcher, int i) {
        this.strategy = strategy;
        this.omitEmptyStrings = z;
        this.trimmer = charMatcher;
        this.limit = i;
    }

    public static Splitter on(char c) {
        return on(CharMatcher.is(c));
    }

    public static Splitter on(final CharMatcher charMatcher) {
        Preconditions.checkNotNull(charMatcher);
        return new Splitter(new Strategy() { // from class: com.google.common.base.Splitter.1
            @Override // com.google.common.base.Splitter.Strategy
            public SplittingIterator iterator(Splitter splitter, CharSequence charSequence) {
                return new SplittingIterator(splitter, charSequence) { // from class: com.google.common.base.Splitter.1.1
                    @Override // com.google.common.base.Splitter.SplittingIterator
                    int separatorEnd(int i) {
                        return i + 1;
                    }

                    @Override // com.google.common.base.Splitter.SplittingIterator
                    int separatorStart(int i) {
                        return CharMatcher.this.indexIn(this.toSplit, i);
                    }
                };
            }
        });
    }

    public Splitter limit(int i) {
        Preconditions.checkArgument(i > 0, "must be greater than zero: %s", i);
        return new Splitter(this.strategy, this.omitEmptyStrings, this.trimmer, i);
    }

    public Iterable<String> split(final CharSequence charSequence) {
        Preconditions.checkNotNull(charSequence);
        return new Iterable<String>() { // from class: com.google.common.base.Splitter.5
            @Override // java.lang.Iterable
            public Iterator<String> iterator() {
                return Splitter.this.splittingIterator(charSequence);
            }

            public String toString() {
                Joiner on = Joiner.on(", ");
                StringBuilder sb = new StringBuilder();
                sb.append('[');
                StringBuilder appendTo = on.appendTo(sb, this);
                appendTo.append(']');
                return appendTo.toString();
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Iterator<String> splittingIterator(CharSequence charSequence) {
        return this.strategy.iterator(this, charSequence);
    }

    public List<String> splitToList(CharSequence charSequence) {
        Preconditions.checkNotNull(charSequence);
        Iterator<String> splittingIterator = splittingIterator(charSequence);
        ArrayList arrayList = new ArrayList();
        while (splittingIterator.hasNext()) {
            arrayList.add(splittingIterator.next());
        }
        return Collections.unmodifiableList(arrayList);
    }

    /* loaded from: classes2.dex */
    private static abstract class SplittingIterator extends AbstractIterator<String> {
        int limit;
        int offset = 0;
        final boolean omitEmptyStrings;
        final CharSequence toSplit;
        final CharMatcher trimmer;

        abstract int separatorEnd(int i);

        abstract int separatorStart(int i);

        protected SplittingIterator(Splitter splitter, CharSequence charSequence) {
            this.trimmer = splitter.trimmer;
            this.omitEmptyStrings = splitter.omitEmptyStrings;
            this.limit = splitter.limit;
            this.toSplit = charSequence;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.google.common.base.AbstractIterator
        public String computeNext() {
            int separatorStart;
            int i = this.offset;
            while (true) {
                int i2 = this.offset;
                if (i2 == -1) {
                    return endOfData();
                }
                separatorStart = separatorStart(i2);
                if (separatorStart == -1) {
                    separatorStart = this.toSplit.length();
                    this.offset = -1;
                } else {
                    this.offset = separatorEnd(separatorStart);
                }
                int i3 = this.offset;
                if (i3 == i) {
                    int i4 = i3 + 1;
                    this.offset = i4;
                    if (i4 > this.toSplit.length()) {
                        this.offset = -1;
                    }
                } else {
                    while (i < separatorStart && this.trimmer.matches(this.toSplit.charAt(i))) {
                        i++;
                    }
                    while (separatorStart > i && this.trimmer.matches(this.toSplit.charAt(separatorStart - 1))) {
                        separatorStart--;
                    }
                    if (!this.omitEmptyStrings || i != separatorStart) {
                        break;
                    }
                    i = this.offset;
                }
            }
            int i5 = this.limit;
            if (i5 == 1) {
                separatorStart = this.toSplit.length();
                this.offset = -1;
                while (separatorStart > i && this.trimmer.matches(this.toSplit.charAt(separatorStart - 1))) {
                    separatorStart--;
                }
            } else {
                this.limit = i5 - 1;
            }
            return this.toSplit.subSequence(i, separatorStart).toString();
        }
    }
}
