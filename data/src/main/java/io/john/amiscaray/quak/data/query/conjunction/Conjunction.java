package io.john.amiscaray.quak.data.query.conjunction;

import io.john.amiscaray.quak.data.query.QueryCriteria;

import java.util.List;

public abstract class Conjunction implements QueryCriteria {

    protected final List<QueryCriteria> conditions;

    public Conjunction(List<QueryCriteria> conditions) {
        this.conditions = conditions;
    }

}
