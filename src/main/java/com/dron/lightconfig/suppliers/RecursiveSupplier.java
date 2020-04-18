package com.dron.lightconfig.suppliers;

import com.dron.lightconfig.suppliers.exceptions.SupplyingException;
import com.dron.lightconfig.utils.MiscUtils;

import java.util.Optional;
import java.util.Set;
import java.util.Stack;

/**
 * Supplier decorator which can resolve recursive references
 *
 * @author Vasiliy Dronov
 */
public class RecursiveSupplier implements RawPropertiesSupplier {

    /**
     * Original, not recursive supplier
     */
    private final RawPropertiesSupplier originalSupplier;

    /**
     * Prefix for placeholders
     */
    private final String prefix;

    /**
     * Suffix for placeholders
     */
    private final String suffix;

    /**
     * Constructs supplier with default prefix and suffix for placeholders - '${place.holder}'
     *
     * @param originalSupplier Original, not recursive supplier
     */
    public RecursiveSupplier(RawPropertiesSupplier originalSupplier) {
        this(originalSupplier, "${", "}");
    }

    /**
     * Constructs supplier with custom prefix and suffix for placeholders - '${place.holder}'
     *
     * @param originalSupplier Original, not recursive supplier
     * @param prefix           Prefix for placeholders
     * @param suffix           suffix for placeholders
     */
    public RecursiveSupplier(RawPropertiesSupplier originalSupplier, String prefix, String suffix) {
        if (originalSupplier == null) {
            throw new NullPointerException("Nested original supplier can't be null");
        }
        this.originalSupplier = originalSupplier;
        this.prefix = prefix;
        this.suffix = suffix;
    }


    @Override
    public Optional<String> supply(String propertyName) {
        Optional<String> supply = originalSupplier.supply(propertyName);
        if (!supply.isPresent()) {
            return Optional.empty();
        }

        String originalValue = supply.get();

        Set<String> placeholders = MiscUtils.extractPlaceholders(originalValue, prefix, suffix);
        if (placeholders.isEmpty()) {
            return supply;
        }

        Stack<String> stack = new Stack<>();
        stack.addAll(placeholders);

        ReferenceTree referenceTree = new ReferenceTree(propertyName, originalValue);
        referenceTree.addReferencedKeys(propertyName, placeholders);

        while (!stack.isEmpty()) {
            String key = stack.pop();
            String value = originalSupplier.supply(key).orElseThrow(
                    () -> new SupplyingException("Required key is absent, key:" + key + ", tree:" + referenceTree)
            );
            referenceTree.setNodeValue(key, value);
            Set<String> referencedKeys = MiscUtils.extractPlaceholders(value, prefix, suffix);
            if (!referencedKeys.isEmpty()) {
                referenceTree.addReferencedKeys(key, referencedKeys);
                stack.addAll(referencedKeys);
            }
        }


        String finalValue = referenceTree.calculateValue(prefix, suffix);

        return Optional.ofNullable(finalValue);
    }


    /**
     * Gets Prefix for placeholders.
     *
     * @return Value of Prefix for placeholders.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Gets Suffix for placeholders.
     *
     * @return Value of Suffix for placeholders.
     */
    public String getSuffix() {
        return suffix;
    }
}
