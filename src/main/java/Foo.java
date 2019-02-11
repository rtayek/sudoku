

public class Foo {  // https://stackoverflow.com/questions/54620124/error-when-using-generics-with-junit-4-thoeries
    void foo(boolean b) {
        System.out.println(b);
    }
    public <T extends Comparable> void comparableTransitiveTheory(T a, T b, T c) {
        foo (Math.signum(a.compareTo(b)) == Math.signum(b.compareTo(c)));
        foo (Math.signum(a.compareTo(c)) == Math.signum(a.compareTo(b)));
}
    public static void main(String[] args) {
        Foo foo=new Foo();
        Comparable c1=new Comparable() {
            @Override public int compareTo(Object o) {
                return 0;
            }
        };
        foo.comparableTransitiveTheory(c1,c1,c1);
    }
}
