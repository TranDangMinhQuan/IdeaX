package com.novaid.ideax.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JitsiJwtUtil {

    @Value("${jitsi.app-id}")
    private String appId;

    @Value("${jitsi.kid}")
    private String keyId;

    @Value("${jitsi.private-key-path}")
    private String privateKeyPath;

    @Value("${jitsi.base-url}")
    private String jitsiBaseUrl;

    @Autowired
    private ResourceLoader resourceLoader;

    private PrivateKey loadPrivateKey() {
        try {
            Resource resource = resourceLoader.getResource(privateKeyPath);
            String key = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8)
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key));
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        } catch (Exception e) {
            throw new RuntimeException("Cannot load private key", e);
        }
    }

    public String generateJwt(String roomName, String userEmail) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(3600); // 1h

        PrivateKey privateKey = loadPrivateKey();

        return Jwts.builder()
                .setHeaderParam("kid", keyId)
                .setHeaderParam("typ", "JWT")
                .setIssuer(appId)
                .setSubject(appId)
                .setAudience("jitsi")
                .setExpiration(Date.from(exp))
                .setNotBefore(Date.from(now))
                .setIssuedAt(Date.from(now))
                .setId(UUID.randomUUID().toString())
                .claim("room", roomName) // chỉ định phòng
                .claim("moderator", true) // có thể phân quyền
                .claim("context", new java.util.HashMap<>() {{
                    put("user", new java.util.HashMap<>() {{
                        put("name", userEmail);
                        put("email", userEmail);
                    }});
                }})
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public String buildMeetingUrl(String roomName, String jwt) {
        return jitsiBaseUrl + "/" + appId + "/" + roomName + "?jwt=" + jwt;
    }
}

