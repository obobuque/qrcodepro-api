package com.qrpro.application.port.out;

import com.qrpro.domain.vo.ShortCode;

public interface QrCodeCachePort {
    void cacheDestination(ShortCode shortCode, String url);
    String getDestination(ShortCode shortCode);
    void invalidate(ShortCode shortCode);
}
