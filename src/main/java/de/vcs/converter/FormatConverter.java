package de.vcs.converter;

import de.vcs.converter.strategies.ConverterStrategy;
import de.vcs.model.odr.core.OpenDRIVE;

import java.io.File;
import java.io.IOException;
import java.util.function.Function;

public class FormatConverter<T extends AbstractFormat> implements ConverterStrategy<T> {

    private final Function<OpenDRIVE, T> fromODR;

    public FormatConverter(Function<OpenDRIVE, T> fromODR) {
        this.fromODR = fromODR;
    }

    public final T convertFromODR(OpenDRIVE odr) {
        return fromODR.apply(odr);
    }

    @Override
    public void write(T format, File outputFile) throws IOException {
    }
}
