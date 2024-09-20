package com.sun.tools.javac.comp;

import com.sun.source.util.*;
import com.sun.tools.javac.api.*;
import com.sun.tools.javac.code.*;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.*;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.*;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.Map;

public final class Nil implements Plugin {
    @Override
    public String getName() {
        return "nil";
    }

    @Override
    public void init(JavacTask task, String... args) {
        try {
            this.init(task);
        } catch (Throwable e) {
            this.throwUnchecked(e);
        }
    }

    public void init(JavacTask task) throws Throwable {
        final Module currentModule = Nil.class.getModule();
        //noinspection OptionalGetWithoutIsPresent
        final Module compilerModule = ModuleLayer.boot().findModule("jdk.compiler").get();

        final Method opener = Module.class.getDeclaredMethod("implAddOpens", String.class, Module.class);
        opener.setAccessible(true);
        for (String pack : new String[] {
                "com.sun.tools.javac.api",
                "com.sun.tools.javac.code",
                "com.sun.tools.javac.comp",
                "com.sun.tools.javac.main",
                "com.sun.tools.javac.util",
                "com.sun.tools.javac.tree"
        }) {
            opener.invoke(compilerModule, pack, currentModule);
        }

        final Context context = ((BasicJavacTask) task).getContext();

        final Class<?> attrType = this.reload(NilAttr.class);
        final Object attr = this.instance(attrType, context);

        final Map<?, ?> singletons = (Map<?, ?>) this.getProtected(context, "ht");
        for (Object component : singletons.values()) {
            if (component == null) continue;
            try {
                this.setProtected(component, "attr", attr);
            } catch (NoSuchFieldException ignored) {
            }
        }
    }

    public static final class NilAttr extends Attr {
        private final Context context;

        public NilAttr(Context context) {
            super(context);
            this.context = context;
        }

        @Override
        public void visitIf(JCIf tree) {
            final JCTree.JCParens condition = (JCParens) tree.getCondition();

            if (condition.expr instanceof JCIdent) {
                final Type type = super.attribExpr(condition.expr, env, syms.objectType);
                if (type == syms.objectType) {
                    final TreeMaker treeMaker = TreeMaker.instance(context).at(tree.pos);

                    final JCBinary binary = treeMaker.Binary(
                            JCTree.Tag.NE,
                            condition.expr,
                            treeMaker.Literal(TypeTag.BOT, null)
                    );

                    tree.cond = treeMaker.Parens(binary);
                }
            }

            super.visitIf(tree);
        }

        public static NilAttr instance(Context context) {
            final Attr current = context.get(attrKey);
            //noinspection ConditionCoveredByFurtherCondition
            if (current != null && current instanceof NilAttr) {
                return (NilAttr) current;
            } else {
                context.put(attrKey, (NilAttr) null);
                return new NilAttr(context);
            }
        }
    }

    Object instance(Class<?> type, Context context) throws Exception {
        return type.getDeclaredMethod("instance", Context.class).invoke(null, context);
    }

    Class<?> reload(Class<?> type) throws Exception {
        try (final InputStream input = Nil.class.getClassLoader().getResourceAsStream(type.getName().replace('.', '/') + ".class")) {
            assert input != null;
            final byte[] bytes = new byte[input.available()];
            input.read(bytes);

            final Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
            defineClass.setAccessible(true);

            try {
                return (Class<?>) defineClass.invoke(Context.class.getClassLoader(), type.getName(), bytes, 0, bytes.length);
            } catch (InvocationTargetException e) {
                return type;
            }
        }
    }

    Object getProtected(Object object, String field) throws Exception {
        final Field f = object.getClass().getDeclaredField(field);
        f.setAccessible(true);
        return f.get(object);
    }

    void setProtected(Object object, String field, Object value) throws Exception {
        final Field f = object.getClass().getDeclaredField(field);
        f.setAccessible(true);
        f.set(object, value);
    }

    @SuppressWarnings("unchecked")
    <T extends Throwable> void throwUnchecked(Throwable e) throws T {
        throw (T) e;
    }
}