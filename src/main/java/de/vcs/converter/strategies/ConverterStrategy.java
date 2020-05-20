package de.vcs.converter.strategies;

import de.vcs.converter.AbstractFormat;
import java.io.IOException;

public interface ConverterStrategy<T extends AbstractFormat> {
    void write(T format) throws IOException;
}
