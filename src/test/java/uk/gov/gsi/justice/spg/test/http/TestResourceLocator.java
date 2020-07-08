package uk.gov.gsi.justice.spg.test.http;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class TestResourceLocator {
    public Path getPath(final String fileName) throws IOException, URISyntaxException {
        final URL url = Optional.ofNullable(Thread.currentThread().getContextClassLoader()
                .getResource(fileName))
                .orElseThrow(IOException::new);

        return Paths.get(url.toURI());
    }
}