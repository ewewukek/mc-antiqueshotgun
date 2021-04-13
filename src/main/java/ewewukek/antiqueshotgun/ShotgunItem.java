package ewewukek.antiqueshotgun;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ShotgunItem extends Item {
    public ShotgunItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity player, Hand hand) {
        System.out.println("PEW!");
        ItemStack stack = player.getHeldItem(hand);
        return ActionResult.resultConsume(stack);
    }
}
