package com.teamzeromanager.lootfilter.client;

import com.teamzeromanager.lootfilter.config.LootFilterSelection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
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
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class LootFilterScreen extends Screen {
    private static final int COLUMNS = 9;
    private static final int PAGE_SIZE = COLUMNS * 4;
    private static final int SLOT_SIZE = 20;
    private static final int GRID_TOP = 82;
    private static String searchQuery = "";
    private EditBox searchBox;

    private final Category category;
    private final int page;
    private final boolean showingEntries;

    public LootFilterScreen() { this(null, 0, false); }

    private LootFilterScreen(Category category, int page, boolean showingEntries) {
        super(Component.literal("LootFilter"));
        this.category = category;
        this.page = Math.max(0, page);
        this.showingEntries = showingEntries;
    }

    @Override
    protected void init() {
        int center = width / 2;
        searchBox = new EditBox(font, center - 110, 48, 220, 20, Component.literal("Search items"));
        searchBox.setHint(Component.literal("Search blocks and items..."));
        searchBox.setValue(searchQuery);
        searchBox.setResponder(value -> searchQuery = value);
        addRenderableWidget(searchBox);
        if (!showingEntries) {
            int buttonWidth = 126, buttonHeight = 20, gap = 6, columns = 3;
            int totalWidth = columns * buttonWidth + (columns - 1) * gap;
            int left = (width - totalWidth) / 2, top = 76;
            Category[] categories = Category.values();
            for (int i = 0; i < categories.length; i++) {
                Category entry = categories[i];
                int x = left + (i % columns) * (buttonWidth + gap);
                int y = top + (i / columns) * (buttonHeight + 5);
                addRenderableWidget(Button.builder(Component.literal(entry.label),
                        button -> open(entry, 0, true)).bounds(x, y, buttonWidth, buttonHeight).build());
            }
            addRenderableWidget(Button.builder(Component.literal("Done"), button -> onClose())
                    .bounds(center - 40, height - 28, 80, 20).build());
        } else {
            addRenderableWidget(Button.builder(Component.literal("< Categories"), button -> open(null, 0, false))
                    .bounds(center - 104, 32, 100, 20).build());
            addRenderableWidget(Button.builder(Component.literal("< Previous"), button -> open(category, Math.max(0, page - 1), true))
                    .bounds(center - 104, height - 32, 100, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Next >"), button -> open(category, Math.min(page + 1, maxPage()), true))
                    .bounds(center + 4, height - 32, 100, 20).build());
            addRenderableWidget(Button.builder(Component.literal("Done"), button -> onClose())
                    .bounds(center - 40, height - 56, 80, 20).build());
        }
    }

    private void open(Category nextCategory, int nextPage, boolean entries) {
        Minecraft.getInstance().setScreen(new LootFilterScreen(nextCategory, nextPage, entries));
    }

    private int maxPage() { return Math.max(0, (entries().size() - 1) / PAGE_SIZE); }

    private List<Entry> entries() {
        List<Entry> result = new ArrayList<>();
        String query = searchQuery.trim().toLowerCase(Locale.ROOT);
        boolean globalSearch = !query.isEmpty();
        if (category == null && !globalSearch) return result;
        if (globalSearch) {
            for (Block block : BuiltInRegistries.BLOCK) {
                ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
                Item item = block.asItem();
                if (id == null || item == Items.AIR || !matchesSearch(id, query)) continue;
                result.add(new Entry(id.toString(), "block:" + id, new ItemStack(item)));
            }
            for (Item item : BuiltInRegistries.ITEM) {
                ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
                if (id == null || item == Items.AIR || item instanceof BlockItem || !matchesSearch(id, query)) continue;
                result.add(new Entry(id.toString(), "item:" + id, new ItemStack(item)));
            }
        } else if (category.blockCategory) {
            for (Block block : BuiltInRegistries.BLOCK) {
                Item item = block.asItem();
                ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
                if (item == Items.AIR || id == null || classifyBlock(id.getPath()) != category) continue;
                result.add(new Entry(id.toString(), "block:" + id, new ItemStack(item)));
            }
        } else {
            for (Item item : BuiltInRegistries.ITEM) {
                ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
                if (item == Items.AIR || id == null || item instanceof BlockItem || classifyItem(item, id.getPath()) != category) continue;
                result.add(new Entry(id.toString(), "item:" + id, new ItemStack(item)));
            }
        }
        result.sort(Comparator.comparing(Entry::id));
        return result;
    }

    private static boolean matchesSearch(ResourceLocation id, String query) {
        return id.getPath().replace('_', ' ').toLowerCase(Locale.ROOT).contains(query)
                || id.toString().toLowerCase(Locale.ROOT).contains(query);
    }

    private static String displayName(String id) {
        ResourceLocation location = ResourceLocation.tryParse(id);
        if (location == null) return id;
        String[] words = location.getPath().replace('_', ' ').split(" ");
        StringBuilder name = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) continue;
            if (name.length() > 0) name.append(' ');
            name.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        if (!location.getNamespace().equals("minecraft")) name.append(" (").append(location.getNamespace()).append(')');
        return name.toString();
    }

    private static Category classifyBlock(String path) {
        String p = path.toLowerCase(Locale.ROOT);
        if (containsAny(p, "sapling", "leaves", "flower", "tall_grass", "short_grass", "fern", "mushroom", "roots", "vine", "crop", "cactus", "bamboo", "azalea", "lily", "seagrass", "kelp", "coral", "wheat", "carrots", "potatoes", "beetroots", "sugar_cane", "dead_bush", "moss", "dripleaf", "hanging_roots", "propagule", "fungus", "nether_sprouts")) return Category.PLANTS;
        if (containsAny(p, "ore", "ancient_debris", "amethyst_block", "budding_amethyst", "raw_iron_block", "raw_gold_block", "raw_copper_block", "coal_block", "iron_block", "gold_block", "copper_block", "diamond_block", "emerald_block", "lapis_block", "redstone_block", "netherite_block")) return Category.ORES;
        if (containsAny(p, "redstone", "repeater", "comparator", "observer", "piston", "dispenser", "dropper", "hopper", "lever", "button", "pressure_plate", "tripwire", "target", "daylight_detector", "sculk_sensor", "rail", "redstone_lamp", "lectern", "note_block")) return Category.REDSTONE;
        if (containsAny(p, "stairs", "slab", "wall", "fence", "fence_gate", "door", "trapdoor", "planks", "log", "wood", "stem", "hyphae", "brick", "bricks", "concrete", "terracotta", "stone", "cobblestone", "deepslate", "sandstone", "quartz_block", "prismarine", "blackstone", "basalt", "purpur", "end_stone", "netherrack", "mud_brick", "mosaic", "plank", "polished", "cut_", "chiseled", "pillar", "tile", "tiles", "glass", "wool", "carpet", "copper", "bookshelf", "crafting_table", "furnace", "chest", "barrel", "anvil")) return Category.BUILDING;
        return Category.DECORATION;
    }

    private static Category classifyItem(Item item, String path) {
        String p = path.toLowerCase(Locale.ROOT);
        if (item.getFoodProperties() != null || containsAny(p, "food", "stew", "soup", "cookie", "bread", "apple", "berries", "melon_slice", "cake", "pie", "jerky", "sandwich", "honey_bottle")) return Category.FOOD;
        if (containsAny(p, "sapling", "seed", "seeds", "flower", "plant", "crop", "fertilizer", "bone_meal", "mushroom", "root", "sprout")) return Category.PLANTS;
        if (containsAny(p, "pickaxe", "axe", "shovel", "hoe", "shears", "flint_and_steel", "fishing_rod", "brush", "compass", "clock", "spyglass", "carrot_on_a_stick", "warped_fungus_on_a_stick", "wrench", "hammer", "multitool")) return Category.TOOLS;
        if (containsAny(p, "sword", "bow", "crossbow", "trident", "shield", "helmet", "chestplate", "leggings", "boots", "armor", "armour", "arrow", "mace")) return Category.COMBAT;
        if (containsAny(p, "potion", "tipped_arrow", "brewing", "dragon_breath", "fermented_spider_eye", "blaze_powder", "ghast_tear", "glistering_melon")) return Category.POTIONS;
        if (containsAny(p, "boat", "minecart", "saddle", "elytra", "rail", "lead", "leash", "harness")) return Category.TRANSPORT;
        if (containsAny(p, "ingot", "nugget", "raw_", "gem", "diamond", "emerald", "coal", "charcoal", "stick", "string", "leather", "feather", "flint", "clay_ball", "brick", "prismarine_shard", "shard", "crystal", "dust", "powder", "scrap", "bucket", "spawn_egg")) return Category.MATERIALS;
        return Category.OTHER;
    }

    private static boolean containsAny(String value, String... parts) {
        for (String part : parts) if (value.contains(part)) return true;
        return false;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, title, width / 2, 12, 0xFFFFFF);
        if (!showingEntries) {
            graphics.drawCenteredString(font, searchQuery.isBlank() ? "Choose a category or search" : "Search results", width / 2, 28, 0xDDDDDD);
            graphics.drawCenteredString(font, "All items are enabled by default", width / 2, height - 24, 0x80E080);
            if (searchQuery.isBlank()) return;
        }
        List<Entry> all = entries();
        String heading = !searchQuery.isBlank() ? "Search: " + searchQuery : category.label;
        graphics.drawCenteredString(font, Component.literal(heading + "  •  Page " + (page + 1) + " / " + (maxPage() + 1)), width / 2, 72, 0xDDDDDD);
        int start = page * PAGE_SIZE, end = Math.min(all.size(), start + PAGE_SIZE);
        int left = width / 2 - (COLUMNS * SLOT_SIZE) / 2;
        for (int index = start; index < end; index++) {
            Entry entry = all.get(index);
            int slot = index - start, x = left + (slot % COLUMNS) * SLOT_SIZE, y = GRID_TOP + (slot / COLUMNS) * SLOT_SIZE;
            boolean enabled = LootFilterSelection.isEnabled(entry.selectionKey());
            graphics.fill(x, y, x + 18, y + 18, enabled ? 0xAA287A36 : 0xAA555555);
            graphics.renderItem(entry.stack(), x + 1, y + 1);
            if (mouseX >= x && mouseX < x + 18 && mouseY >= y && mouseY < y + 18) {
                graphics.renderTooltip(font, Component.literal(displayName(entry.id())), mouseX, mouseY);
            }
        }
        graphics.drawCenteredString(font, "Green = enabled    Gray = disabled", width / 2, height - 70, 0xCCCCCC);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if ((showingEntries || !searchQuery.isBlank()) && button == 0) {
            List<Entry> all = entries();
            int start = page * PAGE_SIZE, left = width / 2 - (COLUMNS * SLOT_SIZE) / 2;
            for (int index = start; index < Math.min(all.size(), start + PAGE_SIZE); index++) {
                int slot = index - start, x = left + (slot % COLUMNS) * SLOT_SIZE, y = GRID_TOP + (slot / COLUMNS) * SLOT_SIZE;
                if (mouseX >= x && mouseX < x + 18 && mouseY >= y && mouseY < y + 18) {
                    LootFilterSelection.toggle(all.get(index).selectionKey());
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searchBox != null && searchBox.isFocused() && searchBox.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (searchBox != null && searchBox.isFocused() && searchBox.charTyped(codePoint, modifiers)) return true;
        return super.charTyped(codePoint, modifiers);
    }

    @Override public boolean isPauseScreen() { return false; }

    private enum Category {
        BUILDING("Construction Blocks", true), DECORATION("Decoration Blocks", true),
        PLANTS("Saplings & Plants", true), ORES("Ores & Mineral Blocks", true),
        REDSTONE("Redstone", true), TOOLS("Tools", false), COMBAT("Weapons & Armor", false),
        FOOD("Food", false), MATERIALS("Materials & Resources", false), POTIONS("Potions & Brewing", false),
        TRANSPORT("Transport", false), OTHER("Other Items", false);
        private final String label;
        private final boolean blockCategory;
        Category(String label, boolean blockCategory) { this.label = label; this.blockCategory = blockCategory; }
    }

    private record Entry(String id, String selectionKey, ItemStack stack) {}
}
