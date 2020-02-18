package de.vcs.converter;

import de.vcs.model.odr.core.OpenDRIVE;

import java.util.function.Function;

public class FormatConverter<T> {

    private final Function<OpenDRIVE, T> fromODR;

    public FormatConverter(Function<OpenDRIVE, T> fromODR) {
        this.fromODR = fromODR;
    }

    public final T convertFromODR(OpenDRIVE odr) {
        return fromODR.apply(odr);
    }
}
