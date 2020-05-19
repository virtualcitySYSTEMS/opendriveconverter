package de.vcs.converter.strategies;

import de.vcs.converter.AbstractFormat;
import de.vcs.converter.GeoJsonFormat;
import de.vcs.model.odr.core.OpenDRIVE;

import java.io.File;
import java.io.IOException;

public interface ConverterStrategy<T extends AbstractFormat> {
    void write(T format) throws IOException;
}
