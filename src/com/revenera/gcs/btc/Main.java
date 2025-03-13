package com.revenera.gcs.btc;

import com.flexnet.lm.FlxException;

import static com.revenera.gcs.btc.ReportType.*;
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

      token.report(DesignImport, 1L);
      token.report(DesignCommit, 1L);
      token.report(DesignExport, 1L);
      token.report(DesignExportXml, 1L);
      token.report(DesignExportExcel, 1L);
      token.report(SimulationStart, 1L, Entry.of("name", "jools"), Entry.of("company", "revenera"));

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