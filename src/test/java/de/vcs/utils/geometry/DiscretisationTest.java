package de.vcs.utils.geometry;

import org.junit.Test;

public class DiscretisationTest {

    @Test
    public void checkSRunner() {
        Discretisation.generateSRunner(2.22, 19.0).forEach(System.out::println);
    }
}
