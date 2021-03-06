/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2019, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.buuz135.industrial.proxy.client.event;

import com.buuz135.industrial.module.ModuleCore;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class IFWorldRenderLastEvent {

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        ClientPlayerEntity playerIn = Minecraft.getInstance().player;
        if (!playerIn.getHeldItemMainhand().getItem().equals(ModuleCore.BOOK_MANUAL) || Minecraft.getInstance().currentScreen != null)
            return;
        float f = playerIn.rotationPitch;
        float f1 = playerIn.rotationYaw;
        double d0 = playerIn.func_233580_cy_().getX();
        double d1 = playerIn.func_233580_cy_().getY() + (double) playerIn.getEyeHeight();
        double d2 = playerIn.func_233580_cy_().getZ();
        Vector3d vec3d = new Vector3d(d0, d1, d2);
        float f2 = MathHelper.cos(-f1 * 0.017453292F - (float) Math.PI);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - (float) Math.PI);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d3 = 5.0D;
        Vector3d vec3d1 = vec3d.add((double) f6 * d3, (double) f5 * d3, (double) f7 * d3);
        RayTraceResult result = Minecraft.getInstance().world.rayTraceBlocks(new RayTraceContext(vec3d, vec3d1, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, playerIn));
        if (result != null && result.getType() == RayTraceResult.Type.BLOCK) {
            BlockPos pos = ((BlockRayTraceResult) result).getPos();
            if (Minecraft.getInstance().world.getBlockState(pos).getBlock().getRegistryName().getNamespace().equals(Reference.MOD_ID)) {
                //Minecraft.getInstance().fontRenderer.drawString(TextFormatting.GOLD + "SNEAK" + TextFormatting.WHITE + "+" + TextFormatting.GOLD + "Right Click", event.getResolution().getScaledWidth() / 2 + 10, event.getResolution().getScaledHeight() / 2 - 5, 0xFFFFFF, true);
                //Minecraft.getInstance().fontRenderer.drawString(TextFormatting.YELLOW + "Open Block Description", event.getResolution().getScaledWidth() / 2 + 10, event.getResolution().getScaledHeight() / 2 - 5 + Minecraft.getInstance().fontRenderer.FONT_HEIGHT + 2, 0xFFFFFF, true);

            }
        }
    }
}
