//Problem: Ad Campaign Configuration Manager
/*
You are building the backend for an Ad Campaign Management Platform (think InMobi). The platform has:

A CampaignConfigManager that loads campaign targeting rules, budget caps, frequency caps, and geo-targeting configs from a config store at startup
This config is expensive to load (database call + in-memory cache warm-up)
Multiple services access it: BidEngine, FrequencyCapper, BudgetTracker, AudienceTargeter
If multiple instances of CampaignConfigManager exist, different services could read inconsistent configs mid-campaign, causing wrong bids or budget overruns

The constraint: There must be exactly one instance of CampaignConfigManager alive at any time in the JVM, shared across all services.
*/

// File: ClassicSingleton.java

// CampaignConfigManager is the real domain class. package-private (no public)

import java.util.*;

// Classic (Naive) Singleton . NOT thread-safe
// Problem: if two threads call getInstance() simultaneously when instance is null,
// both pass the null check and create two separate instances.
// This is acceptable only in single-threaded environments.

final class CampaignConfigManager{

    private static CampaignConfigManager instance;

    private Map<String, String> campaignTargetingRules;
    private int budgetCap;
    private int frequencyCap;
    private Set<String> geoTargetingConfigs;

    private CampaignConfigManager(){
        try{
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt(); 
        }
        //Simulating Loading the Config
        this.campaignTargetingRules = new HashMap<>();
        this.campaignTargetingRules.put("audience", "18-35");
        this.campaignTargetingRules.put("device", "mobile");
        this.budgetCap = 10000;
        this.frequencyCap = 5;
        this.geoTargetingConfigs = new HashSet<>(Arrays.asList("IN", "US", "SG"));
    }

    public static CampaignConfigManager getInstance(){
        if(instance == null){
            instance = new CampaignConfigManager();
        }
        return instance;
    }

    // Expose config via getters. Never expose mutable fields directly
    public Map<String, String> getCampaignTargetingRules() {
        return Collections.unmodifiableMap(campaignTargetingRules);
    }

    public int getBudgetCap() { return budgetCap; }
    public int getFrequencyCap() { return frequencyCap; }
    public Set<String> getGeoTargetingConfigs() {
        return Collections.unmodifiableSet(geoTargetingConfigs);
    }

}

// ClassicSingleton is the public entry point. This is to match filename
public class ClassicSingleton {
    // Client usage : how BidEngine, FrequencyCapper etc. consume this
    public static void main(String[] args) {
        CampaignConfigManager config1 = CampaignConfigManager.getInstance();
        CampaignConfigManager config2 = CampaignConfigManager.getInstance();

        System.out.println("Same instance? " + (config1 == config2)); // must print true
        System.out.println("Budget cap: " + config1.getBudgetCap());
        System.out.println("Geo targets: " + config1.getGeoTargetingConfigs());
    }
}