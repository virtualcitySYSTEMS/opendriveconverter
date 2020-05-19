package de.vcs.converter;

import de.vcs.converter.strategies.ConverterStrategy;
import de.vcs.model.odr.core.OpenDRIVE;

import java.io.File;
import java.io.IOException;
import java.util.function.Function;

public class FormatConverter<T extends AbstractFormat> implements ConverterStrategy<T> {

    private final Function<OpenDRIVE, T> fromODR;
    private File outputFile;

    public FormatConverter(Function<OpenDRIVE, T> fromODR, File outputFile) {
        this.fromODR = fromODR;
        this.outputFile = outputFile;
    }

    public final T convertFromODR(OpenDRIVE odr) {
        return fromODR.apply(odr);
    }

    @Override
    public void write(T format) throws IOException {
    }

    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }
}
