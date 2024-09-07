package io.john.amiscaray.backend.framework.data.update;

import lombok.Singular;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public record FieldUpdate<T>(String fieldName, List<UpdateExpression<T>> updates) {

    public static <T> FieldUpdateBuilder<T> builder(String fieldName) {
        return new FieldUpdateBuilder<>(fieldName);
    }

    public static class FieldUpdateBuilder<T> {
        private final String fieldName;
        private ArrayList<UpdateExpression<T>> updates;

        FieldUpdateBuilder(String fieldName) {
            this.fieldName = fieldName;
        }

        public FieldUpdateBuilder<T> apply(UpdateExpression<T> apply) {
            if (this.updates == null) this.updates = new ArrayList<UpdateExpression<T>>();
            this.updates.add(apply);
            return this;
        }

        public FieldUpdateBuilder<T> updates(Collection<? extends UpdateExpression<T>> updates) {
            if (updates == null) {
                throw new NullPointerException("updates cannot be null");
            }
            if (this.updates == null) this.updates = new ArrayList<UpdateExpression<T>>();
            this.updates.addAll(updates);
            return this;
        }

        public FieldUpdateBuilder<T> clearUpdates() {
            if (this.updates != null)
                this.updates.clear();
            return this;
        }

        public FieldUpdate<T> build() {
            List<UpdateExpression<T>> updates;
            switch (this.updates == null ? 0 : this.updates.size()) {
                case 0:
                    updates = java.util.Collections.emptyList();
                    break;
                case 1:
                    updates = java.util.Collections.singletonList(this.updates.get(0));
                    break;
                default:
                    updates = java.util.Collections.unmodifiableList(new ArrayList<UpdateExpression<T>>(this.updates));
            }

            return new FieldUpdate<T>(this.fieldName, updates);
        }

        public String toString() {
            return "FieldUpdate.FieldUpdateBuilder(fieldName=" + this.fieldName + ", updates=" + this.updates + ")";
        }
    }
}
