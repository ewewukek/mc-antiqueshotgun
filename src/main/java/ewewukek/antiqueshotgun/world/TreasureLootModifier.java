package ewewukek.antiqueshotgun.world;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;

public class TreasureLootModifier extends LootModifier {
    public static final Serializer SERIALIZER = new Serializer();

    private final Map<String, LootEntry[]> overrides;

    protected TreasureLootModifier(ILootCondition[] conditionsIn, Map<String, LootEntry[]> overrides) {
        super(conditionsIn);
        this.overrides = overrides;
    }

    @Override
    public List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        LootEntry[] entries = overrides.get(context.getQueriedLootTableId().toString());
        if (entries != null) {
            Random random = context.getRandom();
            for (LootEntry entry: entries) {
                if (random.nextFloat() < entry.probability) {
                    generatedLoot.add(new ItemStack(entry.item));
                }
            }
        }
        return generatedLoot;
    }

    public static class LootEntry {
        public final Item item;
        public final float probability;

        public LootEntry(Item item, float probability) {
            this.item = item;
            this.probability = probability;
        }
    }

    public static class Serializer extends GlobalLootModifierSerializer<TreasureLootModifier> {
        @Override
        public TreasureLootModifier read(ResourceLocation location, JsonObject object,
            ILootCondition[] conditions) {

            JsonObject overridesIn = object.getAsJsonObject("overrides");
            Map<String, LootEntry[]> overrides = new HashMap<>();

            for (Map.Entry<String, JsonElement> override: overridesIn.entrySet()) {
                String tableId = override.getKey();
                JsonArray entriesIn = override.getValue().getAsJsonArray();
                LootEntry[] entries = new LootEntry[entriesIn.size()];

                for (int i = 0; i < entries.length; ++i) {
                    JsonObject entryIn = entriesIn.get(i).getAsJsonObject();

                    String itemId = entryIn.getAsJsonPrimitive("item").getAsString();
                    float probability = entryIn.getAsJsonPrimitive("probability").getAsFloat();

                    entries[i] = new LootEntry(ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId)), probability);
                }
                overrides.put(tableId, entries);
            }

            return new TreasureLootModifier(conditions, overrides);
        }

        @Override
        public JsonObject write(TreasureLootModifier instance) {
            return makeConditions(instance.conditions);
        }
    }
}
