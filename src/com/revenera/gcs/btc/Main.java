package com.revenera.gcs.btc;

import com.flexnet.lm.FlxException;

import com.revenera.gcs.btc.fne.*;

import static com.revenera.gcs.btc.Metadata.*;
import static com.revenera.gcs.btc.fne.ReportType.*;
import static flxexamples.IdentityClient.IDENTITY_DATA;

enum Metadata {
  USER, OS, MACHINE, OTHER
}

public class Main {
  public static void main(final String...args) {
    System.out.println("hello...");
    try {

      final Client client = Client
              .create()
              //TODO: this needs to be BTC identity
              .withIdentity(IDENTITY_DATA)
              //TODO: specification of host-id or user-id needs to be completed
              .withHostId("A249CC37-C6F2-463F-AD5C-C924CA9A6BC1")
              .withHostName("BTC Test Client")
              .withPublisher("flex1115-uat", "com")
              .initialize();

      final Client.Requester requester = client.createAnonymousRequester("X6Z5CAX64BLR");


      requester.request(DesignImport).submit();
      requester.request(DesignCommit).submit();
      requester.request(DesignExport).submit();
      requester.request(DesignExportXml).submit();

      requester.request(DesignExportExcel)
              .withCount(1)
              .submit();

      requester.request(SimulationStart)
               .withCount(10)
               .withMetadata(USER, "")
               .withMetadata(OS, "")
               .withMetadata(MACHINE, "")
               .submit();

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