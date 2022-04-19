package onim.en.etl.core.injector;

import onim.en.etl.core.Bytecodes;
import onim.en.etl.core.HookInjector;
import onim.en.etl.core.ObfuscateType;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class ImproveChestBackgroundRender extends HookInjector {

    private static final String GUI_CHEST = "net/minecraft/client/gui/inventory/GuiChest";

    private static final String GUI_CONTAINER = "net/minecraft/client/gui/inventory/GuiContainer";

    public ImproveChestBackgroundRender() {
        super("net.minecraft.client.gui.inventory.GuiChest");
        this.registerEntry(ObfuscateType.NONE, "drawGuiContainerBackgroundLayer", "(FII)V");
        this.registerEntry(ObfuscateType.OBF, "a", "(FII)V");
    }

    @Override
    public boolean injectHook(InsnList list, ObfuscateType type) {
        MethodInsnNode hook = new MethodInsnNode(Opcodes.INVOKESTATIC, HOOK, "onDrawGuiContainerBackgroundLayer",
                String.format("(L%s;III)Z", GUI_CHEST), false);

        LabelNode label = new LabelNode();
        InsnList inject = new InsnList();

        inject.add(new VarInsnNode(Opcodes.ALOAD, 0));
        inject.add(Bytecodes.stackField(GUI_CONTAINER, type == ObfuscateType.NONE ? "xSize" : "field_146999_f", "I"));
        inject.add(Bytecodes.stackField(GUI_CONTAINER, type == ObfuscateType.NONE ? "ySize" : "field_147000_g", "I"));
        inject.add(Bytecodes.stackField(GUI_CHEST, type == ObfuscateType.NONE ? "inventoryRows" : "field_147018_x", "I"));
        inject.add(hook);
        inject.add(new JumpInsnNode(Opcodes.IFEQ, label));
        inject.add(new InsnNode(Opcodes.RETURN));
        inject.add(label);

        list.insert(inject);

        return true;
    }

}
