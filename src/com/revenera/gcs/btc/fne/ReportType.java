package com.revenera.gcs.btc.fne;

public enum ReportType {

  EmbeddedTester("EmbeddedTester","25.1"),
  EmbeddedTesterRemote("EmbeddedTesterRemote","25.1");

  public final Feature feature;

  ReportType(final String name, final String version) {
    this.feature = Feature.of(name, version);
  }
}
