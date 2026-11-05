package com.acme.coldtrace.platform.maintenancemanagement.infrastructure.clients;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;

class OwnerClientTest {
  private static final String KEY = "isolated-http-owner-service-key-with-32-characters";
  record Snapshot(Long organizationId, Long assetId) {}
  private HttpServer server;
  private OwnerClient client;
  private int status;
  private String body;
  @BeforeEach void owner() throws Exception {
    server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.createContext("/organizations/7/assets/20", exchange -> {
      assertEquals(KEY, exchange.getRequestHeaders().getFirst("X-Service-Key"));
      byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
      exchange.getResponseHeaders().set("Content-Type", "application/json");
      exchange.sendResponseHeaders(status, bytes.length);
      exchange.getResponseBody().write(bytes); exchange.close();
    });
    server.start();
    client = new OwnerClient("http://127.0.0.1:" + server.getAddress().getPort(), KEY);
  }
  @AfterEach void stop() { server.stop(0); }
  @Test void explicitOwnerAbsenceIsAnEmptyResource() {
    status = 404; body = "{}";
    assertTrue(client.find("/organizations/{org}/assets/{id}", Snapshot.class, 7, 20).isEmpty());
  }
  @Test void ownerFailureIsNotAnEmptyResource() {
    status = 503; body = "{}";
    assertEquals(503, assertThrows(ResponseStatusException.class,
        () -> client.find("/organizations/{org}/assets/{id}", Snapshot.class, 7, 20)).getStatusCode().value());
  }
  @Test void validOwnerSnapshotPreservesItsIdentity() {
    status = 200; body = "{\"organizationId\":7,\"assetId\":20}";
    assertEquals(new Snapshot(7L,20L), client.find("/organizations/{org}/assets/{id}", Snapshot.class, 7, 20).orElseThrow());
  }
}
