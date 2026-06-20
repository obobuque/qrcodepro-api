package com.qrpro.application.port.out;

import com.qrpro.domain.model.ScanEvent;

public interface ScanEventIngestionPort {
    void ingest(ScanEvent event);
}
