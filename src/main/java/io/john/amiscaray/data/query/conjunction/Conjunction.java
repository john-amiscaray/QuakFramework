package io.john.amiscaray.data.query.conjunction;

import io.john.amiscaray.data.query.QueryCriteria;

import java.util.List;

public abstract class Conjunction implements QueryCriteria {

    protected final List<QueryCriteria> conditions;

    public Conjunction(List<QueryCriteria> conditions) {
        this.conditions = conditions;
    }

}
