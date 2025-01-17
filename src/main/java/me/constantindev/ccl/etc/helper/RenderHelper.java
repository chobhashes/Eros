package me.constantindev.ccl.etc.helper;

import com.mojang.blaze3d.systems.RenderSystem;
import me.constantindev.ccl.Cornos;
import me.constantindev.ccl.etc.render.RenderType;
import me.constantindev.ccl.etc.render.RenderableBlock;
import me.constantindev.ccl.etc.render.RenderableLine;
import me.constantindev.ccl.etc.render.RenderableText;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RenderHelper {
    public static List<RenderableBlock> BPQueue = new ArrayList<>();
    public static List<RenderableLine> B1B2LQueue = new ArrayList<>();
    public static List<RenderableText> B1S1TQueue = new ArrayList<>();

    public static void addToQueue(RenderableBlock block) {
        if (!BPQueue.contains(block)) BPQueue.add(block);
    }

    public static void addToQueue(RenderableLine line) {
        if (!B1B2LQueue.contains(line)) B1B2LQueue.add(line);
    }

    public static void addToQueue(RenderableText text) {
        if (!B1S1TQueue.contains(text)) B1S1TQueue.add(text);
    }

    public static void renderBlockOutline(Vec3d bpos, Vec3d dimensions, int r, int g, int b, int a, MatrixStack matrices, Camera camera) {
        Vec3d cameraPos = camera.getPos();
        VertexConsumerProvider.Immediate entityVertexConsumers = Cornos.minecraft.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer builder = entityVertexConsumers.getBuffer(RenderType.OVERLAY_LINES);

        matrices.push();
        matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        RenderHelper.renderBlockBounding(matrices, dimensions, builder, bpos, (float) r / 255, (float) g / 255, (float) b / 255, (float) a / 255);

        RenderSystem.disableDepthTest();
        matrices.pop();
        entityVertexConsumers.draw(RenderType.OVERLAY_LINES);
    }

    public static void renderLine(Vec3d from, Vec3d to, Color col, int width) {
        Camera c = BlockEntityRenderDispatcher.INSTANCE.camera;
        GL11.glPushMatrix();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(width);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glRotated(MathHelper.wrapDegrees(c.getPitch()), 1, 0, 0);
        GL11.glRotated(MathHelper.wrapDegrees(c.getYaw() + 180.0), 0, 1, 0);
        GL11.glTranslated(-c.getPos().x, -c.getPos().y, -c.getPos().z);

        GL11.glColor4f(col.getRed() / 255F, col.getGreen() / 255F, col.getBlue() / 255F, col.getAlpha() / 255F);

        GL11.glBegin(GL11.GL_LINES);
        {
            GL11.glVertex3d(from.x, from.y, from.z);
            GL11.glVertex3d(to.x, to.y, to.z);
        }
        GL11.glEnd();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);

        GL11.glPopMatrix();
    }

    public static Vec3d getClientLookVec() {
        ClientPlayerEntity player = Cornos.minecraft.player;
        double f = 0.017453292;
        double pi = Math.PI;

        assert player != null;
        double f1 = Math.cos(-player.yaw * f - pi);
        double f2 = Math.sin(-player.yaw * f - pi);
        double f3 = -Math.cos(-player.pitch * f);
        double f4 = Math.sin(-player.pitch * f);

        return new Vec3d(f2 * f3, f4, f1 * f3);
    }

    private static void renderBlockBounding(MatrixStack matrices, Vec3d dim, VertexConsumer builder, Vec3d bp, float r, float g, float b, float a) {
        if (bp == null) {
            return;
        }
        final double x = bp.getX(), y = bp.getY(), z = bp.getZ();
        //MinecraftClient.getInstance().gameRenderer.render();
        WorldRenderer.drawBox(matrices, builder, x, y, z, x + dim.x, y + dim.y, z + dim.z, r, g, b, a);
    }

    public static void drawImage(MatrixStack matrices, Identifier identifier, int x, int y, int imageWidth, int imageHeight) {

        RenderSystem.enableAlphaTest();
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(770, 771);

        Cornos.minecraft.getTextureManager().bindTexture(identifier);
        Screen.drawTexture(matrices, x, y, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);

        RenderSystem.disableAlphaTest();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();

    }
}
