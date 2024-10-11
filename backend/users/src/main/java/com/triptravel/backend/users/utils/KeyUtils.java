package com.triptravel.backend.users.utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyUtils {

	private static final Logger errLog = (Logger) LoggerFactory.getLogger("errLog");
	private static final Logger infoLog = (Logger) LoggerFactory.getLogger("infLog");

    public static PrivateKey getPrivateKey(String privateKeyPath) throws Exception {
        try {
            infoLog.info("Loading private key from path: {}", privateKeyPath);
            String key = new String(Files.readAllBytes(Paths.get(privateKeyPath)));
            key = key.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePrivate(spec);
        } catch (Exception e) {
            errLog.error("Error loading private key from path: {}", privateKeyPath, e);
            throw new Exception("Failed to load private key", e);
        }
    }

    public static PublicKey getPublicKey(String publicKeyPath) throws Exception {
        try {
        	infoLog.info("Caricamento chiave pubblica da: {}", publicKeyPath);
            String key = new String(Files.readAllBytes(Paths.get(publicKeyPath)));
            key = key.replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] keyBytes = Base64.getDecoder().decode(key);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(spec);
        } catch (Exception e) {
        	errLog.error("Errore nel caricamento della chiave pubblica da: {}", publicKeyPath, e);
            throw e;
        }
    }
}