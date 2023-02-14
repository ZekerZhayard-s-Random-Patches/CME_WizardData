package io.github.zekerzhayard.cme_nbttaglist.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class ClassTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String className, String transformedName, byte[] basicClass) {
        if ("net.minecraft.nbt.NBTTagList".equals(transformedName)) {
            ClassNode cn = new ClassNode();
            new ClassReader(basicClass).accept(cn, ClassReader.EXPAND_FRAMES);
            for (MethodNode mn : cn.methods) {
                if (RemapUtils.checkMethodName(cn.name, mn.name, mn.desc, "<init>") && RemapUtils.checkMethodDesc(mn.desc, "()V")
                    || RemapUtils.checkMethodName(cn.name, mn.name, mn.desc, "func_152446_a") && RemapUtils.checkMethodDesc(mn.desc, "(Ljava/io/DataInput;ILnet/minecraft/nbt/NBTSizeTracker;)V")) {
                    for (AbstractInsnNode ain : mn.instructions.toArray()) {
                        if (ain.getOpcode() == Opcodes.PUTFIELD) {
                            FieldInsnNode fin = (FieldInsnNode) ain;
                            if (RemapUtils.checkClassName(fin.owner, "net/minecraft/nbt/NBTTagList") && RemapUtils.checkFieldName(fin.owner, fin.name, fin.desc, "field_74747_a") && RemapUtils.checkFieldDesc(fin.desc, "Ljava/util/List;")) {
                                mn.instructions.insertBefore(fin, new InsnNode(Opcodes.POP));
                                mn.instructions.insertBefore(fin, new MethodInsnNode(Opcodes.INVOKESTATIC, "io/github/zekerzhayard/cme_nbttaglist/CopyOnWriteArrayListWithMutableIterator", "create", "()Ljava/util/List;", false));
                            }
                        }
                    }
                }
            }
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cn.accept(cw);
            basicClass = cw.toByteArray();
        }
        return basicClass;
    }
}
