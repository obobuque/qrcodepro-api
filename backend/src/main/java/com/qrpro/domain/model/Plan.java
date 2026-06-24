package com.qrpro.domain.model;

public enum Plan {
    FREE("free", "Free", 0, 10, 100, false, false, true, false, 30),
    STARTER("starter", "Starter", 900, 100, 10000, true, false, true, false, 120),
    PRO("pro", "Pro", 2900, 1000, 100000, true, true, true, false, 300),
    BUSINESS("business", "Business", 9900, 999999, 1000000, true, true, true, true, 1000);

    private final String id;
    private final String displayName;
    private final int priceCents;
    private final int maxQrCodes;
    private final int maxScansPerMonth;
    private final boolean allowsDynamic;
    private final boolean allowsCustomLogo;
    private final boolean allowsCustomColors;
    private final boolean allowsWebhooks;
    private final int apiRateLimitPerMinute;

    Plan(String id, String displayName, int priceCents, int maxQrCodes, int maxScansPerMonth,
         boolean allowsDynamic, boolean allowsCustomLogo, boolean allowsCustomColors,
         boolean allowsWebhooks, int apiRateLimitPerMinute) {
        this.id = id;
        this.displayName = displayName;
        this.priceCents = priceCents;
        this.maxQrCodes = maxQrCodes;
        this.maxScansPerMonth = maxScansPerMonth;
        this.allowsDynamic = allowsDynamic;
        this.allowsCustomLogo = allowsCustomLogo;
        this.allowsCustomColors = allowsCustomColors;
        this.allowsWebhooks = allowsWebhooks;
        this.apiRateLimitPerMinute = apiRateLimitPerMinute;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public int getPriceCents() { return priceCents; }
    public int getMaxQrCodes() { return maxQrCodes; }
    public int getMaxScansPerMonth() { return maxScansPerMonth; }
    public boolean isAllowsDynamic() { return allowsDynamic; }
    public boolean isAllowsCustomLogo() { return allowsCustomLogo; }
    public boolean isAllowsCustomColors() { return allowsCustomColors; }
    public boolean isAllowsWebhooks() { return allowsWebhooks; }
    public int getApiRateLimitPerMinute() { return apiRateLimitPerMinute; }

    public static Plan fromId(String id) {
        for (Plan plan : values()) {
            if (plan.id.equalsIgnoreCase(id)) return plan;
        }
        return FREE;
    }
}
