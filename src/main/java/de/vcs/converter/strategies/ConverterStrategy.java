package de.vcs.converter.strategies;

import de.vcs.model.odr.core.OpenDRIVE;

import java.io.File;
import java.io.IOException;

public interface ConverterStrategy {
    void write(OpenDRIVE odr, File outputFile) throws IOException;
}
