package io.john.amiscaray.quak.data.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public record DatabaseQuery(List<QueryCriteria> criteria) {

    public static DatabaseQueryBuilder builder() {
        return new DatabaseQueryBuilder();
    }

    public static class DatabaseQueryBuilder {
        private ArrayList<QueryCriteria> criteria;

        DatabaseQueryBuilder() {
        }

        public DatabaseQueryBuilder withCriteria(QueryCriteria criteria) {
            if (this.criteria == null) this.criteria = new ArrayList<>();
            this.criteria.add(criteria);
            return this;
        }

        public DatabaseQueryBuilder withCriteria(Collection<? extends QueryCriteria> criteria) {
            if (criteria == null) {
                throw new NullPointerException("criteria cannot be null");
            }
            if (this.criteria == null) this.criteria = new ArrayList<>();
            this.criteria.addAll(criteria);
            return this;
        }

        public DatabaseQueryBuilder clearCriteria() {
            if (this.criteria != null)
                this.criteria.clear();
            return this;
        }

        public DatabaseQuery build() {
            List<QueryCriteria> criteria;
            switch (this.criteria == null ? 0 : this.criteria.size()) {
                case 0:
                    criteria = java.util.Collections.emptyList();
                    break;
                case 1:
                    criteria = java.util.Collections.singletonList(this.criteria.get(0));
                    break;
                default:
                    criteria = java.util.Collections.unmodifiableList(new ArrayList<>(this.criteria));
            }

            return new DatabaseQuery(criteria);
        }

        public String toString() {
            return "DatabaseProxy.DatabaseQuery.DatabaseQueryBuilder(criteria=" + this.criteria + ")";
        }
    }
}