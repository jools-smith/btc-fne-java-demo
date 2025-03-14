package com.revenera.gcs.btc.fne;

public class Feature {
  public String name;
  public String version;

  private Feature(final String name, final String version) {
    this.name = name;
    this.version = version;
  }

  public static Feature of(final String name, final String version) {
    return new Feature(name, version);
  }
}
