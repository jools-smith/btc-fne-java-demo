package com.revenera.gcs.btc;

import com.flexnet.lm.FlxException;
import com.revenera.gcs.btc.fne.Client;
import com.revenera.gcs.btc.fne.ReportType;
import flxexamples.IdentityClient;

import java.net.InetAddress;

import static com.revenera.gcs.btc.Main.Metadata.*;

public class Main {

  enum Metadata {
    USER_NAME, OS_NAME, OS_ARCH, OS_VERSION, MACHINE_NAME
  }

  public static void main(final String...args) {
    System.out.println("hello...");

    try {
      final Client client = Client
              .create()
              .withIdentity(IdentityClient.IDENTITY_DATA)
              .withHostId("A249CC37-C6F2-463F-AD5C-C924CA9A6BC1")
              .withHostName("Revenera Test Client")
              .withPublisher("flex13064-uat", "eu")
              .initialize();

      final Client.Requester builder = client.createAnonymousRequester("5RF8TJB7Z1H7");
      
      builder.request(ReportType.EmbeddedTester)
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