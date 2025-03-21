package com.revenera.gcs.btc;

import com.flexnet.lm.FlxException;
import com.revenera.gcs.btc.fne.Client;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.revenera.gcs.btc.Main.Metadata.*;
import static com.revenera.gcs.btc.fne.ReportType.*;
import static flxexamples.IdentityClient.IDENTITY_DATA;



public class Main {

  enum Metadata {
    USER_NAME, OS_NAME, OS_ARCH, OS_VERSION, MACHINE_NAME
  }

  public static void main(final String...args) {
    System.out.println("hello...");

    try {
      final Client client = Client
              .create()
              .withIdentity(IDENTITY_DATA)
              .withHostId("A249CC37-C6F2-463F-AD5C-C924CA9A6BC1")
              .withHostName("BTC Test Client")
              .withPublisher("flex1850-uat", "com")
              .initialize();

      final Client.Requester requester = client.createAnonymousRequester("0H37CS91CG51");

      final List<CompletableFuture<?>> futures = new ArrayList<>();

      requester.request(DesignImport)
               .submit();

      requester.request(DesignCommit)
               .submit();

      requester.request(DesignExport)
               .submit();

      requester.request(DesignExportXml)
               .submit();

      requester.request(DesignExportExcel)
               .withCount(2)
               .submit();

      requester.request(SimulationStart)
               .withCount(1)
               .withMetadata(USER_NAME, System.getProperty("user.name"))
               .withMetadata(OS_NAME, System.getProperty("os.name"))
               .withMetadata(OS_VERSION, System.getProperty("os.version"))
               .withMetadata(OS_ARCH, System.getProperty("os.arch"))
               .withMetadata(MACHINE_NAME, InetAddress.getLocalHost().getHostName())
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