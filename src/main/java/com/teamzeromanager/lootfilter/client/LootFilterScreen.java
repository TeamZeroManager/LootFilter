package com.teamzeromanager.lootfilter.client;

import com.teamzeromanager.lootfilter.config.LootFilterSelection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public final class LootFilterScreen extends Screen {
    private static final int COLUMNS = 9;
    private static final int PAGE_SIZE = COLUMNS * 4;
    private static final int SLOT_SIZE = 20;

    private final Category category;
    private final int page;

    public LootFilterScreen() {
        this(Category.BLOCKS, 0);
    }

    private LootFilterScreen(Category category, int page) {
        super(Component.literal("LootFilter"));
        this.category = category;
        this.page = Math.max(0, page);
    }

    @Override
    protected void init() {
        int center = width / 2;
        addRenderableWidget(Button.builder(Component.literal("Blocks"), button -> open(Category.BLOCKS, 0))
                .bounds(center - 104, 32, 100, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Items"), button -> open(Category.ITEMS, 0))
                .bounds(center + 4, 32, 100, 20).build());
        addRenderableWidget(Button.builder(Component.literal("< Previous"), button -> open(category, Math.max(0, page - 1)))
                .bounds(center - 104, height - 32, 100, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Next >"), button -> open(category, Math.min(page + 1, maxPage())))
                .bounds(center + 4, height - 32, 100, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Done"), button -> onClose())
                .bounds(center - 40, height - 56, 80, 20).build());
    }

    private void open(Category nextCategory, int nextPage) {
        Minecraft.getInstance().setScreen(new LootFilterScreen(nextCategory, nextPage));
    }

    private int maxPage() {
        return Math.max(0, (entries().size() - 1) / PAGE_SIZE);
    }

    private List<Entry> entries() {
        List<Entry> result = new ArrayList<>();
        if (category == Category.BLOCKS) {
            for (Block block : BuiltInRegistries.BLOCK) {
                Item item = block.asItem();
                ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
                if (item != Items.AIR && id != null) result.add(new Entry(id.toString(), "block:" + id, new ItemStack(item)));
            }
        } else {
            for (Item item : BuiltInRegistries.ITEM) {
                ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
                if (item != Items.AIR && id != null) result.add(new Entry(id.toString(), "item:" + id, new ItemStack(item)));
            }
        }
        return result;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, title, width / 2, 12, 0xFFFFFF);
        graphics.drawCenteredString(font,
                Component.literal((category == Category.BLOCKS ? "Block filter" : "Item filter") + "  •  Page " + (page + 1) + " / " + (maxPage() + 1)),
                width / 2, 57, 0xDDDDDD);

        List<Entry> all = entries();
        int start = page * PAGE_SIZE;
        int end = Math.min(all.size(), start + PAGE_SIZE);
        int left = width / 2 - (COLUMNS * SLOT_SIZE) / 2;
        int top = 80;
        for (int index = start; index < end; index++) {
            Entry entry = all.get(index);
            int slot = index - start;
            int x = left + (slot % COLUMNS) * SLOT_SIZE;
            int y = top + (slot / COLUMNS) * SLOT_SIZE;
            boolean enabled = LootFilterSelection.isEnabled(entry.selectionKey());
            graphics.fill(x, y, x + 18, y + 18, enabled ? 0xAA287A36 : 0xAA555555);
            graphics.renderItem(entry.stack(), x + 1, y + 1);
            if (mouseX >= x && mouseX < x + 18 && mouseY >= y && mouseY < y + 18) {
                graphics.renderTooltip(font, Component.literal(entry.id()), mouseX, mouseY);
            }
        }
        graphics.drawCenteredString(font, "Green = enabled    Gray = disabled", width / 2, height - 70, 0xCCCCCC);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            List<Entry> all = entries();
            int start = page * PAGE_SIZE;
            int left = width / 2 - (COLUMNS * SLOT_SIZE) / 2;
            int top = 80;
            for (int index = start; index < Math.min(all.size(), start + PAGE_SIZE); index++) {
                int slot = index - start;
                int x = left + (slot % COLUMNS) * SLOT_SIZE;
                int y = top + (slot / COLUMNS) * SLOT_SIZE;
                if (mouseX >= x && mouseX < x + 18 && mouseY >= y && mouseY < y + 18) {
                    LootFilterSelection.toggle(all.get(index).selectionKey());
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private enum Category { BLOCKS, ITEMS }
    private record Entry(String id, String selectionKey, ItemStack stack) {}
}
