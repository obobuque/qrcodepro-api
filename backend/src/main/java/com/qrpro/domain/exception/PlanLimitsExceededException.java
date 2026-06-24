package com.qrpro.domain.exception;

public class PlanLimitsExceededException extends RuntimeException {
    private final String planId;
    private final String limitType;
    private final String upgradeUrl;

    public PlanLimitsExceededException(String planId, String limitType, String upgradeUrl) {
        super("Limite do plano " + planId + " excedido: " + limitType);
        this.planId = planId;
        this.limitType = limitType;
        this.upgradeUrl = upgradeUrl;
    }

    public String getPlanId() { return planId; }
    public String getLimitType() { return limitType; }
    public String getUpgradeUrl() { return upgradeUrl; }
}
