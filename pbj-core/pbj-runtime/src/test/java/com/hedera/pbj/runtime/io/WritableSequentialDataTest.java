package com.hedera.pbj.runtime.io;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.nio.BufferOverflowException;
import java.util.Arrays;

public class WritableSequentialDataTest extends WritableTestBase {
    @NonNull
    @Override
    protected WritableSequentialData sequence() {
        return new StubbedWritableSequentialData(new byte[1024 * 1024 * 2]);
    }

    @NonNull
    @Override
    protected WritableSequentialData eofSequence() {
        return new StubbedWritableSequentialData(new byte[0]);
    }

    @NonNull
    @Override
    protected byte[] extractWrittenBytes(@NonNull WritableSequentialData seq) {
        final var stub = (StubbedWritableSequentialData) seq;
        return Arrays.copyOf(stub.bytes, (int) stub.position);
    }

    private static final class StubbedWritableSequentialData implements WritableSequentialData {
        private final byte[] bytes;
        private long position = 0;
        private long limit;

        private StubbedWritableSequentialData(@NonNull final byte[] bytes) {
            this.bytes = bytes;
            this.limit = this.bytes.length;
        }


        @Override
        public long capacity() {
            return bytes.length;
        }

        @Override
        public long position() {
            return position;
        }

        @Override
        public long limit() {
            return limit;
        }

        @Override
        public void limit(long limit) {
            this.limit = Math.min(Math.max(position, limit), capacity());
        }

        @Override
        public long skip(long count) {
            final long numToSkip = Math.max(Math.min(count, limit - position), 0);
            position += numToSkip;
            return numToSkip;
        }

        @Override
        public void writeByte(byte b) throws DataAccessException {
            if (position >= limit) {
                throw new BufferOverflowException();
            }

            bytes[(int) position++] = b;
        }
    }
}
