package com.revenera.gcs.btc.fne;

public enum ReportType {
  SimulationStart("simulation.start","2025"),
  DesignImport("design.import","2025"),
  DesignCommit("design.commit","2025"),
  DesignExport("design.export","2025"),
  DesignExportXml("design.export.xml","2025"),
  DesignExportExcel("design.export.excel","2025");

  public final Feature feature;

  ReportType(final String name, final String version) {
    this.feature = Feature.of(name, version);
  }
}
