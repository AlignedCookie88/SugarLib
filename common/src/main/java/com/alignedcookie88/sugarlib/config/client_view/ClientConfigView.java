package com.alignedcookie88.sugarlib.config.client_view;

import com.alignedcookie88.sugarlib.ModInfo;
import com.alignedcookie88.sugarlib.config.ConfigOption;
import com.alignedcookie88.sugarlib.config.client_view.networking.ClientConfigViewNetworking;
import com.alignedcookie88.sugarlib.config.ui.optionuiprovider.OptionUIProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

public interface ClientConfigView<V extends ClientConfigView<V>> {

    Collection<Option<?, V>> getOptions();


    /**
     * Options may be made invalid upon calling this, always re-call getOptions()!
     */
    void load();


    /**
     * Options may be made invalid upon calling this, always re-call getOptions()!
     */
    void save();



    boolean isWritable();


    /**
     * Internal function, use Option.get().
     */
    @ApiStatus.Internal
    <T> T _internal_getValue(Option<T, V> opt);


    /**
     * Checks if any values have changed.
     */
    default boolean haveAnyValuesChanged() {
        return getOptions().stream().anyMatch(Option::hasChanged);
    }

    /**
     * Starts loading the config from the server.
     */
    default void startLoad() {

    }


    boolean hasFinishedLoading();


    Component getName();

    ModInfo getMod();


    abstract class Option<T, V extends ClientConfigView<V>> {

        protected ConfigOption<T> opt;

        protected V view;

        protected T change;

        protected Option(ConfigOption<T> opt, V view) {
            this.opt = opt;
            this.view = view;
        }

        public void set(T value) {
            T current = getCurrentValue();

            if (value instanceof Float vf && current instanceof Float cf) {
                if (cf.equals(vf)){
                    this.change = null;
                    return;
                }
            }

            if (value instanceof Double vf && current instanceof Double cf) {
                if (cf.equals(vf)){
                    this.change = null;
                    return;
                }
            }

            if (value != current)
                this.change = value;
            else this.change = null;
        }

        public void setIfTypeCorrect(Object value) {
            try {
                set((T) value);
            } catch (ClassCastException ignored) {

            }
        }

        public OptionUIProvider<T> getUIProvider() {
            return opt.getUIProvider();
        }

        public boolean hasChanged() {
            return change != null;
        }

        private T getCurrentValue() {
            return view._internal_getValue(this);
        }

        /**
         * Gets the value of this option
         * @return The value
         */
        public T get() {
            if (change != null)
                return change;
            T value = getCurrentValue();
            if (value != null)
                return value;
            return opt.defaultValue;
        }

        public Class<T> getValueClass() {
            return opt.clazz;
        }

        public Component getName() {
            return opt.name;
        }

        /**
         * Checks if a value is valid.
         * @param value The value
         * @return null if valid, or an error message if invalid.
         */
        public Component isValid(T value) {
            if (opt.valueLimiter == null)
                return null;
            return opt.valueLimiter.getLimit(value);
        }

        public Component isSelfValid() {
            return isValid(get());
        }

        public String getId() {
            return opt.id;
        }

        public ResourceLocation getFullId() {
            return opt.getFullId();
        }

        public T getDefault() {
            return opt.defaultValue;
        }

        public boolean isResettable() {
            T d = getDefault();
            T v = get();

            if (d instanceof String ds && v instanceof String vs) {
                return !ds.equals(vs);
            }
            if (d instanceof Float df && v instanceof Float vf) {
                return !df.equals(vf);
            }
            if (d instanceof Double df && v instanceof Double vf) {
                return !df.equals(vf);
            }

            return d != v;
        }

        public void reset() {
            set(getDefault());
        }



        void sendRemotely() {
            ClientConfigViewNetworking.ConfigUpdate.setOption(this.getId(), this.get(), this.getValueClass());
        }

    }

}
