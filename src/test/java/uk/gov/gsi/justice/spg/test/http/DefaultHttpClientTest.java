package uk.gov.gsi.justice.spg.test.http;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.codearte.jfairy.Fairy;
import io.codearte.jfairy.producer.text.TextProducer;
import io.vavr.control.Either;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.gov.gsi.justice.spg.test.model.HttpFault;
import uk.gov.gsi.justice.spg.test.model.HttpSuccess;

import java.nio.file.Path;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class DefaultHttpClientTest {
    private final Fairy fairy = Fairy.create();
    private final TextProducer textProducer = fairy.textProducer();
    private final static WireMockServer WIREMOCK = new WireMockServer(
            options()
                    .dynamicPort()
                    .dynamicHttpsPort()
                    .keystorePath("assets/Wiremock-Keystore.jks")
                    .keystorePassword("password")
    );

    @BeforeAll
    public static void prepare() {
        WIREMOCK.start();
    }

    @AfterEach
    void after() {
        WIREMOCK.resetAll();
    }

    @AfterAll
    static void clean() throws Exception {
        WIREMOCK.shutdown();
        SECONDS.sleep(2);
    }

    @Test
    public void testPostOverHttp() throws Exception {
        final String testUrl = "/something";
        final String testEndpoint = "http://localhost:" + WIREMOCK.port() + testUrl;
        WIREMOCK.stubFor(
                post(urlPathEqualTo(testUrl))
                        .willReturn(aResponse().withStatus(200)
                                .withBody("success"))
        );

        WIREMOCK.verify(0, postRequestedFor(anyUrl()));

        final HttpInterfaceProvider httpInterfaceProvider = new HttpInterfaceProvider.Builder().build();
        final HttpClient httpClient = new DefaultHttpClient(httpInterfaceProvider.provideHttpInterface());

        final String payload = textProducer.sentence();
        final Either<HttpFault, HttpSuccess> result = httpClient.postTo(testEndpoint, payload);

        assertTrue(result.isRight());
        assertThat(result.get().getBody(), is("success"));

        WIREMOCK.verify(1, postRequestedFor(urlEqualTo(testUrl))
                .withRequestBody(equalTo(payload))
                .withHeader("accept-encoding", equalTo("deflate")));
    }

    @Test
    public void testPostOverHttps() throws Exception {
        final String testUrl = "/something";
        final String testEndpoint = "https://localhost:" + WIREMOCK.httpsPort() + testUrl;
        WIREMOCK.stubFor(
                post(urlPathEqualTo(testUrl))
                        .willReturn(aResponse().withStatus(200)
                                .withBody("success"))
        );

        WIREMOCK.verify(0, postRequestedFor(anyUrl()));

        final String resourceName = "certificates/SPG-And-Wiremock-Certs.pem";
        TestResourceLocator resourceLocator = new TestResourceLocator();
        final Path certPath = resourceLocator.getPath(resourceName);

        final HttpInterfaceProvider httpInterfaceProvider = new HttpInterfaceProvider.Builder()
                .withCertificate(certPath)
                .build();
        final HttpClient httpClient = new DefaultHttpClient(httpInterfaceProvider.provideHttpInterface());

        final String payload = textProducer.sentence();
        final Either<HttpFault, HttpSuccess> result = httpClient.postTo(testEndpoint, payload);

        assertTrue(result.isRight());
        assertThat(result.get().getBody(), is("success"));

        WIREMOCK.verify(1, postRequestedFor(urlEqualTo(testUrl))
                .withRequestBody(equalTo(payload))
                .withHeader("accept-encoding", equalTo("deflate")));
    }

}