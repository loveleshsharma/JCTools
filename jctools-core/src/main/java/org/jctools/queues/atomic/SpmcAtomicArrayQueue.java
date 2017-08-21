/*
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
package org.jctools.queues.atomic;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.AtomicLongArray;

/**
 * NOTE: This class was automatically generated by org.jctools.queues.atomic.JavaParsingAtomicArrayQueueGenerator
 * which can found in the jctools-build module. The original source file is SpmcArrayQueue.java.
 */
abstract class SpmcAtomicArrayQueueL1Pad<E> extends AtomicReferenceArrayQueue<E> {

    long p01, p02, p03, p04, p05, p06, p07;

    long p10, p11, p12, p13, p14, p15, p16, p17;

    public SpmcAtomicArrayQueueL1Pad(int capacity) {
        super(capacity);
    }
}

/**
 * NOTE: This class was automatically generated by org.jctools.queues.atomic.JavaParsingAtomicArrayQueueGenerator
 * which can found in the jctools-build module. The original source file is SpmcArrayQueue.java.
 */
abstract class SpmcAtomicArrayQueueProducerIndexField<E> extends SpmcAtomicArrayQueueL1Pad<E> {

    private static final AtomicLongFieldUpdater<SpmcAtomicArrayQueueProducerIndexField> P_INDEX_UPDATER = AtomicLongFieldUpdater.newUpdater(SpmcAtomicArrayQueueProducerIndexField.class, "producerIndex");

    protected volatile long producerIndex;

    public final long lvProducerIndex() {
        return producerIndex;
    }

    protected final void soProducerIndex(long newValue) {
        P_INDEX_UPDATER.lazySet(this, newValue);
    }

    public SpmcAtomicArrayQueueProducerIndexField(int capacity) {
        super(capacity);
    }
}

/**
 * NOTE: This class was automatically generated by org.jctools.queues.atomic.JavaParsingAtomicArrayQueueGenerator
 * which can found in the jctools-build module. The original source file is SpmcArrayQueue.java.
 */
abstract class SpmcAtomicArrayQueueL2Pad<E> extends SpmcAtomicArrayQueueProducerIndexField<E> {

    long p01, p02, p03, p04, p05, p06, p07;

    long p10, p11, p12, p13, p14, p15, p16, p17;

    public SpmcAtomicArrayQueueL2Pad(int capacity) {
        super(capacity);
    }
}

/**
 * NOTE: This class was automatically generated by org.jctools.queues.atomic.JavaParsingAtomicArrayQueueGenerator
 * which can found in the jctools-build module. The original source file is SpmcArrayQueue.java.
 */
abstract class SpmcAtomicArrayQueueConsumerIndexField<E> extends SpmcAtomicArrayQueueL2Pad<E> {

    private static final AtomicLongFieldUpdater<SpmcAtomicArrayQueueConsumerIndexField> C_INDEX_UPDATER = AtomicLongFieldUpdater.newUpdater(SpmcAtomicArrayQueueConsumerIndexField.class, "consumerIndex");

    private volatile long consumerIndex;

    public SpmcAtomicArrayQueueConsumerIndexField(int capacity) {
        super(capacity);
    }

    public final long lvConsumerIndex() {
        return consumerIndex;
    }

    protected final boolean casConsumerIndex(long expect, long newValue) {
        return C_INDEX_UPDATER.compareAndSet(this, expect, newValue);
    }
}

/**
 * NOTE: This class was automatically generated by org.jctools.queues.atomic.JavaParsingAtomicArrayQueueGenerator
 * which can found in the jctools-build module. The original source file is SpmcArrayQueue.java.
 */
abstract class SpmcAtomicArrayQueueMidPad<E> extends SpmcAtomicArrayQueueConsumerIndexField<E> {

    long p01, p02, p03, p04, p05, p06, p07;

    long p10, p11, p12, p13, p14, p15, p16, p17;

    public SpmcAtomicArrayQueueMidPad(int capacity) {
        super(capacity);
    }
}

/**
 * NOTE: This class was automatically generated by org.jctools.queues.atomic.JavaParsingAtomicArrayQueueGenerator
 * which can found in the jctools-build module. The original source file is SpmcArrayQueue.java.
 */
abstract class SpmcAtomicArrayQueueProducerIndexCacheField<E> extends SpmcAtomicArrayQueueMidPad<E> {

    // This is separated from the consumerIndex which will be highly contended in the hope that this value spends most
    // of it's time in a cache line that is Shared(and rarely invalidated)
    private volatile long producerIndexCache;

    public SpmcAtomicArrayQueueProducerIndexCacheField(int capacity) {
        super(capacity);
    }

    protected final long lvProducerIndexCache() {
        return producerIndexCache;
    }

    protected final void svProducerIndexCache(long newValue) {
        producerIndexCache = newValue;
    }
}

/**
 * NOTE: This class was automatically generated by org.jctools.queues.atomic.JavaParsingAtomicArrayQueueGenerator
 * which can found in the jctools-build module. The original source file is SpmcArrayQueue.java.
 */
abstract class SpmcAtomicArrayQueueL3Pad<E> extends SpmcAtomicArrayQueueProducerIndexCacheField<E> {

    long p01, p02, p03, p04, p05, p06, p07;

    long p10, p11, p12, p13, p14, p15, p16, p17;

    public SpmcAtomicArrayQueueL3Pad(int capacity) {
        super(capacity);
    }
}

/**
 * NOTE: This class was automatically generated by org.jctools.queues.atomic.JavaParsingAtomicArrayQueueGenerator
 * which can found in the jctools-build module. The original source file is SpmcArrayQueue.java.
 */
public class SpmcAtomicArrayQueue<E> extends SpmcAtomicArrayQueueL3Pad<E> {

    public SpmcAtomicArrayQueue(final int capacity) {
        super(capacity);
    }

    @Override
    public boolean offer(final E e) {
        if (null == e) {
            throw new NullPointerException();
        }
        final AtomicReferenceArray<E> buffer = this.buffer;
        final int mask = this.mask;
        final long currProducerIndex = lvProducerIndex();
        final int offset = calcElementOffset(currProducerIndex, mask);
        if (null != lvElement(buffer, offset)) {
            long size = currProducerIndex - lvConsumerIndex();
            if (size > mask) {
                return false;
            } else {
                // spin wait for slot to clear, buggers wait freedom
                while (null != lvElement(buffer, offset)) ;
            }
        }
        spElement(buffer, offset, e);
        // single producer, so store ordered is valid. It is also required to correctly publish the element
        // and for the consumers to pick up the tail value.
        soProducerIndex(currProducerIndex + 1);
        return true;
    }

    @Override
    public E poll() {
        long currentConsumerIndex;
        long currProducerIndexCache = lvProducerIndexCache();
        do {
            currentConsumerIndex = lvConsumerIndex();
            if (currentConsumerIndex >= currProducerIndexCache) {
                long currProducerIndex = lvProducerIndex();
                if (currentConsumerIndex >= currProducerIndex) {
                    return null;
                } else {
                    currProducerIndexCache = currProducerIndex;
                    svProducerIndexCache(currProducerIndex);
                }
            }
        } while (!casConsumerIndex(currentConsumerIndex, currentConsumerIndex + 1));
        // and wrap to hit same location.
        return removeElement(buffer, currentConsumerIndex, mask);
    }

    private E removeElement(final AtomicReferenceArray<E> buffer, long index, final int mask) {
        final int offset = calcElementOffset(index, mask);
        // load plain, element happens before it's index becomes visible
        final E e = lpElement(buffer, offset);
        // store ordered, make sure nulling out is visible. Producer is waiting for this value.
        soElement(buffer, offset, null);
        return e;
    }

    @Override
    public E peek() {
        final int mask = this.mask;
        final long currProducerIndexCache = lvProducerIndexCache();
        long currentConsumerIndex;
        E e;
        do {
            currentConsumerIndex = lvConsumerIndex();
            if (currentConsumerIndex >= currProducerIndexCache) {
                long currProducerIndex = lvProducerIndex();
                if (currentConsumerIndex >= currProducerIndex) {
                    return null;
                } else {
                    svProducerIndexCache(currProducerIndex);
                }
            }
        } while (null == (e = lvElement(buffer, calcElementOffset(currentConsumerIndex, mask))));
        return e;
    }

    @Override
    public boolean relaxedOffer(E e) {
        if (null == e) {
            throw new NullPointerException("Null is not a valid element");
        }
        final AtomicReferenceArray<E> buffer = this.buffer;
        final int mask = this.mask;
        final long producerIndex = lvProducerIndex();
        final int offset = calcElementOffset(producerIndex, mask);
        if (null != lvElement(buffer, offset)) {
            return false;
        }
        spElement(buffer, offset, e);
        // single producer, so store ordered is valid. It is also required to correctly publish the element
        // and for the consumers to pick up the tail value.
        soProducerIndex(producerIndex + 1);
        return true;
    }

    @Override
    public E relaxedPoll() {
        return poll();
    }

    @Override
    public E relaxedPeek() {
        final AtomicReferenceArray<E> buffer = this.buffer;
        final int mask = this.mask;
        final long consumerIndex = lvConsumerIndex();
        return lvElement(buffer, calcElementOffset(consumerIndex, mask));
    }

    @Override
    public int drain(final Consumer<E> c) {
        final int capacity = capacity();
        int sum = 0;
        while (sum < capacity) {
            int drained = 0;
            if ((drained = drain(c, MpmcAtomicArrayQueue.RECOMENDED_POLL_BATCH)) == 0) {
                break;
            }
            sum += drained;
        }
        return sum;
    }

    @Override
    public int fill(final Supplier<E> s) {
        return fill(s, capacity());
    }

    @Override
    public int drain(final Consumer<E> c, final int limit) {
        final AtomicReferenceArray<E> buffer = this.buffer;
        final int mask = this.mask;
        long currProducerIndexCache = lvProducerIndexCache();
        int adjustedLimit = 0;
        long currentConsumerIndex;
        do {
            currentConsumerIndex = lvConsumerIndex();
            // is there any space in the queue?
            if (currentConsumerIndex >= currProducerIndexCache) {
                long currProducerIndex = lvProducerIndex();
                if (currentConsumerIndex >= currProducerIndex) {
                    return 0;
                } else {
                    currProducerIndexCache = currProducerIndex;
                    svProducerIndexCache(currProducerIndex);
                }
            }
            // try and claim up to 'limit' elements in one go
            int remaining = (int) (currProducerIndexCache - currentConsumerIndex);
            adjustedLimit = Math.min(remaining, limit);
        } while (!casConsumerIndex(currentConsumerIndex, currentConsumerIndex + adjustedLimit));
        for (int i = 0; i < adjustedLimit; i++) {
            c.accept(removeElement(buffer, currentConsumerIndex + i, mask));
        }
        return adjustedLimit;
    }

    @Override
    public int fill(final Supplier<E> s, final int limit) {
        final AtomicReferenceArray<E> buffer = this.buffer;
        final int mask = this.mask;
        long producerIndex = this.producerIndex;
        for (int i = 0; i < limit; i++) {
            final int offset = calcElementOffset(producerIndex, mask);
            if (null != lvElement(buffer, offset)) {
                return i;
            }
            producerIndex++;
            // StoreStore
            soElement(buffer, offset, s.get());
            // ordered store -> atomic and ordered for size()
            soProducerIndex(producerIndex);
        }
        return limit;
    }

    @Override
    public void drain(final Consumer<E> c, final WaitStrategy w, final ExitCondition exit) {
        int idleCounter = 0;
        while (exit.keepRunning()) {
            if (drain(c, MpmcAtomicArrayQueue.RECOMENDED_POLL_BATCH) == 0) {
                idleCounter = w.idle(idleCounter);
                continue;
            }
            idleCounter = 0;
        }
    }

    @Override
    public void fill(final Supplier<E> s, final WaitStrategy w, final ExitCondition e) {
        final AtomicReferenceArray<E> buffer = this.buffer;
        final int mask = this.mask;
        long producerIndex = this.producerIndex;
        int counter = 0;
        while (e.keepRunning()) {
            for (int i = 0; i < 4096; i++) {
                final int offset = calcElementOffset(producerIndex, mask);
                if (null != lvElement(buffer, offset)) {
                    // LoadLoad
                    counter = w.idle(counter);
                    continue;
                }
                producerIndex++;
                counter = 0;
                // StoreStore
                soElement(buffer, offset, s.get());
                // ordered store -> atomic and ordered for size()
                soProducerIndex(producerIndex);
            }
        }
    }
}
