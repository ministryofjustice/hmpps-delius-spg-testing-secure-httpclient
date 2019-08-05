package uk.gov.gsi.justice.spg.test.http;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.codearte.jfairy.Fairy;
import io.codearte.jfairy.producer.text.TextProducer;
import io.vavr.control.Either;
import org.junit.Rule;
import org.junit.Test;
import uk.gov.gsi.justice.spg.test.model.HttpFault;
import uk.gov.gsi.justice.spg.test.model.HttpSuccess;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class DefaultHttpClientTest {
    private final Fairy fairy = Fairy.create();
    private final TextProducer textProducer = fairy.textProducer();

    @Rule
    public WireMockRule server = new WireMockRule(
            options()
                    .dynamicPort()
                    .dynamicHttpsPort()
                    .keystorePath("assets/Wiremock-Keystore.jks")
                    .keystorePassword("password")
    );

    @Test
    public void testPostOverHttp() throws Exception {
        final String testUrl = "/something";
        final String testEndpoint = "http://localhost:" + server.port() + testUrl;
        server.stubFor(
                post(urlPathEqualTo(testUrl))
                        .willReturn(aResponse().withStatus(200)
                                .withBody("success"))
        );

        verify(0, postRequestedFor(anyUrl()));

        final HttpInterfaceProvider httpInterfaceProvider = new HttpInterfaceProvider.Builder().build();
        final HttpClient httpClient = new DefaultHttpClient(httpInterfaceProvider.provideHttpInterface());

        final String payload = textProducer.sentence();
        final Either<HttpFault, HttpSuccess> result = httpClient.postTo(testEndpoint, payload);

        assertTrue(result.isRight());
        assertThat(result.get().getBody(), is("success"));

        verify(1, postRequestedFor(urlEqualTo(testUrl))
                .withRequestBody(equalTo(payload))
                .withHeader("accept-encoding", equalTo("deflate")));
    }

    @Test
    public void testPostOverHttps() throws Exception {
        final String testUrl = "/something";
        final String testEndpoint = "https://localhost:" + server.httpsPort() + testUrl;
        server.stubFor(
                post(urlPathEqualTo(testUrl))
                        .willReturn(aResponse().withStatus(200)
                                .withBody("success"))
        );

        verify(0, postRequestedFor(anyUrl()));

        final HttpInterfaceProvider httpInterfaceProvider = new HttpInterfaceProvider.Builder().build();
        final HttpClient httpClient = new DefaultHttpClient(httpInterfaceProvider.provideHttpInterface());

        final String payload = textProducer.sentence();
        final Either<HttpFault, HttpSuccess> result = httpClient.postTo(testEndpoint, payload);

        assertTrue(result.isRight());
        assertThat(result.get().getBody(), is("success"));

        verify(1, postRequestedFor(urlEqualTo(testUrl))
                .withRequestBody(equalTo(payload))
                .withHeader("accept-encoding", equalTo("deflate")));
    }

}