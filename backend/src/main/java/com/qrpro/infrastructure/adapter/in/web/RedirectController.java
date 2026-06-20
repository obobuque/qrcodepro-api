package com.qrpro.infrastructure.adapter.in.web;

import com.qrpro.application.port.in.ProcessScanUseCase;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@Slf4j
@RestController
@RequestMapping("/r")
@RequiredArgsConstructor
public class RedirectController {

    private final ProcessScanUseCase processScanUseCase;

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode,
            HttpServletRequest request) {

        long startTime = System.nanoTime();

        var metadata = new ProcessScanUseCase.ScanMetadata(
            request.getRemoteAddr(),
            request.getHeader("User-Agent"),
            request.getHeader("Referer"),
            Instant.now()
        );

        var result = processScanUseCase.process(shortCode, metadata);

        long durationMs = (System.nanoTime() - startTime) / 1_000_000;
        log.debug("Redirect processed in {}ms for shortCode: {}", durationMs, shortCode);

        if (result.redirectUrl() == null) {
            return ResponseEntity.status(HttpStatus.GONE).build();
        }

        return ResponseEntity.status(HttpStatus.FOUND)
            .header("Location", result.redirectUrl())
            .header("Cache-Control", "no-cache, no-store, must-revalidate")
            .build();
    }
}
