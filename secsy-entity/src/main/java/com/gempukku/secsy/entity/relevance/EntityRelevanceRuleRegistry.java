package com.gempukku.secsy.entity.relevance;

public interface EntityRelevanceRuleRegistry {
    void registerEntityRelevanceRule(EntityRelevanceRule entityRelevanceRule);

    void deregisterEntityRelevanceRule(EntityRelevanceRule entityRelevanceRule);
}
