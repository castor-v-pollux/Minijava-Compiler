package piglet;

/**
 * <p>piglet代码包装类，继承自{@link PStmt}，表示NOOP语句。
 * <p>由于NOOP语句形式唯一，PNoOpStmt使用单例类。
 *
 * @author castor_v_pollux
 */
public class PNoOpStmt extends PStmt {

	private static PNoOpStmt instance = new PNoOpStmt();

	private PNoOpStmt() {
	}

	public static PNoOpStmt getInstance() {
		return instance;
	}

	@Override
	public int print(int depth) {
		System.out.println("NOOP");
		return MULTILINE;
	}

}
