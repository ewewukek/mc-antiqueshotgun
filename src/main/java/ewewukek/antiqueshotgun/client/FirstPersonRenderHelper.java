package ewewukek.antiqueshotgun.client;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FirstPersonRenderHelper {
    public static void renderFirstPersonShotgun(FirstPersonRenderer renderer, AbstractClientPlayerEntity player, Hand hand, float partialTicks, float interpolatedPitch, float swingProgress, float equipProgress, ItemStack stack, MatrixStack matrixStack, IRenderTypeBuffer render, int packedLight) {
        HandSide handside = hand == Hand.MAIN_HAND ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
        boolean isRightHand = handside == HandSide.RIGHT;
        float sign = isRightHand ? 1 : -1;

        matrixStack.push();

        if (swingProgress > 0) {
            float swingSharp = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
            float swingNormal = MathHelper.sin(swingProgress * (float)Math.PI);
            matrixStack.translate(sign * (-0.05f * swingNormal), -0.05f * swingNormal, -0.4f * swingSharp);
            matrixStack.translate(sign * 0.31f, -0.26f, -0.41f);
            matrixStack.rotate(Vector3f.XP.rotationDegrees(180 + sign * (20 - 20 * swingSharp)));

        } else {
            matrixStack.translate(sign * 0.31f, -0.29f, -0.41f);
        }

        // compensate rotated model
        matrixStack.translate(0, 0.085f, 0);
        matrixStack.rotate(Vector3f.XP.rotationDegrees(-70));

        renderer.renderItemSide(player, stack, isRightHand ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !isRightHand, matrixStack, render, packedLight);

        matrixStack.pop();
    }
}
