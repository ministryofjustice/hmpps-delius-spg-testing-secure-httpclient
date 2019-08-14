package uk.gov.gsi.justice.spg.test.wss;

import io.vavr.Tuple3;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.crypto.CryptoFactory;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.common.util.XMLUtils;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.engine.WSSConfig;
import org.apache.wss4j.dom.message.WSSecHeader;
import org.apache.wss4j.dom.message.WSSecSignature;
import org.apache.wss4j.dom.message.WSSecTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

public class SOAPMessageSecurer {
    private static final Logger LOGGER = LoggerFactory.getLogger(SOAPMessageSecurer.class);

    private final String keystoreFile;
    private final String keystoreAlias;
    private final String keystorePassword;
    private final Crypto crypto;
    private final Integer messageTtl;

    public static class Builder {
        private final String defaultKeystore = "/certificates/SOAPMessage-Keystore.jks";
        private final String defaultAlias = "spg-test";
        private final String defaultPassword = "password";

        private String keystoreOverride;
        private String aliasOverride;
        private String passwordOverride;

        public Builder withKeystore(Path keystore, String alias, String password) {
            this.keystoreOverride = keystore.toString();
            this.aliasOverride = alias;
            this.passwordOverride = password;

            return this;
        }

        public SOAPMessageSecurer build() {
            final Tuple3<String, String, String> keystore = Optional.ofNullable(keystoreOverride)
                    .map(x -> new Tuple3<>(x, aliasOverride, passwordOverride))
                    .orElse(new Tuple3<>(defaultKeystore, defaultAlias, defaultPassword));

            return new SOAPMessageSecurer(keystore._1(), keystore._2(), keystore._3());
        }
    }

    private SOAPMessageSecurer(String keystoreFile, String keystoreAlias, String keystorePassword) {
        WSSConfig.init();
        this.keystoreFile = keystoreFile;
        this.keystoreAlias = keystoreAlias;
        this.keystorePassword = keystorePassword;
        this.crypto = buildCrypto();
        this.messageTtl = 1000;
    }

    public SOAPMessage signSOAPMessage(SOAPMessage soapMessage) throws SOAPException, IOException, TransformerException, WSSecurityException {
        final Document unsignedDocument = soapMessage.getSOAPBody().getOwnerDocument();

        final WSSecHeader secHeader = buildHeader(unsignedDocument);
        addTimeToLive(secHeader);
        signDocument(secHeader);

        soapMessage.saveChanges();

        return soapMessage;
    }

    private WSSecHeader buildHeader(Document document) throws WSSecurityException {
        final WSSecHeader secHeader = new WSSecHeader(document);
        secHeader.setMustUnderstand(false);
        secHeader.insertSecurityHeader();

        return secHeader;
    }

    private Document addTimeToLive(WSSecHeader secHeader) throws IOException, TransformerException {
        final WSSecTimestamp wsSecTimeStamp = new WSSecTimestamp(secHeader);
        wsSecTimeStamp.setTimeToLive(messageTtl);
        final Document timestampedDocument = wsSecTimeStamp.build();
        final String timestampedXml = XMLUtils.prettyDocumentToString(timestampedDocument);
        LOGGER.debug("Timestamped Document::\n{}", timestampedXml);

        return timestampedDocument;
    }

    private Document signDocument(WSSecHeader secHeader) throws WSSecurityException, IOException, TransformerException {
        final WSSecSignature signature = new WSSecSignature(secHeader);
        signature.setUserInfo(keystoreAlias, keystorePassword);
        signature.setKeyIdentifierType(WSConstants.BST_DIRECT_REFERENCE);
        final String signatureAlgorithm = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
        signature.setSignatureAlgorithm(signatureAlgorithm);
        signature.setSigCanonicalization(WSConstants.C14N_EXCL_OMIT_COMMENTS);

        final Document signedDoc = signature.build(crypto);

        final String outputXml = XMLUtils.prettyDocumentToString(signedDoc);
        LOGGER.debug("The Signed Document::\n{}", outputXml);

        return signedDoc;
    }

    private Crypto buildCrypto() {
        final String merlin = "org.apache.ws.security.components.crypto.Merlin";
        final Properties cryptoProps = new Properties();
        cryptoProps.setProperty("org.apache.ws.security.crypto.provider", merlin);
        cryptoProps.setProperty("org.apache.ws.security.crypto.merlin.keystore.type", "jks");
        cryptoProps.setProperty("org.apache.ws.security.crypto.merlin.keystore.file", keystoreFile);
        cryptoProps.setProperty("org.apache.ws.security.crypto.merlin.keystore.alias", keystoreAlias);
        cryptoProps.setProperty("org.apache.ws.security.crypto.merlin.keystore.password", keystorePassword);

        try {
            return CryptoFactory.getInstance(cryptoProps);
        } catch (WSSecurityException e) {
            LOGGER.error("Error getting a Crypto instance:: ", e);
        }
        return null;
    }
}
