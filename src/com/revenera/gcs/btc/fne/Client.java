package com.revenera.gcs.btc.fne;

import com.flexnet.licensing.client.*;
import com.flexnet.lm.FlxException;
import com.flexnet.lm.SharedConstants;
import com.flexnet.lm.net.Comm;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
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

  /**
   * Opaque class to use for reporting - no knowledge of underlying FNE required
   */
  public class Requester {
    public final String id;
    public final String clsid;

    /**
     * Opaque class to use for building and submitting usage requests
     */
    public final class  Request {
      final ReportType type;
      final AtomicLong count = new AtomicLong(1L);
      final Map<String, String> metadata = new LinkedHashMap<>();

      Request(final ReportType type){
        this.type = type;
      }

      public Request withCount(final long value) {
        this.count.getAndSet(value);
        return this;
      }

      public <E extends Enum<E>> Request withMetadata(final E key, final Object value) {
        this.metadata.put(key.toString(), value.toString());
        return this;
      }

      public void submit() throws FlxException {
        final ICapabilityRequestOptions options = manager.createCapabilityRequestOptions();
        //REPORT == usage
        options.setRequestOperation(SharedConstants.RequestOperation.REPORT);

        options.addDesiredFeature(type.feature.name, type.feature.version, this.count.get());

        // add VD items to request
        if (!metadata.isEmpty()) {
          options.includeVendorDictionary(true);

          metadata.forEach(options::addVendorDictionaryItem);
        }

        options.setRequestorId(id);

        final byte[] request = manager.generateCapabilityRequest(options);

        final byte[] response = Comm.getHttpInstance(getServerUrl(clsid)).sendBinaryMessage(request);

        //TODO: debug
        if (Objects.nonNull(response) && response.length > 0) {
          final ICapabilityResponseData capabilityResponse = manager.getResponseDetails(response);
          capabilityResponse.getResponseStatus().forEach(status -> {
            System.err.println(status.getDetails());
          });
        }
      }
    }

    private Requester(final String clsid) {
      this(UUID.randomUUID().toString(), clsid);
    }

    private Requester(final String id, final String clsid) {
      this.id = id;
      this.clsid = clsid;
    }

    public Request request(final ReportType type) {
      return new Request(type);
    }
  }

  // load native library
  static {
    System.loadLibrary("FlxCore64");
  }

  public static String getErrorDetails(final FlxException e) {
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

  public Requester createAnonymousRequester(final String clsid) {
    return new Requester(clsid);
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

  public void terminate() {
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
