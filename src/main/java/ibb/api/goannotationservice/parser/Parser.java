package ibb.api.goannotationservice.parser;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class Parser<T> implements Closeable, Iterable<T> {

    private static class ItemIterator<T> implements Iterator<T> {
        private T current;
        private Parser<T> parser;

        public ItemIterator(Parser<T> parser) {
            this.parser = parser;
        }

        private T getNextItem() {
            try {
                return parser.getNextItem();
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage());
            }
        }

        @Override
        public boolean hasNext() {
            if (parser.closed) {
                return false;
            }
            if (this.current == null) {
                this.current = this.getNextItem();
            }
            return this.current != null;
        }

        @Override
        public T next() {
            if (parser.closed) {
                throw new NoSuchElementException("Parser has been closed");
            }
            T next = this.current;
            this.current = null;

            if (next == null) {
                next = this.getNextItem();
                if (next == null) {
                    throw new NoSuchElementException("No more items available");
                }
            }
            return next;
        }
    }

    protected BufferedReader bufferedReader;
    private ItemIterator<T> iterator;
    private boolean closed;

    public Parser(InputStream inputStream) {
        this.bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream));
        this.iterator = new ItemIterator<>(this);
        this.closed = false;
    }

    @Override
    public Iterator<T> iterator() {
        return iterator;
    }

    @Override
    public void close() throws IOException {
        if (!closed) {
            closed = true;
            bufferedReader.close();
        }
    }

    protected abstract T getNextItem() throws IOException;

    public Stream<T> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                iterator(), Spliterator.ORDERED), false);
    }
}
