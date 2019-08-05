package uk.gov.gsi.justice.spg.test.tls;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class TrustManagerProvider {
    public static X509TrustManager trustManagerFor(KeyStore keyStore) {
        final TrustManagerFactory tmf = trustManagerFactoryFor(keyStore);

        final TrustManager[] trustManagers = tmf.getTrustManagers();
        if (trustManagers.length != 1) {
            throw new IllegalStateException("Unexpected number of trust managers:"
                    + Arrays.toString(trustManagers));
        }
        final TrustManager trustManager = trustManagers[0];
        if (trustManager instanceof X509TrustManager) {
            return (X509TrustManager) trustManager;
        }
        throw new IllegalStateException("'" + trustManager + "' is not a X509TrustManager");
    }

    private static TrustManagerFactory trustManagerFactoryFor(KeyStore keyStore) {
        try {
            final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
            return tmf;
        } catch (KeyStoreException | NoSuchAlgorithmException e) {
            throw new IllegalStateException("Can't load trust manager for keystore : " + keyStore, e);
        }
    }
}
