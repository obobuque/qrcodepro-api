package com.qrpro.application.port.in;

import com.qrpro.domain.model.QrCode;

public interface UpdateDynamicDestinationUseCase {
    QrCode updateDestination(String qrCodeId, UpdateDestinationCommand command);

    record UpdateDestinationCommand(
        String newUrl
    ) {}
}
