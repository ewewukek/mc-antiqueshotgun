package ewewukek.antiqueshotgun;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Vector3f;

public class FirstPersonRenderHelper {
    public static void renderFirstPersonShotgun(FirstPersonRenderer renderer, AbstractClientPlayerEntity player, Hand hand, float partialTicks, float interpolatedPitch, float swingProgress, float equipProgress, ItemStack stack, MatrixStack matrixStack, IRenderTypeBuffer render, int packedLight) {
        HandSide handside = hand == Hand.MAIN_HAND ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
        boolean isRightHand = handside == HandSide.RIGHT;
        float sign = isRightHand ? 1 : -1;

        matrixStack.push();

        matrixStack.translate(sign * 0.15f, -0.27f, -0.37f);

        // compensate rotated model
        matrixStack.translate(0, 0.085f, 0);
        matrixStack.rotate(Vector3f.XP.rotationDegrees(-70));

        renderer.renderItemSide(player, stack, isRightHand ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !isRightHand, matrixStack, render, packedLight);

        matrixStack.pop();
    }
}
