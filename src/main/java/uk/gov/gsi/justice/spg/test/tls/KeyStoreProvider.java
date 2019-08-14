package uk.gov.gsi.justice.spg.test.tls;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;

public class KeyStoreProvider {
    @SuppressWarnings("unchecked")
    public static KeyStore makeJavaKeyStore(final InputStream in) {
        try (final BufferedInputStream bis = new BufferedInputStream(in)) {
            final CertificateFactory cf = CertificateFactory.getInstance("X.509");

            final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(null, null);
            int certificateCounter = 0;
            for (X509Certificate certificate : (Collection<X509Certificate>) cf.generateCertificates(bis)) {
                ks.setCertificateEntry("cert_" + certificateCounter++, certificate);
            }

            return ks;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (CertificateException e) {
            throw new IllegalStateException("Can't load keystore : ", e);
        } catch (KeyStoreException | NoSuchAlgorithmException e) {
            throw new IllegalStateException("Can't create the internal keystore : ", e);
        }
    }

    public static KeyStore makeJavaKeyStore(Path keystorePath) {
        try (final InputStream in = Files.newInputStream(keystorePath)) {
            return makeJavaKeyStore(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static KeyStore makeJavaKeyStore(String keystoreFile) {
        try (final InputStream in = ClassLoader.getSystemClassLoader().getClass().getResourceAsStream(keystoreFile)) {
            return makeJavaKeyStore(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}