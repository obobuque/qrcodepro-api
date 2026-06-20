package com.qrpro.domain.service;

import com.qrpro.domain.model.QrCode;
import com.qrpro.domain.vo.QrCodeDesign;

public interface QrCodeGenerator {
    byte[] generate(QrCode qrCode);
    byte[] generateWithDesign(QrCode qrCode, QrCodeDesign design);
}
