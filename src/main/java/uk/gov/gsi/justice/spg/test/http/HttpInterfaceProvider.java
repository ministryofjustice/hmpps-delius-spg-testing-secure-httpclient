package uk.gov.gsi.justice.spg.test.http;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import uk.gov.gsi.justice.spg.test.tls.KeyStoreProvider;
import uk.gov.gsi.justice.spg.test.tls.TrustManagerProvider;
import uk.gov.gsi.justice.spg.test.tls.TrustSelfSignedX509TrustManager;

import javax.net.ssl.*;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;

public class HttpInterfaceProvider {
    private final KeyStore keyStore;

    private HttpInterfaceProvider(KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    public static class Builder {
        private final String resourceName = "/certificates/SPG-And-Wiremock-Certs.pem";
        private Path certOverride;

        /**
         *
         * @param certificatePath a path to the certificate to use
         * @return HttpInterfaceProvider.Builder
         */
        public Builder withCertificate(Path certificatePath) {
            certOverride = certificatePath;

            return this;
        }

        public HttpInterfaceProvider build() {
            final KeyStore keyStore = Optional.ofNullable(certOverride)
                    .map(KeyStoreProvider::makeJavaKeyStore)
                    .orElseGet(() -> KeyStoreProvider.makeJavaKeyStore(resourceName));

            return new HttpInterfaceProvider(keyStore);
        }
    }

    /**
     *
     * @return An HttpInterface
     */
    public HttpInterface provideHttpInterface() {
        return provideRetrofit().create(HttpInterface.class);
    }

    private Retrofit provideRetrofit() {
        final X509TrustManager trustManager = TrustManagerProvider.trustManagerFor(keyStore);

        final SSLContext sslContext = sslContext(null, TrustSelfSignedX509TrustManager.wrap(trustManager));

        final HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BASIC);
        final OkHttpClient httpClient = new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory(), trustManager)
                .hostnameVerifier(allowAllHostNames())
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(chain -> {
                    final Request original = chain.request();
                    final Request.Builder requestBuilder = original.newBuilder()
                            .header("accept-encoding", "deflate");
                    final Request request = requestBuilder.build();
                    return chain.proceed(request);
                })
                .build();

        return new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl("http://localhost/")
                .client(httpClient)
                .build();
    }

    @SuppressWarnings("SameParameterValue")
    private SSLContext sslContext(KeyManager[] keyManagers, TrustManager[] trustManagers) {
        try {
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagers, trustManagers, new SecureRandom());
            return sslContext;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new IllegalStateException("Couldn't init TLS context", e);
        }
    }

    private HostnameVerifier allowAllHostNames() {
        return (hostname, sslSession) -> true;
    }
}