package onim.en.etl.core.injector;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

import onim.en.etl.core.Bytecodes;
import onim.en.etl.core.HookInjector;
import onim.en.etl.core.ObfuscateType;

public class DrawStringDropShadow extends HookInjector {

  public DrawStringDropShadow() {
    super("net.minecraft.client.gui.FontRenderer");
    this.registerEntry(ObfuscateType.NONE, "drawString", "(Ljava/lang/String;FFIZ)I");
    this.registerEntry(ObfuscateType.SRG, "func_175065_a", "(Ljava/lang/String;FFIZ)I");
    this.registerEntry(ObfuscateType.OBF, "a", "(Ljava/lang/String;FFIZ)I");
  }

  @Override
  public boolean injectHook(InsnList list, ObfuscateType type) {
    MethodInsnNode hook = new MethodInsnNode(Opcodes.INVOKESTATIC, "onim/en/etl/Hooks", "getShadowOffset", "(F)F", false);
    return Bytecodes.injectAfterSequence(list, new int[] {Opcodes.FCONST_1}, (loc) -> {
      list.insert(loc, hook);
      return true;
    });
  }

}
