package ewewukek.antiqueshotgun.item.crafting.conditions;

import com.google.gson.JsonObject;

import ewewukek.antiqueshotgun.AntiqueShotgunMod;
import ewewukek.antiqueshotgun.item.AntiqueShotgunItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class CraftingEnabled implements ICondition {
    public static final ResourceLocation ID = new ResourceLocation(AntiqueShotgunMod.MODID, "crafting_enabled");
    public static final Serializer SERIALIZER = new Serializer();

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public boolean test() {
        return AntiqueShotgunItem.enableCrafting;
    }

    public static class Serializer implements IConditionSerializer<CraftingEnabled> {
        @Override
        public ResourceLocation getID() {
            return ID;
        }

        @Override
        public CraftingEnabled read(JsonObject json) {
            return new CraftingEnabled();
        }

        @Override
        public void write(JsonObject json, CraftingEnabled value) {
        }
    }
}
