package io.john.amiscaray.backend.framework.data.query.conjunction;

import io.john.amiscaray.backend.framework.data.query.QueryCriteria;

import java.util.List;

public abstract class Conjunction implements QueryCriteria {

    protected final List<QueryCriteria> conditions;

    public Conjunction(List<QueryCriteria> conditions) {
        this.conditions = conditions;
    }

}
