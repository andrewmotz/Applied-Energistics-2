package appeng.integration.modules.jei.throwinginwater;

import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class ThrowingInWaterDisplay {
    private final boolean supportsAccelerators;

    private final List<Ingredient> ingredients;
    private final ItemStack result;

    public ThrowingInWaterDisplay(List<Ingredient> inputs, ItemStack result, boolean supportsAccelerators) {
        this.ingredients = inputs;
        this.result = result;
        this.supportsAccelerators = supportsAccelerators;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public ItemStack getResult() {
        return result;
    }

    public boolean isSupportsAccelerators() {
        return supportsAccelerators;
    }
}
