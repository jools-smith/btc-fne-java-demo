package com.revenera.gcs.btc;

import com.flexnet.licensing.client.*;
import com.flexnet.lm.FlxException;
import com.flexnet.lm.SharedConstants;
import com.flexnet.lm.net.Comm;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Client {
  byte[] identity = null;
  String tenant = null;
  String domain = null;
  String hostId = null;
  String hostName = null;
  String hostType = "FLX_CLIENT";
  String storagePath = null;
  ILicensing licensing = null;
  ILicenseManager manager = null;

  public class ServerToken {
    public final String id;
    public final String clsid;

    private ServerToken(final String clsid) {
      this.id = UUID.randomUUID().toString();
      this.clsid = clsid;
    }

    public void report(final ReportType type, final long count, final Entry...vd) throws FlxException {
      final ICapabilityRequestOptions options = manager.createCapabilityRequestOptions();
      options.setRequestOperation(SharedConstants.RequestOperation.REPORT);
      options.addDesiredFeature(type.feature.name, type.feature.version, count);

      if (Objects.nonNull(vd)) {
        options.includeVendorDictionary(true);
        for (final Entry item : vd) {
          options.addVendorDictionaryItem(item.key, item.value);
        }
      }

      options.setRequestorId(id);

      final byte[] request = manager.generateCapabilityRequest(options);

      final byte[] response = Comm.getHttpInstance(getServerUrl(clsid)).sendBinaryMessage(request);

      //TODO: debug
      final ICapabilityResponseData capabilityResponse = manager.getResponseDetails(response);
      capabilityResponse.getResponseStatus().forEach(status -> {
        System.err.println(status.getDetails());
      });
    }
  }


  static {
    System.loadLibrary("FlxCore64");
  }

  static String getErrorDetails(final FlxException e) {
    return new ArrayList<Object>() {
      {
        Optional.ofNullable(e.getKey()).ifPresent(this::add);
        Optional.ofNullable(e.getDiagnosticMessage()).ifPresent(this::add);
        Optional.ofNullable(e.getMessage()).ifPresent(this::add);
        Optional.ofNullable(e.getArguments()).ifPresent(x -> this.addAll(Arrays.asList(x)));
      }
    }.stream().map(Object::toString).collect(Collectors.joining("\n"));
  }

  private Client() {

  }

  public ServerToken createServerToken(final String clsid) {
    return new ServerToken(clsid);
  }

  public Client withPublisher(final String tenant, final String domain) {
    this.tenant = tenant;
    this.domain = domain;
    return this;
  }

  public Client withIdentity(final byte[] value) {
    this.identity = value;
    return this;
  }

  public Client withStoragePath(final Path value) {
    this.storagePath = value.toAbsolutePath().toString();
    return this;
  }

  public Client withHostName(final String value) {
    this.hostName = value;
    return this;
  }

  public Client withHostType(final String value) {
    this.hostType = value;
    return this;
  }

  public Client withHostId(final String value) {
    this.hostId = value;
    return this;
  }

  public Client initialize() throws FlxException {
    this.licensing = LicensingFactory.getLicensing(this.identity, this.storagePath, this.hostId);

    this.manager = licensing.getLicenseManager();

    if (Objects.nonNull(this.hostName)) {
      this.manager.setHostName(this.hostName);
    }

    if (Objects.nonNull(this.hostType)) {
      this.manager.setHostType(this.hostType);
    }

    // will be in memory if no storage path provided
    this.manager.addTrustedStorageLicenseSource();
    
    return this;
  }

  public boolean isInitialized() {

    return this.licensing != null && this.manager != null;
  }

  void terminate() {
    this.licensing.close();
  }

  public String getServerUrl(final String clsid) {
    return String.format("https://%s.compliance.flexnetoperations.%s/instances/%s/request",
                         tenant,
                         domain,
                         clsid);
  }

  public static Client create() {
    return new Client();
  }
}
