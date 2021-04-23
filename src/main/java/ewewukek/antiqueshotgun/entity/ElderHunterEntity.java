package ewewukek.antiqueshotgun.entity;

import ewewukek.antiqueshotgun.AntiqueShotgunMod;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class ElderHunterEntity extends AbstractIllagerEntity {
    public ElderHunterEntity(EntityType<? extends ElderHunterEntity> type, World worldIn) {
        super(type, worldIn);
        setItemStackToSlot(EquipmentSlotType.MAINHAND, new ItemStack(AntiqueShotgunMod.ANTIQUE_SHOTGUN));
    }

    public static AttributeModifierMap createEntityAttributes() {
        return MonsterEntity.func_234295_eP_()
            .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.35)
            .createMutableAttribute(Attributes.FOLLOW_RANGE, 32.0)
            .createMutableAttribute(Attributes.ATTACK_DAMAGE, 10.0)
            .createMutableAttribute(Attributes.MAX_HEALTH, 24.0)
            .create();
    }

    @Override
    public void applyWaveBonus(int wave, boolean p_213660_2_) {
    }

    @Override
    public SoundEvent getRaidLossSound() {
        return null;
    }
}
