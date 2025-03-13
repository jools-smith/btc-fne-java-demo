package com.revenera.gcs.btc;

import com.flexnet.lm.FlxException;

import static flxexamples.IdentityClient.IDENTITY_DATA;

public class Main {
  public static void main(final String...args) {
    System.out.println("hello...");
    try {

      final Client client = Client
              .create()
              .withIdentity(IDENTITY_DATA)
              .withHostId("A249CC37-C6F2-463F-AD5C-C924CA9A6BC1")
              .withHostName("BTC Test Client")
              .withPublisher("flex1115-uat", "com")
              .initialize();

      final Client.ServerToken token = client.createServerToken("X6Z5CAX64BLR");

      final Client.Feature feature = Client.Feature.of("name", "0");
      final Client.Entry p1 = Client.Entry.of("name", "jools");
      final Client.Entry p2 = Client.Entry.of("company", "revenera");
      token.report(feature, 1L, p1, p2);


      client.terminate();
    }
    catch (final FlxException e) {
      System.err.println(Client.getErrorDetails(e));
    }
    catch (final Throwable t) {
      System.err.println(t.getMessage());
    }
    finally {
      System.out.println("goodbye...");
    }
  }
}