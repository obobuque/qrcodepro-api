package com.qrpro.application.port.in;

import com.qrpro.domain.model.QrCode;
import com.qrpro.domain.vo.QrCodeDesign;

public interface GenerateQrCodeUseCase {
    QrCode generateStatic(StaticQrCommand command);
    QrCode generateDynamic(DynamicQrCommand command);

    record StaticQrCommand(
        String ownerId,
        String content,
        QrCodeDesign design
    ) {}

    record DynamicQrCommand(
        String ownerId,
        String initialUrl,
        QrCodeDesign design
    ) {}
}
