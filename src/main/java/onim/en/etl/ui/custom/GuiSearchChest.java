package onim.en.etl.ui.custom;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import com.google.common.base.Strings;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import onim.en.etl.ExtendTheLow;

public class GuiSearchChest extends GuiChest {

  private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

  private static String searchString = "";

  private final boolean improveBackgroundRender;

  private final int inventoryRows;

  private GuiTextField searchField;

  private boolean hideSearchBox = true;

  private List<Slot> searchResult;

  private boolean initialized = false;

  public GuiSearchChest(IInventory playerInv, IInventory chestInv, boolean improveBackgroundRender) {
    super(playerInv, chestInv);
    this.improveBackgroundRender = improveBackgroundRender;
    this.inventoryRows = chestInv.getSizeInventory() / 9;

    this.searchResult = new LinkedList<>();
  }

  public void initGui() {
    super.initGui();

    this.searchField = new GuiTextField(5505, ExtendTheLow.AdvancedFont, this.width / 2 - 75, this.height / 2
        - 6, 150, 12);
    this.searchField.setText(searchString);
  }

  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    if (!initialized) {
      this.updateSearchResult();
      initialized = true;
    }

    super.drawScreen(mouseX, mouseY, partialTicks);
    if (!this.hideSearchBox) {
      GlStateManager.disableDepth();
      this.searchField.drawTextBox();
      GlStateManager.enableDepth();
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    if (this.improveBackgroundRender) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(i, j, 0, 0, this.xSize, 17);

      for (int y = 0; y < this.inventoryRows; y++) {
        this.drawTexturedModalRect(i, 17 + j + (18 * y), 0, 17, this.xSize, 18);
      }

      this.drawTexturedModalRect(i, j + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
    } else {
      super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);

    if (this.hideSearchBox) {
      return;
    }

    for (Slot slot : searchResult) {
      GlStateManager.disableLighting();
      GlStateManager.disableDepth();
      int x = slot.xDisplayPosition;
      int y = slot.yDisplayPosition;
      GlStateManager.colorMask(true, true, true, false);

      GlStateManager.pushMatrix();
      GlStateManager.translate(x, y, 0);
      GlStateManager.color(1F, 0F, 0F, 1F);
      GlStateManager.disableTexture2D();
      GlStateManager.disableLighting();
      GL11.glLineWidth(4f);
      WorldRenderer buf = Tessellator.getInstance().getWorldRenderer();
      buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);

      buf.pos(-0.5, 0, 0).endVertex();
      buf.pos(16.5, 0, 0).endVertex();

      buf.pos(16, 0, 0).endVertex();
      buf.pos(16, 16, 0).endVertex();

      buf.pos(16.5, 16, 0).endVertex();
      buf.pos(-0.5, 16, 0).endVertex();

      buf.pos(0, 16, 0).endVertex();
      buf.pos(0, 0, 0).endVertex();

      Tessellator.getInstance().draw();

      GlStateManager.colorMask(true, true, true, true);
      GlStateManager.enableLighting();
      GlStateManager.enableDepth();
      GlStateManager.popMatrix();
      GlStateManager.enableTexture2D();
    }

  }

  @Override
  public void drawDefaultBackground() {
    super.drawDefaultBackground();

    if (this.hideSearchBox || Strings.isNullOrEmpty(searchString)) {
      String text = I18n.format("onim.en.etl.chestSearchBox.help1");
      ExtendTheLow.AdvancedFont.drawString(text, 4, 4, 0xFFFFFF);
    } else {
      String text = I18n.format("onim.en.etl.chestSearchBox.help2", searchString);
      ExtendTheLow.AdvancedFont.drawString(text, 4, 4, 0xFFFFFF);
    }
  }

  protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType) {
    super.handleMouseClick(slotIn, slotId, clickedButton, clickType);
    this.updateSearchResult();
  }

  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    if (!this.hideSearchBox) {
      this.searchField.mouseClicked(mouseX, mouseY, mouseButton);
    }
    if (!this.searchField.isFocused()) {
      super.mouseClicked(mouseX, mouseY, mouseButton);
    }
  }

  @Override
  protected void keyTyped(char typedChar, int keyCode) throws IOException {
    if (typedChar == '\n' || typedChar == '\r') {
      this.hideSearchBox = !this.hideSearchBox;
      this.searchField.setFocused(!this.hideSearchBox);
      if (!this.hideSearchBox) {
        this.updateSearchResult();
      }
      return;
    }

    if (!this.hideSearchBox && this.searchField.textboxKeyTyped(typedChar, keyCode)) {
      searchString = this.searchField.getText();
    } else {
      super.keyTyped(typedChar, keyCode);
    }
    this.updateSearchResult();
  }

  private void updateSearchResult() {
    this.searchResult.clear();
    for (Slot slot : this.inventorySlots.inventorySlots) {
      ItemStack stack = slot.getStack();

      if (this.hasRelation(stack, searchString)) {
        this.searchResult.add(slot);
      }

    }

  }

  private boolean hasRelation(ItemStack stack, String text) {
    if (stack == null || Strings.isNullOrEmpty(text)) {
      return false;
    }

    List<String> lore = stack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);

    return lore.stream().anyMatch(s ->
            s.replaceAll("§{2,}", "§")   //2文字以上連続する"§"を一文字の"§"に置換
                    .replaceAll("§.", "")//カラーコードを削除
                    .toLowerCase()
                    .contains(text.toLowerCase())
    );
  }

}
