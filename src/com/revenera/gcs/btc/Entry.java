package com.revenera.gcs.btc;

public class Entry {
  public final String key;
  public final String value;

  private Entry(final String key, final String value) {
    this.key = key;
    this.value = value;
  }

  public static Entry of(final String key, final String value) {
    return new Entry(key, value);
  }
}
