package io.john.amiscaray.quak.data.update;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A class representing a single database update operation. Often used with the static methods of {@link io.john.amiscaray.quak.data.update.UpdateExpression UpdateExpression} for semantic field updates:<br>
 * <pre> {@code
 *import static io.john.amiscaray.quak.data.update.UpdateExpression.*;
 *
 *public class Test {
 *    public void test() {
 *         dbProxy.updateAll(Employee.class,
 *             FieldUpdate.<Number>forField("salary")
 *                 .apply(multiply(1.5))
 *                 .apply(add(2000))
 *                 .build()
 *          );
 *    }
 *}}</pre>
 * @param fieldName The name of the field to update.
 * @param updates The changes made to the value of the field.
 * @param <T> The entity class this applies to.
 */
public record FieldUpdate<T>(String fieldName, List<UpdateExpression<T>> updates) {

    /**
     * Creates a builder for a field with a given name.
     * @param fieldName The name of the field to update.
     * @return A builder for a field update.
     * @param <T> The type of the field being updated.
     */
    public static <T> FieldUpdateBuilder<T> forField(String fieldName) {
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
