package com.revenera.gcs.btc.fne;

public enum ReportType {
  SimulationStart("simulation.start","1"),
  DesignImport("design.import","1"),
  DesignCommit("design.commit","1"),
  DesignExport("des.ign.export","1"),
  DesignExportXml("design.export.xml","1"),
  DesignExportExcel("design.export.excel","1");

  public final Feature feature;

  ReportType(final String name, final String version) {
    this.feature = Feature.of(name, version);
  }
}
