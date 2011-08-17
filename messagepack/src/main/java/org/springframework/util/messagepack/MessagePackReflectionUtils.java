package org.springframework.util.messagepack;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * TODO we need to crawl the entire object graph and make sure that every type that can be registered with MessagePack is indeed registered
 */
public abstract class MessagePackReflectionUtils {

    public static Class[] getGenericTypesForReturnValue(Method method) {
        Type t1 = method.getGenericReturnType();
        if (t1 instanceof ParameterizedType) {

            ParameterizedType genericReturnType = (ParameterizedType) method.getGenericReturnType();

            Type[] type = genericReturnType.getActualTypeArguments();
            Class[] classes = new Class[type.length];
            int ctr = 0;
            for (Type t : type) {

                if (!(t instanceof WildcardType)) {
                    Assert.isInstanceOf(Class.class, t);
                    classes[ctr++] = (Class) t;
                }
            }
            return classes;
        }
        return new Class[0];
    }

    public static interface ClassTraversalCallback {
        void doWithClass(Class<?> o);
    }

    static class ObjectClassTraverser {

        private Collection<Class> findClassesToVisit(Method method) {
            Set<Class> classes = new HashSet<Class>();
            classes.add(method.getReturnType());
            Collections.addAll(classes, method.getParameterTypes());
            Collections.addAll(classes, MessagePackReflectionUtils.getGenericTypesForReturnValue(method));
            return classes;
        }

        protected void doCrawl(Class<?> clzz, final Set<Class> tovisit, final ClassTraversalCallback callback) {

            final String javaLang = String.class.getPackage().getName();

            ReflectionUtils.doWithMethods(clzz, new ReflectionUtils.MethodCallback() {
                @Override
                public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {


                    Collection<Class> classesToVisit = findClassesToVisit(method);
                    for (Class c : classesToVisit) {

                        boolean jdkType = c == null || c.equals(void.class) ||
                               c.getPackage().getName().startsWith(javaLang);
                        boolean collectionType = Collection.class.isAssignableFrom(c);

                        if (!jdkType && !collectionType && !c.isPrimitive()) {
                            if (!classesToVisit.contains(c)) {
                                tovisit.add(c);

                            }
                        }
                    }
                }
            });

            for (Class<?> cl : tovisit) {
                doCrawl(cl, tovisit, callback);
            }



        }
    }

    public static void crawlJavaBeanObjectGraph(Object src, ClassTraversalCallback objectCallback) {
        final Set<Class> classesToVisit = new HashSet<Class>();
        Class<?> clzz = src.getClass();
        ObjectClassTraverser classTraverser = new ObjectClassTraverser();
        classTraverser.doCrawl(clzz, classesToVisit, objectCallback);

    }

}
