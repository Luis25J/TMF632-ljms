package mx.att.digital.api.tmf632.infrastructure.out.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.KeyStore;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RestTemplateConfig632Test {

  private RestTemplateConfig632 config;

  @BeforeEach
  void setUp() {
    config = new RestTemplateConfig632();
  }

  @Test
  void restTemplate632_success() throws Exception {
    // 1) Generate an in-memory empty JKS truststore
    char[] pwd = "pass".toCharArray();
    KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
    ks.load(null, pwd);

    File tsFile = File.createTempFile("truststore", ".jks");
    tsFile.deleteOnExit();
    try (OutputStream out = new FileOutputStream(tsFile)) {
      ks.store(out, pwd);
    }

    // 3) Call and verify
    RestTemplate tpl = config.restTemplate632();
    assertNotNull(tpl, "RestTemplate must not be null");
    assertTrue(tpl.getRequestFactory() instanceof HttpComponentsClientHttpRequestFactory);

  }
}
