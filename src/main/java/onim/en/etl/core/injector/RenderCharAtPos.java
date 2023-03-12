package onim.en.etl.core.injector;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

import onim.en.etl.core.Bytecodes;
import onim.en.etl.core.HookInjector;
import onim.en.etl.core.ObfuscateType;

public class RenderCharAtPos extends HookInjector {

  public RenderCharAtPos() {
    super("net.minecraft.client.gui.FontRenderer");

    this.registerEntry(ObfuscateType.NONE, "renderChar", "(CZ)F");
    this.registerEntry(ObfuscateType.OBF, "a", "(CZ)F");
  }

  @Override
  public boolean injectHook(InsnList list, ObfuscateType type) {

    MethodInsnNode hook = new MethodInsnNode(Opcodes.INVOKESTATIC, "onim/en/etl/Hooks",
        "onRenderCharAtPos", "(Z)V", false);
    
    InsnList inject = new InsnList();

    String owner = type == ObfuscateType.NONE ? "net/minecraft/client/gui/FontRenderer" : "avn";

    inject.add(Bytecodes.stackField(owner, type == ObfuscateType.NONE ? "boldStyle" : "s", "Z"));
    inject.add(hook);

    list.insert(inject);
    
    return true;
  }

}
