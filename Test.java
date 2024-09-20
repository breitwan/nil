public final class Test {
    public static void main(String... args) {
        Object object = null;

        if (object) System.out.println("object not null?");
        object = "string";
        if (object) System.out.println("object not null!");

        boolean bool = true;
        if (bool) System.out.println("everything works fine");
    }
}