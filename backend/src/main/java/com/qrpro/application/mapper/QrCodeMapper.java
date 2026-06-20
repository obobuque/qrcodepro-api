package com.qrpro.application.mapper;

import com.qrpro.application.dto.response.QrCodeResponse;
import com.qrpro.domain.model.QrCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface QrCodeMapper {

    @Mapping(source = "type", target = "type", qualifiedByName = "enumToString")
    @Mapping(source = "shortCode", target = "shortCode", qualifiedByName = "shortCodeToString")
    @Mapping(source = "content", target = "content", qualifiedByName = "contentToString")
    @Mapping(source = "destination", target = "destinationUrl", qualifiedByName = "destinationToString")
    @Mapping(source = "design.foregroundColor", target = "foregroundColor")
    @Mapping(source = "design.backgroundColor", target = "backgroundColor")
    @Mapping(source = "design.dotStyle", target = "dotStyle", qualifiedByName = "enumToString")
    @Mapping(source = "design.errorCorrectionLevel", target = "errorCorrectionLevel", qualifiedByName = "enumToString")
    @Mapping(source = "design.logo", target = "sizePixels", qualifiedByName = "logoToSize")
    QrCodeResponse toResponse(QrCode qrCode);

    @Named("enumToString")
    default String enumToString(Enum<?> enumValue) {
        return enumValue != null ? enumValue.name() : null;
    }

    @Named("shortCodeToString")
    default String shortCodeToString(com.qrpro.domain.vo.ShortCode shortCode) {
        return shortCode != null ? shortCode.value() : null;
    }

    @Named("contentToString")
    default String contentToString(com.qrpro.domain.vo.QrCodeContent content) {
        return content != null ? content.value() : null;
    }

    @Named("destinationToString")
    default String destinationToString(com.qrpro.domain.vo.DynamicDestination destination) {
        return destination != null ? destination.url() : null;
    }

    @Named("logoToSize")
    default int logoToSize(Object logo) {
        return logo != null ? 500 : 300;
    }
}
